define(['lodash','react','moment','d3','d3tip'], function(_,React,moment,d3,d3tip) {
  /*
  https://bl.ocks.org/mbostock/raw/3886208/
  https://bl.ocks.org/mbostock/3885211
  */
  class StackedBar extends React.Component {
    constructor(props) {
      super(props);
      this.state = {grpData:[]};
      this.legends = ["지반침하", "붕괴", "호우", "지진", "태풍일반", "화재", "감염병", "침수", "홍수"];
    }
    render() {
      return (
        <div {...this.props} >
          <svg className="stackedBar"></svg>
        </div>
      );
    }
    componentDidMount() {
      let margin = {top: 20, right: 30, bottom: 30, left: 40};
      this.width = (document.body.clientWidth > 50?document.body.clientWidth:300) - margin.left - margin.right;
      this.height = 300 - margin.top - margin.bottom;
      this.xRange = d3.scale.ordinal().rangeRoundBands([0, this.width], .1);
      this.yRange = d3.scale.linear().range([this.height,0]),
      this.colors = d3.scale.ordinal().range(["#ce5621", "#527f84", "#8f7f43", "#f5a10b", "#c00000", "#274f47", "#2f2933","#cd0067","#e46c0b"]);

      this.xAxis = d3.svg.axis().scale(this.xRange).orient("bottom");
      this.yAxis = d3.svg.axis().scale(this.yRange).orient("left");

      this.tip = d3tip()
        .attr('class', 'd3-tip')
        .offset([-10, 0])
        .html(function(d) {
          return "<span>date:" + d.key_as_string + "</span>"+"<Br>"+"<span>count:" + d.doc_count + "</span>";
        });
      this.graphLength = 60;

      this.chart = d3.select('.stackedBar')
        .attr('width',this.width + margin.left + margin.right)
        .attr('height',this.height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        .call(this.tip);
      this.update();
    }
    componentWillUpdate (props,nextState,obj) {
      this.graphLength = props.grpData.data.length;
      this.state = {
        grpData:props.grpData.data,
        interval:props.grpData.interval,
        minDate:props.grpData.minDate,
        maxDate:props.grpData.maxDate
      };
      //this.chart.data(props.grpData).enter();
      this.update();
      //this.setState({});
    }
    update() {
      this.chart.selectAll('.x.axis,.y.axis,.time').remove();
      function xLabelFunc(d) {
        return moment(d.key).format('HH:mm');
      }

      let data = this.state.grpData;
      let dLength = this.graphLength;
      this.colors.domain(this.legends);
      this.xRange.domain(data.map(function(d) {
        return xLabelFunc(d);
      }));
      this.yRange.domain([0,d3.max(data,function(d){
        return d.doc_count;
      })]);

      let width = this.width,
        height = this.height,
        chart = this.chart,
        xRange = this.xRange,
        yRange = this.yRange,
        colors = this.colors;

      chart.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + this.height + ")")
        .call(this.xAxis);

      chart.append("g")
        .attr("class", "y axis")
        .call(this.yAxis);

      if(chart.selectAll('p')[0].length>0) {
        chart.selectAll('p').html("minDate : "+this.state.minDate);
      }else{
        chart.append('p').html("minDate : "+this.state.minDate);
      }

      function isHidden(i){
        if(dLength > 25) {
          if(i%Math.floor(dLength*0.1) === 0) {
            return false;
          }else{
            return true;
          }
        }
        return true;
      }
      var timeseries = chart.selectAll(".time")
        .data(data).enter().append("g")
        .attr("class","time")
        .attr("tranform",function(d) {
          return "translate(" + xRange(xLabelFunc(d)) + ",0)";
        });

      timeseries.selectAll("rect")
        .data(function(d){ return d.cateGroup.buckets; })
        .enter().append("rect")
        .attr("width", xRange.rangeBand())
        .attr("y", function(d) { return yRange(d.doc_count); })
        .attr("height", function(d) { return height - yRange(d.doc_count); })
        .style("fill",function(d) {return colors(d.key)})
        .on('mouseover',this.tip.show)
        .on('mouseout',this.tip.hide);
        /*
        .attr("x", function(d) {
          return xRange(xLabelFunc(d))
        })
        var legend = svg.selectAll(".legend")
          .data(color.domain().slice().reverse())
        .enter().append("g")
          .attr("class", "legend")
          .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

      legend.append("rect")
          .attr("x", width - 18)
          .attr("width", 18)
          .attr("height", 18)
          .style("fill", color);

      legend.append("text")
          .attr("x", width - 24)
          .attr("y", 9)
          .attr("dy", ".35em")
          .style("text-anchor", "end")
          .text(function(d) { return d; });
        */

      let xAsixTicks = chart.selectAll('.x.axis .tick');

      xAsixTicks.select('line').attr('style',function(d,i,j){
        return (isHidden(i))?'stroke:black':'stroke:red';
      });
      xAsixTicks.select('text').attr('style',function(d,i,j){
        return (isHidden(i))?'opacity:0':'opacity:1;fill:red';
      });
    }
  }
  StackedBar.propTypes = {
    grpData : React.PropTypes.object
  };
  return StackedBar;
});