<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>

<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="expires" content="0">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">

		<!-- jquery -->
		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		<script src="../../js/jquery.layout-latest.js"></script>
		<script src="../js/jquery.simulate.js"></script>
		
		<!-- ace -->
		<script src="/static/ace/src-noconflict/ace.js"></script>
		<!-- tether -->
		<script src="/static/tether-1.3.3/js/tether.js"></script>
		<!-- bootstrap -->
		<script src="/static/bootstrap/3.3.7/js/bootstrap.js"></script>
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		<script src="../html/business/tianma.js"></script>
		
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/>
		<link href="/static/bootstrap/3.3.7/css/bootstrap.css" rel="stylesheet" type="text/css" />
		
    </head>
    <body>
    	
   		<div class="ui-layout-center">
			<h1 id="title">代码编辑器 <button onclick="run();">执行</button></h1>
			<form id="codeForm">
	   			<textarea id="code" name="code"></textarea>
	   		</form>
	   		<div id="editor" style="width:100%;height:500px;float:left;margin-right:15px;"></div>
		</div>
		
		<div class="ui-layout-south">
			<div>
		   		<ul id="outputTab" class="nav nav-tabs">
				    <li class="active"><a href="#html" data-toggle="tab">html</a></li>
				    <li><a href="#json" data-toggle="tab">json</a></li>
				</ul>
				<div id="outputTabContent" class="tab-content">
				    <div id="html" class="tab-pane fade in active" style="width:100%;height:300px;padding:10px;"></div>
				    <div id="json" class="tab-pane fade" style="width:100%;height:300px;"></div>
				</div>
	   		</div>
		</div>
		
		<script>
		var editor = null;
		var showResult = null;
		var data = {};
		$(function(){
			$("body").layout({ 
				applyDefaultStyles:true
			});
			
			editor = ace.edit("editor");
			editor.session.setMode("ace/mode/javascript");
			
			$("#code").hide();
			$('#outputTab').tab('show');
			
			showResult = ace.edit("json");
			showResult.session.setMode("ace/mode/javascript");
			showResult.getSession().setUseWrapMode(true);
		});
		
		function run(){
			$("#code").val(editor.getValue());
			
			$.ajax({
			    cache:true,
			    type:"POST",
			    dataType : 'json',
			    url:'/metadata/code2.htm',
			    data:$("#codeForm").serialize(),
			    async:false,
			    success:function(result,status){
			    	//$("#result").val(ts(result.result));
			    	//var val = JSON.stringify(result.result, null, 4);
			    	//showResult.setValue(val);
			    	
			    	showResult.setValue(result.result);
			    	try {
			    		data.responce = JSON.parse(result.result);
			    	}catch(e){
			    		data.responce = result.result;
			    	}
			    	html();
			    }
			});
		}
		
		function tableList(list,cls){
			
			function readHead(){
				var heads = [];
				$.each(list,function(){
					if($.isPlainObject(this)){
						for(var k in this){
							var isRepeat = false;
							
							$.each(heads,function(){
								if(k == this){
									isRepeat = true;
									return;
								}
							});
							
							if(!isRepeat){
								heads.push(k);
							}
						}
					}
				});
				return heads;
			}
			
			function createTHead(heads,thead){
				if(heads.length > 0){
					var tr = $("<tr />").appendTo(thead);
					$.each(heads,function(){
						var th = $("<th />").text(this).addClass(this).appendTo(tr);
					});
				}
			}
			
			var table = $("<table class='table' />").addClass(cls);
			
			var thead = $("<thead />").appendTo(table);
			var tbody = $("<tbody />").appendTo(table);
			
			var heads = readHead();
			createTHead(heads,thead);
			
			$.each(list,function(){
				var tr = $("<tr />").appendTo(tbody);
				
				var d = this;
				if(heads.length > 0){
					$.each(heads,function(){
						valColumn(d[this],$('<td />').appendTo(tr));
					});
				}else {
					valColumn(d,$('<td />').appendTo(tr));
				}
				
			});
			
			return table;
		}
		
		function html(){
			$("#html").empty();
			
			var table = createElement(data.responce,'table-bordered');
			table.appendTo($("#html"));
		}
		
		function createElement(obj,tableCls){
			if($.isArray(obj)){
				return tableList(obj,tableCls);
			}
			
			if($.isPlainObject(obj)){
				return rows(obj);
			}
			return $("<p />").text(obj);
		}
		
		
		function rows(obj){
			var rows = [];
			for(var k in obj){
				var row = $('<div class="row">');
				
				if(k.charAt(0) == '$'){
					$('<div class="col-md-12"></div>').text(obj[k]).appendTo(row);
				}else {
					$('<div class="col-md-2"></div>').text(k).appendTo(row);
					valColumn(obj[k],$('<div class="col-md-10" />').appendTo(row));
				}
				
				rows.push(row);
			}
			return rows;
		}
		
		function valColumn(val,col){
			if(val == null || val == undefined){
				val = "";
			}
			
			var tt = createElement(val,'');
			if(tt == null){
				col.text(val);
			}else {
				col.append(tt);
			}
		}
		</script>
		<style>
		form table td {
			padding:3px 10px;
			border-bottom:1px dashed #ccc;
		}
		
		form table .label {
			text-align:right;
		}
		
		form table .hidden {
			display:none;
		}
		
		form table td div {
			margin:5px;
		}
		
		form table td label {
			margin-left:3px;
			margin-right:10px;
		}
		
		td {
			word-break:break-all; 
			word-wrap:break-word;
			text-overflow:ellipsis;
			overflow:hidden;
			min-width:60px;
			max-width:900px;
		}
		
		.tableBlock {
			min-width:400px;
		}
		
		.tableBlock td {
			padding:8px 10px;
			border-bottom:1px dashed #ccc;
			max-width:700px;
		}
		
		.tableBlock .label {
			text-align:right;
			font-weight:bold;
			vertical-align:top;
		}
		
		.tableBlock img {
			max-width:200px;
			max-height:200px;
		}
		
		.tableList {
			background-color:#ccc;
			width:100%;
		}
		
		.tableList td,.tableList th{
			padding:8px 10px;
			background-color:#fff;
			min-width:150px;
			border:0px;
		}
		
		.tableList a{
			margin-left:10px;
		}
		
		.tableList input{
			max-width:80px;
		}
		
		#title {
			border-bottom:2px solid #AFE2E4;
			width:600px;
			padding-left:15px;
			padding-bottom:10px;
			font-size:20px;
			font-weight:bold;
		}
		
		#main {
			margin:30px;
		}
		
		.menu a {
			margin:10px;
		}
		
		.error {
			border:0px solid red;
			margin-top:30px;
			margin-left:200px;
			font-size:20px;
			font-weight:bold;
			color:red;
		}
		</style>
		
		
	</body>
	
	
	<!-- 

pdb.Order.aggregate([
    {
        $unwind:'$buyItems'
    },
    {
        $group:{
            _id:'$buyItems.product',
            count:{$sum:1}
        }
    },
    {
        $sort:{
            count:1-2
        }
    },
    {
        $limit:3
    },
    {
        $lookup:{
          from: 'Product',
          localField: '_id',
          foreignField: '_id',
          as: 'ps'
        }
    },
    {
        $unwind:'$ps'
    },
    {
        $project:{
            _id:1,
            count:1,
            name:'$ps.name'
        }
    }
])

 -->
</html>