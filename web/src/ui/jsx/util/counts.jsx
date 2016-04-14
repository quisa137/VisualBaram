/**
그래프 시간 간격에 대한 로직
1. 해당 시간대에 속한 인덱스들을 모두 찾아온다.
2. 시간을 계산하여 그래프의 갯수가 60이 나오도록 시간간격을 정한다.
3. 인덱스 하나마다 쿼리를 돌려서 카운트를 가져온다.
4. 결과들을 한 곳에 담아 그래프 컴포넌트에 입력한다.
**/
define(['lodash','moment-timezone','jsx!/ui/util/ajaxRequest'], function(_,moment,AjaxRequest){
  class countsLoader{
    constructor(timeText='~',react){
      this.BAR_CNT_PER_ONE_PAGE = 60;
      this.reactInstance = react;
      this.currentTimeZone = moment.tz.guess();
      this.dateTimes = _.map(timeText.split('~'),function(item){
        return moment(item);
      });

      this.chartInterval = setChartInterval.bind(this)();

      function setChartInterval() {
        if(this.dateTimes.length===2) {
          this.dateTimes[1].diff(this.dateTimes[0]);
          let timeDiff = this.dateTimes[1].diff(this.dateTimes[0]),
            BAR_CNT_PER_ONE_PAGE = 60,
            interval = Math.round(timeDiff/BAR_CNT_PER_ONE_PAGE),
            /*아래 변수들은 es의 interval 단위*/
            amountObj = {'S':1,'s':1000,'m':60,'h':60,'d':24,'M':30.416,'y':12,'0y':10},
            graphInterval = interval,
            unit = '',
            keys = _.keys(amountObj),
            cnt = 0,
            item = 0;
          /*위의 정의된 순서대로 interval값을 나눠서 적절한 단위를 찾아낸다.*/
          for(item of _.values(amountObj)) {
            let temp = (graphInterval / item);
            if(temp < 1) {
              break;
            } else {
              graphInterval = temp;
              unit = keys[cnt];
              cnt++;
            }
          }
          return Math.round(graphInterval)+unit;
        }
      }

      function loadData(searchUri,method,body) {
        let ajaxReq = new AjaxRequest();
        return ajaxReq.request({
          uri:'/api/ElasticSearch/' + searchUri,
          method:method,
          body:body
        }).then(function(resp) {
          //에러 메시지 감지
          //console.log(resp.error);
          if(_.isObjectLike(resp.responses)){
            if(_.has(resp,'responses.0.error')) {
              console.log(resp.responses[0].error);
            }else{
              return resp;
            }
          }else{
            return resp;
          }
      });
      }
      function getFieldStats(){
        let bodyField = {
          "fields":["@timestamp"],
          "index_constraints":{
            "@timestamp":{
              "max_value":{"gte":this.dateTimes[0].format('x'),"format":"epoch_millis"},
              "min_value":{"lte":this.dateTimes[1].format('x'),"format":"epoch_millis"}
            }
          }
        };

        return loadData('logstash-*/_field_stats?level=indices','POST',JSON.stringify(bodyField));
      }
      function getIndices(resp){
        let indices = resp.indices;
        let bodyField = [
        {"index":[],"ignore_unavailable":true},
        {
          "size":0,
          "sort":[{
            "@timestamp":{
              "order":"desc",
              "unmapped_type":"boolean"
            }
          }],
          "query":{
            "filtered":{
              "query":{
                "query_string":{
                  "analyze_wildcard":true,
                  "query":"*"
                }
              },
              "filter":{
                "bool":{
                  "must":[{
                    "range":{
                      "@timestamp":{
                        "gte":parseInt(this.dateTimes[0].format('x')),
                        "lte":parseInt(this.dateTimes[1].format('x')),
                        "format":"epoch_millis"
                      }
                    }
                  }],
                  "must_not":[]
                }
              }
            }
          },
          "highlight":{
            "pre_tags":["@kibana-highlighted-field@"],
            "post_tags":["@/kibana-highlighted-field@"],
            "fields":{
              "*":{}
            },
            "require_field_match":false,
            "fragment_size":2147483647
          },
          "aggs":{
            "2":{
              "date_histogram":{
                "field":"@timestamp",
                "interval":this.chartInterval,
                "time_zone":this.currentTimeZone,
                "min_doc_count":0,
                "extended_bounds":{
                  "min":parseInt(this.dateTimes[0].format('x')),
                  "max":parseInt(this.dateTimes[1].format('x'))
                }
              }
            }
          },
          "fields":["*","_source"],
          "script_fields":{},
          "fielddata_fields":["@timestamp","received_at"]
        }];

        let LIMIT = 500,
          REQ_LIMIT = 20,
          totalDataCnt = 0,
          indexName = '',
          promises = [];
        /*
        TODO : 요청이 아주 장기간 일 때는 Array에 Promise를 넣기도 전에 Promise의 응답이 오는 사태가 벌어질 수 있다. 일정한 갯수가 되면 반환하고 만들고 하는 형식으로 작업이 되어야 한다.
         - 해당 방법은 bluebird의 설정 중에 동시 요청 수를 제한하여 해결하기로 한다.
        */
        for(indexName in indices) {
          let target = bodyField[0];
          let options = bodyField[1];
          target.index[0] = indexName;

          if(totalDataCnt < LIMIT) {
            options.size = LIMIT;
          }else{
            target['search_type'] = 'count';
            options.size = 0;
          }


          let p = loadData(
            '_msearch?timeout=0&ignore_unavailable=true&preference=1459842496606',
            'POST',
            JSON.stringify(target)+'\n'+JSON.stringify(options)+'\n')
          totalDataCnt += indices[indexName].fields['@timestamp'].doc_count;

          promises.push(p);

          // if(promises.length > REQ_LIMIT) {
          //   Promise.bind(this).all(promises).then(this.storeToContainer);
          // }

        }

        return Promise.all(promises);
      }
      function convertData(values) {
        var rsult = _.reduce(values,function(result,item) {
          if(_.isObjectLike(result.responses)) {
            result = result.responses[0].aggregations[2].buckets;
          }
          return _.mergeWith(
            result,
            item.responses[0].aggregations[2].buckets,
            function(objValue,srcValue) {
              if(_.isObjectLike(objValue) && _.isObjectLike(srcValue)) {
                if(objValue.key_as_string === srcValue.key_as_string) {
                  return (objValue.doc_count > srcValue.doc_count)? objValue:srcValue;
                }
              }
              return srcValue;
            });
        });
        return rsult;
      }
      /*데이터를 부르기 전에 등록되었던 핸들러들을 모조리 실행한다.*/
      /*
      addSubscribe(callback) {
        if(this.callbacks === undefined) {
          this.callbacks = [];
        }
        this.callbacks.push(callback);
      }
      storeToContainer(values) {
        this.values = values;
        if(_.isArray(this.callbacks)) {
          for(let callback of this.callbacks) {
            if(_.isFunction(callback)) {
              callback(this.values);
            }
          }
        }
      }
      */
      /*
      아래 Promise들은 일련의 흐름을 나타내며 위에 있는 메소드의 연산결과가 아래의 매개변수에 입력된다.
      물론, 단독 실행도 가능함
      bind(this)로 실행 컨텍스트의 일관성을 유지함.
      즉, Promise의 실행종료시, 컨텍스트도 파괴됨
      */
      this.myPromise = Promise.resolve('OK')
        .bind(this)
        .then(getFieldStats)
        .then(getIndices)
        .then(convertData);
    }
    getPromise() {
      return this.myPromise;
    }
  }
  return countsLoader;
});