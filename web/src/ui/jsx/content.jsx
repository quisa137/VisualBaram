define(['react','jsx!/ui/util/ajaxRequest'],
  function(React,Ajax) {
    class ContentModule extends React.Component {
      //생성자
      constructor(props) {
        super(props);
        this.state = {reqBody:'{"index":["logstash-2016.01.29"],"ignore_unavailable":true}\n{"size":500,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"query":{"filtered":{"query":{"query_string":{"analyze_wildcard":true,"query":"*"}},"filter":{"bool":{"must":[{"range":{"@timestamp":{"gte":1451624961876,"lte":1454217861877,"format":"epoch_millis"}}}],"must_not":[]}}}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"require_field_match":false,"fragment_size":2147483647},"aggs":{"2":{"date_histogram":{"field":"@timestamp","interval":"12h","time_zone":"Asia/Tokyo","min_doc_count":0,"extended_bounds":{"min":1451624961876,"max":1454217861877}}}},"fields":["*","_source"],"script_fields":{},"fielddata_fields":["@timestamp","received_at"]}\n{"index":["logstash-2016.01.28"],"search_type":"count","ignore_unavailable":true}\n{"size":0,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"query":{"filtered":{"query":{"query_string":{"analyze_wildcard":true,"query":"*"}},"filter":{"bool":{"must":[{"range":{"@timestamp":{"gte":1451624961876,"lte":1454217861877,"format":"epoch_millis"}}}],"must_not":[]}}}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"require_field_match":false,"fragment_size":2147483647},"aggs":{"2":{"date_histogram":{"field":"@timestamp","interval":"12h","time_zone":"Asia/Tokyo","min_doc_count":0,"extended_bounds":{"min":1451624961876,"max":1454217861877}}}},"fields":["*","_source"],"script_fields":{},"fielddata_fields":["@timestamp","received_at"]}\n'}
        this.reqSearch = this.reqSearch.bind(this); //아래에서 하는 수도 있다.
      }
      reqSearch(e) {
        e.preventDefault();
        e.stopPropagation();

        let ajax = new Ajax();
        ajax.request({
          uri:'/api/ElasticSearch/_msearch?timeout=0&ignore_unavailable=true&preference=1457671581063',
          method:'POST',
          body:this.state.reqBody
        })
        .then(function(data){
          console.log(data);
        })
        .catch(function(e){
          //Header 정보 표시
          for(let key of e.headers.keys()){
            console.log(key+' : '+e.headers.get(key) );
          }
          console.log(e.body);
          console.log(e);
        });
      }
      handleChange(e) {
        this.setState({reqBody:e.target.value})
      }
      render() {
        /*
        React.createClass로 컴포넌트를 생성하면 아래 클릭 이벤트에 this가 바인딩 되어야 했으나 ES6으로 넘어오면서 오토바인딩 기능이 사라졌다.
        위의 생성자에서 바인딩해도 되고 아래의 문장에서 바인딩 해도 된다.
        */
        return (
        <div className="ui main text container padded grid">
          <div className="ui four wide grey column">
          <form method="POST" className="ui form inverted" id="esForm">
            <button className="ui button" onClick={this.reqSearch}>submit</button>
          </form>
          </div>
          <div className="rightSide">

          </div>
        </div>
        );
      }
    }
    return ContentModule;
  }
);