<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1">
<link href="http://example.codeboy.me/d3/css/bootstrap.min.css" rel="stylesheet">

<!-- jquery -->
		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		<script src="../../js/jquery.layout-latest.js"></script>
		<script src="../js/jquery.simulate.js"></script>
		
<script src="http://example.codeboy.me/d3/js/bootstrap.min.js" charset="utf-8"></script>
<script src="http://visualdataweb.de/webvowl/js/d3.min.js"></script>

<title>d3 layout update</title>
</head>
<body style="height: 100%;">
<div class="container" style="padding-left: 0; width: 100%;">
    <div id="svg">
    </div>
</div>

<script>
    $(document).ready(function () {
        var height = document.body.clientHeight;
        var width = document.body.clientWidth;

        var nodes_data = [{'name': 'Web'},
            {'name': 'Cart'},
            {'name': 'Order'},
            {'name': 'User'},
            {'name': 'Product'},
            {'name': 'fffff'},
            {'name': 'ggggg'},
            {'name': 'productName','type':'field'},
            {'name': 'price','type':'field'},
            {'name': 'picture','type':'field'}];
            
        var edges_data = [{'source': 0, 'target': 1},
            {'source': 0, 'target': 2},
            {'source': 0, 'target': 3},
            {'source': 0, 'target': 4},
            {'source': 0, 'target': 5},
            {'source': 0, 'target': 6},
            {'source': 4, 'target': 7},
            {'source': 4, 'target': 8},
            {'source': 4, 'target': 9},
            {'source': 4, 'target': 2},
            {'source': 2, 'target': 3}];
        
        nodes_data = [{'name': 'Order'},
                      {'name': 'User'},
                      {'name': 'Product'},
                      {'name': 'Brand'},
                      {'name': 'PostStrategy'},
                      {'name': 'consigneeInfo'},
                      {'name': 'items'},
                      {'name': 'name','type':'field'},
                      {'name': 'price','type':'field'},
                      {'name': 'store','type':'field'},
                      {'name': 'postage','type':'field'},
                      {'name': 'pictures','type':'field'},
                      {'name': 'grouding','type':'field'},
                      {'name': 'comments'},
                      {'name': 'consigneeInfo','type':'inner'},
                      {'name': 'items','type':'inner'},
                      {'name': 'status','type':'field'},
                      {'name': 'totalAmount','type':'field'},
                      {'name': 'freight','type':'field'},
                      {'name': 'address','type':'field'},
                      {'name': 'consignee','type':'field'},
                      {'name': 'mobile','type':'field'}];
        
        edges_data = [{'source': 0, 'target': 1},
                      {'source': 0, 'target': 2},
                      {'source': 2, 'target': 3},
                      {'source': 0, 'target': 4},
                      {'source': 0, 'target': 5},
                      {'source': 0, 'target': 6},
                      {'source': 2, 'target': 7},
                      {'source': 2, 'target': 8},
                      {'source': 2, 'target': 9},
                      {'source': 2, 'target': 10},
                      {'source': 2, 'target': 11},
                      {'source': 2, 'target': 12},
                      {'source': 2, 'target': 13},
                      {'source': 0, 'target': 14},
                      {'source': 0, 'target': 15},
                      {'source': 0, 'target': 16},
                      {'source': 0, 'target': 17},
                      {'source': 0, 'target': 18},
                      {'source': 14, 'target': 19},
                      {'source': 14, 'target': 20},
                      {'source': 14, 'target': 21}];
        

        var color = d3.scale.category20();
        var edgeWidth = 2;
        var r = 40;
        var fr = 5;
        var svg = d3.select("#svg").append("svg")
                .attr("width", width)
                .attr("height", height);

        var force = d3.layout.force()
                .nodes(nodes_data)
                .links(edges_data)
                .size([width, height])
                .linkDistance(200)
                .friction(0.8)
                .charge(-1500)
                .start();

        //连线
        var links = svg.selectAll("line")
                .data(edges_data)
                .enter()
                .append("line")
                .attr("marker-end", "url(#arrow)")
                .style("stroke", "#ccc")
                .style("stroke-width", edgeWidth);
        
		//节点
		var nodes = svg.selectAll("circle")
			        .data(nodes_data.filter(function(d){
			        	return d.type != 'field';
			        }))
			        .enter()
			        .append("circle")
			        .attr("r", function (d, i) {
			            if(d.type === 'field'){
			            	return fr;
			            }
			            return r;
			        })
			        .style("fill", function (d, i) {
			        	if(d.type === 'field'){
			        		return "#000";
			        	}
			            return color(i);
			        }).on("click", function (d, i) {
			            if (i == 0) {
			                update();
			            }
			        }).call(force.drag);
		
		var rs = svg.selectAll("rect")
			        .data(nodes_data.filter(function(d){
			        	if(d.type == 'field'){
			        		return true;
			        	};
			        	return false;
			        }))
			        .enter()
			        .append("rect")
			        .attr("x", function (d, i) {
			            return d.x-50;
			        })
			        .attr("y", function (d, i) {
			            return d.y;
			        })
			        .attr("width", 120)
			        .attr("height", 30)
			        .attr("fill","#ccffff")
			        .attr("stroke","#99ccff").call(force.drag);
		
		//æ ç­¾
        var nodes_labels = svg.selectAll("text")
                .data(nodes_data)
                .enter()
                .append("text")
                .attr("dx", function (d, i) {
                    //return -16 * (nodes_data[i].name.length);
                    if(d.type === 'field'){
                    	return -35;
                    }
                    return -16 - (nodes_data[i].name.length);
                })
                .attr("dy", function (d, i) {
                    if(d.type === 'field'){
                    	return 20;
                    }
                    return 5;
                })
                .attr("fill", function (d, i) {
                    if(d.type === 'field'){
                    	return "#808080";
                    }
                    return "#fff";
                })
                .style("font-size", 16)
                .text(function (d, i) {
                    return d.name;
                }).call(force.drag);
		
		
		
        force.on("tick", function (d) {
            links.attr("x1", function (d) {
                var distance = Math.sqrt((d.target.y - d.source.y) * (d.target.y - d.source.y) +
                        (d.target.x - d.source.x) * (d.target.x - d.source.x));
                var x_distance = (d.target.x - d.source.x) / distance * r;
                return d.source.x + x_distance;
            }).attr("y1", function (d) {
                var distance = Math.sqrt((d.target.y - d.source.y) * (d.target.y - d.source.y) +
                        (d.target.x - d.source.x) * (d.target.x - d.source.x));
                var y_distance = (d.target.y - d.source.y) / distance * r;
                return d.source.y + y_distance;
            }).attr("x2", function (d) {
            	var l = r;
            	if(d.target.type == 'field'){
                	l = fr;
                }
                
                var distance = Math.sqrt((d.target.y - d.source.y) * (d.target.y - d.source.y) +
                        (d.target.x - d.source.x) * (d.target.x - d.source.x));
                var x_distance = (d.target.x - d.source.x) / distance * l;
                return d.target.x - x_distance;
            }).attr("y2", function (d) {
            	var l = r;
                if(d.target.type == 'field'){
                	l = fr;
                }
                
                var distance = Math.sqrt((d.target.y - d.source.y) * (d.target.y - d.source.y) +
                        (d.target.x - d.source.x) * (d.target.x - d.source.x));
                var y_distance = (d.target.y - d.source.y) / distance * l;
                return d.target.y - y_distance;
            });
            


            nodes.attr("cx", function (d) {
                return d.x;
            }).attr("cy", function (d) {
                return d.y;
            });

            nodes_labels.attr("x", function (d) {
                return d.x;
            });
            nodes_labels.attr("y", function (d) {
                return d.y;
            });
            
            rs.attr("x", function (d) {
                return d.x-50;
            });
            rs.attr("y", function (d) {
                return d.y;
            });


        });

	//ç¨äºäº§çä¸åé¢è²çèç¹
        var colorIndex = 8;

        //æ·»å èç¹æ´æ°
        function update() {
            nodes_data.push({'name': 'xxx'});
            edges_data.push({'source': 0, 'target': nodes_data.length - 1});

            links = links.data(force.links());
            links.enter()
                    .append("line")
                    .style("stroke", "#ccc")
                    .style("stroke-width", 2);
            links.exit().remove();

            nodes = nodes.data(force.nodes());
            nodes.enter().append("circle")
                    .attr("r", 40)
                    .style("fill", color(colorIndex++))
                    .call(force.drag);
            nodes.exit().remove();

            force.start();
        }

        //åè½¦äºä»¶
        $(document).keydown(function(e) {
            if(e.which == 13) {
               update();
            }
        });
    });

</script>

</body>
</html>