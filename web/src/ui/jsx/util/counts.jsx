/**
그래프 시간 간격에 대한 로직
1. 해당 시간대에 속한 인덱스들을 모두 찾아온다.
2. 시간을 계산하여 그래프의 갯수가 60이 나오도록 시간간격을 정한다.
3. 인덱스 하나마다 쿼리를 돌려서 카운트를 가져온다.
4. 결과들을 한 곳에 담아 그래프 컴포넌트에 입력한다.
**/
define(['lodash','moment-timezone','jsx!/ui/util/ajaxRequest'], function(_,moment,AjaxRequest){
  class countsLoader{
    constructor(timeText='~'){
      const BAR_CNT_PER_ONE_PAGE = 60;
      this.currentTimeZone = moment.tz.guess();
      this.dateTimes = _.map(timeText.split('~'),function(item){
        return moment(item);
      });
      this.chartInterval = this.setChartInterval();
      Promise.resolve('OK')
        .bind(this)
        .then(this.getFieldStats)
        .then(this.getIndices);
    }
    loadData(searchUri,method,body) {
      let ajaxReq = new AjaxRequest();
      return ajaxReq.request({
        uri:'/api/ElasticSearch/' + searchUri,
        method:method,
        body:body
      });
    }
    getFieldStats(){
      let bodyField = {
        "fields":["@timestamp"],
        "index_constraints":{
          "@timestamp":{
            "max_value":{"gte":this.dateTimes[0].format('x'),"format":"epoch_millis"},
            "min_value":{"lte":this.dateTimes[1].format('x'),"format":"epoch_millis"}
          }
        }
      };

      return this.loadData('logstash-*/_field_stats?level=indices','POST',JSON.stringify(bodyField));
    }
    getIndices(resp){
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
              "interval":this.chartInterval[0]+this.chartInterval[1],
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
        totalDataCnt = 0,
        indexName = '',
        promises = [];
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
        promises.push(
          this.loadData(
            '_msearch?timeout=0&ignore_unavailable=true&preference=1459842496606',
            'POST',
            JSON.stringify(target)+'\n'+JSON.stringify(options)+'\n')
        );
        totalDataCnt += indices[indexName].fields['@timestamp'].doc_count;
      }
      return promises;
    }
    setChartInterval() {
      if(this.dateTimes.length===2) {
        this.dateTimes[1].diff(this.dateTimes[0]);
        let timeDiff = this.dateTimes[1].diff(this.dateTimes[0]),
          BAR_CNT_PER_ONE_PAGE = 60,
          interval = Math.round(timeDiff/BAR_CNT_PER_ONE_PAGE),
          amountObj = {
            'S':1,
            's':1000,
            'm':60,
            'h':60,
            'd':24,
            'M':30.416,
            'y':12,
            '0y':10
          },
          graphInterval = interval,
          unit = '',
          keys = _.keys(amountObj),
          cnt = 0,
          item = 0;

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
        return [Math.round(graphInterval),unit];
      }
    }
  }
  return countsLoader;
});