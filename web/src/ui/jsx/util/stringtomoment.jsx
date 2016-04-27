define(['moment-timezone'],function(moment){
  class StringToMoment{
    constructor(dateStr){
      this.dateStr = dateStr;
      this.abber = {
        'S':'millisecond',
        's':'second',
        'm':'minute',
        'h':'hour',
        'd':'day',
        'M':'month',
        'y':'year'
      };
    }
    parse(){
      let dateStrRange = this.dateStr.split('~');
      return dateStrRange.map(this.subParse.bind(this));
    }
    subParse(dateStr) {
      dateStr = dateStr.trim();
      if(dateStr === 'now') {
        return moment();
      }
      dateStr = dateStr.replace('now', '');
      if(dateStr.charAt(0)==='+'){
        return moment().add(dateStr.match(/\d+/g)[0],this.abber[dateStr.substr(-1)]);
      }else if (dateStr.charAt(0)==='-') {
        return moment().subtract(dateStr.match(/\d+/g)[0],this.abber[dateStr.substr(-1)]);
      }
    }
    stringify(interval) {
      let number = parseInt(interval.match(/\d+/g)[0]);
      let endfix = this.abber[interval.substr(-1)]+(number>1?"s":"");

      return number + " " + endfix;
    }
  }
  return StringToMoment;
});