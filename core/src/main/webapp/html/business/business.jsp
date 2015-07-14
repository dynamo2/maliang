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
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
		<script>
		var resultModel = ${resultJson};

		$(function(){
			if(resultModel && resultModel.components){
				$.each(resultModel.components,function(){
					buildComponent(this,$("#main"));
				});
			}
		});
		
		</script>
		<div id="main"></div>
		
	</body>
</html>