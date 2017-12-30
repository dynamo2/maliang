<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
        <script src="../js/angular.js"></script>
		<script src="../js/jquery-2.1.3.min.js"></script>
		
		<!----> 
		<script src="../js/jquery-ui.min.js"></script>
		<script src="../js/jquery.simulate.js"></script>
		<script src="/static/ace/src-noconflict/ace.js"></script>
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/datatables.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/> 
		
		<!-- bootstrap -->
		<script src="/static/bootstrap/4.0/js/bootstrap.js"></script>
		<link href="/static/bootstrap/4.0/css/bootstrap.css" rel="stylesheet" type="text/css"/> 
		 
    </head>
    <body>
    
  <style>
  .ui-tabs-vertical {
      border:0px;
      background:none;
      float:left;
  }
  
  .ui-tabs-vertical .ui-tabs-nav { 
      padding: .2em .1em .2em .2em; 
      float: left; 
      width: 8em;
      background:none;
      border:0px;
  }
  
  .ui-tabs-vertical .ui-tabs-nav li {
      clear: left; 
      width: 100%;
      background:none;
      //border-bottom-width: 1px !important; 
      //border-right-width: 0 !important;
      margin: 0 -1px .2em 0;
      border:0px;
  }
  
  .ui-tabs-vertical .ui-tabs-nav li a {
      display:block;
  }
  
  .ui-tabs-vertical .ui-tabs-nav li.ui-tabs-active { 
      padding-bottom: 0; 
      padding-right: 0.1em; 
      border:0px;
      border-right:2px red solid;
      background:#EEEEEE;
  }
  
  .ui-tabs-vertical .ui-tabs-panel { 
      padding: 1em; 
      float: right; 
      width: 50em;
  }
  </style>
  
		<script>
		var business = ${resultJson};
		var basicEditerDialog = null;
		var workflowEditerDialog = null;
		var blockEditerDialog = null;
		//var aceEditor = null;
		
		$(function(){
			init();
			
			aceEditor.init();
			blockEditor.init();
		});
		
		function refresh(json){
			business = json;
			init();
		}
		
		function init(){
			initBasic();
			initWorkflows();
			initBlocks();
			initHtmlTemplates();
		}
		
		function save(formId){
			var req = readFormDatas($("#"+formId));
			req['business.id'] = business.id;
			
			$.ajax('/business/save.htm',{
				data:req,
				dataType:'json',
				type:'POST',
				async:false
			}).done(function(result,status){
				refresh(result);
			});
		}
		
		function initBasic(){
			$("#idLabel").text(business.id);
			$("#nameLabel").text(business.name);
			$("#uniqueCodeLabel").text(business.uniqueCode);
			$("#projectLabel").text(business.project && business.project.name);
			
			$("#name").val(business.name);
			$("#uniqueCode").val(business.uniqueCode);
			
			//预览
			$("#previewLink").attr("href","/flows/flow.htm?bid="+business.id);

			basicEditerDialog = $("#basicEditerDialog").dialog({
				autoOpen: false,
				modal:true,
				width:800,
				height:600,
				buttons: {
					"Save": function() {
						save("basicEditForm");
						$(this).dialog("close");
					},
					"Cancel": function() {
					  $(this).dialog("close");
					}
				  }
				});
		}
		
		function showBasicEditer(){
			basicEditerDialog.dialog("open");
		}
		
		function initBlocks(){
			if(business && business.blocks){
				$("#blockDiv").empty();
				
				$.each(business.blocks,function(){
					var bnt = $("<input type='button' />");
					bnt.val(this.name);
					
					bnt.on("click",{"block":this},function(event){
						showBlockEditer(event.data.block);
					});
					
					$("#blockDiv").append(bnt);
				});
			}
			
			blockEditerDialog = $("#blockEditerDialog").dialog({
				autoOpen: false,
				modal:true,
				width:'99%',
				height:800,
				buttons: {
					"Save": function() {
						save("blockEditForm");
						$(this).dialog("close");
					},
					"Cancel": function() {
					  $(this).dialog("close");
					}
				  }
				});
			
			//$("#blockTab").tabs();
			
			blockEditerDialog.siblings('div.ui-dialog-titlebar').remove();
			
			$( "#blockTab" ).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
		    $( "#blockTab li" ).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
		}
		
		function initHtmlTemplates(){
			if(business && business.htmlTemplates){
				$("#htmlTemplateDiv").empty();

				$.each(business.htmlTemplates,function(){
					var bnt = $("<input type='button' />");
					bnt.val(this.name);
					
					bnt.on("click",{"id":this.id},function(event){
						htmlTemplate(event.data.id);
					});
					
					$("#htmlTemplateDiv").append(bnt);
				});
			}
		}
		
		function showBlockEditer(block){
			if(!block)block = {};
			
			$("#blockId").val(block.id);
			$("#blockName").val(block.name);
			$("#blockCode").val(block.code);

			$.each($("input[name='business.blocks.type']"),function(){
				if(this.value == 1){
					this.checked = true;
				}
				
				if(this.value == block.type){
					this.checked = true;
				}
			});
			
			blockEditerDialog.dialog("open");
			
			$("#blockCodeDivLink > a").trigger('click');
		}
		
		function initWorkflows(){
			if(business && business.workflows){
				$("#workflowDiv").empty();
				
				$.each(business.workflows,function(){
					var bnt = $("<input type='button' />");
					bnt.val(this.step+'-'+this.name);
					
					bnt.on("click",{"workflow":this},function(event){
						showWorkflowEditer(event.data.workflow);
					});
					
					$("#workflowDiv").append(bnt);
				});
			}
			
			workflowEditerDialog = $("#workflowEditerDialog").dialog({
				autoOpen: false,
				modal:true,
				width:'99%',
				height:800,
				buttons: {
					"Save": function() {
						save("workflowEditForm");
						$(this).dialog("close");
					},
					"Cancel": function() {
					  $(this).dialog("close");
					}
				  }
				});
			
			
			workflowEditerDialog.siblings('div.ui-dialog-titlebar').remove();
			
			//$("#workflowTab").tabs();
			
			$( "#workflowTab" ).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
		    $( "#workflowTab li" ).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
		}

		function loadWorkFlow(id){
			$.ajax('/business/workFlow.htm',{
				data:{id:id},
				dataType:'json',
				type:'POST',
				async:false
			}).done(function(result,status){
				showWorkflowEditer(result);
			});
		}
		
		function showWorkflowEditer(flow){
			refreshWorkflowEditer(flow);
			workflowEditerDialog.dialog("open");
		}
		
		function refreshWorkflowEditer(flow){
			if(!flow)flow = {};
			
			$("#id").val(flow.id);
			$("#flowIDLabel").val(flow.id);
			$("#step").val(flow.step);
			$("#workflowName").val(flow.name);
			$("#code").val(flow.code);
			$("#response").val(flow.response);
			$("#css").val(flow.css);
			$("#javaScript").val(flow.javaScript);
			$("#ajax").val(flow.ajax);
			
			$("#previewFlowLink").prop("href","/flows/flow.htm?bid="+business.id+"&fid="+flow.step);
			$("#codeDivLink > a").trigger("click");
		}
		
		function deleteBusiness(){
			$.ajax('/business/delete.htm?id='+business.id).done(function(result,status){
				$("#refreshMainLink").simulate("click");
			});
		}
		
		function htmlTemplate(id){
			defaultAjaxEdit({
				edit:'/business/htmlTemplate.htm?id='+id,
				save:'/business/save.htm',
				data:function(formDatas){
					formDatas['business.id'] = business.id;
					return formDatas;
				},
				done:function(result,status){
					refresh(result);
				}
			});
		}
		</script>

		<div class="title">
			<label>Basic</label>
			<input type="button" value="编辑" onclick="showBasicEditer();" />
			<input type="button" value="删除" onclick="deleteBusiness();" />
			<a style="display:none;" id="refreshMainLink" href="/business/main.htm" target="_top">刷新</a>
			<a id="previewLink" href="" target="_blank">预览</a>
		</div>
		<div id="basicDiv">
			<div>ID:<label id="idLabel"></label></div>
			<div>名称:<label id="nameLabel"></label></div>
			<div>唯一代码:<label id="uniqueCodeLabel"></label></div>
			<div>所属项目:<label id="projectLabel"></label></div>
		</div>
		
		<div class="title">
			<label>Block</label>
			<input type="button" value="新增" onclick="showBlockEditer({});" />
		</div>
		<div id="blockDiv"></div>
		
		<div class="title">
			<label>HtmlTemplate</label>
			<input type="button" value="新增" onclick="htmlTemplate(null);" />
		</div>
		<div id="htmlTemplateDiv"></div>
		
		<div class="title">
			<label>Workflow</label>
			<input type="button" value="新增" onclick="showWorkflowEditer({});" />
		</div>
		
		<div id="workflowDiv"></div>

		
		<!-- NEW CODE -->
		<style>
		.title {
			padding:15px 10px;
			border-bottom:2px #77C9FF solid;
			width:100%;
		}
		
		.title label {
			font-size:26px;
			font-weight:bold;
			color:#007ACC;
			padding-right:20px;
		}
		
		
		#basicDiv {
			padding:15px;
			font-size:14px;
		}
		
		#basicDiv div{
			padding:5px;
		}
		
		#basicDiv label{
			padding-left:10px;
		}
		
		#workflowEditerDialog textarea,
		#blockEditerDialog textarea {
			width:100%;
			height:0px;
		}
		
		#workflowEditerDialog input[type='text'],
		#blockEditerDialog input[type='text'] {
			width:200px;
		}
		
		#workflowTab,
		#blockTab {
			border:0px;
			padding:0px;
			width:100%;
		}
		
		#workflowDiv,
		#blockDiv,
		#htmlTemplateDiv {
			padding:15px;
		}
		
		#workflowDiv input[type='button'],
		#blockDiv input[type='button'],
		#htmlTemplateDiv input[type='button'] {
			margin:5px;
		}
		
		#formDialog textarea {
			width:100%;
			height:150px;
		}
		
		.ui-tabs .ui-tabs-panel{
			padding:5px 5px;
		}
		</style>
		
		<div id="basicEditerDialog" title="Basic info Editer">
			<form id="basicEditForm">
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

		<div id="blockEditerDialog" title="Block Editer">
			<form id="blockEditForm" class="form-horizontal">
				<div id="blockTab">
					<ul>
						<li id="nameDivLink"><a href="#nameDiv" onclick="blockEditor.hide();">Name</a></li>
						<li id="blockCodeDivLink"><a href="#blockCodeDiv" onclick="blockEditor.edit('blockCode');">Code</a></li>
					</ul>
					<div id="nameDiv">
						<input type="hidden" name="business.blocks.id" id="blockId" />
						<div class="form-group">
							<label class="col-sm-2 control-label">名称:</label>
							<div class="col-sm-10">
								<input type="text" style="width:500px;" class="form-control" name="business.blocks.name" id="blockName" value="" />
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">类型:</label>
							<div class="col-sm-10">
								<label class="radio-inline">
								  <input type="radio" name="business.blocks.type" value="1" /> code
								</label>
								<label class="radio-inline">
								  <input type="radio" name="business.blocks.type" value="2" /> html
								</label>
							</div>
						</div>
					</div>
					<div id="blockCodeDiv">
						<textarea id="blockCode" name="business.blocks.code"></textarea>
					</div>
				</div>
			</form>
			
			<div id="blockEditor" style="height:670px;"></div>
		<div>
		
		<div id="workflowEditerDialog" title="Workflow Editer">
			<form id="workflowEditForm" class="form-horizontal">
				<div id="workflowTab">
					<ul>
						<li id="stepDivLink"><a href="#stepDiv" onclick="aceEditor.hide();">Step</a></li>
						<li id="codeDivLink"><a href="#codeDiv" onclick="aceEditor.edit('code');">Code</a></li>
						<li id="responseDivLink"><a href="#responseDiv" onclick="aceEditor.edit('response');">Response</a></li>
						<li id="javaScriptDivLink"><a href="#javaScriptDiv" onclick="aceEditor.edit('javaScript');">JavaScript</a></li>
						<li id="cssDivLink"><a href="#cssDiv" onclick="aceEditor.edit('css');">CSS</a></li>
						<li id="ajaxDivLink"><a href="#ajaxDiv" onclick="aceEditor.edit('ajax');">Ajax</a></li>
						<p><a id="previewFlowLink" style="margin-left:15px;" href="www.sohu.com" target="_blank">预览</a></p>
					</ul>
					
					<div id="stepDiv">
						<input type="hidden" name="business.workflows.id" id="id" />
						<div class="form-group">
							<label class="col-sm-2 control-label">ID:</label>
							<div class="col-sm-10">
								<input type="text" style="width:500px;" class="form-control" id="flowIDLabel" value="" />
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">step:</label>
							<div class="col-sm-10">
								<input type="text" style="width:500px;" name="business.workflows.step" class="form-control" id="step" value="" />
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">名称:</label>
							<div class="col-sm-10">
								<input type="text" style="width:500px;" name="business.workflows.name" class="form-control" id="workflowName" value="" />
							</div>
						</div>
					</div>
					<div id="codeDiv">
						<textarea id="code" name="business.workflows.code"></textarea>
					</div>
					
					<div id="responseDiv">
						<textarea id="response" name="business.workflows.response"></textarea>
					</div>
					
					<div id="javaScriptDiv">
						<textarea id="javaScript" name="business.workflows.javaScript"></textarea>
					</div>
					<div id="cssDiv">
						<textarea id="css" name="business.workflows.css"></textarea>
					</div>
					<div id="ajaxDiv">
						<textarea id="ajax" name="business.workflows.ajax"></textarea>
					</div>
				</div>
			</form>
			
			<div id="editor" style="height:670px;"></div>
		<div>
		
		
		
		
		
		
		
		
	</body>
	
	<script>
		var aceEditor = {
			bindTextarea:null,
			editor:null,
			init:function(){
				var _ = aceEditor;
				
				_.editor = ace.edit("editor");
				_.editor.setTheme("ace/theme/twilight");
				_.editor.session.setMode("ace/mode/javascript");
				
				_.editor.on("change",_.changeText);
			},
			edit:function(tid){
				var _ = aceEditor;
				_.bindTextarea = $("#"+tid);
				
				_.editor.setValue(_.bindTextarea.val());
				_.bindTextarea.hide();
				_.show();
				_.editor.clearSelection();
				_.editor.focus();
				_.editor.moveCursorTo(0,0);
			},
			changeText:function(){
				var _ = aceEditor;
				if(_.bindTextarea){
					_.bindTextarea.val(_.editor.getValue());
				}
			},
			show:function(){
				$("#editor").show();
				$(".ui-tabs-vertical").css("width","10em");
			},
			hide:function(){
				$("#editor").hide();
				$(".ui-tabs-vertical").css("width","60em");
			}
		};
		
		var blockEditor = {
				bindTextarea:null,
				editor:null,
				init:function(){
					var _ = blockEditor;
					
					_.editor = ace.edit("blockEditor");
					_.editor.setTheme("ace/theme/twilight");
					_.editor.session.setMode("ace/mode/javascript");
					
					_.editor.on("change",_.changeText);
				},
				edit:function(tid){
					var _ = blockEditor;
					_.bindTextarea = $("#"+tid);
					
					_.editor.setValue(_.bindTextarea.val());
					_.bindTextarea.hide();
					_.show();
					_.editor.clearSelection();
					_.editor.focus();
					_.editor.moveCursorTo(0,0);
				},
				changeText:function(){
					var _ = blockEditor;
					if(_.bindTextarea){
						_.bindTextarea.val(_.editor.getValue());
					}
				},
				show:function(){
					$("#blockEditor").show();
					$(".ui-tabs-vertical").css("width","10em");
				},
				hide:function(){
					$("#blockEditor").hide();
					$(".ui-tabs-vertical").css("width","60em");
				}
		};
		
		function initFormDialog(){
			if($("#formDialog").size() == 0){
				var dialog = $('<div id="formDialog" title="编辑"><div id="formDialogContent" /></div>');
				$("body").append(dialog);
				
				dialog.dialog({
					resizable: false,
					height:800,
					width:1000,
					autoOpen: false});
			}
		}

		function showInFormDialog(json,saveFun){
			initFormDialog();
			
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
		
		function ajaxJSONData(url,doneFun) {
			$.ajax(url, {
				dataType : 'json',
				type : 'POST',
				async : false
			}).done(doneFun);
		}
		
		function defaultAjaxEdit(options){
			ajaxJSONData(options.edit,function(result,status){
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
					}).done(options.done?options.done:function(){});
					
					dialog.dialog("close");
				});
			});
		}
	</script>
</html>