<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="../../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
        <script src="../../js/angular.js"></script>
		<script src="../../js/jquery-2.1.3.min.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		<script src="../../js/go.js"></script>
		<script src="../../js/jquery.layout-latest.js"></script>
		
		<!-- wysiwyg -->
		<script src="../js/wysiwyg/wysiwyg.js"></script>
		<script src="../js/wysiwyg/wysiwyg-editor.js"></script>
		<script src="../js/wysiwyg/config.js"></script>
		<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css">
		<link href="../style/wysiwyg/wysiwyg-editor.css" rel="stylesheet" type="text/css"/>
		
		
		<!-- tianma business start -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/datatables.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		<!-- tianma business end -->
		
		<script src="../../html/metadata/gojs/js/tree.js?ddd"></script>
		<script src="../../html/metadata/gojs/js/metadata.js?d"></script>
		<script src="../../html/metadata/gojs/js/main.js?ddddd"></script>
		
		<script src="../../html/business/tianma.js"></script>
		<link href="../../html/business/style.css" rel="stylesheet" type="text/css"/> 
    </head>
	<body>
		<div class="ui-layout-north">
			<a href="/metadata/main.htm">对象模型</a>&nbsp;&nbsp;
			<a href="/business/main.htm">业务</a>&nbsp;&nbsp;
			<a href="/uctype/main.htm">自定义类型</a>&nbsp;&nbsp;
			<a href="/metadata/code.htm" target="_blank">编辑器</a>
		</div>
		<div class="ui-layout-center">
			<div id="objectPanel">
				<ul id="objectList" />
			</div>
			<p id="dbTable"></p>
			<p><textarea id="print" style="width:800px;height:500px;"></textarea></p>
		</div>
		<div class="ui-layout-west">
			<div id="treeDiagram" style="width:100%; height:100%;" />
		</div>
		<div class="ui-layout-east">字段字典</div>
		
		<div id="editDialog" title="字段操作面板">
			<p id="editDialogNav">
				<input type="button" value="新增自定义类型" onclick="addUCType();" />
				<input type="button" value="新增对象模型" onclick="addObjectMetadata();" />
			</p>
			<div id="editPanel">
			  <ul id="panelList" />
			</div>
		</div>
		
		<div id="addObjectMetadataDialog" title="新增对象模型" />
		<div id="uctypedialog" title="新增对象模型" /><div id="dialogContent" /></div>
		<div id="dialog" title="新增对象模型" /><div id="dialogPanel" /></div>
		<div id="formDialog" title="编辑" /><div id="formDialogContent" /></div>
		<script type="text/javascript">
		var json = ${resultJson};
		var objectPanelTab = null;
		var dataModel = null;
		var metadataModel = new objectMetadataModel();
		
		$(function(){
			metadataModel.compile(json.metadataList);
			initObjectPanelTab();
			initEditPanel();
			initAddObjectMetadataDialog();
			
			initDefaultOptionsDialog("dialog");
			initDefaultOptionsDialog("formDialog");
			
			$("body").layout({ 
				applyDemoStyles: true,
				west:{
					size:350
				}
			});
		});
		
		function initDefaultOptionsDialog(dialogId){
			$("#"+dialogId).dialog({
				resizable: false,
				height:600,
				width:800,
				autoOpen: false});
		}
		
		function addObjectMetadata(){
			var json = ['form','metadata',
			            	[['$name','名称','','[n]'],
			            	 ['$label','标签','','[n]'],
			            	 ['$modelType','模型类型','','[n]']]];
			 var form = build(json);

			 $("#dialogContent").empty();
			 $("#dialogContent").append(form);
			 
			 $("#dialog").dialog("option","buttons",{
					"Save": function(){
						var form = $(this).find("form");
						var reqDatas = readFormDatas(form);
						
						saveNewMetadata(reqDatas);
						$(this).dialog("close");
					},
					Cancel: function() {
					  $(this).dialog( "close" );
					}
				});
			 $("#dialog").dialog("open");
		}
		
		function addUCType(){
			var json = ['form','uctype',
			            	[['$name','名称','','[n]'],
			            	 ['$key','key','','[n]'],
			            	 ['$units','单位','','',{change:factors},'[n]'],
			            	 ['$factor','单位换算','label','[n]']]];
			
			showInDialog(json,function(){
				var form = $("#dialogContent").find("form");
				var reqDatas = readFormDatas(form);
				
				ajaxSaveUCType(reqDatas,function(){
					metadataModel.removeData('fieldTypes');
				});
				
				$(this).dialog("close");
			});
		}
		
		function showInFormDialog(json,saveFun){
			var element = build(json);

			 $("#formDialogContent").empty();
			 $("#formDialogContent").append(element);
			 
			 $("#formDialog").dialog("option","buttons",{
					"Save": saveFun,
					Cancel: function() {
					  $(this).dialog( "close" );
					}
			});
			 $("#formDialog").dialog("open");
		}
		
		function showInDialog(json,saveFun){
			var element = build(json);

			 $("#dialogContent").empty();
			 $("#dialogContent").append(element);
			 
			 $("#uctypedialog").dialog("option","buttons",{
					"Save": saveFun,
					Cancel: function() {
					  $(this).dialog( "close" );
					}
			});
			 $("#uctypedialog").dialog("open");
		}
		
		function factors(){
			var u = $(this).val();
			var us = u.split(",");
			
			var factorTd = $(this).closest("form").find("label[name='factor']").closest("td");
			factorTd.empty();
			for(var i = 0; i < us.length-1; i++){
				var div = $("<div />");
				var val = '';
				
				div.append("<label>1"+us[i]+"  =</label>")
					.append("<input type='text' name='factors' style='width:50px;' value='"+val+"' />")
					.append("<label>"+us[i+1]+"</label>");
				
				factorTd.append(div);
			}
		}
		
		/**
		** 初始化对象操作面板
		**/
		function initObjectPanelTab(){
			objectPanelTab = $("#objectPanel").tabs();
			
			/**
			** 激活tab时加载对象数据
			**/
			objectPanelTab.on( "tabsactivate", function( event, ui ) {
				var omId = ui.newTab.closest("li").attr("aria-controls");
				metadataModel.activeMetadataTab(omId);
			});
			
			/*
			* 对象操作tab的删除按钮
			**/
			objectPanelTab.delegate( "span.ui-icon-close", "click", function() {
				var omId = $(this).closest("li").remove().attr("aria-controls");
				metadataModel.removeTab(omId);
			});
		}
		
		/**
		** 初始化新增对象模型dialog
		**/
		function initAddObjectMetadataDialog(){
			$("#addObjectMetadataDialog").dialog({
				resizable: false,
				height:400,
				width:500,
				autoOpen: false,
				buttons: {
					"Save": function(){
						var obj = $("#addObjectMetadataDialog").data("data");
						var diagram = $("#addObjectMetadataDialog").data("diagram");
						
						var metadata = metadataModel.readRequestObject(obj);
						saveNewMetadata(metadata);
						
						$(this).dialog("close");
					},
					Cancel: function() {
					  $(this).dialog( "close" );
					}
				}
			});
		}
		
		function saveNewMetadata(metadata){
			var newNode = metadataModel.newTreeNode(metadata);
			
			$.ajax("save2.htm",{
				data:{metadata:JSON.stringify(metadata)},
				dataType:'json',
				type:'POST',
				success:function(result,textStatus){
					var omId = result.metadata.id.value;
					metadataModel.syncPool(omId,result.metadata);

					//重新解析metadata数据，并刷新diagram
					metadataModel.refreshMetadataDiagram(omId);
					
					newNode.id = omId;
				}
			});
			
			metadataModel.addMetadataNode(newNode);
		}
		
		/**
		** 初始化字段操作dialog
		**/
		function initEditPanel(){
			$("#editPanel").tabs();
			$("#editDialog").dialog({
				resizable: false,
				height:900,
				width:1000,
				html:true,
				autoOpen: false,
				buttons: {
					"Read":function(){
						readChildrenData();
					},
					"Save": function(){
						readFields();
						
						var omId = $("#editPanel").data("omId");
						var newData = $("#editPanel").data("data");
						var oldData = newData['_clone_'];
						var addFields = [];
						
						/**
						** 新增字段
						**/
						if(newData['_add_']){
							newData._add_ = undefined;
							
							var parent = $("#editPanel").data("parent");
							if(!parent.fields){
								parent.fields = [];
							}
							parent.fields.push(newData);
							
						}else { 
							
							/**
							** 编辑字段
							**/
							if(newData.fields){
								$.each(newData.fields,function(i,field){
									if(field['_delete_']){
										if(field['_clone_']){
											oldData.fields.splice(i,1);
										}
									}else if(field['_add_']){
										field._add_ = undefined;
										addFields.push(field);
									}else if(field['_clone_']){
										updateProperties(field['_clone_'],field);
									}
								});
							}
							updateProperties(oldData,newData);
							
							if(addFields.length > 0){
								if(!oldData.fields){
									oldData.fields = [];
								}
								
								$.each(addFields,function(i,v){
									oldData.fields.push(v);
								});
							}
						}
						
						metadataModel.ajaxSaveMetadata(omId);
						
						$(this).dialog("close");
					},
					Cancel: function() {
					  $(this).dialog( "close" );
					}
				}
			});
		}
		
		function updateProperties(oldObj,newObj){
			metadataModel.clearObject(oldObj,['fields','id']);
			metadataModel.copy(newObj,oldObj,['fields','id']);
		}
		
		function readFields(){
			$("#editChildrenTable > tbody > .new").remove();
			
			$.each($("#editChildrenTable > tbody > .fields"),function(){
				readProperties($(this));
			});
		}
		
		function readProperties(tr){
			var field = tr.data("field");
			metadataModel.clearObject(field,['fields','id']);
			
			$.each(tr.children(),function(){
				var prop = $(this).data("prop");
				if(!prop)return;
				
				if(prop instanceof Array){
					var typeProp = null;
					$.each(prop,function(){
						if(this.name === 'type'){
							typeProp = this;
							field.type = typeProp;
							return;
						}
					});
					
					$.each(prop,function(){
						if(this.name === 'linkedObject'){
							typeProp.linkedObject = this.value;
						}
						
						if(this.name === 'elementType'){
							typeProp.elementType = this.value;
						}
					});
				}else {
					field[prop.name] = prop;
				}
			});
		}
		
		function print(str){
			$('#print').text(str);
		}

		
		/************** DB Table start **************************/
		
		function readDBdatas(oid){
			alert("readDBdatas : " + oid);
			$.ajax({
			    cache:true,
			    type:"POST",
			    dataType : 'json',
			    url:'/metadata/dbDatas.htm',
			    data:{oid:oid},
			    async:false,
			    success:function(result,status){
			    	var data = JSON.parse(result.result);
			    	html(data);
			    	
			    	console.log(JSON.stringify(result));
			    }
			});
		}
		
		function tableList(list,cls){
			
			function readHead(){
				var heads = [];
				$.each(list,function(){
					if($.isPlainObject(this)){
						for(var k in this){
							var isRepeat = false;
							
							$.each(heads,function(){
								if(k == this){
									isRepeat = true;
									return;
								}
							});
							
							if(!isRepeat){
								heads.push(k);
							}
						}
					}
				});
				return heads;
			}
			
			function createTHead(heads,thead){
				if(heads.length > 0){
					var tr = $("<tr />").appendTo(thead);
					$.each(heads,function(){
						var th = $("<th />").text(this).addClass(this).appendTo(tr);
					});
				}
			}
			
			var table = $("<table class='table' />").addClass(cls);
			
			var thead = $("<thead />").appendTo(table);
			var tbody = $("<tbody />").appendTo(table);
			
			var heads = readHead();
			createTHead(heads,thead);
			
			$.each(list,function(){
				var tr = $("<tr />").appendTo(tbody);
				
				var d = this;
				if(heads.length > 0){
					$.each(heads,function(){
						valColumn(d[this],$('<td />').appendTo(tr));
					});
				}else {
					valColumn(d,$('<td />').appendTo(tr));
				}
				
			});
			
			return table;
		}
		
		function html(data){
			$("#dbTable").empty();
			
			var table = createElement(data,'table-bordered');
			table.appendTo($("#dbTable"));
		}
		
		function createElement(obj,tableCls){
			if($.isArray(obj)){
				return tableList(obj,tableCls);
			}
			
			if($.isPlainObject(obj)){
				return rows(obj);
			}
			return $("<p />").text(obj);
		}
		
		
		function rows(obj){
			var rows = [];
			for(var k in obj){
				var row = $('<div class="row">');
				
				if(k.charAt(0) == '$'){
					$('<div class="col-md-12"></div>').text(obj[k]).appendTo(row);
				}else {
					$('<div class="col-md-2"></div>').text(k).appendTo(row);
					valColumn(obj[k],$('<div class="col-md-10" />').appendTo(row));
				}
				
				rows.push(row);
			}
			return rows;
		}
		
		function valColumn(val,col){
			if(val == null || val == undefined){
				val = "";
			}
			
			var tt = createElement(val,'');
			if(tt == null){
				col.text(val);
			}else {
				col.append(tt);
			}
		}
		/************** DB Table end **************************/
		
		</script>
		
