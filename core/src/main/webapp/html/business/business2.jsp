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
		<script src="../../js/jquery-ui.min.js"></script>
		<link href="../style/jquery-ui.min.css" rel="stylesheet" type="text/css"/> 
		
		<!-- datatables -->
		<script src="../js/jquery.dataTables.js"></script>
		<link href="../style/jquery.dataTables.min.css" rel="stylesheet" type="text/css"/> 
		
		<!-- wysiwyg -->
		<script src="../js/wysiwyg/wysiwyg.js"></script>
		<script src="../js/wysiwyg/wysiwyg-editor.js"></script>
		<script src="../js/wysiwyg/config.js"></script>
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
   		<h1 id="title">完善个人资料</h1>
		<div id="main">
			<div id="html"></div>
		</div>
		
		<textarea id="print" style="width:700px;height:500px;"></textarea>
		
		<div id="dialog">
			<div id="dialogPanel">
		</div>
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

		var json = result.json;
		var htmlCode = result.html;
		var jsUrl = result.jsUrl;
		
		if(jsUrl){
			$.getScript(jsUrl,function(){
				init();
			});
		}else {
			$(function(){
				init();
			});
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
				build(json,$("#main"));
			}
		}
		     
		/*
		$(function(){
			$("#dialog").dialog({
				resizable: false,
				height:500,
				width:500,
				autoOpen: false,
				buttons: {
					"Save": function(){
						$(this).dialog("close");
					},
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
				build(json,$("#main"));
			}
		});*/
		
		function build(json,parent){
			var type = json[0];
			if(utils.isString(type)){
				if(type === 'tableBlock'){
					parent.append(TableBlock(json));
				}else if(type === 'tableList'){
					parent.append(TableList(json));
				}else if(type === 'dialog'){
					appendToDialog(json);
				}else if(type === 'form'){
					var options = readForm(json);
					pt(ts(options));
					
					var ft = new FormTable();
					ft.init(options);
					
					parent.append(ft.form);
				}
			}else {
				$.each(json,function(){
					build(this);
				});
			}
		}
		
		function appendToDialog(json){
			$("#dialogPanel").empty();
			build(json[1],$("#dialogPanel"));
			
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
		
		function ajax(data){
			$.ajax('/business/ajax.htm',{
				data:data,
				dataType:'json',
				type:'POST',
				async:false
			}).done(function(result,status){
				if(result && result.json){
					build(result.json,$("#main"));
				}
			});
		}
		
		function ajaxForm(formId){
			var form = $("#"+formId);
			var reqDatas = readFormDatas(form);
			
			pt(ts(reqDatas));
			
			ajax(reqDatas);
		}
		
		function readFormDatas(form){
			var inputs = form.find(":input");
			
			var reqDatas = {};
			$.each(inputs,function(){
				reqDatas[$(this).attr("name")] = $(this).val();
			});

			return reqDatas;
		}
		
		function build2(json){
			var type = json[0];
			if(utils.isString(type)){
				if(type === 'a'){
					return buildA(json);
				}else if(type === 'img'){
					return buildImg(json);
				}else if(type === 'button'){
					return buildButton(json);
				}else if(type === 'div'){
					return buildDiv(json);
				}else if(type === 'span'){
					return buildSpan(json);
				}
			}else {
				var comps = [];
				$.each(json,function(){
					var ccs = build2(this);
					if($.isArray(ccs)){
						comps = comps.concat(css);
					}else {
						comps.push(ccs);
					}
				});
				
				return comps;
			}
		}
		
		function addChildren(parent,json){
			if(utils.isString(json)){
				parent.text(json);
			}else if($.isArray(json)){
				parent.append(build2(json));
			}else if($.isPlainObject(json)){
				parent.html(json.html);
			}
			return parent;
		}
		
		function buildA(json){
			var a = $("<a />");
			
			addChildren(a,json[1]);
			
			var href = "/business/business2.htm?"+json[2];
			a.attr("href",href);
			return a;
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
								topts.inputs.push(readInput(this,input.name));
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
		
		.tableBlock {
			min-width:400px;
		}
		
		.tableBlock td {
			padding:8px 10px;
			border-bottom:1px dashed #ccc;
		}
		
		.tableBlock .label {
			text-align:right;
		}
		
		.tableList {
			background-color:#ccc;
		}
		
		.tableList td,.tableList th{
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
		
		
	</body>
</html>