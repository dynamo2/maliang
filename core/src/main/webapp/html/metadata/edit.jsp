<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
        <script src="../js/angular.js"></script>
		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../js/jquery-ui.min.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/> 
    </head>
    <body>
    	<style>
    	#metadataForm input[type=text] {
    		width:160px;
    		border:1px solid #cccccc;
    	}
    	
    	#metadataForm select {
    		border:1px solid #cccccc;
    	}
    	
    	</style>
    	
		<script>
		var resultModel = ${resultJson};
		
		$(function(){
			var inputDiv = TM_formBuilder.newInputsDiv(resultModel);
			$("#metadataForm").append(inputDiv);
			
			$("select[name*='.type']").change(ajaxFieldType);
			autoColumnsWith($("select[name*='.type']").parent().attr("column"));
			
			$("#addNewItemRow").click(function(){
				$("select[name*='.type']").change(ajaxFieldType);
				autoColumnsWith($("select[name*='.type']").parent().attr("column"));
			});
		});
		
		function ajaxFieldType(event){
			var typeDom = event.currentTarget;
			var colObj = $(typeDom).parent();
			var rowObj = colObj.parent();
			var col = colObj.attr("column");
			var row = rowObj.attr("row");

			$(typeDom).nextAll().remove();
			
			if(typeDom.value == 8 || typeDom.value == 7){
				$.ajax({
					url: "/metadata/linkedObject.htm",
					dataType: "json"
				}).done(function(info) {
					info.name = row+"."+info.name;
					var inputObj = TM_formBuilder.newInputElement(info).css("margin-left","5px");
					
					$(typeDom).after(inputObj);
					autoColumnsWith(col);
				});
			}else if(typeDom.value == 9) {
				$.ajax({
					url: "/metadata/elementType.htm",
					dataType: "json"
				}).done(function(info) {
					info.name = row+"."+info.name;
					var inputObj = TM_formBuilder.newInputElement(info).css("margin-left","5px").change(ajaxFieldType);
					
					$(typeDom).after(inputObj);
					autoColumnsWith(col);
				});
			}else {
				autoColumnsWith(colObj.attr("column"));
			}
		}
		
		function autoColumnsWith(col){
			var maxW = 180;
			$("li[column="+col+"]").each(function(){
				
				var w = 20;
				$(this).children().each(function(){
					w += $(this).width();
				});
				
				if(w > maxW){
					maxW = w;
				}
			});

			$("li[column="+col+"]").width(maxW);
		}
		</script>
		<div style="margin:20px;"><a href="list.htm">列表</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="edit.htm">新增集合</a></div>
    	<div id="editTitle">新增数据结构</div>
    	<div id="editDiv">
    		<form id="metadataForm" action="/metadata/save.htm" method="post" />
    		
    		<!-- 
    		<form id="metadataForm" action="/metadata/save.htm" method="post">
    			<input type="hidden" name="maxIndex" id="maxIndex" value="" />
				<input type="hidden" name="metadata.id" value="${metadata.id}" />
    			<div><label>名称</label><input type="text" name="metadata.name" value="${metadata.name}" /></div>
    			<div><label>标签</label><input type="text" name="metadata.label" value="${metadata.label}" /></div>
				<div id="buttonDiv"><input type="button" onclick="addField()" value="添加新字段" />
				<input type="submit" id="saveButton" disabled value="保存" /></div>
				
				<div id="fieldsDiv">
					<ul class="ul-list header">
						<li>名称</li>
						<li>标签</li>
						<li>类型</li>
						<li>关联对象</li>
						<li>关联类型</li>
						<li>操作</li>
					</ul>
				</div>
			</form>
			 -->
		</div>
		
		<script>
		var emptyField = {};
		var fieldsDivObj = null;
		var fieldIndex = 0;
		
		function initFields(fields){
			
			
			for(var i in fields){
				var field = fields[i];
				fieldsDivObj.append(buildField(field,"ul-field-"+i));
				
				if(i == 0){
					initEmptyField(field);
				}
				fieldIndex++;
			}
			fieldsDivObj.append(buildField(emptyField,"newField"));
		}
		
		function initEmptyField(field){
			emptyField.name = field.name;
			emptyField.name.info.value = "";
			
			emptyField.label = field.label;
			emptyField.label.info.value = "";
			
			emptyField.type = field.type;
			emptyField.type.info.value = "";
			
			emptyField.linkedObject = field.linkedObject;
			emptyField.linkedObject.info.value = "";
			
			emptyField.relationship = field.relationship;
			emptyField.relationship.info.value = "";
		}
		
		function buildField(field,ulId){
			var ulObj = $("<ul></ul>").addClass("ul-list").attr("id",ulId);
			
			ulObj.append(buildLi(field.name));
			ulObj.append(buildLi(field.label));
			ulObj.append(buildLi(field.type));
			
			if(field.type.info.value == 7 || field.type.info.value == 8){
				ulObj.append(buildLi(field.linkedObject));
				ulObj.append(buildLi(field.relationship));
			}else {
				ulObj.append($("<li></li>"));
				ulObj.append($("<li></li>"));
			}
			if(ulId != "newField"){
				ulObj.append(buildDeleteLi());
			}else {
				ulObj.append($("<li></li>"));
			}
			
			return ulObj;
		}
		
		function buildLi(fieldItem){
			var liObj = $("<li></li>");
			var oldName = fieldItem.info.name;
			fieldItem.info.name = fieldIndex+"."+oldName;
			var inputObj = TM_formBuilder.newInput(fieldItem.info);
			if(fieldItem.info.name == "field.type"){
				inputObj.change(changeType);
			}
			fieldItem.info.name = oldName;
			
			liObj.append(inputObj);
			return liObj;
		}
		
		function buildDeleteLi(){
			var liObj = $("<li></li>");
			var inputObj = $("<input type='button' value='删除'>");
			inputObj.click(function(event){
				$(event.delegateTarget).parent().parent().remove();
				shouldSave();
			});
			
			liObj.append(inputObj);
			return liObj;
		}
						
		function addField(){
			fieldsDivObj.off("change","ul#newField > li > :input",addField);
			fieldsDivObj.find("#newField").attr("id","");
			fieldsDivObj.append(buildField(emptyField,"newField"));
			
			resetChange();
		}
		
		function shouldSave(){
			$("#saveButton").attr("disabled",false);
		}
		
		function resetChange(){
			fieldsDivObj.on("change","ul#newField > li > :input",addField);
			fieldsDivObj.on("change","ul > li > :input",shouldSave);
		}
		
		function changeType(event){
			var typeValue = event.delegateTarget.value;
			var objectLi = $(event.delegateTarget).parent().next();
			var relationshipLi = objectLi.next();

			if(typeValue == 7 || typeValue == 8){
				objectLi.append(TM_formBuilder.newInput(emptyField.linkedObject.info));
				relationshipLi.append(TM_formBuilder.newInput(emptyField.relationship.info));
			}else {
				objectLi.html("");
				relationshipLi.html("");
			}
		}

		/*
		$(function(){
			fieldsDivObj = $("#fieldsDiv");
			
			if(resultModel.metadata != "undefined"){
				
			}
			initFields(resultModel.metadata.fields);
			//fieldsDivObj.append(buildField(emptyField,"newField"));
			resetChange();
			$("#metadataForm").submit(function(){
				$("#maxIndex").val(fieldIndex);
				$("#newField").remove();
			});
		});*/
		</script>
		
		<style>
		/*
    	body {
    		font-size:12px;
    	}
    	
    	form label {
    		margin:5px;
    	}
    	
    	form input[type=text] {
    		border:1px solid #cccccc;
    		margin:5px;
    	}
    	
    	form select {
    		border:1px solid #cccccc;
    		margin:5px;
    	}
    	
    	#editDiv {
    		width:500px;
    		height:300px;
    	}
    	
    	#editTitle {
    		margin-top:30px;
    		margin-bottom:5px;
    		font-weight:bold;
    	}
		
		#buttonDiv {
			margin:10px;
		}
		
		.ul-list {
			clear:both;
			width:1000px;
			margin:0px;
			padding:0px;
		}
		
		.header li{
			font-weight:bold;
		}
		
		.ul-list li {
			float:left;
			width:150px;
			border-bottom:1px dashed #ccc;
			list-style:none;
			padding:5px;
			text-align:center;
			height:25px;
		}*/
		</style>
	</body>
</html>