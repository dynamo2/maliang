<%@ page pageEncoding="utf-8" %>
<html>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <script src="../js/jquery-2.1.3.min.js"></script>
    </head>
	<style>
	body{
		font: 62.5% "Trebuchet MS", sans-serif;
		margin: 50px;
		font-size:12px;
	}
	.ul-list .header {
		font-weight:bold;
	}
	
	.div-detail {
		clear:both;
	}
	.div-detail div {
		width:1000px;
		float:left;
		padding:6px;
		text-align:left;
		border:1px solid #F5F5F5;
	}
	
	.div-detail .label {
		width:100px;
		
		font-weight:bold;
		background:#F5F5F5;
		border-bottom:1px solid #ffffff;
	}
	
	.ul-list {
		clear:both;
		width:100%;
		margin:0px;
		padding:0px;
	}
	
	.ul-list li {
		float:left;
		width:100px;
		border-bottom:1px dashed #ccc;
		list-style:none;
		padding:5px;
	}
	</style>
	<div style="margin:20px;"><a href="list.htm">列表</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="edit.htm">新增集合</a></div>
	<div id="detail"></div>
	
	<script>
	
	/*
	var resultModel = {"metadata":{"name":"Order3","id":"55504c39d27702a4071e0fd8","label":"dingdan",
						"fields":[{"name":"userddd","label":"yonghu","type":"string"},{"name":"price","label":"jiage","type":"double"}]}};
	*/
	
	var resultModel = ${resultJson};
	
	$(function(){
		for(var i in resultModel.metadata){
			var value = resultModel.metadata[i];
			if(value instanceof Array){
				var itemDiv = $("<div class='div-detail'></div>")
								.append(newLabel(i))
								.append(newArrayList(value));
				
				$("#detail").append(itemDiv);
			}else {
				$("#detail").append(newItem(i,value));
			}
		}
	});
	
	function newItem(label){
		return $("<div class='div-detail'></div>").append(newLabel(label));
	}
	
	function newItem(label,value){
		return $("<div class='div-detail'></div>").append(newLabel(label)).append(newValue(value));
	}
	
	function newLabel(label){
		return $("<div class='label'></div>").text(label);
	}
	
	function newValue(value){
		return $("<div></div>").text(value);
	}
	
	function newArrayList(arrayValue){
		var valueDiv = $("<div></div>");
		
			for(var fi in arrayValue){
				var vDiv = $("<ul class='ul-list'></ul>");
				valueDiv.append(vDiv);
				
				for(var fii in arrayValue[fi]){
					vDiv.append($("<li class='header'>"+fii+"</li>"));
				}
				break;
			}
			
			for(var fi in arrayValue){
				var vDiv = $("<ul class='ul-list'></ul>");
				valueDiv.append(vDiv);
				
				for(var fii in arrayValue[fi]){
					vDiv.append($("<li>"+arrayValue[fi][fii]+"</li>"));
				}
			}
		return valueDiv;
	}
	</script>
</html>