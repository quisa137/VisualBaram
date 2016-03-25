define(['lodash','fetch','bluebird'],function(_,fetch,Promise) {
  class AjaxRequest {
    constructor() {
      this.defaultHeaders = {
        'accept':'application/json, text/plain, */*',
        'accept-encoding':'gzip, deflate',
        'accept-language':'ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4',
        'cache-control':'no-cache',
        'connection':'keep-alive',
        'content-length':0,
        'content-type':'application/json;charset=UTF-8',
        'dnt':'1',
        'pragma':'no-cache'
      };
      //'Host':'192.168.0.124',
      //'Origin':'http://192.168.0.45',
      //'Referer':'http://192.168.0.45'
    }
    request({uri = '',method = 'GET',headers = {},body = ''} = {}) {
      let reqHeader = {};
      reqHeader = _.extend(this.defaultHeaders,headers);
      let bodySize = encodeURI(body).split(/%..|./).length - 1;
      let header = new Headers();

      //Lodash 메소드 체이닝
      _(this.defaultHeaders)
        .extend(headers)
        .forEach(function(value,key) {
          header.append(key,value);
        });
      //Default에서 정의해두었기에 값 대체만 한다.
      header.set('content-length',bodySize);

      if(method.toLowerCase() === 'post' && (!body || body.length === 0)) {
        alert('body is required');
        return false;
      }

      let reqVars = {
        method:method,
        headers:header,
        mode:'cors',
        cache:'default',
        body:body
      }

      //FetctAPI는 Ch43,FF39에서 지원되는 Ajax Request 방법이다.
      //Promise 패턴으로 처리과정을 간결하게 짤 수 있고
      //jQuery.ajax를 쓰려고 jQuery를 임포트 안해도 된다.
      //여기서 임포트한 fetch는를 지원하지 않는 브라우저를 위한 것이다.
      //jQuery보다는 작다.

      //상태 체크 기본으로 딸려감
      return fetch(uri,reqVars).then(function(resp){
        if(resp.ok) {
          return resp.json();
        }else{
          return Promise.reject(resp);
        }
      });
    }
  }
  return AjaxRequest;
});