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
		<style>
		.page-content {
			min-height:1000px;
		}
		</style>
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
	</body>
</html>