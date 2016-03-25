/**
그래프 시간 간격에 대한 로직
1. 해당 시간대에 속한 인덱스들을 모두 찾아온다.
2. 시간을 계산하여 그래프의 갯수가 60이 나오도록 시간간격을 정한다.
3. 인덱스 하나마다 쿼리를 돌려서 카운트를 가져온다.
4. 결과들을 한 곳에 담아 그래프 컴포넌트에 입력한다.
**/
define(['lodash','moment','jsx!/ui/util/ajaxRequest'], function(_,moment,AjaxRequest){
  class countsLoader{
    constructor(timeText='~'){
      const BAR_CNT_PER_ONE_PAGE = 60;
      this.dateTimes = _(timeText.split('~')).map(function(item){
        return moment(dateTimes);
      });
    }
    getBarChartInterval() {
      if(this.dateTimes.length===2) {
        this.dateTimes[1].diff(this.dateTimes[0]);
        let timeDiff = this.dateTimes[1].diff(this.dateTimes[0]),
          interval = Math.round(timeDiff/BAR_CNT_PER_ONE_PAGE),
          units = [1,3,5,10,15,30,45],
          units2 = _.cloneDeep(units),
          amounts = [1,1000,60,60,24,30,12,10,10],
          amounts2 = ['millisecond','second','minute','hour','days','month','year','decade'],
          minium = 0,
          finalUnit = 0,
          compareTarget = 0,
          multiplex = 0,
          i = 0,
          j = 0;

        console.log('diff :' + timeDiff);
        console.log("interval : "+interval);

        for(let amount of amounts) {
          i = 0;
          for(let unit of units) {
            console.log("Unit : " + unit + ", Amount : " + amount);

            multiplex = (unit * amount);
            compareTarget = multiplex/interval;
            units[i] = multiplex;

            if(compareTarget > 1.5){
              break;
            }
            if(compareTarget<1){
              if(minium === 0 || minium >= 1-compareTarget){
                minium = 1 - compareTarget;
                finalUnit = multiplex;
              }
            }else{
              if(minium === 0 || minium >= compareTarget - 1){
                minium = compareTarget - 1;
                finalUnit = multiplex;
              }
            }

            console.log(units2[i]+":"+multiplex+":"+compareTarget+":"+minium);
            i++;
          }
          if(compareTarget > 1.5) {
            break;
          }else{
            j++;
          }
        }

        let finalUnitFormatted = '';
        console.log('Final : '+finalUnit);
        i = 0;
        while(Math.floor(finalUnit/amounts[i])>0) {
          finalUnit = Math.round(finalUnit/amounts[i]);
          i++;
        }
        console.log('FinalFormatted : ' +finalUnit + amounts2[i-1]);
      }
    }
    return [finalUnit,finalUnitFormatted];
  }
  loadData(interval) {
    let ajaxReq = new AjaxRequest();
    ajaxReq.request({
      uri:'/ElasticSearch/'
    })
  }
});