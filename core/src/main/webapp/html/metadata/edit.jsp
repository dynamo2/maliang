<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <script src="../js/angular.js"></script>
		<script src="../js/jquery-2.1.3.min.js"></script>
    </head>
    <body>
    	<style>
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
			width:500px;
			margin:0px;
			padding:0px;
		}
		
		.header li{
			font-weight:bold;
			text-align:center;
		}
		
		.ul-list li {
			float:left;
			width:150px;
			border-bottom:1px dashed #ccc;
			list-style:none;
			padding:5px;
		}
    	</style>
		<script>
		var resultModel = ${resultJson};
		var emptyField = {name:"",label:"",type:""};
		var fieldsDivObj = null;
		
		$(function(){
			fieldsDivObj = $("#fieldsDiv");
			initFields(resultModel.metadata.fields);
			resetChange();
		});
		
		function initFields(fields){
			for(var i in fields){
				var field = fields[i];
				fieldsDivObj.append(buildField(field,"ul-field-"+i));
			}
			fieldsDivObj.append(buildField(emptyField,"newField"));
		}
		
		function buildField(field,ulId){
			var ulObj = $("<ul></ul>").addClass("ul-list").attr("id",ulId);
			for(var fname in field){
				var fvalue = field[fname];
				
				var liObj = $("<li></li>");
				var inputObj = $("<input />").attr("type","text").attr("name","field."+fname).attr("value",fvalue);
					
				liObj.append(inputObj);
				ulObj.append(liObj);
			}
			return ulObj;
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
		</script>
    	<div id="editTitle">新增数据结构</div>
    	<div id="editDiv">
    		<form action="/metadata/save.htm" method="post">
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
					</ul>
				</div>
			</form>
		</div>
	</body>
</html>