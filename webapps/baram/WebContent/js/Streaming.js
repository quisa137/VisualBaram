'use strict';

var chartColors = [ '#d95f49', '#55a660', '#489ad8', '#edc233', '#8357ac',
		'#818b8d', '#db8438', '#b44b37', '#64c271', '#5f9ac5', '#ea9f34',
		'#9168b5', '#bec3c7', '#45627f', '#c76026', '#54b59a', '#FFEBCD',
		'#0000FF', '#8A2BE2', '#A52A2A', '#DEB887', '#5F9EA0', '#7FFF00',
		'#D2691E', '#FF7F50', '#6495ED', '#FFF8DC', '#DC143C', '#00FFFF',
		'#00008B', '#008B8B', '#B8860B', '#A9A9A9', '#A9A9A9', '#006400',
		'#BDB76B', '#8B008B', '#556B2F', '#FF8C00', '#9932CC', '#8B0000',
		'#E9967A', '#8FBC8F', '#483D8B', '#2F4F4F', '#2F4F4F', '#00CED1',
		'#9400D3', '#FF1493', '#00BFFF', '#696969', '#696969', '#1E90FF',
		'#B22222', '#FFFAF0', '#228B22', '#FF00FF', '#DCDCDC', '#F8F8FF',
		'#FFD700', '#DAA520', '#808080', '#808080', '#008000', '#ADFF2F',
		'#F0FFF0', '#FF69B4', '#CD5C5C', '#4B0082', '#FFFFF0', '#F0E68C',
		'#E6E6FA', '#FFF0F5', '#7CFC00', '#FFFACD', '#ADD8E6', '#F08080',
		'#E0FFFF', '#FAFAD2', '#D3D3D3', '#D3D3D3', '#90EE90', '#FFB6C1',
		'#FFA07A', '#20B2AA', '#87CEFA', '#778899', '#778899', '#B0C4DE',
		'#FFFFE0', '#00FF00', '#32CD32', '#FAF0E6', '#FF00FF', '#800000',
		'#66CDAA', '#0000CD', '#BA55D3' ];

// d3.selectAll('#version').text(dc.version);
console.log(data);
var timeFormat = d3.time.format('%Y-%m-%d %H:%M:%S');
var numberFormat = d3.format(',.1f');

data.forEach(function(e) {
	e.time = timeFormat.parse(e.time);
	e.k = e.k;
	e.v = +e.v;
});

var crf = crossfilter(data);

var kDimension = crf.dimension(function(d) {
	return d.k;
});

var timeDimension = crf.dimension(function(d) {
	return [ d.k, d.time ];
});

var quarterChart = dc.pieChart("#quarter-chart");
var seriesChart = dc.seriesChart("#seriesChart");

var kGroup = kDimension.group().reduce(function(p, v) {
	p.v += v.v;
	return p;
}, function(p, v) {
	p.v -= v.v;
	return p;
}, function() {
	return {
		v : 0
	};
}).order(function orderValue(p) {
	return p.v;
});
var topValues = kGroup.top(kGroup.size());

var timeGroup = timeDimension.group().reduce(function(p, v) {
	p.v += v.v;
	return p;
}, function(p, v) {
	p.v -= v.v;
}, function() {
	return {
		v : 0
	};
});

var chartWidth = Math.round(window.innerWidth );
var chartHeight = Math.round(window.innerHeight );

function getLineValue(v) {
	return function(d) {
		return d.value[v];
	};
}

quarterChart.width(150).height(100).radius(25).turnOnControls(false).dimension(
		kDimension).group(kGroup).valueAccessor(function(p) {
	return Math.round(p.value.v);
}).ordering(function(p) {
	return -p.value.v;
}).ordinalColors(chartColors).title(function(p) {
	return p.key + ' :' + numberFormat(p.value.v);
});

seriesChart.width(chartWidth-180).height(chartHeight-220).margins({
	top : 50,
	right : 130,
	bottom : 50,
	left : 50
}).turnOnControls(false).dimension(timeDimension).chart(function(c) {
	return dc.lineChart(c).interpolate('linear');
}).x(d3.time.scale().domain(d3.extent(timeDimension, function(d) {
	return d.time;
}))).elasticX(true).elasticY(true).brushOn(true).yAxisLabel('count')
		.xAxisLabel('Time').clipPadding(10).group(timeGroup).mouseZoomable(
				false).seriesAccessor(function(d) {
			return d.key[0];
		}).keyAccessor(function(d) {
			return d.key[1];
		}).valueAccessor(function(d) {
			return d.value.v;
		}).renderHorizontalGridLines(true).renderVerticalGridLines(true).ordinalColors(chartColors)
		.legend(dc.legend().x(chartWidth - 270).y(chartHeight/5).itemHeight(13).gap(5));

dc.renderAll();
