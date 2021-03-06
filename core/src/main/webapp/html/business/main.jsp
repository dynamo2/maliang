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
		<script src="../../js/jquery.simulate.js"></script>
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/datatables.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		
		<script src="../../html/business/main.js"></script>
		
		<script src="../../html/business/tianma.js"></script>
		<link href="../../html/business/style.css" rel="stylesheet" type="text/css"/> 
    </head>
	<body>
		<div class="ui-layout-north">
			<a href="/metadata/main.htm">对象模型</a>&nbsp;&nbsp;
			<a href="/business/main.htm">业务</a>&nbsp;&nbsp;
			<a href="/uctype/main.htm">自定义类型</a>&nbsp;&nbsp;
			<a href="/metadata/code.htm" target="_blank">编辑器</a>
			<a style="display:none;" id="refreshMainLink" href="/business/main.htm" target="_top">刷新</a>
		</div>
		<div class="ui-layout-center">
			<div id="businessPanel">
				<ul id="businessList">
					<li id="businessPreviewLink"><a href="#businessPreview">预览</a><span class="ui-icon ui-icon-close" role="presentation">Remove Tab</span></li>
				</ul>
				<div id="businessPreview">
					<iframe id="previewIframe" style="width:100%;height:600px;"></iframe>
				</div>
			</div>
		</div>
		<div class="ui-layout-west">
			<div id="businessesDiagram" style="width:100%; height:100%;" />
		</div>
		
		<div id="businessEditerDialog" title="Basic info Editer">
			<form id="businessEditForm">
				<div>
					<label>名称:</label>
					<input type="text" name="business.name" id="name" value="" />
				</div>
				<div>
					<label>唯一代码:</label>
					<input type="text" name="business.uniqueCode" id="uniqueCode" value="" />
				</div>
			</form>
		<div>
		
		
		<script type="text/javascript">
		var json = ${resultJson};
		var businessModel = new BusinessModel();
		
		$(function(){
			businessModel.buildTreeDiagram(json.list);

			$("#businessPanel").tabs();
			addTabClose($("#businessPanel"));
			
			$("body").layout({ 
				applyDemoStyles: true,
				west:{
					size:300
				}
			});
			
			initBusinessEditer();
		});
		
		/*
		* tab的删除按钮
		**/
		function addTabClose(tabsObj){
			tabsObj.delegate( "span.ui-icon-close", "click", function() {
				var tid = $(this).closest("li").remove().attr("aria-controls");
				businessModel.removeTab(tid);
			});
		}
		
		function initBusinessEditer(){
			$("#businessEditerDialog").dialog({
				autoOpen: false,
				modal:true,
				width:800,
				height:600,
				buttons: {
					"Save": function() {
						var req = readFormDatas($("#businessEditForm"));
						
						$.ajax('/business/add.htm',{
							data:req,
							dataType:'json',
							type:'POST',
							async:false
						}).done(function(result,status){
							//refresh(result);
							
							//businessModel.buildTreeDiagram(result.list);
						});
						
						$(this).dialog("close");
					},
					"Cancel": function() {
					  $(this).dialog("close");
					}
				  }
				});
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
				width:300,
				autoOpen: false,
				buttons: {
					"Save": function(){
						var obj = $("#addObjectMetadataDialog").data("data");
						var diagram = $("#addObjectMetadataDialog").data("diagram");
						
						var metadata = metadataModel.readRequestObject(obj);
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
						
						diagram.model.addNodeData(newNode);
						$(this).dialog("close");
					},
					Cancel: function() {
					  $(this).dialog( "close" );
					}
				}
			});
		}
		
		/**
		** 初始化字段操作dialog
		**/
		function initEditPanel(){
			$("#editPanel").tabs();
			$("#editDialog").dialog({
				resizable: false,
				height:600,
				width:550,
				autoOpen: false,
				buttons: {
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
		
		function ajaxNoData(url,doneFun) {
			$.ajax(url, {
				dataType : 'json',
				type : 'POST',
				async : false
			}).done(doneFun);
		}
		
		function initDefaultOptionsDialog(dialogId){
			$("#"+dialogId).dialog({
				resizable: false,
				height:400,
				width:500,
				autoOpen: false});
		}
		
		function showInFormDialog(json,saveFun){
			var formDialog = $("#formDialog");
			if(formDialog.length == 0){
				$("body").append($('<div id="formDialog" title="编辑" /></div>').append($('<div id="formDialogContent" />')));
				initDefaultOptionsDialog("formDialog");
			}
			
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
		
		/*
		function readFormDatas22 (form) {
			var inputs = form.find(":input");

			var reqDatas = {};
			$.each(inputs, function() {
				if ($(this).attr("type") == "radio" 
						|| $(this).attr("type") == "checkbox") {
					if (!this.checked) {
						return;
					}
				}
				
				var key = $(this).attr("name");
				var val = $(this).val();
				var oldVal = utils.get(reqDatas,key);
				
				if(oldVal){
					if(!$.isArray(oldVal)){
						oldVal = [oldVal];
						utils.put(reqDatas,key,oldVal);
					}
					oldVal.push(val);
				}else {
					utils.put(reqDatas,key,val);
				}
			});

			return reqDatas;
		}
		*/
		
		function print(str){
			$('#print').text(str);
		}
		
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
}

#editDialog table input,
#addObjectMetadataDialog table input {
	//border:0px;
	//border-bottom:1px solid #ccc;
	width:80px;
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
</style>
	</body>
</html>