define(['react','reactdom','jsx!/ui/util/ajaxRequest','jsx!/ui/util/counts','jsx!/ui/visualization/dateHistogram'],
  function(React,ReactDOM,Ajax,Counts,DateHistogram) {
    class ContentModule extends React.Component {
      //생성자
      constructor(props) {
        super(props);
        this.state = {'grpdata':[]};
        let counts = new Counts('2016-01-01 00:00:00 ~ 2016-01-31 23:59:59',this);

        // let values = counts.addSubscribe(this.dataMapping.bind(this));
        //this.dateHistogram = new DateHistogram();
        this.dataMapping.bind(this);
        counts.getPromise().bind(this).then(this.dataMapping);
      }
      dataMapping(data) {
        /*
        문서에서는 this.state를 수동으로 업데이트 하지 마라고
        되어 있지만 그럴 경우 값 전달이 되지 않는다.
        수동으로 업데이트 한 뒤, setState() 로 React에 통지한다.
        */
        this.state.grpData = data;
        this.setState(this.state);
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
        */

        return (
        <div className="ui main text container padded grid">
          <DateHistogram grpData={this.state.grpData} />
        </div>
        );
      }
    }
    ContentModule.propTypes = {
      grpData:React.PropTypes.array
    };
    return ContentModule;
  }
);