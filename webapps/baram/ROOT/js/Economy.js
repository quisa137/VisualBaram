'use strict';
var chart = dc.boxPlot("#box-test");

// d3.csv('##dataUrl##', function(error, data) {

console.log(data);

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
var chartWidth = Math.round(window.innerWidth - 200);
var chartHeight = Math.round(window.innerHeight - 200);

data.forEach(function(x) {
	x.Speed = +x.Speed;
});

var ndx = crossfilter(data), runDimension = ndx.dimension(function(d) {
	return +d.Run;
}), runGroup = runDimension.group(), experimentDimension = ndx
		.dimension(function(d) {
			return d.Run;
		}), speedArrayGroup = experimentDimension.group().reduce(
		function(p, v) {
			p.push(v.Speed);
			return p;
		}, function(p, v) {
			p.splice(p.indexOf(v.Speed), 1);
			return p;
		}, function() {
			return [];
		});

chart.width(chartWidth).height(chartHeight).margins({
	top : 50,
	right : 50,
	bottom : 100,
	left : 90
}).dimension(experimentDimension).group(speedArrayGroup)
		.renderHorizontalGridLines(true).renderVerticalGridLines(true)
		.clipPadding(200).elasticX(true).elasticY(true);

dc.renderAll();
