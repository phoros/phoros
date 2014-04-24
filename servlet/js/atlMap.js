var width = 800,
    height = 400;

var proj = d3.geo.mercator()
    .center([27,38.5])
    .scale([2200])  ;

var path = d3.geo.path()
    .projection(proj);

var zoom = d3.behavior.zoom()
    .translate(proj.translate())
    .scale(proj.scale())
    .scaleExtent([height*.33, 4 * height])
    .on("zoom", zoom);

var svg = d3.select("#map").append("svg")
    .attr("width", width)
    .attr("height", height)
    .call(zoom);

function zoom() {
    proj.translate(d3.event.translate).scale(d3.event.scale);
    svg.selectAll("path").attr("d", path);
    circles
  	.attr("cx", function(d){return proj([d.lon, d.lat])[0];})
	.attr("cy", function(d){return proj([d.lon, d.lat])[1];});
}

var borders = svg.append("g");

var payments = svg.append("g");

var metorScale = d3.scale.pow().exponent(.5).domain([0, 1000, 10000, 56000, 23000000]);
//var metorScale = d3.scale.linear().domain([0,10000]);

var tooltip = d3.select("body").append("div")
    .attr("class", "tooltip")
    .style("opacity", 1e-6)
    .style("background", "rgba(250,250,250,.7)");

queue()
    .defer(d3.json, "aegean.topojson")
    .defer(d3.csv, "data/phoros.csv")
    .await(ready);

var payrecs;
function ready(error, topology, csv){
    borders.selectAll("path")
	.data(topojson.object(topology, topology.objects.aegean).geometries)
	.enter()
	.append("path")
	.attr("d", path)
	.attr("class", "border")
	
    rawCsv = csv;
    
    payrecs = [];
    rawCsv.forEach(function(d){
	d.obols = +d.obols;
	d.year = +d.year;
	d.id = d.urn;

	payrecs.push(d);
    });
    payrecs.sort(function(a, b){return a.id - b.id;})

    metorScale
	.range([2.5, 3, 4, 5, 10]);


    circles = payments.selectAll("circle")
	.data(payrecs).enter()
	.append("svg:a")
	.attr("xlink:href", function(d) { return d.urn; })
	.attr("xlink:show", "new")
	.append("circle")
	.attr("cx", function(d){
	    return proj([d.lon, d.lat])[0];})
	.attr("cy", function(d){return proj([d.lon, d.lat])[1];})
	.attr("r", function(d){return metorScale(d.obols);})
	.attr("id", function(d){return "id" + d.id;})
	.style("fill", function(d){
	    return d3.rgb(255,0,0);
	})
	.on("mouseover", function(d){
	    d3.select(this)
		.attr("stroke", "black")
		.attr("stroke-width", 1)
		.attr("fill-opacity", 1);

	    tooltip
		.style("left", (d3.event.pageX + 5) + "px")
		.style("top", (d3.event.pageY - 5) + "px")
		.transition().duration(300)
		.style("opacity", 1)
		.style("display", "block")

	    updateDetails(d);
	})
	.on("mouseout", function(d){
	    d3.select(this)
		.attr("stroke", "")
		.attr("fill-opacity", function(d){return 1;})

	    tooltip.transition().duration(700).style("opacity", 0);
	});

    lb = 1.370;
    metorsCF = crossfilter(payrecs),
    all = metorsCF.groupAll(),
    year = metorsCF.dimension(function(d){return d.year;}),
    years = year.group().reduceCount();

    obols = metorsCF.dimension(function(d){return d.obols}),
    obolTotals = obols.group().reduceCount();

    cartoDbId = metorsCF.dimension(function(d){return d.id;});
    cartoDbIds = cartoDbId.group();

    var extObols = d3.extent(csv,function(d){return +d.obols})

    var charts = [
	barChart()
	    .dimension(year)
	    .group(years)
	    .x(d3.scale.linear()
	       .domain([0,9])
	       .rangeRound([-1, 20*24-5])),

	barChart()
	    .dimension(obols)
	    .group(obolTotals)
	    .x(d3.scale.linear()
	       .domain([1,10000])
	       .rangeRound([0,500])
    )];

    var chart = d3.selectAll(".chart")
	.data(charts)
	.each(function(chart){chart.on("brush", renderAll).on("brushend", renderAll)});
    
    d3.selectAll("#total")
	.text(metorsCF.size());
    
    
    function render(method){
	d3.select(this).call(method);
    }


	lastFilterArray = [];
	payrecs.forEach(function(d, i){
		lastFilterArray[i] = 1;
	});

	function renderAll(){
		chart.each(render);

		var filterArray = cartoDbIds.all();
		filterArray.forEach(function(d, i){
			if (d.value != lastFilterArray[i]){
				lastFilterArray[i] = d.value;
				d3.select("#id" + d.key).transition().duration(500)
						.attr("r", d.value == 1 ? 2*metorScale(payrecs[i].obols) : 0)
					.transition().delay(550).duration(500)
						.attr("r", d.value == 1 ? metorScale(payrecs[i].obols) : 0);

			}
		})

		d3.select("#active").text(all.value());
	}

	window.reset = function(i){
		charts[i].filter(null);
		renderAll();
	}

	renderAll();
}


var printDetails = [
    {'var': 'name', 'print': 'Name'},
    {'var': 'obols', 'print': 'Obols'},
    {'var': 'year', 'print': 'Year'},
    {'var': 'nameconf', 'print': 'Confidence: name'},
    {'var': 'amtconf', 'print': 'Confidence: amount'}
];

function updateDetails(metor){

    tooltip.selectAll("div").remove();
    tooltip.selectAll("div").data(printDetails).enter()
	.append("div")
	.append('span')
	.text(function(d){return d.print + ": ";})				
	.attr("class", "boldDetail")
	.insert('span')
	.text(function(d){return metor[d.var];})
	.attr("class", "normalDetail");
}
