
function objectMetadataModel(){
	var _ = this;
	
	var labels = null;
	
	/***
	 * 数据池
	 * metadatas：array模式
	 * 		[metadata1,metadata2,...]
	 * metadataMap：map模式
	 * 		{id=metadata}
	 * 
	 * metadatas与metadataMap共享同一组metadata对象，
	 * 所以更新一个集合里的对象值，会同时更新另一集合的对象值
	 * **/
	var metadatas = null;
	var metadataMap = null;
	
	var metadataTreeNodes = null;
	var metadataTreeDiagram = null;
	var metadataTabMap = null;
	var metadataDiagramMap = null;
	var metadataFieldTreeNodesMap = null;
	var dataPool = null;
	var metadataTreeRoot = null;
	
	this.init = function(source){
		_.metadatas = source;
		_.metadataTreeNodes = [];
		_.metadataMap = {};
		_.metadataTabMap = {};
		_.metadataDiagramMap = {};
		_.dataPool = {};
		_.metadataFieldTreeNodesMap = {};
		
		_.labels = {name:'名称',label:'标签',type:'类型',operate:'操作'};
	};
	
	this.getLabel = function(key){
		return _.labels[key];
	};
	
	this.getData = function(key){
		if(_.dataPool){
			return _.dataPool[key];
		}
		return null;
	};
	
	this.getAndLoadData = function(key,url,reqData){
		var data = _.getData(key);
		if(data)return data;
		
		$.ajax(url,{
			data:reqData,
			dataType:'json',
			type:'POST',
			async:false
		}).done(function(result,status){
			_.setData(key,result);
		});
		
		return _.getData(key);
	};
	
	/*
	this.getAndLoadData = function(key,url,reqData,fun){
		$.ajax(url,{
			data:reqData,
			dataType:'json',
			type:'POST',v
			success:fun?fun:function(result,textStatus){c
				_.setData(key,result);
			}
		});
	};*/
	
	this.setData = function(key,v){
		_.dataPool[key] = v;
	};
	
	this.removeData = function(key){
		_.dataPool[key] = undefined;
	};
	
	/******  Field start **************/
	this.getFieldTreeNodes = function(omId){
		var nodes = _.metadataFieldTreeNodesMap[omId];
		if(!nodes){
			_.parseMetadata(_.get(omId));
		}
		return _.metadataFieldTreeNodesMap[omId];
	}
	
	/**
	 * metadataFieldsModelMap:{
	 * 		metadata:ObjectMetada对象的源数据
	 * 		fieldTreeNodes:[node,node,...]  // metadata 编辑树的节点组
	 * 			node:{key:'',parent:'',id:''}
	 * 		fieldMap:{id:object}  // 编辑模式的字段映射，主要用于快速定位具体的字段对象
	 * 			id:parent.id+field.name.value
	 * 			object:{
	 * 				properties:[name,label,type]
	 * 				fields:[metadata.field.fields]
	 * 			}
	 * }
	 * 
	 * **/
	this.parseMetadata = function(metadata){
		var treeNodes = [];
		var omId = metadata.id.value;

		metadata._isObject_ = true;
		_.parseFields([metadata],null,treeNodes,omId);
		
		_.metadataFieldTreeNodesMap[omId] = treeNodes;
	};
	
	this.parseFields = function(fields,treeParent,treeNodes,omId){
		if(!fields)return;
		
		$.each(fields,function(i,field){
			if(!field)return;

			var tnode = _.buildFieldTreeNode(field,treeParent,omId);
			treeNodes.push(tnode);
			for(x in field){
				if(x === 'fields'){
					_.parseFields(field.fields,tnode,treeNodes,omId);
				}else {
					_.completeEditField(field[x],x);
				}
			}
		});
	}; 
	
	/***
	 * 补充字段的编辑信息
	 * **/
	this.completeEditField = function(field,fname){
		if(!field)return;
		
		if(!field.name){
			field.name = fname;
		}
		if(!field.type){
			field.type = 'text';
		}
		if(field.type === 'select'){
			if(isString(field.options)){
				field.options = _.getData(field.options);
			}
		}
	};
	
	this.buildFieldTreeNode = function(field,parent,omId){
		var node = {};
		node.key = field.name.value+'('+field.label.value+')';
		if(parent){
			node.parent = parent.key;
		}
		node.field = field;
		node.omId = omId;
		
		return node;
	}
	
	this.canHasFields = function(obj){
		return $.isPlainObject(obj)?obj._isObject_ || ($.isPlainObject(obj.type)?obj.type.value == 7:obj.type == 7):obj == 7;
	};
	
	/******  Field end **************/
	
	this.get = function(omId){
		return _.metadataMap[omId];
	};
	
	this.treeNodes = function(){
		return _.metadataTreeNodes;
	};
	
	this.compile = function(source){
		_.init(source);
		
		$.each(_.metadatas, function(i,metadata) {
			_.metadataMap[metadata.id] = metadata;
		});
		_.buildTreeNodes();
		
		_.metadataTreeDiagram = newMetadatasTreeDiagram(_);
	};
	
	/**
	 * 根据 metadataMap 重置生成 metadataTreeNodes
	 * **/
	this.buildTreeNodes = function(){
		_.metadataTreeNodes = [];
		
		_.metadataTreeRoot = {key : 'All Object'};
		_.metadataTreeNodes.push(_.metadataTreeRoot);
		
		for(x in _.metadataMap){
			var metadata = _.metadataMap[x];
			if(!metadata)continue;
			
			var node = _.newTreeNode(metadata);
			_.metadataTreeNodes.push(node);
		}
	};
	
	/**
	 * 同步数据池
	 * ***/
	this.syncPool = function(omId,newData){
		var oldData = _.get(omId);
		if(oldData){
			_.copy(newData,oldData,null);
		}else {
			_.metadataMap[omId] = newData;
		}
		
		//解析metadata数据
		_.parseMetadata(_.get(omId));
		
		_.buildTreeNodes();
	}
	
	this.removeMetadata = function(omId){
		_.metadataMap[omId] = undefined;
		_.removeTab(omId);
		_.buildTreeNodes();
	};
	
	this.openMetadataTab = function (omId) {
		if(!_.isExistsTab(omId)){
			_.addMetadataTab(omId);
		}else {
			_.showTab(omId);
		}
	};
	
	this.showTab = function(omId){
		objectPanelTab.find("a[href='#"+omId+"']").click();
	};
	
	this.isExistsTab = function(omId){
		if(_.metadataTabMap){
			return _.metadataTabMap[omId];
		}
		return false;
	};
	
	/***
	 * 删除Tab
	 * **/
	this.removeTab = function(omId){
		
		$("#"+omId).remove();
		$("#"+omId+"Link").remove();
		objectPanelTab.tabs("refresh");
		
		_.metadataTabMap[omId] = undefined;
		_.metadataDiagramMap[omId] = undefined;
	};
	
	this.addMetadataTab = function (omId) {
		var metadata = metadataModel.get(omId);
		
		var tabId = omId;//+ "Object";
		var diagramId = omId + 'Diagram';

		var tab = $("<div id='"+tabId+"' />");
		var diagram = $('<div id="'+diagramId + '" style="border: 1px solid blue; width:100%; height:600px;margin-top:30px;" />');
		var mdName = metadata.name;
		if($.isPlainObject(mdName)){
			mdName = mdName.value;
		}
		
		objectPanelTab.find(".ui-tabs-nav").append('<li id="'+tabId+'Link"><a href="#' + tabId + '">' + mdName + '</a><span class="ui-icon ui-icon-close" role="presentation">Remove Tab</span></li>');
		objectPanelTab.append(tab.append(diagram));
		objectPanelTab.tabs("refresh");
		
		_.metadataTabMap[omId] = tab;
		_.showTab(omId);
	};
	
	this.activeMetadataTab = function(omId){
		if(_.isMetadataLoaded(omId)){
			if(!_.metadataDiagramMap[omId]){
				_.refreshMetadataDiagram(omId);
			}
		}else {
			_.ajaxLoadMetadata(omId);
		}
	};
	
	this.newTreeNode = function(obj){
		if(!obj)return null;
		
		var node = {};
		
		node.key = obj.name + "(" + obj.label + ")";
		node.parent = _.metadataTreeRoot.key;
		node.id = obj.id;
		
		return node;
	};
	
	this.readRequestMetadata = function(omId){
		var metadata = _.get(omId);
		
		//解析metadata数据
		_.parseMetadata(_.get(omId));
		
		_.refreshMetadataDiagram(omId);
		
		return _.readRequestObject(metadata);
	};
	
	this.readRequestObject = function(editObj){
		var reqObj = {};
		
		// read fields
		if((!editObj.type || editObj.type.value == 7) 
				&& editObj.fields){
			reqObj.fields = [];
			$.each(editObj.fields,function(){
				reqObj.fields.push(_.readRequestObject(this));
			});
		}
		
		// read type
		if(editObj.type){
			reqObj.type = editObj.type.value;
			
			if(reqObj.type == 8){   // linked object
				reqObj.linkedObject = editObj.type.linkedObject;
			}else if(reqObj.type == 9){ // array
				reqObj.elementType = editObj.type.elementType;
				if(reqObj.elementType == 8){
					reqObj.linkedObject = editObj.type.linkedObject;
				}
			}
		}

		// read other props
		for(x in editObj){
			if(isSystemProp(x))continue;
			if(x === 'fields' || x === 'type' || x === 'id')continue;
			
			reqObj[x] = editObj[x].value;
		}
		
		return reqObj;
	};
	
	this.ajaxLoadMetadata = function (omId){
		$.ajax("edit2.htm",{
			data:{
				id:omId,
				metadata:JSON.stringify(metadata(omId))
			},
			dataType:'json',
			type:'POST',
			success:function(result,textStatus){
				
				//将global的值更新到dataPool中
				var global = result.global;
				if(global){
					for(x in global){
						_.setData(x,global[x]);
					}
				}
				
				//更新数据池里的数据
				_.syncPool(omId,result.metadata);
				
				//重新解析metadata数据，并刷新diagram
				_.refreshMetadataDiagram(omId);
			}
		});
	};
	
	this.ajaxSaveMetadata = function (omId){
		var metadata = _.readRequestMetadata(omId);
		
		$.ajax("save2.htm",{
			data:{
				id:omId,
				metadata:JSON.stringify(metadata)
			},
			dataType:'json',
			type:'POST',
			success:function(result,textStatus){
				//更新数据池里的数据
				_.syncPool(omId,result.metadata);
				
				//重新解析metadata数据，并刷新diagram
				_.refreshMetadataDiagram(omId);
			}
		});
	};
	
	/*
	this.ajaxSave = function (obj,succFun){
		var omId = null;
		var metadata = null;
		if(isString(obj)){
			omId = obj;
			metadata = _.readRequestMetadata(omId);
		}else {
			metadata = _.readRequestObject(obj);
		}
		
		if(!$.isFunction(succFun)){
			succFun = function(result,textStatus){
				//更新数据池里的数据
				_.syncPool(omId,result.metadata);
			};
		}
		
		$.ajax("save2.htm",{
			data:{
				id:omId,
				metadata:JSON.stringify(metadata)
			},
			dataType:'json',
			type:'POST',
			success:succFun
		});
	};
	*/
	
	
	/**
	 * 删除对象模型
	 * ***/
	this.ajaxDelete = function(omId){
		$.ajax("delete2.htm",{
			data:{id:omId},
			dataType:'json',
			type:'POST',
			success:function(result,textStatus){
				_.removeMetadata(omId);
			}
		});
	};
	
	/**
	 * fieldModel:{
	 * 		metadata:obj,
	 * 		fieldTreeNodes:[],
	 * 		fieldMap:{id=field}
	 * }
	 * **/
	this.refreshMetadataDiagram = function(omId){
		var fieldTreeNodes = _.getFieldTreeNodes(omId);
		
		var diagram = _.metadataDiagramMap[omId];
		if(diagram){
			diagram.model = new go.TreeModel(fieldTreeNodes);
		}else {
			_.metadataDiagramMap[omId] = newMetadataDiagram(omId+'Diagram',fieldTreeNodes);
		}
	};
	
	this.isMetadataLoaded = function(omId){
		return _.metadataDiagramMap && _.metadataDiagramMap[omId];
	};
	
	this.readMetadataFields = function (fields, parent, nodes) {
		if (!fields)
			return;

		$.each(fields, function(i, field) {
			var node = {};
			node.key = field.name.value + "(" + field.label.value + ")";
			node.parent = parent.key;

			nodes.push(node);

			if (field.fields && field.fields.value) {
				readFields(field.fields.value, node, nodes);
			}
		});
	};
	
	/***
	复制对象属性，
		exts：不操作的属性
	**/
	this.copy = function (fromObj,toObj,exts){
		for(x in fromObj){
			if(_.hasName(exts,x))continue;
			if(isSystemProp(x))continue;
			
			toObj[x] = fromObj[x];
		}
	};
	
	/***
	清除对象属性，
		exts：不操作的属性
	**/
	this.clearObject = function (obj,exts){
		for(x in obj){
			if(_.hasName(exts,x))continue;
			if(isSystemProp(x))continue;
			
			delete obj[x];
		}
	};
	
	this.hasName = function (names,n){
		if(!names)return false;
			
		var is = false;
		if(names instanceof Array){
			$.each(names,function(i,v){
				if(v === n){
					is = true;
					return;
				}
			});
		}else {
			is = (names === x);
		}
		return is;
	};
	
	this.allowFields = function(obj){
		if(!obj.type)return true;
		
		return obj.type == 7;
	}
}

