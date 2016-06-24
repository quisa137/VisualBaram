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
console.log(data);

var interData = [];
var equation = '';
//d3.selectAll('#version').text(dc.version);

data.forEach(function(e) {
	e.k = e.k;
	e.level = e.level;
	e.v = +e.v;
});

var crf = crossfilter(data);

var kDimension = crf.dimension(function(d) {
	return d.k;
});

var kGroup = kDimension.group().reduce(function(p, v) {
	p.v += v;
	return p;
}, function(p, v) {
	p.v -= v;
	return p;
}, function() {
	return {
		v : 0
	};
}).order(function orderValue(p) {
	return p.v;
});

var dim = crf.dimension(function(d) {
	return d.level;
});

var topValues = kGroup.top(kGroup.size());

var grp = dim.group().reduce(function(p, v) {
	p[v.k] = (p[v.k] || 0) + v.v;
	return p;
}, function(p, v) {
	p[v.k] = (p[v.k] || 0) - v.v;
	return p;
}, function() {
	return {};
});

var chartWidth = Math.round(window.innerWidth)-100;
var chartHeight = Math.round(window.innerHeight);

function getStack(v) {
	return function(d) {
		return d.value[v];
	};
}

if (topValues.length == 1) {

	interData.forEach(function(e) {
		e.k = e.k;
		e.level = +e.level;
		e.v = +e.v;
	});

	var interCrf = crossfilter(interData);
	var interDim = interCrf.dimension(function(d) {
		return d.level;
	});
	var interGroup = interDim.group().reduce(function(p, v) {
		p[v.k] = (p[v.k] || 0) + v.v;
		return p;
	}, function(p, v) {
		p[v.k] = (p[v.k] || 0) - v.v;
		return p;
	}, function() {
		return {};
	});

	var chart = dc.compositeChart('#main-chart').margins({
		top : 50,
		right : 200,
		bottom : 50,
		left : 50
	}).width(chartWidth).height(chartHeight).turnOnControls(false)
	.x(d3.scale.ordinal()).xUnits(dc.units.ordinal).elasticX(true).elasticY(true).brushOn(false)
			.yAxisLabel('count').xAxisLabel('c12')
			.renderHorizontalGridLines(true).renderVerticalGridLines(
					true).legend(
					dc.legend().x(chartWidth).y(100).itemHeight(
							13).gap(5));
	chart.compose(
			[
					dc.barChart(chart).colors(chartColors[3]).margins({
						top : 50,
						right : 200,
						bottom : 50,
						left : 50
					}).dimension(dim).group(grp, topValues[0].key,
							getStack(topValues[0].key)).clipPadding(70)
							.gap(10).transitionDuration(1000),
					dc.lineChart(chart).colors(chartColors[2]).margins(
							{
								top : 50,
								right : 200,
								bottom : 50,
								left : 50
							}).interpolate('basis').dimension(dim)
							.group(grp, 'basis interpolation',
									getStack(topValues[0].key))
							.transitionDuration(1500),
					dc.lineChart(chart).colors(chartColors[0]).margins(
							{
								top : 50,
								right : 200,
								bottom : 50,
								left : 50
							}).interpolate('linear').dimension(dim)
							.group(interGroup, equation,
									getStack(topValues[0].key))
							.transitionDuration(2000) ]).render();
} else {
	var chart = dc.barChart('#main-chart').width(chartWidth).height(
			chartHeight-200).margins({
		top : 50,
		right : 200,
		bottom : 100,
		left : 50
	}).turnOnControls(false).dimension(dim).x(d3.scale.ordinal()).xUnits(dc.units.ordinal).elasticX(true).elasticY(true).brushOn(false)
			.yAxisLabel('count').xAxisLabel('c12').clipPadding(200)
			.group(grp, topValues[0].key, getStack(topValues[0].key))
			.gap(5);
	for (var i = 1; i < topValues.length; ++i) {
		chart.stack(grp, topValues[i].key, getStack(topValues[i].key));
	}
	chart.ordinalColors(chartColors).renderHorizontalGridLines(true)
			.renderVerticalGridLines(true).legend(
					dc.legend().x(chartWidth - 170).y(chartHeight/6).itemHeight(13).gap(5)).render();
}

dc.renderAll();
