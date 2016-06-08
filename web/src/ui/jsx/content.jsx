define(['react','reactdom','jsx!/ui/util/ajaxRequest','jsx!/ui/util/counts','jsx!/ui/visualization/stackedBar','jsx!/ui/etc/restclient'],
  function(React,ReactDOM,Ajax,Counts,StackedBar,RestClient) {
    class ContentModule extends React.Component {
      //생성자
      constructor(props) {
        super(props);
        this.state = {
          'grpData':{
            'minDate':'2016-06-08 21:00:00',
            'maxDate':'2016-06-08 23:59:59',
            'data':[]
          }
        };
        this.dataMapping.bind(this);
        this.getData();
      }
      componentDidMount() {
        //this.timer = setInterval(this.getData.bind(this), 10000);
        this.isMount = true;
      }
      getData() {
        let counts = new Counts(this.state.grpData.minDate +' ~ ' +this.state.grpData.maxDate,this);

        // let values = counts.addSubscribe(this.dataMapping.bind(this));
        //this.dateHistogram = new DateHistogram();
        counts.getPromise().bind(this).then(this.dataMapping).catch(this.dataMapping);
      }
      dataMapping(data) {
        /*
        문서에서는 this.state를 수동으로 업데이트 하지 마라고
        되어 있지만 그럴 경우 값 전달이 되지 않는다.
        수동으로 업데이트 한 뒤, setState() 로 React에 통지한다.
        */
        //this.state.grpData = data;
        this.setState({'grpData':Object.assign(this.state.grpData,data)});
      }
      render() {
        /*
        React.createClass로 컴포넌트를 생성하면 아래 클릭 이벤트에 this가 바인딩 되어야 했으나 ES6으로 넘어오면서 오토바인딩 기능이 사라졌다.
        위의 생성자에서 바인딩해도 되고 아래의 문장에서 바인딩 해도 된다.
        */
        /*
        var grpData = [];
        if(_.isObjectLike(this.grpData)) {
          Object.assign(grpData,this.grpData);
        }
        <RestClient />
        */

        return (
        <div className="ui main text container padded grid">
          <div className="center">
            <ul>
              <li><span>From to : </span>( {this.state.grpData.minDate} ~ {this.state.grpData.maxDate} )</li>
              <li><span>Interval : </span>{this.state.grpData.interval}</li>
            </ul>
          </div>
          <StackedBar grpData={this.state.grpData}/>
          {/*<DateHistogram grpData={this.state.grpData}/>*/}
        </div>
        );
      }
    }
    ContentModule.propTypes = {
      grpData:React.PropTypes.object
    };
    return ContentModule;
  }
);