/**
 * 显示字段操作面板
 * ***/
function showEditPanel(e,node){
	clearEditPanel();
	
	var dataObject = completelyClone(node.data.field);
	
	editPropertiesTable(dataObject);
	if(metadataModel.canHasFields(dataObject)){
		editChildrenTable(dataObject.fields);
	}
	
	$("#editPanel").tabs("refresh");
	
	$("#editPanel").data("omId",node.data.omId);
	$("#editPanel").data("data",dataObject);
	$("#editDialog").dialog("open");
}

function clearEditPanel(){
	$("#editPropertiesList").remove();
	$("#editProperties").remove();
	$("#editChildrenList").remove();
	$("#editChildren").remove();
}

/*
var fields = [
	{key:'price',name:'价格',type:'double'},
	{key:'stock',name:'库存',type:'object'}
];
*/
function editChildrenTable(fields){
	var tObj = newEditTable("editChildrenTable");
	var header = ['name','label','type'];
	
	// add header
	var trObj = $("<tr class='header' />");
	$.each(header,function(i,v){
		trObj.append(newLabelTD(v));
	});
	trObj.append($("<td>操作</td>"));
	tObj.append(trObj);
	
	// add edit fields td 
	if(fields && fields.length > 0){
		$.each(fields,function(i,field){
			var trObj = $("<tr class='fields' />");
			trObj.data("field",field);
			
			$.each(header,function(ii,x){
				trObj.append(newEditTD(field[x]));
			});
			
			trObj.append(removeButton());
			tObj.append(trObj);
		});
	}
	tObj.append(newFieldTr(header));
	
	addEditPanelTab("editChildren","字段编辑",tObj);
	return tObj;
}

