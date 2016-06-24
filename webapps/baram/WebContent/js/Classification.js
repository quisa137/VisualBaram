'use strict';

var chart = dc.scatterPlot("#scatter");

var chartWidth = Math.round(window.innerWidth);
var chartHeight = Math.round(window.innerHeight);

var chartColors = ['#d95f49', '#55a660', '#489ad8', '#edc233', '#8357ac',
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
            		'#66CDAA', '#0000CD', '#BA55D3'];
console.log(data);
data.forEach(function(x) {
    x.v1 = +x.v1;
    x.v2 = +x.v2;
});

var crf = crossfilter(data);
var dim = crf.dimension(function(d) {return [+d.v1, +d.v2]; });

var grp = dim.group().reduce(function(p, v) {
	p.v1 = v.v1;
	p.v2 = v.v2;
	return p;
}, function(p, v) {
	p.v1 = v.v1;
	p.v2 = v.v2;
	return p;
}, function() {
	return {
		v1 : 0,
		v2 : 0
	};
}).order(function orderValue(p) { return p.v1; });
var topValue = grp.top(1);
chart
    .width(chartWidth*0.95)
    .height(chartHeight*0.7).margins({
    	top : 20,
    	right : 50,
    	bottom : 50,
    	left : 50
    })
    .x(d3.scale.linear().domain([0,topValue[0].value.v1]))
    .brushOn(false)
    .symbolSize(8)
    .clipPadding(10)
    .xAxisLabel("c14")
    .yAxisLabel("c13")
    .dimension(dim)
    .group(grp);

  chart.render();