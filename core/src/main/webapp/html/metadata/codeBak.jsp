<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>

<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="expires" content="0">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		
		<link href="../../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/>

		<!-- jquery -->
		<script src="../js/jquery-2.1.3.min.js"></script>
		<script src="../../js/jquery-ui.min.js"></script>
		<script src="../../js/jquery.layout-latest.js"></script>
		<script src="../js/jquery.simulate.js"></script>
		
		<!-- ace -->
		<script src="/static/ace/src-noconflict/ace.js"></script>
		<!-- tether -->
		<script src="/static/tether-1.3.3/js/tether.js"></script>
		<!-- bootstrap
		<script src="/static/bootstrap/3.3.7/js/bootstrap.js"></script>
		 -->
		
		
		
		
		

		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		<script src="../js/tianma/bind.js"></script>
		<script src="../js/tianma/build.js"></script>
		<script src="../html/business/tianma.js"></script>
		
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/>
		<!-- 
		<link href="/static/bootstrap/4.0.0/css/bootstrap.css" rel="stylesheet" type="text/css" />
		 -->
		<link href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" type="text/css" /> 
		
		<script src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js"></script> 
		<script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
    </head>
    <body>
    	
   		<div class="ui-layout-center">
			<h1 id="title">代码编辑器 
				<button onclick="run();">执行</button>
				<button style="margin-left:15px;" onclick="readDBdatas('5adf31cf9f7b032e782aa27e');">Product</button></h1>
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
		
		
		
		<!-- Modal -->