function removeButton(){
	var tdObj = $("<td />");
	var del = $("<input type='button' value='删除' />");
	del.click(function(){
		var tr = $(this).parents("tr");
		tr.data("field")._delete_ = true;
		
		tr.remove();
	});
	tdObj.append(del);
	return tdObj;
}

function newFieldTr(header){
	var fieldsTemp = completelyClone(metadataModel.getData("fieldTemplate"),true);
	
	var trObj = $("<tr class='fields new' />");
	trObj.data("field",fieldsTemp);
	
	$.each(header,function(ii,x){
		var field = fieldsTemp[x];
		field.name = x;
		field.value = '';
		if(field.type === 'select'){
			if(isString(field.options)){
				field.options = metadataModel.getData(field.options);
			}
		}
		
		trObj.append(newEditTD(field));
	});
	
	trObj.change(function(){
		$("#editChildrenTable").append(newFieldTr(header));
		$(this).append(removeButton());
		$(this).removeClass("new");
		$(this).unbind("change");
		
		var fields = $(this).data("field");
		fields._add_ = true;
		
		if(!$("#editPanel").data("data").fields){
			$("#editPanel").data("data").fields = [];
		}
		$("#editPanel").data("data").fields.push(fields);
	});
	return trObj;
}

/*
 * example:
 * 		props:[{
			"name" : "name",
			"label" : "名称",
			"type" : "text",
			"value" : "Product"
		},{
			"name" : "label",
			"label" : "标签",
			"type" : "text",
			"value" : "商品"
		}]
 * 
 * **/
