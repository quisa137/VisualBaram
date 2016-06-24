/**
 * 
 */


	d3.csv('##temp##',function(err, data){
		var pie_chart = dc.pieChart("#pie-chart");
		
		var ndx = crossfilter(temp);
		var chartWidth = Math.round(window.innerWidth)-500;
		var chartHeight = Math.round(window.innerHeight)-400;
		var runDimension = ndx.dimension(function(d){
			return d.category;
		});
		
		var speedSumGroup = runDimension.group().reduceCount();
		
			
		
		pie_chart.width(chartWidth)
		.height(chartHeight)
		.slicesCap(8)
		.innerRadius(100)
		.dimension(runDimension)
		.group(speedSumGroup)
		.legend(dc.legend())
		.on('pretransition', function(pie_chart){
			pie_chart.selectAll('text.pie-slice').text(function(d){
				return d.data.key + ' ' + dc.utils.printSingleValue((d.endAngle - d.startAngle) / (2*Math.PI) * 100) + '%' ;
			})
		});
		pie_chart.render();
	});	