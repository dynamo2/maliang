<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
        <script src="../js/angular.js"></script>
		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../js/jquery-ui.min.js"></script>
		<script src="../js/jquery.simulate.js"></script>
		
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
		
    </head>
    <body>
		<script>
		var business = ${resultJson};
		var basicEditerDialog = null;
		var workflowEditerDialog = null;
		var blockEditerDialog = null;
		
		$(function(){
			init();
		});
		
		function refresh(json){
			business = json;
			init();
		}
		
		function init(){
			initBasic();
			initWorkflows();
			initBlocks();
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
			$("#previewLink").attr("href","/business/business.htm?bid="+business.id);

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
				width:1000,
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
			
			$("#blockTab").tabs();
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
				width:1000,
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
			
			$("#workflowTab").tabs();
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
			$("#step").val(flow.step);
			$("#workflowName").val(flow.name);
			$("#code").val(flow.code);
			$("#response").val(flow.response);
			$("#requestType").val(flow.requestType);
			$("#javaScript").val(flow.javaScript);
			$("#ajax").val(flow.ajax);
			
			$("#previewFlowLink").prop("href","/business/business.htm?bid="+business.id+"&fid="+flow.step);
		}
		
		function deleteBusiness(){
			$.ajax('/business/delete.htm?id='+business.id).done(function(result,status){
				$("#refreshMainLink").simulate("click");
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
			<label>Workflow</label>
			<input type="button" value="新增" onclick="showWorkflowEditer({});" />
		</div>
		<div id="workflowDiv"></div>

		
		<!-- NEW CODE -->
		<style>
		.title {
			padding:15px 10px;
			border-bottom:2px #77C9FF solid;
			width:800px;
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
			width:930px;
			height:593px;
		}
		
		#workflowEditerDialog input[type='text'],
		#blockEditerDialog input[type='text'] {
			width:200px;
		}
		
		#workflowTab,
		#blockTab {
			border:0px;
			padding:0px;
		}
		
		#workflowDiv,
		#blockDiv {
			padding:15px;
		}
		
		#workflowDiv input[type='button'],
		#blockDiv input[type='button'] {
			margin:5px;
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
			<form id="blockEditForm">
				<div id="blockTab">
					<ul>
						<li id="nameDivLink"><a href="#nameDiv">Name</a></li>
						<li id="blockCodeDivLink"><a href="#blockCodeDiv">Code</a></li>
					</ul>
					<div id="nameDiv">
						<input type="hidden" name="business.blocks.id" id="blockId" />
						<div>
							<label>名称:</label>
							<input type="text" name="business.blocks.name" id="blockName" value="" />
						</div>
						<div>
							<label>类型:</label>
							<input type="radio" name="business.blocks.type" value="1" />code
							<input type="radio" name="business.blocks.type" value="2" />html
						</div>
					</div>
					<div id="blockCodeDiv">
						<textarea id="blockCode" name="business.blocks.code"></textarea>
					</div>
				</div>
			</form>
		<div>
		
		<div id="workflowEditerDialog" title="Workflow Editer">
			<form id="workflowEditForm">
				<div id="workflowTab">
					<ul>
						<li id="stepDivLink"><a href="#stepDiv">Step</a></li>
						<li id="requestTypeDivLink"><a href="#requestTypeDiv">RequestType</a></li>
						<li id="codeDivLink"><a href="#codeDiv">Code</a></li>
						<li id="responseDivLink"><a href="#responseDiv">Response</a></li>
						<li id="javaScriptDivLink"><a href="#javaScriptDiv">JavaScript</a></li>
						<li id="ajaxDivLink"><a href="#ajaxDiv">Ajax</a></li>
					</ul>
					<div id="stepDiv">
						<input type="hidden" name="business.workflows.id" id="id" />
						<p><a id="previewFlowLink" href="www.sohu.com" target="_blank">预览</a></p>
						<div>
							<label>step:</label>
							<input type="text" name="business.workflows.step" id="step" value="" />
						</div>
						<div>
							<label>名称:</label>
							<input type="text" name="business.workflows.name" id="workflowName" value="" />
						</div>
					</div>
					<div id="requestTypeDiv">
						<textarea id="requestType" name="business.workflows.requestType"></textarea>
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
					<div id="ajaxDiv">
						<textarea id="ajax" name="business.workflows.ajax"></textarea>
					</div>
				</div>
			</form>
		<div>
	</body>
</html>