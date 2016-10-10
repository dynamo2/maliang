/***
 * Data Manager
 * 管理数据，可以在‘{}’数据上绑定并触发事件
 * **/
var dm = new DM();
function DM(){
	var _ = this;
	var sys_events = null;
	var SYS_KEY = ['_events_'];
	var EVENT_CHANGE = 'change';
	
	this.on = function(data,ename,target,fun){
		if(!data)return;
		
		_.getEvents(data,ename).push([target,fun]);
	};
	
	this.change = function(data,newData){
		utils.copy(newData,data,null);
		
		_.trigger(data,EVENT_CHANGE);
	};
	
	this.trigger = function(data,eventName){
		var events = _.getEvents(data,eventName);
		$.each(events,function(){
			this[1].call(this[1],this[0],data);
		});
	};
	
	this.getEvents = function(data,eventName){
		if(!_.sys_events){
			_.sys_events = {};
		}
		
		if(!_.sys_events[data]){
			_.sys_events[data] = {};
		}
		
		if(!_.sys_events[data][eventName]){
			_.sys_events[data][eventName] = [];
		}
		
		return _.sys_events[data][eventName];
	};
}

/**
 * 清空数组
 * **/
Array.prototype.empty = function(){
	this.splice(0,this.length);
};

Array.prototype.isEmpty = function(){
	return this.length == 0;
};

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
	var ARRAY = null;
	var INNER = null;
	
	this.init = function(source){
		_.metadatas = source;
		_.metadataTreeNodes = [];
		_.metadataMap = {};
		_.metadataTabMap = {};
		_.metadataDiagramMap = {};
		_.dataPool = {};
		_.metadataFieldTreeNodesMap = {};
		
		_.labels = {name:'名称',label:'标签',type:'类型',operate:'操作'};
		
		_.INNER = 7;
		_.ARRAY = 9;
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
		//var data = _.getData(key);
		//if(data)return data;
		
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
	};
	
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
		//return $.isPlainObject(obj)?obj._isObject_ || ($.isPlainObject(obj.type)?obj.type.value == 7:obj.type == 7):obj == 7;
		return _.isObject(obj) || _.isInnerType(obj) || _.isArrayInnerType(obj);
	};
	
	this.isObject = function(obj){
		return $.isPlainObject(obj) && obj._isObject_;
	};
	
	this.isInnerType = function(obj){
		if($.isPlainObject(obj)){
			return $.isPlainObject(obj.type)?obj.type.value == _.INNER:obj.type == _.INNER;
		}else {
			return obj == _.INNER;
		}
	};
	
	this.isArrayInnerType = function(obj){
		if($.isPlainObject(obj.type)){
			if(obj.type.value == _.ARRAY){
				return obj.type.elementType == _.INNER;
			}
		}
		return false;
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
	
	this.addMetadataNode = function(mNode){
		if(_.metadataTreeDiagram && _.metadataTreeDiagram.model){
			_.metadataTreeDiagram.model.addNodeData(mNode);
		}
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
		return _.metadataTabMap && _.metadataTabMap[omId];
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
		if((_.isObject(editObj) || _.isInnerType(editObj) || _.isArrayInnerType(editObj)) 
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
		fieldsFromTab(dataObject.fields);
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
	$("#formTabList").remove();
	$("#formTab").remove();
}

function readChildrenData(){
	var table = $("#editChildrenTable");
	var tds = table.find("td");

	var list = [];
	$.each($("#editChildrenTable > tbody > .fields"),function(){
		var field = $(this).data("field");
		
		$.each(field,function(){
			this['_clone_'] = undefined;
		});
		
		list.push(field);
	});
	
	print(ts(list));
	
	/*
	var list = [];
	$.each(tds,function(){
		if(this.data())
	});
	*/
}

function fieldsFromTab(fields){
	/*
	var json = ['form','uctype',
            	[['$name','名称','text','[n]'],
            	 ['$key','key','text','[n]'],
            	 ['$units','单位','text','',{change:factors},'[n]'],
            	 ['$factor','单位换算','label','[n]']]];
            	 */
	
	var json = ['form','uctype'];
	var inputs = [];
	$.each(fields,function(){
		var type = this.type.value;
		if(type == 8){
			type = ['select',[{key:this.type.linkedObject,label:this.type.linkedObject}]];
		}else if(type == 4){
			type = "date";
		}else type = 'text';
		
		inputs.push([this.name.value,this.label.value,type,'[n]']);
	});
	json.push(inputs);
	
	var div = $("<div style='width:100%;height:100%' />");
	var jsonDiv = $("<div id='json'><p><button id='htmlBtn' value='html'>Html</button></p>" +
					"<textarea id='jsonText' style='width:800px;height:500px;'>"+ts(json)+"</textarea></div>");
	
	var htmlDiv = $("<div id='html' style='display:none;'><p><button id='jsonBtn' value='json'>Json</button></p></div>").append(build(json));
	
	div.append(jsonDiv);
	div.append(htmlDiv);

	div.delegate( "#jsonBtn", "click", function() {
		$("#html").hide();
		$("#json").show();
	});
	
	div.delegate("#htmlBtn", "click", function() {
		$("#html").find("form").remove();
		$("#html").append(build(JSON.parse($("#jsonText").val())));
		
		$("#html").show();
		$("#json").hide();
	});
	
	addEditPanelTab("formTab","Form",div);
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




/************* Copy from business.jsp *****************************************/
var ARRAY = [];
var EMPTY = ARRAY[-1];
var LINE_BREAK = '[n]';

function deleteDiv(event){
	deleteItem(event,'div');
}

function deleteTd(event){
	deleteItem(event,'td');
}

function deleteTr(event){
	deleteItem(event,'tr');
}

function deleteTable(event){
	deleteItem(event,'table');
}

function deleteItem(event,tag){
	var item = $(event.currentTarget).closest(tag);
    item.remove();
}

function init(){
	$("#dialog").dialog({
		resizable: false,
		height:500,
		width:500,
		autoOpen: false,
		buttons: {
			Cancel: function() {
			  $(this).dialog( "close" );
			}
		}
	});
	
	if(result && result.title){
		$("#title").text(result.title);
	}
	
	if(htmlCode){
		$("#html").html(htmlCode);
	}
	
	if(json){
		$("#main").append(build(json));
	}
}

function build(json){
	if(utils.isString(json)){
		return $("<span />").text(json);
	}else if($.isPlainObject(json)){
		if(json.html){
			return $(json.html);
		}
	}else if($.isArray(json)){
		var type = json[0];
		if(utils.isString(type)){
			return buildOne(json);
		}else {
			var comps = [];
			$.each(json,function(){
				var ccs = build(this);
				if($.isArray(ccs)){
					comps = comps.concat(css);
				}else {
					comps.push(ccs);
				}
			});
			
			return comps;
		}
	}
	
	return null;
}

function buildOne(json){
	var type = json[0];
	
	if(type === 'tableBlock'){
		return TableBlock(json);
	}else if(type === 'tableList'){
		return TableList(json);
	}else if(type === 'menu'){
		return Menu(json);
	}else if(type === 'error'){
		return Error(json);
	}else if(type === 'dialog'){
		appendToDialog(json);
		return null;
	}else if(type === 'form'){
		var options = readForm(json);
		pt(ts(options));
		var ft = new FormTable();
		ft.init(options);
		
		return ft.form;
	}else if(type === 'a'){
		return buildA(json);
	}else if(type === 'img'){
		return buildImg(json);
	}else if(type === 'button'){
		return buildButton(json);
	}else if(type === 'div'){
		return buildDiv(json);
	}else if(type === 'span'){
		return buildSpan(json);
	}else if(type === 'input'){
		return buildInput(json);
	}else if(type === 'bind'){
		return buildBind(json);
	}
}

function appendToDialog(json){
	$("#dialogPanel").empty();
	$("#dialogPanel").append(build(json[1]));
	
	/**
	** Dialog options
	**/
	if(json.length >= 3){
		var opts = json[2];
		var dopts = {};
		dopts.buttons = {};

		for(x in opts){
			if(x === 'buttons'){
				var btns = opts.buttons;
				
				for(bn in btns){
					dopts.buttons[bn] = function(){
						eval(btns[bn]);
					};
				}
			}else {
				dopts[x] = opts[x];
			}
		}
		
		if(!dopts.buttons.Cancel){
			dopts.buttons.Cancel = function(){
				$(this).dialog( "close" );
			};
		}
		
		$("#dialog").dialog(dopts);
	}
	$("#dialog").dialog("open");
}

function ajax(data,doneFun){
	$.ajax('/business/ajax.htm',{
		data:data,
		dataType:'json',
		type:'POST',
		async:false
	}).done(doneFun?doneFun:function(result,status){
		var js = '/business/js.htm?bid='+data.bid+'&fid='+data.fid;
		
		$.getScript(js,function(){
			if(result && result.json){
				$("#main").append(build(result.json));
			}
		});
	});
}

function ajaxForm(formId,doneFun){
	var form = $("#"+formId);
	var reqDatas = readFormDatas(form);

	ajax(reqDatas,doneFun);
}

function readFormDatas(form) {
	var inputs = form.find(":input");

	var reqDatas = {};
	$.each(inputs, function() {
		if ($(this).attr("type") == "radio") {
			if (!this.checked) {
				return;
			}
		}
		
		var key = $(this).attr("name");
		var oldVal = reqDatas[key];
		if(reqDatas[key]){
			var val = reqDatas[key];
			
			if(!$.isArray(val)){
				val = [val];
				reqDatas[key] = val;
			}
			val.push($(this).val());
		}else {
			reqDatas[key] = $(this).val();
		}
	});

	return reqDatas;
}

function addChildren(parent,json){
	if($.isArray(json)){
		parent.append(build(json));
	}else if($.isPlainObject(json)){
		parent.html(json.html);
	}else {
		parent.text(json);
	}
	return parent;
}

function buildA(json){
	var a = $("<a />");
	
	addChildren(a,json[1]);
	
	var reqs = json[2];
	var href = "/business/business.htm?";
	if(utils.isString(reqs)){
		href += reqs;
	}else if($.isPlainObject(reqs)){
		if(!reqs.bid){
			reqs.bid = data.bid;
		}
		
		var s = "";
		$.each(reqs,function(k,v){
			if(s.length > 0){
				s += "&";
			}
			s += k+"="+v;
		});
		href += s;
	}
	a.attr("href",href);
	return a;
}

function buildInput(json){
	var input = $("<input />");
	var type = json[1];
	type = type&&type.length>0?type:'text';
	var name = json[2];
	var val = json[3];
	
	return input.attr("type",type).attr("name",name).val(val);
}

function buildButton(json){
	var bnt = $("<input type='button' />");
	bnt.val(json[1]);
	bnt.attr("onclick",json[2]);

	return bnt;
}

function buildImg(json){
	var img = $("<img />");
	img.attr("src",json[1]);
	
	return img;
}

function buildDiv(json){
	var div = $("<div style='padding:3px 5px' />");
	
	for(i in json){
		if(i > 0){
			addChildren(div,json[i]);
		}
	}
	
	return div;
}

function buildSpan(json){
	var span = $("<span style='margin-right:5px;' />");

	for(i in json){
		if(i > 0){
			addChildren(span,json[i]);
		}
	}
	
	return span;
}

function Error(json){
	var err = $("<div class='error'/>");
	
	if(json.length > 1){
		addChildren(err,json[1]);
	}
	return err;
}

function Menu(json){
	var menu = $("<p class='menu' />");
	
	if($.isArray(json) && json.length > 1){
		var menuList = json[1];
		var bid = menuList[0];
		
		if(utils.isString(bid) && $.trim(bid).length > 0){
			bid = 'bid='+bid;
		}else bid = '';
		
		var href = '/business/business.htm?'+bid;
		for(var i = 1; i < menuList.length; i++){
			var opts = menuList[i];
			
			var a = $("<a />").appendTo(menu);
			if(utils.isString(opts)){
				a.attr('href',href);
				a.text(opts);
			}else if($.isPlainObject(opts)){
				for(var x in opts){
					a.text(x);
					
					var tail = opts[x];
					if(utils.isString(tail)){
						a.attr('href',href+tail);
					}else if($.isNumeric(tail)){
						a.attr('href',href+'&fid='+tail);
					}
				}
			}
		}
	}
	return menu;
}

/**
 * 个人资料详情列表
 * **
['tableList',
 	['账号','密码','真实姓名','Email','手机号码','操作'],
 	each(accounts){[this.account,this.password,this.personal_profile.real_name,
 	                this.personal_profile.email,this.personal_profile.mobile,
 	                {html:'<a href="/business/business2.htm?bid='+bid+'&fid=4&id='+this.id+'">修改</a>
 		<a href="/business/business2.htm?bid='+bid+'&fid=6&id='+this.id+'">查看</a>'}]}
]*/
function TableList(json){
	var table = $("<table class='tableList' cellpadding='0' cellspacing='1' />");
	
	var tr = $("<tr />").appendTo(table);
	$.each(json[1],function(){
		addChildren($("<th class='header' />"),this).appendTo(tr);
	});

	$.each(json[2],function(){
		tr = $("<tr />").appendTo(table);
		$.each(this,function(){
			addChildren($("<td />"),this).appendTo(tr);
		});
	});
	
	return table;
}

function addRow(table,datas){
	var tbody = table.children("tbody");
	if(tbody == null || tbody == undefined){
		tbody = table;
	}
	
	var tr = $("<tr />").appendTo(tbody);
	refreshRow(tr,datas);
}

function refreshRow(tr,datas){
	tr.empty();
	$.each(datas,function(){
		addChildren($("<td />"),this).appendTo(tr);
	});
}

function removeRow(tr){
	tr.remove();
}

function TableBlock(json){
	var table = $("<table class='tableBlock' cellpadding='0' cellspacing='0' />");
	
	$.each(json[1],function(){
		var tr = $("<tr />").appendTo(table);
		addChildren( $("<td class='label' />"),this[0]).appendTo(tr);
		addChildren($("<td />"),this[1]).appendTo(tr);
	});
	
	return table;
}

function readForm(json){
	var _ = this;
	var source = json;
	
	var tag = 'form';
	var htmlOption = {tag:'form'};
	
	var prefix = null;
	var inputs = null;
	
	this.read = function(){
		_.readHtmlOption();
		_.readInputs();
	};
	
	this.readHtmlOption = function(){
		if($.isArray(source) && source.length >= 2){
			var obj = source[1];

			if($.isPlainObject(obj)){
				utils.copy(obj,htmlOption,null);
			}else if(utils.isString(obj)){
				htmlOption.id = obj+'.'+tag;
				prefix = obj;
			}
		}
	};
	
	this.readInputs = function(){
		inputs = [];
		if($.isArray(source) && source.length >= 3){
			var obj = source[2];
			if($.isArray(obj)){
				$.each(obj,function(){
					var input = readInput(this,prefix);
					inputs.push(input);
				});
			}
		}
	};
	
	_.read();
	htmlOption.inputs = inputs;
	return htmlOption;
}

function readInput(opts,prefix){
	var _ = this;
	var input = {};
	
	this.read = function (){
		var obj = opts;
		if($.isArray(obj)){
			if($.isArray(obj[0])){
				input = [];
				
				$.each(obj,function(){
					input.push(readInput(this,prefix));
				});
			}else {
				input.newLine = _.newLine();
				input.name = _.readName();
				input.label = _.readLabel();
				input.type = _.readType();
				input.value = _.readValue();
				
				var others = _.readOthers();
				if(others){
					input.events = _.readEvents(others);
				}
			}
		}else if($.isPlainObject(obj)){
			input = {
					name:obj.name?obj.name:obj.n,
					label:obj.label?obj.label:obj.l,
					type:obj.type?obj.type:obj.t,
					value:obj.value?obj.value:obj.v,
					newLine:obj.newLine?obj.newLine:obj.nl
			};
		}
	};
	
	this.readEvents = function(defOpts){
		if(!defOpts)return EMPTY;
		
		var events = {};
		var eventNames = ['change','click','dbclick','blur','focus'];
		$.each(eventNames,function(){
			if(defOpts[this]){
				events[this] = defOpts[this];
				defOpts[this] = EMPTY;
			}
		});
		
		return events;
	};
	
	this.readType = function (){
		var type = opts[2];
		if(!type){
			type = 'text';
		}else if($.isArray(type)){
			var topts = {};
			topts.name = type[0];
			
			var list = type.length >= 2?type[1]:null;
			if(topts.name == 'select' || topts.name == 'radio'){
				topts.options = list;
				
				if(type.length >= 3 && $.isPlainObject(type[2])){
					var defOpts = type[2];
					topts.events = _.readEvents(defOpts);
					utils.copy(defOpts,topts,['options']);
				}
			}else if(topts.name == 'group'){
				if($.isArray(list)){
					topts.inputs = [];
					$.each(list,function(){
						topts.inputs.push(readInput(this,input.name));
					});
				}
			}else if(topts.name == 'list'){
				topts.header = type[1];
				topts.inputs = [];
				$.each(type[2],function(idx,val){
					topts.inputs[idx] = [];
					$.each(val,function(){
						topts.inputs[idx].push(readInput(this,input.name));
					});
				});
			}
			
			type = topts;
		}
		return type;
	};
	
	this.readName = function (){
		var name = opts[0];
		if(name && name.slice(0,1) == '$'){
			return name.slice(1);
		}
		
		return prefix?prefix+'.'+name:name;
	};
	
	this.readLabel = function (){
		return opts[1];
	};
	
	this.readValue = function (){
		return opts[3];
	};
	
	this.readOthers = function (){
		if(opts.length >= 4){
			return opts[4];
		}
		
		return null;
	};
	
	this.newLine = function (){
		var last = opts[opts.length-1];
		if(last == LINE_BREAK){
			return opts.pop();
		}
		return opts[-1];
	};
	
	_.read();
	return input;
}

function apt(str){
	var txt = $("#print").text();
	pt(txt?txt+str:str);
}

function pt(str){
	if($.isPlainObject(str)){
		str = ts(str);
	}
	$("#print").text(str);
}




/*********** Metadata method ***************/
function ajaxSaveUCType(data, doneFun) {
	$.ajax('/uctype/save.htm', {
		data : {uctype:ts(data)},
		dataType : 'json',
		type : 'POST',
		async : false
	}).done(doneFun ? doneFun : function(result, status) {
		//alert("result : " + ts(result));
		//addRow($("#"));
	});
}

function ajaxSaveMetadata(metadata){
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
