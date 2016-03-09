<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="expires" content="0">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">

        <script src="../js/angular.js"></script>
        
        <!-- jquery -->
		<script src="../js/jquery-2.1.4.js"></script>
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
		<!-- datatables -->
		<script src="../js/jquery.dataTables.js"></script>
		<link href="../style/jquery.dataTables.min.css" rel="stylesheet" type="text/css"/> 
		
		<!-- wysiwyg -->
		<script src="../js/wysiwyg/wysiwyg.js"></script>
		<script src="../js/wysiwyg/wysiwyg-editor.js"></script>
		<script src="../js/wysiwyg/config.js"></script>
		<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css">
		<link href="../style/wysiwyg/wysiwyg-editor.css" rel="stylesheet" type="text/css"/>
		
		<!-- tianma -->
		<script src="../js/tianma/component.js"></script>
		<script src="../js/tianma/datatables.js"></script>
		<script src="../js/tianma/form.js"></script>
		<script src="../js/tianma/html.js"></script>
		<script src="../js/tianma/util.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
		<script>
		/**
		input:
			[name,label,type,value,newLine]
			{ name|n:, label|l:, type|t:, value|v:, newLine|nl: }
		**/
		
		var ARRAY = [];
		var EMPTY = ARRAY[-1];
		var LINE_BREAK = '[n]';
		
		/******* 完善个人信息 **************/
		var resultModel = ['form','account.personalProfile',[
		                        //{n:'$fid',t:'hidden',v:'2',nl:false} //以{}形式
								['$fid',,'hidden',2],  //以'$'开头的名称不加前缀prefix
								['realName','真实姓名','\n'],
								['sfz','身份证'],
			            		['email','Email','\n'],
			            		['mobile','手机'],
			            		['birthday','生日','date','\n'],
			            		['address','地址',['group',[
			            			['province','?省',['select',['江苏','浙江']]],
			            			['city','?市',['select',['南京','苏州','杭州']]],
			            			['zone','?区',['select',['鼓楼','秦淮','白下','玄武']]],
			            			['address',,'textarea','\n']]]
			            		,'\n']
		                   ]];
		//['city','?市',['select',['南京','苏州','杭州'],{linkage:{ajax:'',link:'province'}}]],
		//h.form('Account.personalProfile',{ex:[2,3,'email'],bid:'uuuuu',fid:2})
		
		var result = ${resultJson};
		
		//alert("resultModel : " + ts(resultModel));
		
		var json = result.json;
		var htmlCode = result.html;

		$(function(){
			if(result && result.title){
				$("#title").text(result.title);
			}
			
			if(htmlCode){
				$("#html").html(htmlCode);
			}
			
			if(json){
				var inputs = readForm(json);
				pt(ts(inputs));
				
				var ft = new FormTable();
				ft.init(inputs);
				$("#testForm").append(ft.table);
			}
			
			
			/***
			if(resultModel && resultModel.components){
				$.each(resultModel.components,function(){
					buildComponent(this,$("#main"));
				});
			}
			***/
		});
		
		function readForm(json){
			var _ = this;
			var source = json;
			
			var tag = 'form';
			var htmlOption = {tag:_.tag};
			
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
					}else if(isString(obj)){
						htmlOption.id = obj+'.'+_.tag;
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
			return inputs;
		}
		
		function readInput(opts,prefix){
			var _ = this;
			var input = {};
			
			this.read = function (){
				var obj = opts;
				if($.isArray(obj)){
					input.newLine = _.newLine();
					input.name = _.readName();
					input.label = _.readLabel();
					input.type = _.readType();
					input.value = _.readValue();
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
			
			this.readType = function (){
				var type = opts[2];
				if(!type){
					type = 'text';
				}else if($.isArray(type)){
					var topts = {};
					topts.name = type[0];
					
					var list = type.length >= 2?type[1]:null;
					if(topts.name == 'select'){
						topts.options = list;
					}else if(topts.name == 'group'){
						if($.isArray(list)){
							topts.inputs = [];
							$.each(list,function(){
								topts.inputs.push(readInput(this));
							});
						}
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
			}
			
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

		function isString(obj){
			return $.type(obj) === 'string';
		}
		
		function ts(obj){
			return JSON.stringify(obj);
		}
		
		function apt(str){
			var txt = $("#print").text();
			pt(txt?txt+str:str);
		}
		
		function pt(str){
			$("#print").text(str);
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
		
		.list {
			background-color:#ccc;
		}
		
		.list td,.list th{
			padding:8px 10px;
			background-color:#fff;
			min-width:150px;
		}
		
		#title {
			border-bottom:2px solid #AFE2E4;
			width:600px;
			padding-left:15px;
			padding-bottom:10px;
		}
		
		#main {
			margin:30px;
		}
		</style>
		
		<h1 id="title">完善个人资料</h1>
		<div id="main">
			<div id="html"></div>
			<div><form id="testForm" action="/business/business2.htm" method="post" /></div>
			
			<!-- 
			<textarea id="print" style="width:700px;height:500px;"></textarea>
			 -->
		</div>
	</body>
</html>