function editPropertiesTable(props){
	var tObj = newEditTable("editPropertiesTable");
	
	for(x in props){
		if(x == 'fields')continue;
		if(x == 'id')continue;
		if(isSystemProp(x))continue;
		
		var field = props[x];
		var trObj = $("<tr />");
		
		trObj.append(newLabelTD(field.name)); //add label td
		trObj.append(newEditTD(field)); // add value td
		
		tObj.append(trObj);
	}
	
	addEditPanelTab("editProperties","基本属性",tObj);
	
	/**
	 * 切换字段编辑表
	 * **/
	tObj.on("change", "select[name='type']", function() {
		if(metadataModel.canHasFields($(this).val())){
			var dataObject = $("#editPanel").data("data");
			editChildrenTable(dataObject.fields);
		}else {
			$("#editChildrenList").remove();
			$("#editChildren").remove();
		}
		
		$("#editPanel").tabs("refresh");
	});
	
	return tObj;
}

function isSystemProp(name){
	return name && typeof name === 'string' 
		&& name.length > 0 && name.charAt(0) === '_';
}

function newLabelTD(key){
	var tdObj = $("<td />");
	tdObj.text(metadataModel.getLabel(key));
	return tdObj;
}

function newEditTD(field){
	var tdObj = $("<td />");
	
	var data = field;
	if(field.name === 'type'){
		var fields = [];
		fields.push(field);
		
		if(field.value == 8){
			fields.push(getLinkedObject(field.linkedObject));
		}else if(field.value == 9){
			var elementType = getElementType(field.elementType); 
			fields.push(elementType);
			
			if(elementType.value == 8){
				fields.push(getLinkedObject(field.linkedObject));
			}
		}

		data = fields;
	}

	tdObj.data("prop",data);
	tdObj.text(showText(data));
	tdObj.dblclick(editFieldMode);
	
	return tdObj;
}

