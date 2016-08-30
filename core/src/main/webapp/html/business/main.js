function BusinessModel(){
	var _ = this;
	var source = null;
	var businessesTree = null;
	var businessRoot = null;
	var businessesTreeDiagram = null;
	var businessMap = null;
	var businessTabMap = null;
	
	this.init = function(){
		_.source = null;
		_.businessesTree = [];
		_.businessRoot = null;
		_.businessesTreeDiagram = null;
		_.businessTabMap = {};
	};
	
	this.get = function(bid){
		return _.businessMap[bid];
	};
	
	this.buildTreeDiagram = function(listSource){
		_.init();
		
		_.source = listSource;
		_.buildBusinessesTree();
		
		_.businessesTreeDiagram = newTreeDiagram();
	};
	
	/**
	 * 构建页面树
	 * ***/
	this.buildBusinessesTree = function(){
		var treeRoot = {key:"所有页面"};
		var treeNodes = [];
		var bMap = {};
		
		treeNodes.push(treeRoot);
		$.each(_.source,function(i,bus){
			var node = {};
			node.key = bus.name;
			node.parent = treeRoot.key;
			node.business = bus;
			
			treeNodes.push(node);
			bMap[bus.id] = bus;
		});
		
		_.businessRoot = treeRoot;
		_.businessesTree = treeNodes;
		_.businessMap = bMap;
	};
	
	
	this.addTab = function (bid) {
		var bus = _.get(bid);
		
		var tabId = bid;
		var iframeId = bid+"Frame";

		var tab = $("<div id='"+tabId+"' />");
		var iframe = $('<iframe id="'+iframeId + '" style="width:100%;height:800px;" />');
		iframe.attr("src","/business/edit.htm?id="+bid);
		var mdName = bus.name;
		
		$("#businessPanel").find(".ui-tabs-nav").append('<li id="'+tabId+'Link"><a href="#' + tabId + '">' + mdName + '</a><span class="ui-icon ui-icon-close" role="presentation">Remove Tab</span></li>');
		$("#businessPanel").append(tab.append(iframe));
		$("#businessPanel").tabs("refresh");
		
		_.businessTabMap[bid] = tab;
	};
	
	this.removeTab = function(bid){
		$("#"+bid).remove();
		
		_.businessTabMap[bid] = undefined;
	};
	
	this.openTab = function(bid){
		if(!_.isExistsTab(bid)){
			_.addTab(bid);
		}
		
		_.showTab(bid);
	};
	
	this.showTab = function(bid){
		$("#businessPanel").find("a[href='#"+bid+"']").click();
	};
	
	this.isExistsTab = function(bid){
		return _.businessTabMap && _.businessTabMap[bid];
	};
}

function newTreeDiagram() {
	var G_Make = go.GraphObject.make;
	var _ = this;
	var yellowgrad = G_Make(go.Brush, "Linear", {
		0 : "rgb(254, 201, 0)",
		1 : "rgb(254, 162, 0)"
	});

	var myDiagram = G_Make(go.Diagram, "businessesDiagram", {
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
		return true;
	};
	
	this.menuText = function(txt){
		return G_Make(go.TextBlock, 
				{ text: txt, margin:5,font : "13px Helvetica"});
	};
	
	
	
	/**
	 * 右键菜单
	 * **/
	var contextMenu = G_Make(go.Adornment, "Vertical", 
		G_Make("ContextMenuButton",_.menuText("打开"),{
			click : function(e, obj) {
				alert("打开");
			}
		},new go.Binding("visible", "", _.isMetadata).ofObject()
    ),G_Make("ContextMenuButton",_.menuText("预览"),{
			click : function(e, obj) {
				var node = obj.diagram.selection.first();
				var bid = node.data && node.data.business && node.data.business.id;
				
				$("#previewIframe").attr("src","/business/business.htm?bid="+bid);
			}
    	},new go.Binding("visible", "", _.isMetadata).ofObject()
    ), G_Make("ContextMenuButton", _.menuText("新增页面"), {
		click : function(e, obj) {
			//alert("新增页面");
			$("#businessEditerDialog").dialog("open");
		}
	}), G_Make("ContextMenuButton", _.menuText("删除页面"), {
		click : function(e, obj) {
			alert("删除页面");
		}
	},new go.Binding("visible", "", _.isMetadata).ofObject()));
	
	myDiagram.contextMenu = contextMenu;
	myDiagram.nodeTemplate = G_Make(go.Node, {
		doubleClick : function(e, node) {
			var bid = node.data && node.data.business && node.data.business.id;
			if(bid){
				businessModel.openTab(bid);
			}else {
				alert("没有找到该对象：" + bid);
			}
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

	myDiagram.model = new go.TreeModel(businessModel.businessesTree);
	return myDiagram;
}