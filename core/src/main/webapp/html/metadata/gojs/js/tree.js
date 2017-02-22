var projectTreeNodes = null;
$(function(){
	ajaxNoData("/project/ajaxList.htm",
		function(result,status){
			projectTreeNodes = [{key:'root',text:'所有项目',category:'root'}];
			projectTreeNodes = projectTreeNodes.concat(result.result);
			pt(ts(projectTreeNodes));
	});
});

function ajaxNoData(url,doneFun) {
	$.ajax(url, {
		dataType : 'json',
		type : 'POST',
		async : false
	}).done(doneFun);
}

function editTrigger(id,oid){
	defaultAjaxEdit({
		edit:'/metadata/trigger.htm?id='+id+'&omId='+oid,
		save:'/metadata/saveTrigger.htm',
		data:function(formDatas){
			var actions = formDatas && formDatas.trigger && formDatas.trigger.actions;
			
			if(actions && $.isPlainObject(actions)){
				var temp = [];
				$.each(actions,function(){
					temp.push(this);
				});
				formDatas.trigger.actions = temp;
			}
			
			return {metaId:formDatas.id,trigger:ts(formDatas.trigger)};
		}
	});
}



function refreshProjectTree(){
	ajaxNoData("/project/ajaxList.htm",
		function(result,status){
			projectTreeNodes = [{key:'root',text:'所有项目',category:'root'}];
			projectTreeNodes = projectTreeNodes.concat(result.result);
			metadataModel.metadataTreeDiagram.model = new go.TreeModel(projectTreeNodes);
			pt(ts(projectTreeNodes));
	});
}