function addEditPanelTab(id,tabName,table){
	$("#panelList").append($("<li id='"+id+"List'><a href='#"+id+"'>"+tabName+"</a></li>"));
	$("#editPanel").append($("<div id='"+id+"' />").append(table));
}

function newEditTable(tableId){
	return $("<table cellpadding='0' cellspacing='1' id='"+tableId+"' />");
}

function getLinkedObject(val){
	var linkedObject = metadataModel.getAndLoadData('linkedObject','linkedObject2.htm',null);
	if(val){
		linkedObject.value = val;
	}
	return linkedObject;
}

function getElementType(val){
	var elementType = metadataModel.getAndLoadData('elementType','elementType2.htm',null);
	if(val){
		elementType.value = val;
	}
	return elementType;
}

function buildEditInput(field){
	if(field.type === 'select'){
		if(isString(field.options)){
			field.options = metadataModel.getData(field.options);
		}
	}
	var input = TM_formBuilder.newInputElement(field);
	
	if(field.name === 'type'){
		input.change(function(){
			var val = $(this).val();
			var td = $(this).parents("td");
			
			if(val != 9){
				if(val != 8){
					removeField(td,"linkedObject");
				}
				removeField(td,"elementType");
			}
			
			if(val != 8){
				removeField(td,"linkedObject");
			}
			
			if(val == 8){
				appendField(td,getLinkedObject(null));
			}else if(val == 9){
				appendField(td,getElementType(null));
			}
		});
	}
	
	if(field.name === 'elementType'){
		input.change(function(){
			var val = $(this).val();
			var td = $(this).parents("td");
			
			if(val == 8){
				appendField(td,getLinkedObject(null));
			}else {
				removeField(td,"linkedObject");
			}
		});
	}
	
	input.focusout(groupFocusout);
	input.data("prop",field);
	
	return input;
}

function autoColumnsWith(td){
	var maxW = 580;
	var w = 50;
	$.each(td.children(),function(){
		w += $(this).width();
		
		if(w > maxW){
			maxW = w;
		}
	});

	td.width(maxW);
}


function editFieldMode(){
	$(this).text("");
	
	var td = $(this);
	var data = td.data("prop");
	if(data instanceof Array){
		$.each(data,function(){
			var input = buildEditInput(this);
			td.append(input);
			
			input.focus();
		});
	}else {
		var input = buildEditInput(data);
		td.append(input);
		
		input.focus();
	}
}

function showText(inputData){
	if(inputData instanceof Array){
		var text = null;
		$.each(inputData,function(){
			if(text){
				text += ','+showText(this);
			}else {
				text = showText(this);
			}
		})
		
		return text;
	}else {
		var text = inputData.value;
		if(inputData && inputData.type === 'select' && inputData.value){
			$.each(inputData.options,function(i,v){
				if(v.key == inputData.value){
					text = v.label;
					return;
				}
			});
		}
		if(!text){
			text = '';
		}
		return text;
	}
}

function removeField(td,fname){
	// remove field data by fname
	var tdData = td.data("prop");
	if(tdData instanceof Array){
		$.each(tdData,function(i,f){
			if(f.name === fname){
				tdData.splice(i,1);
			}
		});
	}else {
		if(tdData.name == fname){
			tdData.remove();
		}
	}
	td.data("prop",tdData);
	
	//remove field element
	$.each(td.children(),function(){
		if($(this).attr("name") == fname){
			$(this).remove();
		}
	});
}

function appendField(td,field){
	addFieldData(td,field);
	
	var input = buildEditInput(field);
	td.append(input);
	input.focus();
	
	return input;
}

function addFieldData(td,field){
	var tdData = td.data("prop");
	if(tdData){
		var farr = [];
		if(tdData instanceof Array){
			farr = tdData;
		}else {
			farr.push(tdData);
		}
		farr.push(field);
		td.data("prop",farr);
	}else {
		td.data("prop",field);
	}
}

function groupFocusout(event){
	var parent = $(this).parents("td");
	var next = event.relatedTarget;
	
	if(!inGroup(parent,next)){
		groupText(parent);
	}
}

