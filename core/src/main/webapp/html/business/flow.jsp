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
		
		<!-- datatables -->
		<script src="../js/jquery.dataTables.js"></script>
		<link href="../style/jquery.dataTables.min.css" rel="stylesheet" type="text/css"/> 
		
		<!-- wysiwyg -->
		<script src="../js/wysiwyg/wysiwyg.js"></script>
		<script src="../js/wysiwyg/wysiwyg-editor.js"></script>
		<script src="../js/wysiwyg/config.js"></script>
		<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css">
		<link href="../style/wysiwyg/wysiwyg-editor.css" rel="stylesheet" type="text/css"/>
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/datatables.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
		<div id="main">
			<h1 id="title">完善个人资料</h1>
			<div id="html"></div>
		</div>
		
		<textarea id="print" style="width:700px;height:500px;display:none;"></textarea>
		
		<div id="dialog"><div id="dialogPanel" /></div>
		<script>
		/**
		input:
			[name,label,type,value,newLine]
			{ name|n:, label|l:, type|t:, value|v:, newLine|nl: }
		**/
		
		var ARRAY = [];
		var EMPTY = ARRAY[-1];
		var LINE_BREAK = '[n]';
		
		/******* 完善个人信息 **************/
		var resultModel = ['form','account.personalProfile',[
		                        //{n:'$fid',t:'hidden',v:'2',nl:false} //以{}形式
								['$fid',,'hidden',2],  //以'$'开头的名称不加前缀prefix
								['realName','真实姓名','\n'],
								['sfz','身份证'],
			            		['email','Email','\n'],
			            		['mobile','手机'],
			            		['birthday','生日','date','\n'],
			            		['address','地址',['group',[
			            			['province','?省',['select',['江苏','浙江']]],
			            			['city','?市',['select',['南京','苏州','杭州']]],
			            			['zone','?区',['select',['鼓楼','秦淮','白下','玄武']]],
			            			['address',,'textarea','\n']]]
			            		,'\n']
		                   ]];
		//['city','?市',['select',['南京','苏州','杭州'],{linkage:{ajax:'',link:'province'}}]],
		//h.form('Account.personalProfile',{ex:[2,3,'email'],bid:'uuuuu',fid:2})
		
		var result = ${resultJson};
		var data = result && result.data;

		var json = result && result.json;
		var htmlTemplate = result && result.ht;
		var htmlCode = result && result.html;
		var jsUrl = result.jsUrl;
		if(!jsUrl && data && data.bid && data.fid){
		    jsUrl = '/business/js.htm?bid='+data.bid+'&fid='+data.fid;
		}
		
		if(jsUrl){
			$.getScript(jsUrl,function(){
				init();
			});
		}else {
			$(function(){
				init();
			});
		}
		
		function init(){
			$("#dialog").dialog({
				resizable: false,
				height:900,
				width:1000,
				autoOpen: false,
				buttons: {
					Cancel: function() {
					  $(this).dialog( "close" );
					}
				}
			});
			
			if(result && result.title){
				$("#title").text(result.title);
			}
			
			if(htmlCode){
				$("#html").html(htmlCode);
			}
			
			if(json){
				$("#main").append(build(json));
			}
			
			if(htmlTemplate){
				$("#main").html(htmlTemplate.template);
				
				
				if(htmlTemplate.options){
					$.each(htmlTemplate.options,function(k,v){
						var tempDiv = $("#htmlTemplate-"+k);
						
						tempDiv.after(build(v));
						tempDiv.remove();
					});
				}
			}
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
		
		.horizontal li {
			float:left;
		}
		
		.vertical li {
			float:none;
		}
		
		.ul-checkbox,
		.ul-radio {
			margin:0px;
			padding:0px;
		}
		
		.ul-checkbox li,
		.ul-radio li {
			border:0px;
			margin:0px;
			padding:0px;
			list-style:none;
			padding-top:5px;
		}
		</style>
		
		
	</body>
</html>