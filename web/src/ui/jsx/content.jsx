define(['react','fetch'],
  function(React,fetch) {
    class ContentModule extends React.Component {
      constructor(props) {
        super(props);
        this.state = {reqBody:'{"index":["logstash-2016.01.27"],"search_type":"count","ignore_unavailable":true}\n{"size":0,"sort":[{"@timestamp":{"order":"desc","unmapped_type":"boolean"}}],"query":{"filtered":{"query":{"query_string":{"analyze_wildcard":true,"query":"*"}},"filter":{"bool":{"must":[{"range":{"@timestamp":{"gte":1451622634181,"lte":1454215534181,"format":"epoch_millis"}}}],"must_not":[]}}}},"highlight":{"pre_tags":["@kibana-highlighted-field@"],"post_tags":["@/kibana-highlighted-field@"],"fields":{"*":{}},"require_field_match":false,"fragment_size":2147483647},"aggs":{"2":{"date_histogram":{"field":"@timestamp","interval":"12h","time_zone":"Asia/Tokyo","min_doc_count":0,"extended_bounds":{"min":1451622634181,"max":1454215534181}}}},"fields":["*","_source"],"script_fields":{},"fielddata_fields":["@timestamp","received_at"]}\n'}
        this.reqSearch = this.reqSearch.bind(this); //아래에서 하는 수도 있다.
      }
      reqSearch(e) {
        e.preventDefault();
        e.stopPropagation();
        let body = this.state.reqBody;
        let bodySize = encodeURI(body).split(/%..|./).length - 1;

        var myHeaders = new Headers();
        myHeaders.append('Accept','application/json, text/plain, */*');
        myHeaders.append('Accept-Encoding','gzip, deflate');
        myHeaders.append('Accept-Language','ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4');
        myHeaders.append('Cache-Control','no-cache');
        myHeaders.append('Connection','keep-alive');
        myHeaders.append('Content-Length',bodySize);
        myHeaders.append('Content-Type','application/json;charset=UTF-8');
        myHeaders.append('DNT','1');
        myHeaders.append('Host','192.168.0.124');
        myHeaders.append('Origin','http://192.168.0.45');
        myHeaders.append('Pragma','no-cache');
        myHeaders.append('Referer','http://192.168.0.45');
        myHeaders.append('User-Agent',navigator.userAgent);
        if(!this.state.reqBody || this.state.reqBody.length<=0){
            alert('Empty');
            return false;
        }
        var requestVars = {
          method:'POST',
          headers:myHeaders,
          mode:'cors',
          cache:'default',
          body:body
        }
        //FetctAPI는 IE10,Ch46,FF에서 지원되는 Ajax Request 방법이다.
        //Promise 패턴으로 처리과정을 간결하게 짤 수 있고
        //jQuery.ajax를 쓰려고 jQuery를 임포트 안해도 된다.
        //여기서 임포트한 fetch는를 지원하지 않는 브라우저를 위한 것이다.
        //jQuery보다는 작다.
        fetch('/api/ElasticSearch/_msearch?timeout=0&ignore_unavailable=true&preference=1457671581063',requestVars)
        .then(function(resp){
          if(resp.ok) {
            //resp.json에서는 Promise 객체가 리턴된다.
            return resp.json();
          }else{
            return Promise.reject(new Error(resp));
          }
        })
        .then(function(data){
          console.log(data);
        }).catch(function(e){console.log(e)});
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
          <div className="ui eight wide grey column">
          <form method="POST" className="ui form inverted" id="esForm">
            <div className="field">
              <label htmlFor="">URI</label>
              <input type="text" name="uri"/>
            </div>
            <div className="field">
              <label htmlFor="">SearchData</label>
              <textarea name="searchOption" defaultValue={this.state.reqBody} onChange={this.handleChange.bind(this)}/>
            </div>
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