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