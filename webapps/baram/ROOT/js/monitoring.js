'use strict';

var chartColors = ['#d95f49','#55a660','#489ad8','#edc233','#8357ac','#818b8d','#db8438','#b44b37','#64c271','#5f9ac5','#ea9f34','#9168b5','#bec3c7','#45627f','#c76026','#54b59a'];
var bubbleChart = dc.bubbleChart('#monitoring-bubble-chart');
var quarterChart = dc.pieChart('#quarter-chart');
var memoryChart = dc.rowChart('#memory-chart');
var stackChart = dc.lineChart('#stack-chart');
var lineChart = dc.lineChart('#line-chart');

//d3.selectAll('#version').text(dc.version);
console.log(data);
var timeFormat = d3.time.format('%Y-%m-%d %H:%M:%S');
var numberFormat = d3.format(',.1f');
data.forEach(function(e) {
	e.logtype = e.logtype;
	e.source_size = +e.source_size;
	e.dest_size = +e.dest_size;
	e.linecount = +e.linecount;
	e.elapsed = +e.elapsed;
	e.hdfs_use = +e.hdfs_use;
	e.corrupted = +e.corrupted;
	e.files = +e.files;
	e.dirs = +e.dirs;
	e.loader_memory = +e.loader_memory;
	e.time = timeFormat.parse(e.time);
});

var crf = crossfilter(data);

var logtypeDimension = crf.dimension(function(d) {
	return d.logtype;
});

var timeDimension = crf.dimension(function(d) {
	return d.time;
});

var timeGroup = timeDimension.group().reduce(
		function(p, v) {
			p.linecount += v.linecount;
			p.hdfs_use = v.hdfs_use/1024.0/1024.0;
			return p;
		}, function(p, v) {
			p.linecount -= v.linecount;
			p.hdfs_use = v.hdfs_use/1024.0/1024.0;
		}, function() {
			return {
				linecount : 0,
				hdfs_use : 0.0
			};
		}		
);

var timeTypeGroup = timeDimension.group().reduce(
		function(p, v) {
			p[v.logtype] = (p[v.logtype]||0) + v.linecount;
			return p;
		}, function(p, v) {
			p[v.logtype] = (p[v.logtype]||0) - v.linecount;
			return p;
		}, function() {
			return {};
		}		
);

var dataGroup = logtypeDimension.group().reduce(function(p, v) {
	++p.count;
	p.source_size += v.source_size/1024.0/1024.0;
	p.dest_size += v.dest_size/1024.0/1024.0;
	p.linecount += v.linecount;
	p.elapsed += v.elapsed;
	p.hdfs_use = v.hdfs_use/1024.0/1024.0;
	p.corrupted += v.corrupted;
	p.files = v.files;
	p.dirs = v.dirs;
	p.loader_memory = v.loader_memory/1024.0/1024.0;
	return p;
}, function(p, v) {
	--p.count;
	p.source_size -= v.source_size/1024.0/1024.0;
	p.dest_size -= v.dest_size/1024.0/1024.0;
	p.linecount -= v.linecount;
	p.elapsed -= v.elapsed;
	p.hdfs_use = v.hdfs_use/1024.0/1024.0;
	p.corrupted -= v.corrupted;
	p.files = v.files;
	p.dirs = v.dirs;
	p.loader_memory = v.loader_memory/1024.0/1024.0;
	return p;
}, function() {
	return {
		count : 0,
		source_size : 0.0,
		dest_size : 0.0,
		linecount : 0,
		elapsed : 0,
		hdfs_use : 0.0,
		corrupted : 0,
		files : 0,
		dirs : 0,
		loader_memory : 0.0
	};
}).order(function orderValue(p) { return p.linecount; });

var topValues = dataGroup.top(dataGroup.size());
var quarterChartWidth = Math.round(window.innerWidth*0.18);
var memoryChartWidth = Math.round(window.innerWidth*0.22);
var bubbleChartWidth = Math.round(window.innerWidth-quarterChartWidth-memoryChartWidth-200);
var chartHeight = Math.round(window.innerHeight*0.22);

$(".rowWrap").css("width", window.innerWidth-150);
$(".rowWrap").css("min-height", chartHeight+50);
$(".graphHeight").css("height", chartHeight+88);
$("#bubble-wrap").css("width", bubbleChartWidth);
$("#bubble-wrap").css("height", chartHeight+50);