<div id="exampleModalLong" title="新增对象模型">
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
		
		
		function readDBdatas(oid){
			$.ajax({
			    cache:true,
			    type:"POST",
			    dataType : 'json',
			    url:'/metadata/dbDatas.htm',
			    data:{oid:oid},
			    async:false,
			    success:function(result,status){
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
		
		function getDBdata(reqData){
			var code = "pdb."+reqData['__collection_name']+".get('"+reqData['__id']+"')";
			$.ajax({
			    cache:true,
			    type:"POST",
			    dataType : 'json',
			    url:'/metadata/code2.htm',
			    data:{code:code},
			    async:false,
			    success:function(result,status){
			    	var dbdatas = null;
			    	try {
			    		dbdatas = JSON.parse(result.result);
			    	}catch(e){
			    		dbdatas = result.result;
			    	}
			    	
			    	
			    	console.log("dbdatas : " + JSON.stringify(dbdatas));
			    	
			    	/*
			    	BootstrapDialog.show({
			            message: 'dddd'//createElement(dbdatas,'table-bordered')
			        });
			    	*/
			    	
			    	var detailBody = $("#detailModal").find(".modal-body");
			    	detailBody.empty();
			    	detailBody.append(createElement(dbdatas,'table-bordered'));
			    	
			    	$("#detailModal").modal("show");
			    	
			    	
			    	
			    	/*
			    	var tarEle = $("#exampleModalCenter").find(".modal-body");
			    	tarEle.empty();
			    	tarEle.append(createElement(dbdatas,'table-bordered'));
			    	
			    	$("#exampleModalCenter").modal('show');
			    	*/
			    }
			});
		}
		
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
			$("#html").append(createElement(data.responce,'table-bordered'));
		}
		
		function createElement(obj,tableCls){
			if($.isArray(obj)){
				return tableList(obj,tableCls);
			}
			
			if($.isPlainObject(obj)){
				return rows(obj);
			}
			return textColumn(obj);//$("<p class='text-truncate' style='max-width:100px;' />").text(obj);
		}
		
		function textColumn(text){
			if(text == null || text == undefined)text = "";
			var column = $("<p class='text-truncate' data-toggle='popover' />").text(text);
			
			column.popover({
				container:'body',
				placement:'right',
				content:text+""
			});
			
			
			column.on("mouseover",function(){
				console.log("column mouseover");
				column.popover('show');
				
				console.log("column.popover('show');");
			});
			
			column.on("mouseout",function(){
				column.popover('hide');
			});
			
			column.dblclick(function(){
				
				var lock = column.data("popover");
				if(lock === 'show'){
					column.data("popover","hide");
					column.popover('hide');
					
					column.on("mouseover",function(){
						column.popover('show');
					});
					
					column.on("mouseout",function(){
						column.popover('hide');
					});
				}else {
					column.unbind('mouseover');
					column.unbind('mouseout');
					
					column.data("popover","show");
					column.popover('show');
				}
			});
			return column;
		}
		
		
		function rows(obj){
			var rows = [];
			
			var collName = obj && obj['__collection_name']; 
			if(collName && $.trim(collName) != ""){
				var row = $('<div class="row">');
				
				var oid = obj["__id"];
				var column = textColumn("").appendTo(row);
				
				var aid = $("<a href='#' />").text(oid);
				column.append(aid);
				
				aid.click(function(){
					console.log(JSON.stringify(obj));
					getDBdata(obj);
					//$("#detailModal").modal('show');
				});
				
				rows.push(row);
			}else {
				for(var k in obj){
					var row = $('<div class="row">');
					
					if(k.charAt(0) == '$'){
						textColumn(obj[k]).appendTo(row);
					}else {
						$('<div class="col-md-2"></div>').text(k).appendTo(row);
						valColumn(obj[k],$('<div class="col-md-10" />').appendTo(row));
					}
					
					rows.push(row);
				}
			}
			
			
			return rows;
		}
		
		function valColumn(val,col){
			if(val == null || val == undefined){
				val = "";
			}
			
			if($.isArray(val)){
				val = "展开";
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
			//padding:8px 10px;
			border-bottom:1px dashed #ccc;
			//max-width:700px;
			width:auto;
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
			//width:100%;
			width:auto;
		}
		
		.tableList td,.tableList th{
			//padding:8px 10px;
			background-color:#fff;
			//min-width:150px;
			border:0px;
			width:auto;
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
	
	<script>
	;(function($){
	    $.fn.code = function (method) {
	        // 如果第一个参数是字符串, 就查找是否存在该方法, 找到就调用; 如果是object对象, 就调用init方法;.
	        if (methods[method]) {
	            // 如果存在该方法就调用该方法
	            // apply 是吧 obj.method(arg1, arg2, arg3) 转换成 method(obj, [arg1, arg2, arg3]) 的过程.
	            // Array.prototype.slice.call(arguments, 1) 是把方法的参数转换成数组.
	            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
	        } else if (typeof method === 'object' || !method) {
	            // 如果传进来的参数是"{...}", 就认为是初始化操作.
	            return methods.init.apply(this, arguments);
	        } else {
	            $.error('Method ' + method + ' does not exist on jQuery.pluginName');
	        }
	    };
	    // 不把方法扩展在 $.fn.pluginName 上. 在闭包内建个"methods"来保存方法, 类似共有方法.
	    var methods = {
	        /**
	         * 初始化方法
	         * @param _options
	         * @return {*}
	         */
	        init : function (_options) {
	            return this.each(function () {
	                var $this = $(this);
	                var args = $.extend({}, $.fn.code.defaults, _options);
	            })
	        },
	        show : function(){
	            private_methods.show();
	        }
	    };
	    
	    // 私有方法
	    /*
	    function private_methods = {
	        show : function(){
	        	console.log("code show");
	        }
	    }*/
	    
	    // 默认参数
	    $.fn.code.defaults = {
	    };
	})(jQuery);
	
	$(function(){
		$("#myLargeModalLabel111").modal();
	});
	
	// 调用方式
	// $("div").pluginName({...});  // 初始化
	// $("div").pluginName("publicMethod");  // 调用方法
	// $(".selector").pluginName({
//	     text : "hello world!"
	// });
	</script>
	
	<!-- Large modal -->

<style>

</style>
<div class="modal fade bd-example-modal-lg" id="detailModal" tabindex="-1" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
  <div class="modal-dialog  modal-lg" role="document">
    <div class="modal-content modal-lg">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLongTitle"></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>

	
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