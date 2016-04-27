define(['lodash','react','moment','d3','d3tip'], function(_,React,moment,d3,d3tip) {
  class DateHistogram extends React.Component {
    constructor(props) {
      super(props);
      this.state = {grpData:[]};
    }
    render() {
      return (
        <div {...this.props} >
          <svg className="dateHistogram"></svg>
        </div>
      );
    }
    componentDidMount() {
      let margin = {top: 20, right: 30, bottom: 30, left: 40};
      this.width = (document.body.clientWidth > 50?document.body.clientWidth:300) - margin.left - margin.right;
      this.height = 300 - margin.top - margin.bottom;
      this.xRange = d3.scale.ordinal().rangeRoundBands([0, this.width], .1);
      this.yRange = d3.scale.linear().range([this.height,0]),
      this.xAxis = d3.svg.axis().scale(this.xRange).orient("bottom");
      this.yAxis = d3.svg.axis().scale(this.yRange).orient("left");
      this.tip = d3tip()
        .attr('class', 'd3-tip')
        .offset([-10, 0])
        .html(function(d) {
          return "<span>date:" + d.key_as_string + "</span>"+"<Br>"+"<span>count:" + d.doc_count + "</span>";
        });
      this.graphLength = 60;

      this.chart = d3.select('.dateHistogram')
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
      this.chart.selectAll('.x.axis,.y.axis,.bar').remove();
      function xLabelFunc(d){
        return moment(d.key).format('hh:mm');
      }
      let data = this.state.grpData;
      let dLength = this.graphLength;
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
        yRange = this.yRange;

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
      chart.selectAll(".bar")
        .data(data).enter()
        .append("rect")
        .attr("class", "bar")
        .attr("x", function(d) {
          return xRange(xLabelFunc(d))
        })
        .attr("y", function(d) { return yRange(d.doc_count); })
        .attr("height", function(d) { return height - yRange(d.doc_count); })
        .attr("width", xRange.rangeBand())
      .on('mouseover',this.tip.show)
      .on('mouseout',this.tip.hide);

        /*
        .on('mouseover',function(e){
          let bar = d3.select(this);
          bar.attr('style','fill:purple');
          tip.show(e);
        })
        .on('mouseout',function(e){
          let bar = d3.select(this);
          bar.attr('style','fill:steelblue');
          tip.hide(e);
        });
        */
      let xAsixTicks = chart.selectAll('.x.axis .tick');

      xAsixTicks.select('line').attr('style',function(d,i,j){
        return (isHidden(i))?'stroke:black':'stroke:red';
      });
      xAsixTicks.select('text').attr('style',function(d,i,j){
        return (isHidden(i))?'opacity:0':'opacity:1;fill:red';
      });
      /*
      let barWidth = this.width / data.length;
      let h = this.height;
      let yRange = this.yRange;

      let bar = this.chart.selectAll("g")
        .data(data)
        .enter().append("g")
          .attr("transform", function(d, i) {
            return "translate(" + i * barWidth + ",0)";
          });

      bar.append("rect")
        .attr("y", function(d) { return range(d.doc_count); })
        .attr("height", function(d) { return h - range(d.doc_count); })
        .attr("width", barWidth - 1);

      bar.append("text")
        .attr("x", barWidth / 2)
        .attr("y", function(d) { return range(d.doc_count) + 3; })
        .attr("dy", ".75em")
        .text(function(d) { return d.doc_count; });
      */
    }
  }
  DateHistogram.propTypes = {
    grpData : React.PropTypes.object
  };
  return DateHistogram;
});