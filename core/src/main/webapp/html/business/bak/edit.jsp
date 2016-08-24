<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
        <script src="../js/angular.js"></script>
		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../js/jquery-ui.min.js"></script>
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/datatables.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/> 
		
    </head>
    <body>
    	<style>
    	#businessForm textarea {
    		width:160px;
    		height:25px;
    	}
    	
    	#businessForm input[type=number] {
    		width:50px;
    		height:25px;
    	}
    	</style>
		<script>
		
		$(function(){
			//fieldsDivObj = $("#fieldsDiv");
			
			var inputs = ${resultJson};
			
			//{"name":{"prefix":"business","name":"name","label":"名称","type":"text","value":"editProduct"},"workFlows":{"item-labels":{"step":"step","requestType":"requestType","code":"code","response":"response"},"prefix":"business","name":"workFlows","label":"流程","type":"list","item-prefix":"business.workFlows","value":[{"code":{"prefix":"business.workFlows","name":"code","label":"code","type":"text","value":"addToParams({p2:db.Product.save(request.product),brands:db.Brand.search(),products:db.Product.search()})"},"requestType":{"prefix":"business.workFlows","name":"requestType","label":"requestType","type":"text","value":"{fid:'int',bid:'int'}"},"response":{"prefix":"business.workFlows","name":"response","label":"response","type":"text","value":"{html_template:'<div id='edit_form'><\/div>',contents:[{type:'form',html_parent:'edit_form',action:'',name:'product.edit.form',children:{inputs:[{name:'fid',type:'hidden',value:2},{name:'product.id',type:'hidden'},{name:'product.name',label:'名称',type:'text'},{name:'product.brand',label:'品牌',type:'select',options:each(brands){{key:this.id,label:this.name}}},{name:'product.price',label:'价格',type:'number'}],ul-list:{header:[{name:'name',label:'名称'},{name:'brand',label:'品牌'},{name:'price',label:'价格'},{name:'picture',label:'图片'},{name:'operator',label:'操作'}],data:each(products){{name:{type:'a',href:'/detail.htm?id='+this.id,text:this.name},brand:{type:'a',href:'/detail.htm?id='+this.brand.id,text:this.brand.name},price:{type:'label',text:this.price},picture:{type:'img',src:this.picture},operator:[{type:'a',href:'/edit.htm?id='+this.id,text:'编辑'},{type:'a',href:'/delete.htm?id='+this.id,text:'删除'}]}}}}}]}"},"step":{"prefix":"business.workFlows","name":"step","label":"step","type":"text","value":1},"id":{"prefix":"business.workFlows","name":"id","label":null,"type":"hidden","value":null}}]},"id":{"prefix":"business","name":"id","label":null,"type":"hidden","value":"55657a12bd77fdf857e1b743"}};
			var inputDiv = TM_formBuilder.newInputsDiv(inputs);
			$("#businessForm").append(inputDiv);

			dialogObj = $("#enlargedEditerDialog").dialog({
				autoOpen: false,
				modal:true,
				width:800,
				height:500,
				buttons: {
					"确定": function() {
					  $(this).dialog("close");
					  editInputObj.val($("#enlargedTextarea").val());
					},
					"取消": function() {
					  $(this).dialog("close");
					}
				  }
				});
			
			
			dialogObj = $("#workFlowEditerDialog").dialog({
				autoOpen: false,
				modal:true,
				width:1000,
				height:800,
				buttons: {
					"确定": function() {
						var req = readFormDatas($("#workFlowEditeForm"));
						alert(ts(req));
						
						$(this).dialog("close");
					},
					"取消": function() {
					  $(this).dialog("close");
					}
				  }
				});
			$("#workFlowTab").tabs();
			
			resetDBClick();
			$("#businessForm").find(":input[type='button']").on("click",resetDBClick);
		});
		
		function resetDBClick(){
			$("#businessForm").find(":input").dblclick(enlargedEdit);
			$("#businessForm").find(":input[type='button']").off("dblclick",enlargedEdit);
		}
		
		function enlargedEdit(event){
			$("#enlargedTextarea").val(this.value);
			editInputObj = $(this);
			
			loadWorkFlow('');
			dialogObj.dialog("open");
		}
		
		function loadWorkFlow(id){
			$.ajax('/business/workFlow.htm',{
				data:{id:'579726667b59c29468409c6d'},
				dataType:'json',
				type:'POST',
				async:false
			}).done(function(result,status){
				$("#id").val(result.id);
				$("#step").val(result.step);
				$("#code").val(result.code);
				$("#response").val(result.response);
				$("#requestType").val(result.requestType);
				$("#javaScript").val(result.javaScript);
				$("#ajax").val(result.ajax);
			});
		}
		</script>
		<div style="margin:20px;"><a href="list.htm">列表</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="edit.htm">新增</a></div>
    	<div id="editDiv">
    		<form id="businessForm" action="/business/save.htm" method="post"></form>
		</div>
		
		<div id="enlargedEditerDialog" title="Enlarged editer dialog">
			<textarea id="enlargedTextarea"></textarea>
		<div>
		
		
		
		
		
		<!-- NEW CODE -->
		<style>
		#workFlowEditerDialog textarea {
			width:930px;
			height:593px;
		}
		
		#workFlowEditerDialog #step {
			width:200px;
		}
		
		#workFlowTab {
			border:0px;
			padding:0px;
		}
		</style>
		<div id="workFlowEditerDialog" title="WorkFlow Editer">
			<form id="workFlowEditeForm">
				<div id="workFlowTab">
					<ul>
						<li id="stepDivLink"><a href="#stepDiv">Step</a></li>
						<li id="requestTypeDivLink"><a href="#requestTypeDiv">RequestType</a></li>
						<li id="codeDivLink"><a href="#codeDiv">Code</a></li>
						<li id="responseDivLink"><a href="#responseDiv">Response</a></li>
						<li id="javaScriptDivLink"><a href="#javaScriptDiv">JavaScript</a></li>
						<li id="ajaxDivLink"><a href="#ajaxDiv">Ajax</a></li>
					</ul>
					<div id="stepDiv">
						<input type="hidden" name="id" id="id" />
						<input type="text" name="step" id="step" />
					</div>
					<div id="requestTypeDiv">
						<textarea id="requestType" name="requestType"></textarea>
					</div>
					<div id="codeDiv">
						<textarea id="code" name="code"></textarea>
					</div>
					
					<div id="responseDiv">
						<textarea id="response" name="response"></textarea>
					</div>
					
					<div id="javaScriptDiv">
						<textarea id="javaScript" name="javaScript"></textarea>
					</div>
					<div id="ajaxDiv">
						<textarea id="ajax" name="ajax"></textarea>
					</div>
				</div>
			</form>
		<div>
	</body>
</html>