bubbleChart.width(bubbleChartWidth).height(chartHeight)
		.margins({top : 20, right : 10, bottom : 35, left : 50})
		.dimension(logtypeDimension).group(dataGroup)
		.ordinalColors(chartColors)
		.transitionDuration(1500)
		.keyAccessor(function(p) { return p.value.source_size; })
		.valueAccessor(function(p) { return p.value.dest_size; })
		.radiusValueAccessor(function(p) { return p.value.linecount; })
		.maxBubbleRelativeSize(0.07)
		.xAxisLabel('Source size[MB]')
		.yAxisLabel('HDFS size[MB]')
		.x(d3.scale.linear().domain([ 0, topValues[0].value.source_size ]))
		.y(d3.scale.linear().domain([ 0, topValues[0].value.dest_size ]))
		.r(d3.scale.linear().domain([ 0, topValues[0].value.linecount ]))
		.elasticX(true).xAxisPadding(50)
		.elasticY(true).yAxisPadding(50)
		.renderHorizontalGridLines(true).renderVerticalGridLines(true)
		.renderLabel(true).renderTitle(true).label(function(p) {return p.key;})
		.title(
				function(p) {
					return 'event type : ' + p.key + ', source size :'
							+ numberFormat(p.value.source_size) + 'MB, event count :'
							+ numberFormat(p.value.linecount) + ', hdfs size :'
							+ numberFormat(p.value.dest_size)+'MB';
				})
		.yAxis().tickFormat(function(v) {
			return numberFormat(v);
		});

$("#quarter-wrap").css("width", quarterChartWidth+5);
$("#quarter-wrap").css("height", chartHeight+50);

quarterChart.width(quarterChartWidth).height(chartHeight)
		.radius(quarterChartWidth*0.25).innerRadius(quarterChartWidth*0.10)
		.dimension(logtypeDimension)
		.group(dataGroup).valueAccessor(function (p) { return Math.round(p.value.hdfs_use); }).ordering(function(p){return -p.value.linecount;})
		.ordinalColors(chartColors)
		.label(function(p) {return p.key+' : ' + numberFormat(p.value.hdfs_use/1024.0) + '[GB]';})
		.title(
				function(p) {
					return 'event type : ' + p.key + ', hdfs size :' + numberFormat(p.value.hdfs_use/1024.0)+'[GB]';
				});

$("#memory-wrap").css("width", memoryChartWidth);
$("#memory-wrap").css("height", chartHeight+50);
memoryChart.width(memoryChartWidth+250).height(chartHeight)
		.margins({top : 20, right : 10, bottom : 50, left : 30})
		.dimension(logtypeDimension)
		.group(dataGroup).valueAccessor(function (p) { return Math.round(p.value.loader_memory); }).ordering(function(p){return -p.value.linecount;})
		.ordinalColors(chartColors)
		.label(function(p) {return p.key+' : ' + numberFormat(p.value.loader_memory) + '[MB]';}).title(
				function(p) {
					return 'event type : ' + p.key + ', memory use :' + numberFormat(p.value.loader_memory)+'[MB]';
				});

function getStack(v) {
    return function(d) {
        return d.value[v];
    };
}

var stackChartWidth = Math.round(window.innerWidth*0.90);
$("#stack-wrap").css("width", stackChartWidth);
$("#stack-wrap").css("height", chartHeight+50);

stackChart.width(stackChartWidth).height(chartHeight).margins({ top : 10, right : 75, bottom :40, left : 50})
		.dimension(timeDimension)
		.group(timeTypeGroup, topValues[0].key, getStack(topValues[0].key)).legend(dc.legend().x(stackChartWidth-55).y(50).itemHeight(13).gap(5));

for(var i = 1; i<topValues.length; ++i) {
	stackChart.stack(timeTypeGroup, topValues[i].key, getStack(topValues[i].key));
}

stackChart.ordinalColors(chartColors)
.x(d3.time.scale().domain(d3.extent(timeDimension, function(d) {return d.time;})))
.elasticX(true)
.elasticY(true)
.brushOn(false)
.renderHorizontalGridLines(true)
.renderVerticalGridLines(true)
.renderArea(true)
.renderDataPoints(true)
.clipPadding(10)
.xAxisLabel('Time')
.yAxisLabel('Event Count[N]');

$("#line-wrap").css("width", stackChartWidth);

lineChart.width(stackChartWidth).height(chartHeight).margins({top : 10, right : 10, bottom :40, left : 50})
		.dimension(timeDimension)
		.group(timeGroup).valueAccessor(function (p) { return Math.round(p.value.linecount);}).ordering(function(p){return -p.value.linecount;})
		.ordinalColors(chartColors)
		.x(d3.time.scale().domain(d3.extent(timeDimension, function(d) {return d.time;})))
		.elasticX(true)
		.elasticY(true)
		.brushOn(true)
		.renderHorizontalGridLines(true)
		.renderVerticalGridLines(true).xAxisLabel('Time')
		.yAxisLabel('Total Event Count[N]');

dc.renderAll();