function inGroup(group,element){
	var inGroup = false;
	$.each(group.children(),function(){
		if(this === element){
			inGroup = true;
			return;
		}
	});
	return inGroup;
}

function groupText(td){
	var text = null;
	$.each(td.children(),function(){
		var field = $(this).data("prop");
		field.value = $(this).val();
		
		if(text){
			text += ','+showText(field);
		}else {
			text = showText(field);
		}
		
		$(this).remove();
	});
	
	td.text(text);
}

function newMetadatasTreeDiagram(metadataModel) {
	var G_Make = go.GraphObject.make;
	var _ = this;
	var yellowgrad = G_Make(go.Brush, "Linear", {
		0 : "rgb(254, 201, 0)",
		1 : "rgb(254, 162, 0)"
	});

	var myDiagram = G_Make(go.Diagram, "treeDiagram", {
		allowMove : false,
		allowCopy : false,
		allowDelete : false,
		allowHorizontalScroll : false,
		layout : G_Make(go.TreeLayout, {
			alignment : go.TreeLayout.AlignmentStart,
			angle : 0,
			compaction : go.TreeLayout.CompactionNone,
			layerSpacing : 16,
			layerSpacingParentOverlap : 1,
			nodeIndent : 5,
			nodeIndentPastParent : 0.88,
			nodeSpacing : 5,
			setsPortSpot : false,
			setsChildPortSpot : false
		})
	});

	this.isMetadata = function(o){
		if(o.diagram.selection 
				&& o.diagram.selection.first() 
				&& o.diagram.selection.first().data 
				&& o.diagram.selection.first().data.id){
			return true;
		}
		
		return false;
	};
	
	this.menuText = function(txt){
		return G_Make(go.TextBlock, 
				{ text: txt, margin:5,font : "13px Helvetica"});
		
		/*
		return G_Make(go.Panel, "Auto", G_Make(go.Shape, "RoundedRectangle", {
			fill : yellowgrad,
			name : "SHAPE",
			stroke : null
		}), G_Make(go.TextBlock, {
			font : "bold 13px Helvetica, bold Arial, sans-serif",
			stroke : "white",
			margin : 5,
			text :txt
		}));*/
		
	};
	
	/**
	 * 右键菜单
	 * **/
	var contextMenu = G_Make(go.Adornment, "Vertical", 
		G_Make("ContextMenuButton",_.menuText("打开"),{
			click : function(e, obj) {
				var nodeData = obj.diagram.selection.first().data;
				metadataModel.openMetadataTab(nodeData.id);
			}
	},new go.Binding("visible", "", _.isMetadata).ofObject()
    ), G_Make("ContextMenuButton", _.menuText("新增对象模型"), {
		click : function(e, obj) {
			$("#addObjectMetadataTable").remove();
			
			var tObj = newEditTable("addObjectMetadataTable");
			var props = {
				name:{name:'name'},
				label:{name:'label'}
			};
			for(x in props){
				if(x == 'fields')continue;
				if(x == 'id')continue;
				if(isSystemProp(x))continue;
				
				var field = props[x];
				var trObj = $("<tr />");
				
				trObj.append(newLabelTD(field.name)); //add label td
				trObj.append(newEditTD(field)); // add value td
				
				tObj.append(trObj);
			}
			
			$("#addObjectMetadataDialog").append(tObj);
			$("#addObjectMetadataDialog").data("data",props);
			$("#addObjectMetadataDialog").data("diagram",obj.diagram);
			$("#addObjectMetadataDialog").dialog("open");
		}
	}), G_Make("ContextMenuButton", _.menuText("删除对象模型"), {
		click : function(e, obj) {
			var diagram = obj.diagram;
			var nodeData = diagram.selection.first().data;
			
			metadataModel.ajaxDelete(nodeData.id);
			
			diagram.model.removeNodeData(nodeData);
		}
	},new go.Binding("visible", "", _.isMetadata).ofObject()));
	
	myDiagram.contextMenu = contextMenu;
	myDiagram.nodeTemplate = G_Make(go.Node, {
		doubleClick : function(e, node) {
			metadataModel.openMetadataTab(node.data.id);
		},
		contextMenu : contextMenu
	}, G_Make("TreeExpanderButton", {
		width : 14,
		"ButtonBorder.fill" : "whitesmoke",
		"ButtonBorder.stroke" : null,
		"_buttonFillOver" : "rgba(0,128,255,0.25)",
		"_buttonStrokeOver" : null
	}),

	G_Make(go.Panel, "Auto", {
		position : new go.Point(16, -8)
	}, G_Make(go.Shape, "RoundedRectangle", {
		fill : yellowgrad,
		name : "SHAPE",
		stroke : null
	}), G_Make(go.TextBlock, {
		font : "bold 13px Helvetica, bold Arial, sans-serif",
		stroke : "white",
		margin : 5
	}, new go.Binding("text", "key")))

	);

	// without lines
	// myDiagram.linkTemplate = $(go.Link);

	// with lines
	myDiagram.linkTemplate = G_Make(go.Link, {
		selectable : false,
		routing : go.Link.Orthogonal,
		fromEndSegmentLength : 4,
		toEndSegmentLength : 4,
		fromSpot : new go.Spot(0.001, 1, 7, 0),
		toSpot : go.Spot.Left
	}, G_Make(go.Shape, {
		stroke : "lightgray"
	}));

	myDiagram.model = new go.TreeModel(metadataModel.treeNodes());
	return myDiagram;
}

