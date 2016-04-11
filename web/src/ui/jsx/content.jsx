define(['react','jsx!/ui/util/ajaxRequest','jsx!/ui/util/counts','jsx!/ui/visualization/dateHistogram'],
  function(React,Ajax,Counts,DateHistogram) {
    class ContentModule extends React.Component {
      //생성자
      constructor(props) {
        super(props);
        let counts = new Counts('2016-01-01 00:00:00 ~ 2016-01-31 23:59:59',this);
        let values = counts.addSubscribe(this.dataMapping.bind(this));
        //this.dateHistogram = new DateHistogram();
      }
      dataMapping(data) {

      }
      render() {
        /*
        React.createClass로 컴포넌트를 생성하면 아래 클릭 이벤트에 this가 바인딩 되어야 했으나 ES6으로 넘어오면서 오토바인딩 기능이 사라졌다.
        위의 생성자에서 바인딩해도 되고 아래의 문장에서 바인딩 해도 된다.
        */
        return (
        <div className="ui main text container padded grid">
          <DateHistogram />
        </div>
        );
      }
    }
    return ContentModule;
  }
);