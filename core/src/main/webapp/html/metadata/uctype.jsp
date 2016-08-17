<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="expires" content="0">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">

        <script src="../js/angular.js"></script>
        
        <!-- jquery -->
		<script src="../js/jquery-2.1.4.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    	<div class="ui-layout-north">
			<a href="/metadata/main.htm">对象模型</a>&nbsp;&nbsp;
			<a href="/business/main.htm">业务</a>&nbsp;&nbsp;
			<a href="/uctype/main.htm">自定义类型</a>
		</div>
   		<h1 id="title">自定义字段类型</h1>
   		<input type='button' value="新增" onclick="edit(null);">
		<div id="main">
			<div id="html"></div>
		</div>
		 
		<div id="dialog" title="编辑自定义类型">
			<form id="uctypeForm">
				<table>
					<tr>
						<td>名称</td>
						<td>
							<input type="hidden" id="id" name="id" />
							<input type="text" id="name" name="name" />
						</td>
					</tr>
					<tr>
						<td>key</td>
						<td><input type="text" id="key" name="key" /></td>
					</tr>
					<tr>
						<td>单位</td>
						<td><input type="text" id="units" name="units" onchange="factors();" /></td>
					</tr>
					<tr>
						<td>单位换算</td>
						<td id="factorTd"></td>
					</tr>
				</table>
			</form>
		</div>
		<script>
		
		var result = ${resultJson};
		var json = result.json;
		
		$(function(){
			$("#dialog").dialog({
				resizable : false,
				height : 600,
				width : 500,
				autoOpen : false,
				buttons : {
					Save : function(){
						ajaxForm("uctypeForm");
						$(this).dialog("close");
					},
					Cancel : function() {
						$(this).dialog("close");
					}
				}
			});
			
			if (json) {
				$("#main").append(build(json));
			}
		});
		
		function ajax(data, doneFun) {
			$.ajax('/uctype/save.htm', {
				data : {uctype:ts(data)},
				dataType : 'json',
				type : 'POST',
				async : false
			}).done(doneFun ? doneFun : function(result, status) {
				alert("result : " + ts(result));
				addRow($("#"));
			});
		}
		
		function readFormDatas(form) {
			var inputs = form.find(":input");

			var reqDatas = {};
			$.each(inputs, function() {
				if ($(this).attr("type") == "radio") {
					if (!this.checked) {
						return;
					}
				}
				
				var key = $(this).attr("name");
				var oldVal = reqDatas[key];
				if(reqDatas[key]){
					var val = reqDatas[key];
					
					if(!$.isArray(val)){
						val = [val];
						reqDatas[key] = val;
					}
					val.push($(this).val());
				}else {
					reqDatas[key] = $(this).val();
				}
			});

			return reqDatas;
		}
		
		function factors(vals){
			var u = $("#units").val();
			var us = u.split(",");
			
			$("#factorTd").empty();
			for(var i = 0; i < us.length-1; i++){
				var div = $("<div />");
				var val = '';
				if($.isArray(vals) && vals.length > i){
					val = vals[i];
				}
				
				div.append("<label>1"+us[i]+"  =</label>")
					.append("<input type='text' name='factors' style='width:50px;' value='"+val+"' />")
					.append("<label>"+us[i+1]+"</label>");
				
				$("#factorTd").append(div);
			}
		}
		
		function edit(id){
			if(!id){
				refreshUCTypeForm(null);
				$("#dialog").dialog("open");
				return;
			}
			
			$.ajax('/uctype/get.htm', {
				data : {id:id},
				dataType : 'json',
				type : 'POST',
				async : false
			}).done(function(result, status) {
				refreshUCTypeForm(result);
				$("#dialog").dialog("open");
			});
		}
		
		function refreshUCTypeForm(data){
			if(!data){
				data = {id:'',name:'',key:'',units:'',factors:[]};
			}
			
			$("#id").val(data.id);
			$("#name").val(data.name);
			$("#key").val(data.key);
			$("#units").val(data.units);
			
			factors(data.factors);
		}
		
		</script>
		<style>
		form table td {
			padding:3px 10px;
			border-bottom:1px dashed #ccc;
		}
		
		form table .label {
			text-align:right;
		}
		
		form table .hidden {
			display:none;
		}
		
		form table td div {
			margin:5px;
		}
		
		form table td label {
			margin-left:3px;
			margin-right:10px;
		}
		
		.tableBlock {
			min-width:400px;
		}
		
		.tableBlock td {
			padding:8px 10px;
			border-bottom:1px dashed #ccc;
			max-width:700px;
		}
		
		.tableBlock .label {
			text-align:right;
			font-weight:bold;
			vertical-align:top;
		}
		
		.tableBlock img {
			max-width:200px;
			max-height:200px;
		}
		
		.tableList {
			background-color:#ccc;
		}
		
		.tableList td,.tableList th{
			padding:8px 10px;
			background-color:#fff;
			min-width:150px;
			border:0px;
		}
		
		.tableList a{
			margin-left:10px;
		}
		
		.tableList input{
			max-width:80px;
		}
		
		#title {
			border-bottom:2px solid #AFE2E4;
			width:600px;
			padding-left:15px;
			padding-bottom:10px;
		}
		
		#main {
			margin:30px;
		}
		
		.menu a {
			margin:10px;
		}
		
		.error {
			border:0px solid red;
			margin-top:30px;
			margin-left:200px;
			font-size:20px;
			font-weight:bold;
			color:red;
		}
		</style>
		
		
	</body>
</html>