function newMetadataDiagram(digramId, fieldTreeNodes) {
	var G_Make = go.GraphObject.make; // for conciseness in defining templates
	var _ = this;
	var yellowgrad = G_Make(go.Brush, "Linear", {
		0 : "rgb(254, 201, 0)",
		1 : "rgb(254, 162, 0)"
	});
	var redgrad = G_Make(go.Brush, "Linear", {
		0 : "#C45245",
		1 : "#7D180C"
	});

	var objDiagram = G_Make(go.Diagram, digramId, {
		initialAutoScale : go.Diagram.UniformToFill,
		layout : G_Make(go.TreeLayout, {
			nodeSpacing : 5,
			layerSpacing : 30
		}),
		"draggingTool.dragsTree" : true,
		mouseDrop : function(e) {
			e.diagram.currentTool.doCancel();
		}
	});
	
	this.menuText = function(txt){
		return G_Make(go.TextBlock, 
				{ text: txt, margin:5,font : "13px Helvetica"});
		
		/*
		return G_Make(go.Panel, "Auto", G_Make(go.Shape, "RoundedRectangle", {
			fill : yellowgrad,
			name : "SHAPE",
			stroke : null
		}), G_Make(go.TextBlock, {
			font : "bold 13px Helvetica, bold Arial, sans-serif",
			stroke : "white",
			margin : 5,
			text :txt
		}));*/
		
	};

	/**
	 * 右键菜单
	 * **/
	var contextMenu = G_Make(go.Adornment, "Vertical", 
		G_Make("ContextMenuButton",_.menuText("新增字段"),{
			click : function(e, obj) {
				var node = obj.diagram.selection.first();
				
				clearEditPanel();
				
				var dataObject = completelyClone(metadataModel.getData("fieldTemplate"),true);
				for(x in dataObject){
					if(isSystemProp(x))continue;
					
					var field = dataObject[x];
					if(field instanceof Array)continue;
					
					if(field instanceof Object){
						field.name = x;
					}
				}
				editPropertiesTable(dataObject);
				
				dataObject._add_ = true;
				$("#editPanel").tabs("refresh");
				
				$("#editPanel").data("omId",node.data.omId);
				$("#editPanel").data("data",dataObject);
				$("#editPanel").data("parent",node.data.field);
				$("#editDialog").dialog("open");
			}
	},new go.Binding("visible", "", function(o) {
			return metadataModel.canHasFields(o.diagram.selection.first().data.field);
    	}).ofObject()
    ), G_Make("ContextMenuButton", _.menuText("编辑字段"), {
		click : function(e, obj) {
			showEditPanel(e,obj.diagram.selection.first());
		}
	}), G_Make("ContextMenuButton", _.menuText("删除字段"), {
		click : function(e, obj) {
			alert("待完成");
		}
	}), G_Make("ContextMenuButton", _.menuText("刷新"), {
		click : function(e, obj) {
			alert("待完成");
		}
	}));

	
	objDiagram.nodeTemplate = G_Make(go.Node, "Horizontal", {
		/***
		 * 拖拽
		 * **/
		mouseDragEnter : function(e, node, prev) {
			var diagram = node.diagram;
			var selnode = diagram.selection.first();
			if (!_.mayWorkFor(selnode, node))
				return;
			var shape = node.findObject("SHAPE");
			if (shape) {
				shape._prevFill = shape.fill; // remember the original brush
				shape.fill = redgrad;
			}
		},
		mouseDragLeave : function(e, node, next) {
			var shape = node.findObject("SHAPE");
			if (shape && shape._prevFill) {
				shape.fill = shape._prevFill; // restore the original brush
			}
		},
		mouseDrop : function(e, node) {
			var diagram = node.diagram;
			var selnode = diagram.selection.first(); // assume just one Node
			
			// in selection
			if (_.mayWorkFor(selnode, node)) {
				// find any existing link into the selected node
				var link = selnode.findTreeParentLink();
				var fromFields = link.fromNode.data.field;
				if (link) { // reconnect any existing link
					link.fromNode = node;
				} else { // else create a new link
					diagram.toolManager.linkingTool.insertLink(node, node.port,
							selnode, selnode.port);
				}
				
				_.moveField(fromFields,node.data.field,selnode.data.field);
				metadataModel.ajaxSaveMetadata(node.data.omId);
			} else {
				diagram.currentTool.doCancel();
			}
		},

		doubleClick : showEditPanel,
		contextMenu : contextMenu
		
		/* 
		toolTip : G_Make(go.Adornment, "Auto", G_Make(go.Shape,
				"RoundedRectangle", {
					fill : "rgb(242, 254, 255)",
					stroke : null
				}), G_Make(go.Panel, "Vertical", G_Make(go.TextBlock, {
			font : "bold 13px Helvetica, bold Arial, sans-serif",
			margin : 10
		}, new go.Binding("text", "id", function(id) {
			var prop = dataModel.getObject(id);
			var tip = "key: " + prop.key + "\nlabel: " + prop.label
					+ "\ntype: " + prop.type + "\nid:" + id
			return tip;
		}))))*/
	}, G_Make(go.Panel, "Auto", G_Make(go.Shape, "RoundedRectangle", {
		fill : yellowgrad,
		name : "SHAPE",
		stroke : null
	}), G_Make(go.TextBlock, {
		font : "bold 13px Helvetica, bold Arial, sans-serif",
		stroke : "white",
		margin : 5
	}, new go.Binding("text", "key"))), G_Make("TreeExpanderButton"));

	objDiagram.toolManager.dragSelectingTool.delay = 100;
	objDiagram.toolManager.hoverDelay = 50;
	objDiagram.linkTemplate = G_Make(go.Link, {
		curve : go.Link.Bezier,
		fromShortLength : -2,
		toShortLength : -2,
		// selectable: false,
		relinkableFrom : true,
		relinkableTo : true
	}, G_Make(go.Shape, {
		strokeWidth : 1.5
	}, new go.Binding("stroke", "toNode", function(n) {
		if (n.data.brush) {
			return n.data.brush;
		}
		return "#ccc";
	}).ofObject()));
	
	this.moveField = function (from,to,field){
		$.each(from.fields,function(i,f){
			if(f == field){
				from.fields.splice(i,1);
				return;
			}
		});
		
		if(!to.fields){
			to.fields = [];
		}
		to.fields.push(field);
	};

	this.mayWorkFor = function (node1, to) {
	    if (!(node1 instanceof go.Node)) return false;  // must be a Node
	    if (node1 === to) return false;  // cannot work for yourself
	    if (to.isInTreeOf(node1)) return false;  // cannot work for someone who works for you
		if(node1.findTreeParentNode() === to)return false;

		if(!(metadataModel.canHasFields(to.data.field))){
			return false;
		}
	    return true;
	};

	objDiagram.model = new go.TreeModel(fieldTreeNodes);
	return objDiagram;
}

