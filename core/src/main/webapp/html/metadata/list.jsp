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
		width:400px;
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
		width:150px;
		border-bottom:1px dashed #ccc;
		list-style:none;
		padding:5px;
	}
	</style>
	<div id="detail"></div>
	
	<script>
	
	var resultModel = ${resultJson};
	
	$(function(){
		$("#detail").append(newArrayList(resultModel.metadataList));
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
				
				vDiv.append($("<li class='header'>操作</li>"));
				break;
			}
			
			for(var fi in arrayValue){
				var vDiv = $("<ul class='ul-list'></ul>");
				valueDiv.append(vDiv);
				
				for(var fii in arrayValue[fi]){
					var ftext = arrayValue[fi][fii];
					if(fii == 'id'){
						var aObj = $("<a></a>");
						aObj.text(ftext);
						aObj.attr("href","/metadata/detail.htm?id="+ftext);
						
						vDiv.append($("<li></li>").append(aObj));
					}else {
						vDiv.append($("<li>"+arrayValue[fi][fii]+"</li>"));
					}
				}
				
				var oid = arrayValue[fi]["id"];
				vDiv.append($("<li><a href='/metadata/edit.htm?id="+oid+"'>编辑</a>&nbsp;&nbsp;<a href='/metadata/delete.htm?id="+oid+"'>删除</a></li>"));
			}
		return valueDiv;
	}
	</script>
</html>