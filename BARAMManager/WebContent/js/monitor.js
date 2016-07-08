$(function(){
  var M = {
    timerID:0,
    cpuData:[],
    memData:[],
    diskData:[],
    seek:0,
    statsTabOpend:true,
    formatBytes:function(bytes,decimals) {
      if(bytes == 0) return '0 Byte';
      var k = 1024;
      var dm = decimals + 1 || 2;
      var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
      var i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    },
    loop:function(){
      $.get("/server/monitoring.jsp").error(M.dataError).success(M.dataHandler);
    },
    subscribeLogFile:function(){
      if(M.seek == undefined) {
    	M.seek = 0;
      }
      $.get('/server/taillog.jsp',{'seek':M.seek},M.logHandler);
    },
    dataError:function(){
      $(".timeTxt").css("color","red").text("Data error or Baram just stopped");
      var table = $(".monitoring");
      table.find(".CpuLoadNum").text("0");
      table.find(".CpuTotalLoadNum").text("0");
      table.find(".HeapNum").text("0");
      table.find('.HeapDetail > .used').text('0');
      table.find('.HeapDetail > .max').text('0');
      table.find('.DiskNum').text('');
      table.find('.DiskDetail > .free').text('0');
      table.find('.DiskDetail > .total').text('0');
      M.cpuData = [];
      M.memData = [];
      M.diskData = [];
      if(M.statsTabOpend) {
       Chart(window,d3,M.cpuData,"cpuChart",moment);
       Chart(window,d3,M.memData,"memChart",moment);
       Chart(window,d3,M.diskData,"diskChart",moment);
      }
      
    },
    dataHandler:function(resp) {
      if(resp.trim() === "Baram is not Connected"){
        M.dataError();
        return;
      }
      var data = JSON.parse(resp);
      var statstable = $(".monitoring");

      if(data === null){
        M.dataError();
      }else{
        var heap = data.memory.heap;
        var disk = data.disk["/"];
        var memUsage = parseFloat(heap.used/heap.max * 100).toFixed(2);
        var diskUsage = parseFloat((disk.total - disk.free)/disk.total * 100).toFixed(2);

        M.cpuData.push({"time":data.current_timestamp,"value":data.os.process_cpuload});
        M.memData.push({"time":data.current_timestamp,"value":memUsage});
        M.diskData.push({"time":data.current_timestamp,"value":diskUsage});

        $(".timeTxt").css("color","black").text(moment(data.current_timestamp).format("Y-MM-DD HH:mm:ss"));

        statstable.find(".CpuLoadNum").text(data.os.process_cpuload);
        statstable.find(".CpuTotalLoadNum").text("SYS total : "+data.os.system_cpuload +"%");
        statstable.find(".HeapNum").text(memUsage);
        statstable.find('.HeapDetail > .used').text('used : ' + M.formatBytes(heap.used));
        statstable.find('.HeapDetail > .max').text('max : ' + M.formatBytes(heap.max));
        statstable.find('.DiskNum').text(diskUsage);
        statstable.find('.DiskDetail > .free').text('free : '+ M.formatBytes(disk.free));
        statstable.find('.DiskDetail > .total').text('total : '+ M.formatBytes(disk.total));
        if(M.statsTabOpend) {
          Chart(window,d3,M.cpuData,"cpuChart",moment);
          Chart(window,d3,M.memData,"memChart",moment);
          Chart(window,d3,M.diskData,"diskChart",moment);
        }
      }
    },
    logHandler:function(resp){
      M.seek = resp.offset;
      if(M.seek <= 0 || resp.content != ""){
    	  $('.logOutput').text(resp.content.trim());
    	  $('.logOutput').scrollTop($('.logOutput')[0].scrollHeight);
      }
    }
  }
  /*
  for(var i =0;i<5;i++){
    var ms = 500;
    var start = +(new Date());
    while (new Date() - start < ms);
    M.loop();
  }
  M.loop();
  */
  window.setInterval(M.loop, 3000);
  $('.subscribe').click(function(e){
    event.preventDefault();
    $('.subscribe').prop('disabled',true);

    M.timerID = window.setInterval(M.subscribeLogFile, 3000);
  });
  $('.subscribe').click();
  $('.clearsubscribe').click(function(e){
    event.preventDefault();
    $('.subscribe').prop('disabled',false);
    window.clearInterval(M.timerID);
  });
  $('.nav-tabs a').click(function(e){
    event.preventDefault();
    var href = $(this).attr('href');
    if(href=="#Stats"){
      M.statsTabOpend = true;
    }else{
      M.statsTabOpend = false;
    }
    $('.tab-pane').removeClass('active').filter(href).addClass('active');
  });
});