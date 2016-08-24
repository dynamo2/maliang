<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <script src="../js/angular.js"></script>
		<script src="../js/jquery-2.1.3.min.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/> 
    </head>
    <body>
		<script>
		var resultModel = ${resultJson};
		
		$(function(){
			for(var idx in resultModel){
				var htmlData = resultModel[idx];
				
				if(idx == "ul-list"){
					var listObj = TM_ulListBuilder.newUIList(htmlData);
					$("#main").append(listObj);
				}
			}
		});
		
		</script>
		<div style="margin:20px;"><a href="list.htm">列表</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="edit.htm">新增</a></div>
		<div id="main"></div>
	</body>
</html>