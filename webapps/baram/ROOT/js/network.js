
'use strict';
//console.log(data);
var colors = [ '#d95f49', '#55a660', '#489ad8', '#edc233', '#8357ac',
		'#818b8d', '#db8438', '#b44b37', '#64c271', '#5f9ac5', '#ea9f34',
		'#9168b5', '#bec3c7', '#45627f', '#c76026', '#54b59a' ];

var s, nId = 0, eId = 0;

sigma.classes.graph.addMethod('computePhysics', function() {

});

s = new sigma({
	graph : g,
	renderer : {
		container : document.getElementById('graph-container'),
		type : 'canvas'
	},
	settings : {
		maxArrowSize : 5,
		maxEdgeSize : 5,
		minNodeSize : 8,
		maxNodeSize : 16,
		defaultLabelSize : 12,
		labelSize : "proportional",
		defaultLabelColor : 'gray'
	}
});

var g = {
	nodes : [],
	edges : []
};

function frame() {
	s.graph.computePhysics();
	s.refresh();
	requestAnimationFrame(frame);
}

frame();

var count = 0;
var repeat;
repeat = setInterval(function() {
	var x, y, id, neighbors;

	x = Math.random();
	y = Math.random();

	neighbors = s.graph.nodes().filter(function(n) {
		return Math.random();
	});

	s.graph.addNode({
		
		//type : ShapeLibrary.enumerate().map(function(s){return s.name;})[Math.round(Math.random() * 5)],
		label : data.nodes[count].label,
		id : data.nodes[count].id,
		size : data.nodes[count].size,
		x : Math.random(),
		y : Math.random(),
		color : colors[Math.floor(Math.random() * colors.length)]

	});

	count++;

	// console.log(nId);
	if (count == data.nodes.length) {
		clearInterval(repeat);

		console.log("end");
	}

}, 100);
var count1 = 0;
var repeat1;
repeat1 = setInterval(function() {
	var x, y, id, neighbors;

	neighbors = s.graph.nodes().filter(function(n) {
		return Math.random();
	});

	s.graph.addEdge({
		id : data.edges[count1].id,
		source : data.edges[count1].source,
		target : data.edges[count1].target,
		type : 'curvedArrow',
//		color : data.nodes[count1].color,
		color: colors[Math.floor(Math.random() * colors.length)],
		// color : data.edges[count1].color,
		size : data.edges[count1].size
	});
//	console.log("edges" + count1);
	count1++;

	if (count1 == data.edges.length) {
		clearInterval(repeat1);

	}

}, 100);
