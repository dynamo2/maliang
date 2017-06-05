<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="expires" content="0">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">

		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../js/jquery-ui.min.js"></script>
		<script src="../js/jquery.simulate.js"></script>
		<script src="/static/ace/src-noconflict/ace.js"></script>
		
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
    	<h1 id="title">代码编辑器 <button onclick="run();">执行</button></h1>
		<form id="codeForm">
   			<textarea id="code" name="code"></textarea>
   		</form>

   		<div id="editor" style="width:40%;height:1000px;float:left;margin-right:15px;"></div>
   		<div id="result" style="width:59%;height:1000px;"></div>
   		
		<script>
		var editor = null;
		var showResult = null;
		$(function(){
			editor = ace.edit("editor");
			editor.session.setMode("ace/mode/javascript");
			
			showResult = ace.edit("result");
			showResult.session.setMode("ace/mode/javascript");
			showResult.getSession().setUseWrapMode(true);
			
			$("#code").hide();
		});
		
		function run(){
			$("#code").val(editor.getValue());
			
			$.ajax({
			    cache:true,
			    type:"POST",
			    dataType : 'json',
			    url:'/metadata/code2.htm',
			    data:$("#codeForm").serialize(),
			    async:false,
			    success:function(result,status){
			    	//$("#result").val(ts(result.result));
			    	//var val = JSON.stringify(result.result, null, 4);
			    	//showResult.setValue(val);
			    	
			    	showResult.setValue(result.result);
			    }
			});
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