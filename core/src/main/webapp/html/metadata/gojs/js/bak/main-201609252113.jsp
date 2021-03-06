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
		
		<script src="../../html/metadata/gojs/js/main.js"></script>
		
		<script src="../../html/business/tianma.js"></script>
		<link href="../../html/business/style.css" rel="stylesheet" type="text/css"/> 
		
		<!-- 新 Bootstrap 核心 CSS 文件 --
		<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">
		<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
		
		<script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
		<!-- 新 Bootstrap 核心 CSS 文件 -->
		
    </head>
	<body>
		<div class="ui-layout-north">
			<a href="/metadata/main.htm">对象模型</a>&nbsp;&nbsp;
			<a href="/business/main.htm">业务</a>&nbsp;&nbsp;
			<a href="/uctype/main.htm">自定义类型</a>
		</div>
		<div class="ui-layout-center">
			<div id="objectPanel">
				<ul  id="objectList" />
			</div>
			<p><textarea id="print" style="width:1000px;height:500px;"></textarea></p>
		</div>
		<div class="ui-layout-west">
			<div id="treeDiagram" style="width:100%; height:100%;" />
		</div>
		<div class="ui-layout-east">字段字典</div>
		
		<div id="editDialog" title="字段操作面板">
			<div id="editPanel">
			  <ul id="panelList" class="nav nav-tabs" role="tablist"></ul>
			  <div class="tab-content" id="editTabContent"></div>
			</div>
		</div>
		
		<div id="addObjectMetadataDialog" title="新增对象模型" />
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
			
			$("body").layout({ applyDemoStyles: true });
		});
		
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
			//$("#editPanel").tabs();
			$("#editDialog").dialog({
				resizable: false,
				height:800,
				width:1000,
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
	width:100%;
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

#panelList {
	margin-bottom:15px;
}
</style>
	</body>
</html>