function readFormDatas (form) {
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

function defaultAjaxEdit (options){
	ajaxNoData(options.edit,function(result,status){
		showInFormDialog(result.json, function() {
			var dialog = $(this);
			
			var reqDatas = readFormDatas(dialog.find("form"));
			if(options.data){
				reqDatas = $.isFunction(options.data)?options.data(reqDatas):reqDatas;
			}
			
			$.ajax(options.save, {
				data : reqDatas,
				dataType : 'json',
				type : 'POST',
				async : false
			}).done(options.done?options.done:refreshProjectTree);
			
			dialog.dialog("close");
		});
	});
}

function isMetadata(nodeData){
	return isCategory(nodeData,"metadata");
}

function isRoot(nodeData){
	return isCategory(nodeData,"root");
}

function isProject(nodeData){
	return isCategory(nodeData,"project");
}

function isCategory(nodeData,name){
	return nodeData && nodeData.category && nodeData.category == name;
}

var treeContextMenu = {
		openMetadataTab:function(e, obj) {
			var id = obj.diagram.selection.first().data.key;
			metadataModel.openMetadataTab(id);
		},

		addProject:function(e,obj){
			treeContextMenu.openEditProjectDialog(null);
		},
		
		editProject:function(e,obj){
			var id = obj.diagram.selection.first().data.key;
			treeContextMenu.openEditProjectDialog(id);
		},
		
		openEditProjectDialog:function(id){
			defaultAjaxEdit({
				edit:'/project/edit.htm?id='+id,
				save:'/project/save.htm',
				data:function(formDatas){
					return {project:JSON.stringify(formDatas)};
				}
			});
		},
		
		addMetadata:function(e,obj){
			var nodeData = obj.diagram.selection.first().data;
			var pid = null;
			if(isCategory(nodeData,"project")){
				pid = nodeData.key;
			}else if(isCategory(nodeData,"metadata")){
				pid = nodeData.parent;
			}
			
			treeContextMenu.openEditMetadataDialog(null,pid);
		},

		openEditMetadataDialog:function(id,pid){
			defaultAjaxEdit({
				edit:'/metadata/edit3.htm?id='+id+'&pid='+pid,
				save:'/metadata/save2.htm',
				data:function(formDatas){
					return {metadata:JSON.stringify(formDatas)};
				}
			});
		},
		
		triggers:function(e,obj){
			var id = obj.diagram.selection.first().data.key;
			
			ajaxNoData('/metadata/triggers.htm?id='+id,function(result,status){
				build(result.json);
			});
		},
		
		moveMetadata:function(e,obj){
			var id = obj.diagram.selection.first().data.key;
			
			defaultAjaxEdit({
				edit:'/project/toProject.htm?id='+id,
				save:'/metadata/saveMove.htm'
			});
		},
		
		copyMetadata:function(e,obj){
			var id = obj.diagram.selection.first().data.key;
			
			defaultAjaxEdit({
				edit:'/project/toProject.htm?id='+id,
				save:'/metadata/saveCopy.htm'
			});
		},
		
		deleteMetadata:function(e, obj) {
			var diagram = obj.diagram;
			var nodeData = diagram.selection.first().data;
			
			metadataModel.ajaxDelete(nodeData.key);
			
			refreshProjectTree();
		},
		
		addSubproject:function(e,obj){
			var id = obj.diagram.selection.first().data.key;
			
			defaultAjaxEdit({
				edit:'/project/subproject.htm?pid='+id,
				save:'/project/saveSubproject.htm',
				data:function(formDatas){
					return {pid:formDatas.pid,subproject:ts(formDatas.subproject)};
				}
			});
		},
		
		updateSubproject:function(e,obj){
			var id = obj.diagram.selection.first().data.key;
			var pid = obj.diagram.selection.first().data.parent;
			
			defaultAjaxEdit({
				edit:'/project/subproject.htm?id='+id+'&pid='+pid,
				save:'/project/saveSubproject.htm',
				data:function(formDatas){
					return {pid:formDatas.pid,subproject:ts(formDatas.subproject)};
				}
			});
		},
		
		deleteSubproject:function(e, obj) {
			var id = obj.diagram.selection.first().data.key;
			
			ajaxNoData('/project/deleteSubproject.htm?id='+id,function(result,status){
				refreshProjectTree();
			});
		},
		
		openCURDDialog:function(e, obj) {
			var diagram = obj.diagram;
			var nodeData = diagram.selection.first().data;
			
			var dialog = $("<div id='generateDialog' title='生成CURD业务代码'><form>" +
									"<span style='font-weight:bold;margin:15px;margin-top:30px;'>名称</span>" +
									"<input type='text' name='bname' />" +
									"<input type='hidden' name='oid' />" +
								"</form></div>");

			dialog.dialog({
				resizable: false,
				height:400,
				width:500,
				autoOpen: false,
				buttons: {
					"Save": function(){
						var reqData = readFormDatas(dialog.find("form"));
						$.ajax("/generate/all.htm",{
							data:reqData
						}).done(function(r,s){
							alert(r);
						});
						
						$(this).dialog("close");
					},
					Cancel: function() {
					  $(this).dialog( "close" );
					}
				}
			});
			dialog.dialog("open");
			
			var metadata = metadataModel.get(nodeData.key);
			var bname = '';
			if(utils.isString(metadata.label)){
				bname = metadata.label+"管理";
			}else {
				bname = metadata.label.value+"管理";
			}
			
			dialog.find("input[name='oid']").val(nodeData.key);
			dialog.find("input[name='bname']").val(bname);
		}
}

function newMetadatasTreeDiagram() {
	var G_Make = go.GraphObject.make;
	var _ = this;
	var yellowgrad = G_Make(go.Brush, "Linear", {0 : "rgb(254, 201, 0)",1 : "rgb(254, 162, 0)"});
	var bluegrad = G_Make(go.Brush, "Linear", { 0: "#B0E0E6", 1: "#87CEEB" });
	var textStyle = {
		font : "bold 13px Helvetica, bold Arial, sans-serif",
		stroke : "black",
		margin : 5
	};

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
		return isCategory(_.nodeData(o),"metadata");
	};
	
	this.isProject = function(o){
		return isCategory(_.nodeData(o),"project");
	};
	
	this.isSubproject = function(o){
		return isCategory(_.nodeData(o),"subproject");
	};
	
	this.isRoot = function(o){
		return isCategory(_.nodeData(o),"root");
	};
	
	this.isAddProject = function(o){
		return _.isRoot(o) || _.isProject(o);
	};
	
	this.nodeData = function(o){
		return o && o.diagram 
				&& o.diagram.selection 
				&& o.diagram.selection.first() 
				&& o.diagram.selection.first().data;
	}
	
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
			click : treeContextMenu.openMetadataTab
		},new go.Binding("visible", "", _.isMetadata).ofObject()), 
	    G_Make("ContextMenuButton", _.menuText("新增项目"), {
			click : treeContextMenu.addProject
		},new go.Binding("visible", "", _.isAddProject).ofObject()),
		
		G_Make("ContextMenuButton", _.menuText("新增子项目"), {
			click : treeContextMenu.addSubproject
		},new go.Binding("visible", "", _.isProject).ofObject()), 
		G_Make("ContextMenuButton", _.menuText("编辑子项目"), {
			click : treeContextMenu.updateSubproject
		},new go.Binding("visible", "", _.isSubproject).ofObject()), 
		G_Make("ContextMenuButton", _.menuText("删除子项目"), {
			click : treeContextMenu.deleteSubproject
		},new go.Binding("visible", "", _.isSubproject).ofObject()), 
		
		G_Make("ContextMenuButton", _.menuText("编辑项目"), {
			click : treeContextMenu.editProject
		},new go.Binding("visible", "", _.isProject).ofObject()), 
		G_Make("ContextMenuButton", _.menuText("新增对象模型"), 
				{click : treeContextMenu.addMetadata}), 
		G_Make("ContextMenuButton", _.menuText("触发器列表"), 
				{click : treeContextMenu.triggers},
			new go.Binding("visible", "", _.isMetadata).ofObject()),
		G_Make("ContextMenuButton", _.menuText("移动对象模型"), 
			{click : treeContextMenu.moveMetadata},
			new go.Binding("visible", "", _.isMetadata).ofObject()),
		G_Make("ContextMenuButton", _.menuText("复制对象模型"), 
			{click : treeContextMenu.copyMetadata},
			new go.Binding("visible", "", _.isMetadata).ofObject()),
		G_Make("ContextMenuButton", _.menuText("删除对象模型"), {
			click : treeContextMenu.deleteMetadata
		},new go.Binding("visible", "", _.isMetadata).ofObject()),
		G_Make("ContextMenuButton", _.menuText("生成CURD业务"), {
			click : treeContextMenu.openCURDDialog
		},new go.Binding("visible", "", _.isMetadata).ofObject()));
	
	myDiagram.contextMenu = contextMenu;
	
	
	/**
	 * 节点
	 * **/
	myDiagram.nodeTemplate = G_Make(go.Node, {
		contextMenu : contextMenu
	}, G_Make("TreeExpanderButton", {
		width : 14,
		"ButtonBorder.fill" : "whitesmoke",
		"ButtonBorder.stroke" : null,
		"_buttonFillOver" : "rgba(0,128,255,0.25)",
		"_buttonStrokeOver" : null
	}),G_Make(go.Panel, "Auto", {
		position : new go.Point(16, -8)
	}, G_Make(go.Shape, "RoundedRectangle", {
		fill : bluegrad,
		name : "SHAPE"
	}), G_Make(go.TextBlock,textStyle, new go.Binding("text", "text"))));
	
	/**
	 * metadata node
	 * **/
	myDiagram.nodeTemplateMap.add("metadata",
		G_Make(go.Node, "Auto",
			{
				contextMenu : contextMenu,
				doubleClick : function(e, node) {
					metadataModel.openMetadataTab(node.data.key);
				}
			},
			G_Make(go.Panel, "Auto", 
				{position : new go.Point(16, -8)},
				G_Make(go.Shape, "RoundedRectangle",{
					fill: yellowgrad,
					name : "SHAPE"}),
				G_Make(go.TextBlock, textStyle,new go.Binding("text", "text"))
		))
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

	//myDiagram.model = new go.TreeModel(metadataModel.treeNodes());
	myDiagram.model = new go.TreeModel(projectTreeNodes);
	
	return myDiagram;
}


/*
function ajaxSaveProject(data, doneFun) {
	$.ajax('/project/save.htm', {
		data : {project:ts(data)},
		dataType : 'json',
		type : 'POST',
		async : false
	}).done(refreshProjectTree);
}

function ajaxSaveMetadata2(metadata){
	$.ajax("/metadata/save2.htm",{
		data:{metadata:JSON.stringify(metadata)},
		/*
		data:{"metadata":JSON.stringify({
				"id":"5833fcc9af7901929c42fab3",
				"project":{
					"id":"5833fcc9af7901929c42fab2",
					"name":"电商管理系统",
					"key":"EB"
				},
				"fields":[
				     {"id":"556424cabd777e5a5087db0a","name":"名称","type":1},
				     {"id":"56cfb7b7e45900ecf9b71d4a","name":"价格","type":3}
				 ],
				"name":"User",
				"label":"用户"
		})},
		*
		dataType:'json',
		type:'POST',
		success:refreshProjectTree
	});
}
*/