<style>
.node circle {
  fill: #fff;
  stroke: steelblue;
  stroke-width: 1.5px;
}

.node {
  font: 10px sans-serif;
}

.link {
  fill: none;
  stroke: #ccc;
  stroke-width: 1.5px;
}

.ui-dialog-title{
	font-size:12px;
}

#editDialog,
#addObjectMetadataDialog {
	font-size:12px;
}
#editDialog table,
#addObjectMetadataDialog table  {
	background-color:#ccc;
	width:100%;
	table-layout:fixed ;
}

#editDialog table input,
#addObjectMetadataDialog table input {
	//border:0px;
	//border-bottom:1px solid #ccc;
	width:150px;
}

#editDialog table input[type="button"],
#addObjectMetadataDialog table input[type="button"] {
	//border:0px;
	//border-bottom:1px solid #ccc;
	width:auto;
}

#editDialog td,
#addObjectMetadataDialog td  {
	background-color:#fff;
	padding:7px 10px;
	min-width:100px;
	height:23px;
}

#editDialog .label,
#addObjectMetadataDialog .label {
	text-align:right;
	font-weight:bold;
}

#editDialog .header td,
#addObjectMetadataDialog .header td  {
	text-align:center;
	font-weight:bold;
}

#objectPanel li .ui-icon-close { float: left; margin: 0.4em 0.2em 0 0; cursor: pointer; }


/**************
 * Copy from business.jsp 
 *
*************************/
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