function isString(obj){
	return $.type(obj) === 'string';
}

function ts(obj){
	return JSON.stringify(obj);
}

/***
 * 深度clone：递归克隆对象及对象的子元素，并缓存被克隆对象（noMark=true,不缓存）
 * ***/
function completelyClone(obj,noMark){
	var newObj = null;
	if(obj instanceof Object){
		if(obj instanceof Array){
			newObj = [];
		}else {
			newObj = {};
		}
		
		for(x in obj){
			newObj[x] = completelyClone(obj[x],noMark);
		}
		
		if(!(obj instanceof Array) && !noMark){
			if(noMark){
				delete newObj['_clone_'];
			}else {
				newObj['_clone_'] = obj;
			}
		}
	}else {
		newObj = obj;
	}
	
	return newObj;
}

function metadata(oi){
	return {
		id:oi,
		name:'Product',
		label:'产品',
		fields:[
		    {
				name:'info',
				label:'基本信息',
				type:'7',
				fields:[
				    {name:'name',label:'名称',type:1},
				    {name:'price',label:'价格',type:2}
				]
			},
			{
				name:'dispatch',
				label:'配送',
				type:7,
				fields:[
				    {name:'store',label:'库存',type:1},
				    {name:'brand',label:'品牌',type:2}
				]
			}
		]
	}
}

