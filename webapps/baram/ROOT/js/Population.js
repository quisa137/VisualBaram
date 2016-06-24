/**
 * 
 */

var chart = dc.heatMap("#heatMapChart");

var chartColors = ['#d95f49','#55a660','#489ad8','#edc233','#8357ac','#818b8d','#db8438','#b44b37','#64c271','#5f9ac5','#ea9f34','#9168b5','#bec3c7','#45627f','#c76026','#54b59a'];
var chartWidth = Math.round(window.innerWidth-200);
var chartHeight = Math.round(window.innerHeight-500);


	d3.csv("##test##",function(err, data){
		/* alert(JSON.stringify(temp)); */
		
		var ndx = crossfilter(temp);
		
		var heatDim = ndx.dimension(function(d){return [d.time, d.loc,d.category,d.cnt]});
		
		var timeGroup = heatDim.group().reduce(
				function(p, v) {
					p.v += v.v;
					return p;
				}, function(p, v) {
					p.v -= v.v;
				}, function() {
					return {
						v : 0
					};
				}		
		);
		
		chart.width(chartWidth)
		.height(chartHeight*1.7)
		.dimension(heatDim)
		.group(timeGroup)
		.margins({ top : 0, right : 50, bottom :100, left : 50})
		.transitionDuration(1500)
		.group(timeGroup).keyAccessor(function(d) {return d.key[0];})
		.valueAccessor(function (d){return d.key[1];})
		.colorAccessor(function(d){	return d.key[3];})
		.legend(dc.legend())
		.title(function(d) {	
		
	        return "Cnt :   " + d.key[3] ;
	               }).colors(chartColors).calculateColorDomain();
		chart.render();
		
		var xAsixTicks = chart.selectAll(".cols.axis");
		
		function isHidden(i){
	        if(i%Math.floor(xAsixTicks.selectAll('text')[0].length*0.1) === 0) {
	          return false;
	        }else{
	          return true;
	        }
	    }
		xAsixTicks.selectAll('text').attr('style',function(d,i,j){
	      return (isHidden(i))?'opacity:0':'opacity:1';
	    });
		
	});