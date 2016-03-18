define(['react','semantic','jquery','fetch'],
  function(React,semantic,$) {
    class ContentModule extends React.Component {
      constructor(props) {
        super(props);
        this.state = {reqBody:''}
        this.reqSearch = this.reqSearch.bind(this); //아래에서 하는 수도 있다.

      }
      reqSearch(e) {
        e.preventDefault();
        e.stopPropagation();

        var myHeaders = new Headers();
        myHeaders.append('Accept','application/json, text/plain, */*');
        myHeaders.append('Accept-Encoding','gzip, deflate');
        myHeaders.append('Accept-Language','ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4');
        myHeaders.append('Cache-Control','no-cache');
        myHeaders.append('Connection','keep-alive');
        myHeaders.append('Content-Length','818');
        myHeaders.append('Content-Type','application/json;charset=UTF-8');
        myHeaders.append('DNT','1');
        myHeaders.append('Host','192.168.0.124');
        myHeaders.append('Origin','http://192.168.0.45');
        myHeaders.append('Pragma','no-cache');
        myHeaders.append('Referer','http://192.168.0.45');
        myHeaders.append('User-Agent',navigator.userAgent);

        var requestVars = {
          method:'POST',
          headers:myHeaders,
          mode:'cors',
          cache:'default',
          body:this.state.reqBody
        }
        fetch('/api/ElasticSearch/_msearch?timeout=0&ignore_unavailable=true&preference=1457671581063',requestVars).then(function(resp){
          if(resp.ok) {
            resp.json().then(function(data){
              console.log(data.entries);
            })
          }else{
            console.log(resp);
          }
        }).catch(function(e){console.log(e)});
      }
      render() {
        /*React.createClass로 컴포넌트를 생성하면 아래 클릭 이벤트에 this가 바인딩 되어야 했으나 ES6으로 넘어오면서 오토바인딩 기능이 사라졌다.
        위의 생성자에서 바인딩해도 되고 아래의 문장에서 바인딩 해도 된다.*/
        return (
        <div className="ui main text container padded grid">
          <div className="ui four wide grey column">
          <form method="POST" className="ui form inverted" id="esForm">
            <div className="field">
              <label htmlFor="">URI</label>
              <input type="text" name="uri"/>
            </div>
            <div className="field">
              <label htmlFor="">SearchData</label>
              <textarea name="searchOption">{this.state.reqBody}</textarea>
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