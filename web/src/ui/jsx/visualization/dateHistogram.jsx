define(['lodash','react','moment','d3'], function(_,React,moment,d3) {
  class DateHistogram extends React.Component {
    constructor(props) {
      super(props);
    }
    setData(data) {
      this.props.data = data;
    }
    update() {
      d3.select('div[name=dateHistogram').
    }
    render() {
      return (
        <div name="dateHistogram"></div>
      );
    }
  }
  DateHistogram.propTypes = {
      data:React.PropTypes.array
  };
  return DateHistogram;
});