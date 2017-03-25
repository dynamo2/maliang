<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="expires" content="0">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
        
        <!-- jquery -->
		<script src="../js/jquery-2.1.4.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
		<!-- wysiwyg -->
		<script src="../js/wysiwyg/wysiwyg.js"></script>
		<script src="../js/wysiwyg/wysiwyg-editor.js"></script>
		<script src="../js/wysiwyg/config.js"></script>
		<link href="../style/wysiwyg/wysiwyg-editor.css" rel="stylesheet" type="text/css"/>
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/datatables.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		<script src="../js/tianma/inputs.js"></script>
		<script src="../html/business/tianma.js"></script>
		
		<script src="../js/tianma/processor.js"></script>
		<script src="../js/tianma/metronic.js"></script>
    </head>
    <body class="page-header-fixed page-quick-sidebar-over-content">
		<div id="main">
			<div id="html"></div>
		</div>
		
		<textarea id="print" style="width:700px;height:500px;display:none;"></textarea>
		<div id="dialog"><div id="dialogPanel" /></div>
		
		<script>
		var ARRAY = [];
		var EMPTY = ARRAY[-1];
		var LINE_BREAK = '[n]';
		
		
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
				newInit();
			});
		}else {
			$(function(){
				newInit();
			});
		}
		
		function newInit(){
			var generator = new MGenerator();
			
			var ele = generator.build(htmlCode);
			if(ele){
				$("body").append(ele);
			}
			//$("body").append(generator.build(htmlCode));
		}
		
		function init(){
			if(htmlCode){
				$("#html").html(htmlCode);
			}
			
			if(json){
				$("#main").append(build(json));
			}
			
			if(htmlTemplate){
				$("body").append(buildHtmlTemplate(htmlTemplate));
			}
			
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
		}
		
		function buildHtmlTemplate(ht){
			var ele = $(ht.template);
			
			if(ht.options){
				$.each(ht.options,function(k,v){
					var tempDiv = ele.find("#htmlTemplate-"+k);
					
					var ve = null;
					if(v && v.template){
						ve = buildHtmlTemplate(v);
					}else {
						ve = build(v);
					}
					
					tempDiv.after(ve);
					tempDiv.remove();
				});
			}
			return ele;
		}
		
		</script>
		<style>
		
		/*
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
		*/
		</style>
		
		<script>
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
		</script>
	</body>
</html>