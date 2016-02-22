<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="../../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
        <script src="../../js/angular.js"></script>
		<script src="../../js/jquery-2.1.3.min.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		
		<script src="../../html/metadata/gojs/js/go.js"></script>
		<script src="../../html/metadata/gojs/js/jquery.layout-latest.js"></script>
		<script src="../../html/metadata/gojs/js/main.js"></script>
		
		<script src="../../html/business/tianma.js"></script>
		<link href="../../html/business/style.css" rel="stylesheet" type="text/css"/> 
    </head>
	<body>
		<div class="ui-layout-center">
			<div id="objectPanel">
				<ul id="objectList" />
			</div>
			<p><textarea id="print" style="width:800px;height:500px;"></textarea></p>
		</div>
		<div class="ui-layout-west">
			<div id="treeDiagram" style="width:100%; height:100%;" />
		</div>
		<div class="ui-layout-east">字段字典</div>
		
		<div id="editDialog" title="字段操作面板">
			<div id="editPanel">
			  <ul id="panelList" />
			</div>
		</div>
		
		<div id="addObjectDialog" title="添加新对象">
			<table>
				<tr></tr>
			</table>
		</div>
		<script type="text/javascript">
		var json = ${resultJson};
		var objectPanelTab = null;
		var dataModel = null;
		var metadataModel = new objectMetadataModel();
		
		$(function(){
			metadataModel.compile(json.metadataList);
			objectPanelTab = $("#objectPanel").tabs();
			
			initEditPanel();
			
			$("body").layout({ applyDemoStyles: true });
			
			objectPanelTab.on( "tabsactivate", function( event, ui ) {
				var omId = ui.newTab.closest("li").attr("aria-controls");
				if(!metadataModel.isMetadataLoaded(omId)){
					metadataModel.ajaxLoadMetadata(omId);
				}
			});
			
			objectPanelTab.delegate( "span.ui-icon-close", "click", function() {
				var panelId = $(this).closest("li").remove().attr("aria-controls");
				$( "#" + panelId ).remove();
				objectPanelTab.tabs("refresh");
				
				metadataModel.removeTab(panelId);
			});
		});
		
		function initAddObjectDialog(){
			$("#addObjectDialog").dialog({
				resizable: false,
				height:600,
				width:550,
				autoOpen: false,
				buttons: {
					"Save": function(){
						$(this).dialog("close");
					}
				}
			});
			
			var table = $("<table id='addObjectTable' />");
			var newObj = {name:{},label:{}};
			for(x in newObj){
				var tr = $("<tr />");
			}
		}
		
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
						
						print(ts(metadataModel.readRequestMetadata(omId)));
						
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
#editDialog {
	font-size:12px;
}
#editDialog table {
	background-color:#ccc;
}

#editDialog table input{
	//border:0px;
	//border-bottom:1px solid #ccc;
	width:80px;
}

#editDialog td {
	background-color:#fff;
	padding:7px 10px;
	min-width:100px;
	height:23px;
}

#editDialog .label {
	text-align:right;
	font-weight:bold;
}

#editDialog .header td {
	text-align:center;
	font-weight:bold;
}

#objectPanel li .ui-icon-close { float: left; margin: 0.4em 0.2em 0 0; cursor: pointer; }
</style>
	</body>
</html>