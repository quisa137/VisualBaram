define(['lodash','react','moment','d3'], function(_,React,moment,d3) {
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
      this.width = (window.screen.width > 50?window.screen.width:300) - margin.left - margin.right;
      this.height = 300 - margin.top - margin.bottom;
      this.xRange = d3.scale.ordinal().rangeRoundBands([0, this.width], .1);
      this.yRange = d3.scale.linear().range([this.height,0]),
      this.xAxis = d3.svg.axis().scale(this.xRange).orient("bottom");
      this.yAxis = d3.svg.axis().scale(this.yRange).orient("left");

      this.chart = d3.select('.dateHistogram')
        .attr('width',this.width + margin.left + margin.right)
        .attr('height',this.height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
    }
    componentWillUpdate (props,nextState,obj) {
      this.state = {grpData:props.grpData};
      this.update();
      //this.setState({});
    }
    update() {
      let data = this.state.grpData;
      let dLength = data.length;
      this.xRange.domain(data.map(function(d) {
        return d.key_as_string.split('.')[0];
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

      chart.selectAll(".bar")
          .data(data)
        .enter().append("rect")
          .attr("class", "bar")
          .attr("x", function(d) { return xRange(d.key_as_string.split('.')[0]); })
          .attr("y", function(d) { return yRange(d.doc_count); })
          .attr("height", function(d) { return height - yRange(d.doc_count); })
          .attr("width", xRange.rangeBand());
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
    grpData : React.PropTypes.array
  };
  return DateHistogram;
});