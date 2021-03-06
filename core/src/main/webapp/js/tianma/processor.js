// Inspired by base2 and Prototype
(function(){
var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;

// The base Class implementation (does nothing)
this.Class = function(){};

// Create a new Class that inherits from this class
Class.extend = function(prop) {
var _super = this.prototype;

// Instantiate a base class (but only create the instance,
// don’t run the init constructor)
initializing = true;
var prototype = new this();
initializing = false;

// Copy the properties over onto the new prototype
for (var name in prop) {
// Check if we’re overwriting an existing function
prototype[name] = typeof prop[name] == "function" 
                    && typeof _super[name] == "function" 
                    && fnTest.test(prop[name]) ? (function(name, fn){
                        return function() {
                          var tmp = this._super;
                          
                          // Add a new ._super() method that is the same method
                          // but on the super-class
                          this._super = _super[name];

                          // The method only need to be bound temporarily, so we
                          // remove it when we’re done executing
                          var ret = fn.apply(this, arguments);
                          this._super = tmp;

                          return ret;
                      };
                  })(name, prop[name]): prop[name];
}

// The dummy class constructor
function Class() {
// All construction is actually done in the init method
if ( !initializing && this.init )
this.init.apply(this, arguments);
}

// Populate our constructed prototype object
Class.prototype = prototype;

// Enforce the constructor to be what we expect
Class.prototype.constructor = Class;

// And make this class extendable
Class.extend = arguments.callee;

return Class;
};
})();

function refreshTr(tr,options){
	var tds = $("td",tr);

	if($.isArray(options)){
		for(var i = 0; i < tds.length && i < options.length; i++){
			var td = $(tds[i]);
			var opts = options[i];
			
			td.empty();
			if($.isArray(opts) || $.isPlainObject(opts)){
				td.append(generator.build(opts));
			}else {
				td.text(opts);
			}
		}
	}
}

function log(s){
	console.log(s);
}

function reload(element,newOption){
	return generator.reload(element,newOption);
}

var HTMLGenerator = Class.extend({
	inputTypes:["text","textarea","number2","summernote","password","htmlEditor","hidden","between","radio","date","number","month","week","time","datetime","select","select2","checkbox","button","submit","reset"],
	
	isInput:function(tn){
		return utils.hasName(this.inputTypes,tn);
	},
	
	isButton:function(type){
    	return utils.hasName(['button','submit','reset'],type);
    },
    
    _cache_:{
    	_database:{},
    	add:function(element,option){
    		this._database[element] = option;
//    		console.log('-------- cache add start ------');
//    		console.log('*** element : ' + element.prop('outerHTML'));
//    		console.log('*** option : ' + ts(option));
//    		console.log('-------- cache add end ------');
    	},
    	get:function(element){
    		return this._database && this._database[element]; 
    	},
    	remove:function(element){
    		this._database[element] = undefined;
    	}
    },
    
    reload:function(element,newOption){
    	var option = this._cache_.get(element);
    	option = this.updateOption(option,newOption);
    	if(!option)return;
    	
    	var ele = this.build(option);
    	element.after(ele);
    	element.remove();
    	
    	return ele;
    },
    
    updateOption:function(opts,newOption){
    	if(!opts){
    		return newOption;
    	}
    	
    	if($.isPlainObject(opts) && $.isPlainObject(newOption)){
    		$.each(newOption,function(k,v){
    			opts[k] = v;
    		});
    		
    		return opts;
    	}
    	
    	return newOption;
    },
    
	
	build:function(opts){
		var curr = this;
		
		if($.isArray(opts)){
			return curr.buildArray(opts);
		}
		
		if($.type(opts) === 'string'){
			return $("<span />").text(opts);
		}
		
		var type = opts && opts.type;
		
		if(opts && opts.css){
			if(!opts['class']){
				opts['class'] = '';
			}
			if($.type(opts['class']) != 'string'){
				opts['class'] = JSON.stringify(opts['class']);
			}
			opts["class"] += opts.css;
			
			opts.css = undefined;
		}
		
		if(this.isInput(type)){
			return this.input(opts);
		}
		
		if(type === "HT"){
			return this.htmlTemplate(opts);
		}

		if(type === "html"){
			return this.html(opts);
		}
		
		if(type === 'ajax'){
			//var clone = utils.copy(opts,null,null);
			
			var ele = this.ajax(opts);
			this._cache_.add(ele,opts);
			
			return ele;
		}
		
		if(!type){
			if(opts.html){
				if($.type(opts.html) === 'string'){
					return $(opts.html);
				}
			}
		}
		
		return this.htmlElement(opts);
	},
	
	htmlElement:function(options){
		var type = options && options.type;
		if(type){
			options.tag = type;
			options.type = undefined;
		}
		
		var body = null;
		if(options && options.body){
			body = this.build(options.body);
			options.body = undefined;
		}

		var element = buildHtmlElement(options);
		if(body){
			element.append(body);
		}
		return element;
	},
	
	attr:function(jqObj,opts){
		opts = utils.copy(opts,null,['type','head','body']);
		if(opts && opts.css){
			if(!opts['class']){
				opts['class'] = '';
			}
			if($.type(opts['class']) != 'string'){
				opts['class'] = JSON.stringify(opts['class']);
			}
			opts["class"] += opts.css;
			
			opts.css = undefined;
		}
		
		setHtmlProperties(jqObj,opts);
	},
	
	buildArray:function(opts){
		var curr = this;
		var els = [];
		$.each(opts,function(){
			var ce = curr.build(this);
			if($.isArray(ce)){
				els = els.concat(ce);
			}else {
				els.push(ce);
			}
		});
		return els;
	},
	
	htmlTemplate:function(opts){
		
		
		var ele = $(opts.template);
		var g = this;
		
		$.each(opts,function(k,v){
			if(k === 'type' || k === 'template'){
				return;
			}

			var ve = null;
			if($.isArray(v) || $.isPlainObject(v)){
				ve = g.build(v);
			}else {
				//ve = $("<span />").text(v);
				ve = v;
			}
			
			var tempDiv = ele.find("#htmlTemplate-"+k);
			tempDiv.after(ve);
			tempDiv.remove();
		});
		
		return ele;
	},
	
	html:function(opts){
		var he = null;
		if(opts && opts.head){
			if($.type(opts.head) === 'string'){
				he = $(opts.head);
			}else he = this.build(opts.head);
			
			$("head").append(he);
		}
		
		if(opts && opts.body){
			var be = null;
			if($.type(opts.body) === 'string'){
				be = $(opts.body);
			}else be = this.build(opts.body);
			
			$("body").append(be);
		}
		
		return null;
	},
	
	
	/**
	 * {
	 * 		type:'ajax',
	 * 		data:
	 * }
	 * **/
	ajax:function(opts){
		var ajaxSpan = $("<span model='ajax' />");

		var done = null;
		if(opts && opts.done){
    		done = opts.done;
    		if(!$.isFunction(done)){
    			done = eval(done);
    		}
    	}
		
		$.ajax({
		    cache:true,
		    type:"POST",
		    dataType : 'json',
		    url:'/flows/ajax.htm',
		    data:opts.data,
		    async:false
		}).done(function(result,status){
	    	if(result && result.html){
	    		var ele = generator.build(result.html);
		    	ajaxSpan.append(ele);
	    	}
	    }).done(done?done:function(){});
		
		return ajaxSpan;
	},

	
	input:function(opts){
		/**
	     * {
	     * 	  type:'between',
	     *    name:'',
	     *    input:'',
	     *    vals:[],
	     *    options:
	     * }
	     * 
	     * {type:'between',layout:'vertical',input:'text',name:'ps.price',value:[100,1000]},
	     * ***/
	    this.between = function(opts){
	    	var name = opts && opts.name;
	    	if(name){
	    		name += ".";
	    	}
	    	var rangName = opts && opts.rangName;
	    	if(!($.isArray(rangName) && rangName.length == 2)){
	    		rangName = ['from','to'];
	    	}
	    	
	    	var fromName = rangName[0];
	    	var toName = rangName[1];
	    	var fopts = {
	    		name:name+fromName,
	    		placeholder:'From',
	    		type:opts.input,
	    	};
	    	var topts = {
	    		type:opts.input,
	            name:name+toName,
	            placeholder:'To'
	    	};
	    	
	    	if(opts && opts.value){
	    		if($.isArray(opts.value)){
	    			if(opts.value.length > 0){
	    				fopts.value = opts.value[0];
	    			}
	    			
	    			if(opts.value.length > 1){
	    				topts.value = opts.value[1];
	    			}
	    		}else {
	    			if(opts.value && opts.value[fromName]){
		    			fopts.value = opts.value[fromName];
		    		}
	    			if(opts.value && opts.value[toName]){
	    				topts.value = opts.value[toName];
		    		}
	    		}
	    	}
	    	
	        return [this.input(fopts),this.input(topts)];
	    };
	    
	    if(opts.type === "between"){
	    	return this.between(opts);
	    }
	    
		return TM_formBuilder.newInputElement(opts);
	}
});



var MGenerator = HTMLGenerator.extend({
	build:function(opts){
		var et = opts && opts.type;
		
		if(et === 'ScrollableTable'){
			return this.ScrollableTable(opts);
		}
		
		if(et === 'bootstrapTable'){
			return this.bootstrapTable(opts);
		}

		if(et === 'testTable'){
			return $("<table id='testTable' data-height='428' data-toolbar='#toolbar' data-toggle='table' " +
					"data-url='/static/json/table.json' />" +
					"<tr><th data-field='id'>ID</th><th data-field='name'>Item Name</th><th data-field='price'>Item Price</th></tr>" +
					"</table>");
		}

		if(et === 'table'){
			return this.table(opts);
		}
		
		if(et === 'editTable'){
			return this.editTable(opts);
		}
		
		if(et === "paginate"){
			return this.paginate(opts);
		}
		
		if(et === "page"){
			return this.page(opts);
		}
		
		if(et === "dropdownMenu"){
			return this.dropdownMenu(opts);
		}
		
		if(et === "portlet"){
			return this.portlet(opts);
		}
		
		if(et === "formBody"){
			return this.formBody(opts);
		}
		
		if(et === "form"){
			return this.form(opts);
		}
		
		if(et === "row"){
			return this.row(opts);
		}
		
		if(et === "rows"){
			return this.rows(opts);
		}
		
		if(et === "modal"){
			return this.modal(opts);
		}
		
		if(et === "tab"){
			return this.tab(opts);
		}
		
		if(et === "summernote"){
			return this.summernote(opts);
		}
		
		if(et === "popover"){
			return this.popover(opts);
		}

		return this._super(opts);
	},
	
	
	/**
	 * 
	 * {
    type:'popover',
    modal:'link | button',
    id:'',
    css:'',
    text:'',
    option:{
      content:function(){
        var eles = [
          $("<p><input type='text'></p>"),
          $("<p><input type='text'></p>"),
          $("<p><select><option value='1'>1111</option><option value='2'>2222</option></select></p>"),
          $("<p><input type='button' value='ok' onclick=\"$('#pop').trigger('click')\" /></p>")
        ];
        return eles;
      },
      html:true,
      title:'物流策略'
    }
  }
  
  {
  	type:'popover',
  	id:'',
  	text:'',
  	option:{
  		content:{
  			type:'radio',
  		},
  		title:'物流策略列表'
  	}
  }
	 * **/
	popover:function(option){
		var gb = this;
		
		var modal = option && option.modal;
		if(!modal){
			modal = 'link';
		}
		
		var pop = null;
		if(modal === 'button'){
			pop = $("<button />");
		}else {
			pop = $("<a tabindex='0' role='button' />");
		}
		
		var props = utils.copy(option,null,['type','modal','option']);
		pop.text(option && option.text);
		pop.prop("id",option && option.id);
		pop.addClass(option && option.css);
		
		var popOption = option && option.option;
		var popContent = popOption && popOption.content;
		var popHtml = popOption && popOption.html;
		if(popContent){
			if($.isPlainObject(popContent) || $.isArray(popContent)){
				if(popHtml === null || popHtml === undefined || popHtml === true){
					var popContent = gb.build(popContent); 
					popHtml = true;
				}
			}else if($.type(popContent) === 'string'){
				if(popHtml === null || popHtml === undefined || popHtml === true){
					var fun = eval(popContent);
					if($.isFunction(fun)){
						popContent = fun;
					}
					popHtml = true;
				}
			}
			
			popOption.content = popContent;
			popOption.html = popHtml;
		}
		
		//<script src="http://localhost:8080/static/metronic/theme/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
		$.getScript("/static/metronic/theme/assets/global/plugins/bootstrap/js/bootstrap.min.js",function(){
			pop.popover(popOption);
		});
		
		return pop;
	},
	
	summernote:function(options){
		
		$("head").append($('<link rel="stylesheet" type="text/css" href="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.css">'));
		//$("head").append($('<script src="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.min.js" type="text/javascript"></script>'));
		
		var textarea = $("<textarea name='"+options.name+"' style='width:300px;height:200px;' />");
		var element = $('<div name=summernote_"'+options.name+'" class=".summernote" >'+options.value+'</div>');
		element.show("normal",function(){
			textarea.hide();
			
			$(this).summernote({
				height: 500,
				focus:true
			});
			
			$(this).on('summernote.blur', function() {
				textarea.val($(this).summernote('code'));
			});
		});
		
		
		return [textarea,element];
	},
	
	/**
	 * {
        type:'tab',
        layout:'inline',
        tabs:[
            {
                nav:'宝贝详情',
                content:{
                    type:'div',
                    style:'width:1000px;padding-left:50px;',
                    html:product.description
                }
            },{
                nav:'累计评论',
                content:'累计评论'
            },{
                nav:'专享服务',
                content:'专享服务'
            }
        ]
    }
	 * ***/
	tab:function(options){
		var curr = this;
		var tabs = options && options.tabs;
		var ulNav = $('<ul class="nav nav-tabs" />');
		var divContent = $('<div class="tab-content" />');
		var dateTime = (new Date()).getTime();
		var idx = 1;
		
		this.appendTab = function(tabOpts){
			var cid = tabOpts.contentId;
			if(!cid){
				cid = dateTime+'_'+(idx++);
			}
			
			var liNav = $('<li><a href="#'+cid+'" data-toggle="tab">'+tabOpts.nav+'</a></li>');
			var divPane = $('<div class="tab-pane fade" id="'+cid+'" />');
			this._fillIn(divPane,tabOpts.content);
			
			//active
			if(tabOpts.active){
				liNav.addClass('active');
				divPane.addClass('active in');
			}
			
			ulNav.append(liNav);
			divContent.append(divPane);
		};
		
		if($.isArray(tabs)){
			$.each(tabs,function(){
				curr.appendTab(this);
			});
		}else if($.isPlainObject(tabs)){
			curr.appendTab(tabs);
		}
		
		/**
		 * autoActive
		 * */
		var autoActive = options && options.autoActive;
		if(autoActive == null || autoActive == undefined){
			autoActive = true;
		}
		if(autoActive){
			var active = $('.active.in',divContent);
			if(active.size() == 0){
				var li = $('li:first',ulNav).addClass('active');
				var pane = $('.tab-pane:first',divContent).addClass('active in');
			}
		}
		
		/**
		 * layout:inline
		 * **/
		var inline = options && options.layout && options.layout === 'inline';
		if(inline){
			return $('<div class="tabbable-line" />').append([ulNav,divContent]);
		}

		return [ulNav,divContent];
	},
	
	bootstrapTable:function(options){
		console.log("bootstrapTable : ");
		console.log(options);
		
		var tableOptions = utils.copy(options,null,['type']);
		
		var tt = $("<table />");
		
		if(!tt.bootstrapTable){
			$("head").append($('<link rel="stylesheet" type="text/css" href="/static/bootstrap-table-master/src/bootstrap-table.css"/>'));
			$("head").append("<script src='/static/bootstrap-table-master/src/bootstrap-table.js'></script>");
			$("head").append("<script src='/static/bootstrap-table-master/src/extensions/editable/bootstrap-table-editable.js'></script>");
			$("head").append("<script src='http://rawgit.com/vitalets/x-editable/master/dist/bootstrap3-editable/js/bootstrap-editable.js'></script>");
			$("head").append("<script src='http://rawgit.com/hhurz/tableExport.jquery.plugin/master/tableExport.js'></script>");
			
			$("head").append("<script src='/static/bootstrap-table-master/src/extensions/export/bootstrap-table-export.js'></script>");
			
			
		}
		
		tt.bootstrapTable(tableOptions);
		
		return tt;
	},
	
	/**
	 * {
	 * 		type:'dialog',
	 * 		header:'',
	 * 		body:{
	 * 		},
	 * 		buttons:{
	 * 			'save':fun
	 * 		}
	 * }
	 * **/
	modal:function(options){
		var curr = this;
		
		var $modal = $('<div class="modal fade bs-modal-lg" id="dialog" tabindex="-1" role="dialog" aria-hidden="true">'
				+'<div class="modal-dialog modal-lg">'
					+'<div class="modal-content">'
						+'<div class="modal-header">'
							+'<button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>'
							+'<h4 class="modal-title"></h4>'
						+'</div>'
						
						+'<div class="modal-body">'
						+'</div>'
						
						+'<div class="modal-footer">'
							+'<button type="button" class="btn default" data-dismiss="modal">Close</button>'
						+'</div>'
						
					+'</div>'
				+'</div>'
			+'</div>');

		this.fullIn = function(element,options){
			if(!element)return;
			if(!options)return;
			
			if($.isArray(options) || $.isPlainObject(options)){
				element.append(curr.build(options));
			}else {
				element.text(options);
			}
		};
		
		curr.fullIn($('.modal-title',$modal),options && options.header);
		curr.fullIn($('.modal-body',$modal),options && options.body);
		curr.fullIn($('.modal-footer',$modal),options && options.footer);
		
		if(options && options.buttons){
			var bopts = options.buttons;
			$.each(bopts,function(k,v){
				var bnt = $('<button type="button" class="btn blue" />').text(k);
				
				if($.type(v) == 'function'){
					bnt.click(v);
				}else if($.type(v) === 'string'){
					try {
						bnt.click(eval(v));
					}catch(e){}
				}
				
				$('.modal-footer',$modal).append(bnt);
			});
		}
		
		var newOpts = utils.copy(options,{},['type','header','body','footer']);
		$modal.attr(newOpts);
		$modal.prop(newOpts);
		
		
		
		return $modal;
	},
	
	/*
	 * opts:{
	 * 	type:'page',
	 * 	header:{
	 * 		title:'产品列表',
	 * 		small:'全部产品',
	 * 		breadcrumb:[
	 * 			{text:'Home',href:'home.html',icon:'home'},
	 * 			{text:'Product',href:'product.html',icon:'angle-right'}
	 *		],
	 *		toolbar:{
	 *			type:'dropdownMenu'
	 *		}
	 *	}
	 * }
	 * **/
	page:function(opts){
		var curr = this;
		
		this.header = function(opts){
			var title = $('<h3 class="page-title" />').text(opts.title).append($("<small />").text(opts.small));
			
			var bar = $('<div class="page-bar" />');
			var breadcrumb = this.breadcrumb(opts.breadcrumb); 
			var toolbar = this.toolbar(opts.toolbar);
			
			bar.append(breadcrumb).append(toolbar);
			
			return [title,bar];
		};
		
		this.toolbar = function(opts){
			var div = $('<div class="page-toolbar" />');
			
			if(!opts.type){
				opts.type = "dropdownMenu";
			}
			
			div.append(this.build(opts));
			return div;
		};
		
		this.breadcrumb = function(opts){
			var bmenu = curr.ul_menus(opts);
			bmenu.addClass("page-breadcrumb");
			return bmenu;
		}; 
		
		this.body = function(bopts){
			return this.build(bopts);
		};
		
		var page = $('<div class="page-content" />');
		
		var pHeader = this.header(opts.header);
		var pBody = this.body(opts.body);
		
		return page.append(pHeader).append(pBody);
	},
	
	/**opts:[
	 *	{text:'新增产品',href:'edit.html'},
	 *	{text:'上月报表',href:'edit.html'},
	 *	'divider',
	 *	{text:'上季度报表',href:'edit.html'}
	 *]
	 * **/
	ul_menus:function(opts){
		var ul = $('<ul />');
		
		$.each(opts,function(){
			var li = $("<li />").appendTo(ul);
			if($.type(this) === 'string'){
				li.prop("class",this);
				return;
			}
			
			if(this.icon){
				li.append($('<i class="fa fa-'+this.icon+'" />'));
			}
			if(this.href){
				$('<a href="'+this.href+'">'+this.text+'</a>').appendTo(li);
			}
		});
		return ul;
	},
	
	/**
	 *opts:{
	 *	type:'dropdownMenu',
	 *	text:'更多操作',
	 *	menus:[
	 *		{text:'新增产品',href:'edit.html'},
	 *		{text:'上月报表',href:'edit.html'},
	 *		'divider',
	 *		{text:'上季度报表',href:'edit.html'}
	 *	]
	 *}
	 * **/
	dropdownMenu:function(opts){
		var div = $('<div class="btn-group" />');
		
		
		var bnt = null;
		if(opts && opts.bntType && opts.bntType === 'link'){
			bnt = $('<a class="btn default yellow-stripe dropdown-toggle" href="javascript:;" data-toggle="dropdown"><span class="hidden-480">'+opts.text+' </span><i class="fa fa-angle-down"></i>')
		}else {
			bnt = $('<button type="button" class="btn btn-fit-height grey-salt dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-delay="1000" data-close-others="true">'+opts.text+'<i class="fa fa-angle-down"></i></button>');
		}
		
		if(opts && opts.css){
			bnt.addClass(opts.css);
		}
		
		if(opts && opts.icon){
			var icon = $("<i class='fa fa-"+opts.icon+"' />");
			bnt.prepend(icon);
		}
		
		var downMenu = this.ul_menus(opts.menus);
		downMenu.attr({
			"class":"dropdown-menu pull-right",
			role:"menu"
		});
		
		return div.append(bnt).append(downMenu);
	},
	
	form:function(options){
		var curr = this;
		
		var action = "/flow/flow.jsp";
		if(options && options.action){
			action = options.action;
		}
		var form = this.build(utils.copy(options,{tag:"form",action:action,role:"form"},['type','body']));
		if(options && options.body){
			form.append(this.build(options.body));
		}
		
		return form;
	},
	
	/*
	 * productEditForm:{
		  type:'form',
		  css:'form-horizontal',
		  id:'productEditForm',
		  body:{
		      type:'formBody',
		      groups:[
		        {
		            section:'Basic Info',
		            group-cols:[3,9],
		            rows:[
		                {type:'text',name:'product.name',label:'名称',value:product.name},
		                {type:'text',name:'product.price',label:'价格',value:product.price}
		            ]
		        },
		        {
		            section:'postage Info',
		            group-cols:[3,9],
		            row-col:6,
		            rows:[
		                [
		                    {type:'select',css:'input-medium',name:'product.postage',label:'邮递策略',
		                            value:product.postage,options:[{key:'',label:'默认'}]+each(posts){{key:this.id,label:this.name}}},
		                    {type:'text',name:'product.stock',label:'库存',value:product.stock}
		                ],
		                [
		                    {type:'text',name:'product.orderStock',label:'订单库存',value:product.orderStock}
		                ]
		            ]
		        }
		      ]
		}
	 * **/
	formBody:function(options){
		var curr = this;
		var fbody = $('<div class="form-body" />');
		var hiddenDiv = $("<div style='display:none;' />").appendTo(fbody);
		var bodyOptions = options;
		
		this.isColumnsLayout = function(){
			return bodyOptions && bodyOptions.layout && bodyOptions.layout === 'columns'; 
		};
		
		/**
		 * {
		 * 		type:'text',
		 * 		name:'product.orderStock',
		 * 		cols:[3,9],
		 * 		label:'订单库存',
		 * 		value:product.orderStock
		 * }
		 * **/
		this.formGroup = function(options){
			if(options.type === 'hidden'){
				var opts = utils.copy(options,{},['label']);
				var input = curr.build(opts);
				hiddenDiv.append(input);
				return null;
			}
			
			var fg = $('<div class="form-group" />');
			var labelCol = "col-md-2";
			var inputCol = "col-md-10";
			if($.isArray(options && options.cols)){
				if(options.cols.length > 0){
					labelCol = options.cols[0];
				}
				
				if(options.cols.length > 1){
					inputCol = options.cols[1];
				}
			}
			if($.isNumeric(labelCol)){
				labelCol = "col-md-"+labelCol;
			}
			if($.isNumeric(inputCol)){
				inputCol = "col-md-"+inputCol;
			}

			var label = $('<label class="control-label">'+options.label+'</label>');
			if(!curr.isColumnsLayout()){
				label.addClass(labelCol);
			}
			if(options && options.required){
				label.append($('<span class="required"></span>').text(' * '));
			}

			var opts = utils.copy(options,{},['label','cols']);
			var inputDiv = curr.build(opts);
			if(inputDiv){
				if(options.required){
					inputDiv.attr("data-required",1);
					inputDiv.attr("required",1);
				}
				if(options.icon){
					inputDiv = $('<div class="input-icon"><i class="fa fa-'+options.icon+'"></i></div>').append(inputDiv);
				}
			}
			if(!curr.isColumnsLayout()){
				inputDiv = $('<div />').addClass(inputCol).append(inputDiv);
			}
			if(options && options.help){
				inputDiv.append($('<span class="help-block"></span>').text(options.help));
			}
			
			return fg.append(label).append(inputDiv);
		};
		
		
		this.formRow = function(rowOpts){
			if(!$.isArray(rowOpts.rows) || rowOpts.rows.length == 0){
				return null;
			}

			var rc = 12/rowOpts.rows.length;
			if(rowOpts && rowOpts['row-col']){
				rc = rowOpts['row-col'];
			}
			if($.isNumeric(rc)){
				rc = 'col-md-'+rc;
			}
			
			var gc = null;
			if(rowOpts && rowOpts['group-cols']){
				gc = rowOpts['group-cols'];
			}
			
			var row = null;
			var rows = null;
			$.each(rowOpts.rows,function(){
				if($.isArray(this)){
					var newOpts = {
						'row-col':rc,
						'group-cols':gc,
						rows:this
					}
					
					var r = curr.formRow(newOpts);
					if(r){
						if(!rows){
							rows = [];
						}
						rows.push(r);
					}
					return;
				}

				if(!row){
					row = $("<div class='row' />");
				}
				if(gc){
					this['cols'] = gc;
				}
				var group = curr.formGroup(this);
				if(group){
					$("<div />").addClass(rc).appendTo(row).append(group);
				}
			});
			
			if(row){
				return row;
			}
			return rows;
		};
		
		var defaultValidateOptions = {
			errorElement: "span", //default input error message container
			errorClass: 'help-block help-block-error', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            ignore: "", // validate all fields including form hidden input

            errorPlacement: function (error, element) { // render error placement for each input type
                if (element.parent(".input-group").size() > 0) {
                    error.insertAfter(element.parent(".input-group"));
                } else if (element.attr("data-error-container")) { 
                    error.appendTo(element.attr("data-error-container"));
                } else if (element.parents('.radio-list').size() > 0) { 
                    error.appendTo(element.parents('.radio-list').attr("data-error-container"));
                } else if (element.parents('.radio-inline').size() > 0) { 
                    error.appendTo(element.parents('.radio-inline').attr("data-error-container"));
                } else if (element.parents('.checkbox-list').size() > 0) {
                    error.appendTo(element.parents('.checkbox-list').attr("data-error-container"));
                } else if (element.parents('.checkbox-inline').size() > 0) { 
                    error.appendTo(element.parents('.checkbox-inline').attr("data-error-container"));
                } else {
                    error.insertAfter(element); // for other inputs, just perform default behavior
                }
            },


            highlight: function (element) { // hightlight error inputs
               $(element)
                    .closest('.form-group').addClass('has-error'); // set error class to the control group
            },

            unhighlight: function (element) { // revert the change done by hightlight
                $(element)
                    .closest('.form-group').removeClass('has-error'); // set error class to the control group
            },

            success: function (label) {
                label
                    .closest('.form-group').removeClass('has-error'); // set success class to the control group
            },

            submitHandler: function (form) {
                form[0].submit(); // submit the form
            }
        };
		
		if(options && options.groups && $.isArray(options.groups)){
			$.each(options.groups,function(){
				if(this.section){
					$('<h3 class="form-section">'+this.section+'</h3>').appendTo(fbody);
					var row = curr.formRow(this);
					if(row){
						fbody.append(row);
					}
				}else {
					var group = curr.formGroup(this);
					if(group){
						fbody.append(group);
					}
				}
			});
		}

		return fbody;
	},
	
	rows:function(options){
		var curr = this;
		var optsRows = options && options.rows;
		
		if($.isArray(optsRows)){
			var elements = [];
			
			$.each(optsRows,function(){
				var rowOpts = {type:'row'};
				rowOpts.body = this;
				
				rowOpts.css = options && options['row-css'];
				rowOpts.cols = options && options.cols;
				
				elements.push(curr.row(rowOpts));
			});

			return elements;
		}
		return null;
	},
	
	/**
	 * <div class="row static-info">
    <div class="col-md-5 name">Order #:</div>
    <div class="col-md-7 value">
        12313232 <span class="label label-info label-sm">Email confirmation was sent </span>
    </div>
</div>
	 * **/
	row:function(options){
		var curr = this;
		
		var rowElement = $('<div class="row" style="margin-bottom:15px;" />');
		if(options && options.css){
			rowElement.addClass(options.css);
		}
		
		var body = options && options.body;
		var cols = options && options.cols;
		
		this.fnColumnElement = function(col,columnBody){
			var columnElement = $('<div />').addClass('col-md-'+col);
			curr._fillIn(columnElement,columnBody);
			return columnElement;
		};

		var idx = 0;
		var defaultCol = 1;
		if($.isArray(body)){
			if(body.length > 0){
				defaultCol = 12/body.length;
			}
			
			$.each(body,function(){
				/**
				 * col class: 'col-md-4'
				 * **/
				var col = (this && this.col) 
							|| ($.isArray(cols) && cols.length > idx && cols[idx++])
							|| defaultCol;

				curr.fnColumnElement(col,this).appendTo(rowElement);
			});
		}else {
			/**
			 * col class: 'col-md-4'
			 * **/
			var col = (body && body.col) 
						|| ($.isArray(cols) && cols.length > 0 && cols[0])
						|| defaultCol;
			
			curr.fnColumnElement(col,body).appendTo(rowElement);
		}
		
		return rowElement;
	},
	
	_fillIn:function(element,childOptions){
		if($.isArray(childOptions) || $.isPlainObject(childOptions)){
			element.append(this.build(childOptions));
		}else {
			if(!childOptions){
				childOptions = '';
			}
			element.text(childOptions);
		}
		return element;
	},
	
	portlet:function(options){
		var curr = this;
		
		/**
		 * title:{
		 * 		caption:'产品列表',
		 * 		icon:'shop-cart',
		 * 		actions:[
		 * 			{text:'新增产品',href:'add.html',icon:'plus'},
		 * 			{
		 * 				type:'dropdownMenu',
		 * 				text:'Tools',
		 * 				menus:[]
		 * 			}
		 * 		]
		 * }
		 * **/
		this.title = function(topts){
			var title = $('<div class="portlet-title" />');
			
			var caption = $('<div class="caption"><i class="fa fa-'+topts.icon+'"></i>'+topts.caption+'</div>');
			var actions = $('<div class="actions" />');
			
			
			$.each(topts.actions,function(){
				if(this.type){
					actions.append(curr.build(this));
				}else if(this.href){
					var a = $('<a href="'+this.href+'" class="btn default yellow-stripe"><i class="fa fa-'+this.icon+'"></i><span class="hidden-480">'+this.text+'</span></a>');
					actions.append(a);
				}
			});
			
			return title.append(caption).append(actions);
		};
		
		this.body = function(bopts){
			var body = $('<div class="portlet-body" />');

			body.append(this.build(bopts));
			return body;
		};
		
		var pe = $('<div class="portlet" />');
		var te = this.title(options.title);
		var be = this.body(options.body);
		
		return pe.append(te).append(be);
	},
	
	editTable:function (options){
		var curr = this;
		var dataIndex = 0;
		var newDatas = [];
		
		this.toArrayName = function(options){
			var isInput = curr.isInput(options && options.type);
			if(isInput && options && options.name){
				options.name = utils.toArrayName(options.name,dataIndex);
				return;
			}
			
			if($.isArray(options)){
				$.each(options,function(){
					curr.toArrayName(this);
				});
			}
		};
		
		this.clearInputValue = function(options){
			if($.isArray(options)){
				$.each(options,function(){
					curr.clearInputValue(this);
				});
			}
			
			var isInput = curr.isInput(options && options.type);
			if(isInput && options && options.value){
				options.value = null;
				return;
			}
		};
		
		this.deleteTD = function(){
			var delBnt = $("<button class='red btn' type='button'><i class='fa fa-remove'></i></button>");
			
			delBnt.click(function(){
				$(this).closest('tr').remove();
			});
			
			return $('<td />').append(delBnt);
		};

		this.addButton = function(){
			var addBnt = $("<button class='yellow btn' type='button' style='margin:5px;width:100px;'><i class='fa fa-plus'></i>新增</button>");
			var tbody = $('tbody',table);
			
			addBnt.click(function(){
				if(newDatas.length > 0){
					var newOpts = utils.clone(newDatas);
					curr.toArrayName(newOpts);
					var row = $("<tr />").appendTo(tbody);
					$.each(newOpts,function(){
						$("<td />").append(curr.build(this)).appendTo(row);
					});
					
					if(hasDel){
						curr.deleteTD().appendTo(row);
					}
					
					$('input:text:first',row).focus();
					dataIndex++;
				}
			});
			
			$('thead',table).prepend(addBnt);
		};
		
		this.appendDelete = function(){
			var tbody = $('tbody',table);
			var rows = $('tr',tbody);
			$.each(rows,function(){
				curr.deleteTD().appendTo($(this));
			});
		};
		
		if($.isArray(options && options.body)){
			if(options.body.length > 0){
				newDatas = utils.clone(options.body[0]);
				curr.clearInputValue(newDatas);
			}
			$.each(options.body,function(){
				curr.toArrayName(this);
				dataIndex++;
			});
		}
		
		log('options : ' + JSON.stringify(options));
		
		var hasDel = options.deleteButton;
		if(hasDel && $.isArray(options.head.heading)){
			options.head.heading.push({text:''});
		}
		
		var table = this.table(options);
		if(hasDel){
			this.appendDelete();
		}
		this.addButton();

		return table;
	},
	
	ScrollableTable:function (options){
		if(!(options && options.table && options.table['class'])){
			options.table['class'] = "table table-striped table-bordered table-hover dataTable no-footer";
		}
		return this.table(options);
	},
	
	table:function (options){
	    var _ = this;
	    
	    this.table = function(opts){
	        var table = $("<table />");
	        
	        this.full(table,this.readWithDefaultConfig(opts.table,this.config.table));
	        
	        table.append(this.thead(opts.head));
	        table.append(this.tbody(opts.body));
	        
	        if(opts.foot){
	        	table.append(this.tfoot(opts.foot));
	        }
	        
	        return table;
	    };
	    
	    this.tbody = function(opts){
	        var tbody = $("<tbody />");
	        $.each(opts,function(){
	            var row = $("<tr />").appendTo(tbody);
	            
	            $.each(this,function(){
	                _.full($("<td />"),this).appendTo(row);
	            });
	        });
	        return tbody;
	    };
	    
	    this.tfoot = function(option){
	    	var tfoot = $("<tfoot />");
	    	var row = $("<tr />").appendTo(tfoot);
	    	
	    	$.each(option,function(){
	    		_.full($("<td />"),this).appendTo(row);
	    	});
	    	return tfoot;
	    };
	    
	    this.full = function(ele,opts){
	        if($.isArray(opts)){
	        	ele.append(this.build(opts));
	        }else if($.isPlainObject(opts)){
	        	if(opts.type){
	        		return ele.append(this.build(opts));
	        	}
	        	
	        	if(opts.text){
	        		ele.text(opts.text);
	        	}
	        	
	        	if($.isPlainObject(opts.html)){
	        		ele.append(this.build(opts.html));
	        	}else if($.type(opts.html) === 'string'){
	        		ele.html(opts.html);
	        	}
	        	
	        	if(opts.class){
	        		ele.addClass(opts.class);
	        	}
	        	
	        	var newOpts = utils.copy(opts,{},['type','text','html','class']);
	            ele.attr(newOpts);
	        }else {
	            ele.text(opts);
	        }
	        return ele;
	    };
	    
	    /**
	     * {
	     * 		type:'text',
	     * 		name:'order_id',
	     * 		value:''
	     * }
	     * **/
	    this.thead = function(opts){
	        var thead = $("<thead />");
	        var row = $('<tr role="row" class="heading" />').appendTo(thead);
	        
	        $.each(opts.heading,function(){
	            if(this.type == 'checker'){
	            }
	            
	            _.full($('<th />'),_.readWithDefaultConfig(this,_.config.thead.heading)).appendTo(row);
	        });
	        
	        
	        /*
	         * filter:[
	         * 	{type:'text',name:'product[name]',value:'SK2神仙水330ML'},
	         * 	{type:'text',name:'product[price]',value:'1098'},
	         * 	{type:'button',name:'ok',css:'yellow',icon:'search',text:' Search '}
	         * ]
	         * **/
	        if($.isArray(opts && opts.filter)){
	        	row = $('<tr role="row" class="filter" />').appendTo(thead);
		        $.each(opts.filter,function(){
		        	var td = $('<td />').appendTo(row);
		        	var cfig = _.config.thead.filter;
		        	
		        	if($.isArray(this)){
		        		td.append(_.build(this));
		        	}else if(this.type){
		        		var isInput = _.isInput(this.type);
		        		var ele = _.build(this);
		        		if(isInput){
		        			if(this.type == "between"){
//		        				$.each(ele,function(){
//		        					this.addClass("form-filter input-sm");
//		        				});
//		        				ele[0].wrap("<div class='margin-bottom-5' />");
		        			}else if(_.isButton(this.type)){
		        				ele.addClass("btn-sm");
		        			}else {
		        				ele.addClass("form-filter input-sm");
		        			}
		        		}
		        		
		        		td.append(ele);
		        	}else {
		        		cfig = _.readWithDefaultConfig(this,_.config.thead.filter);
		        	}
		        	_.full(td,cfig);
		        });
	        }

	        return thead;
	    };

	    this.config = {
	        "table":{
	            "class":"table table-hover table-bordered",
	            "aria-describedby":"datatable_products_info",
	            "role":"grid"
	        },
	        "input":{
	        	"class":"input-sm"
	        },
	        "thead":{
	            "heading":{
	            	"class":"sorting",
                    "tabindex":"0",
                    "aria-controls":"datatable_products",
                    "rowspan":"1",
                    "colspan":"1"
	            },
	            "filter":{
	            	"rowspan":"1",
                    "colspan":"1"
	            }
	        }
	    };
	    
	    this.readWithDefaultConfig = function(opts,config){
	        if($.type(opts) === 'string'){
	            opts = {text:opts};
	        }
	        
	        if(!$.isPlainObject(opts)){
	            opts = {};
	        }
	        
	        $.each(config,function(k,v){
	            if(!opts[k]){
	                opts[k] = v;
	            }
	        });
	        return opts;
	    };
	    
	    
	    var div = $('<div class="table-scrollable" />');
        div.append(_.table(options));
        
        return div;
	},


	input:function(opts){
		var curr = this;
		
//		this.date = function(opts){
//	        var de = $('<div class="input-group date date-picker margin-bottom-5" data-date-format="dd/mm/yyyy" />');
//	        var dinput = $('<input type="text" class="form-control form-filter input-sm" readonly="" />').prop('name',opts.name).prop('placeholder',opts.placeholder);
//	        var dbnt = $('<span class="input-group-btn"><button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button></span>');
//	        
//	        de.append(dinput).append(dbnt);
//	        
//	        $(function(){
//	        	dinput.datepicker({
//		            format: 'yyyy-mm-dd hh:ii'
//		        });
//	        });
//	        
//			return de;
//	    };
	    
	    this.date = function(opts){
	    	opts.type = "text";
	        var input = curr.input(opts);
	        input.addClass("form-control input-sm");
	        input.prop("readonly",true);
	        
	        var dateDIV = $('<div class="input-group date date-picker margin-bottom-5" data-provide="datepicker" data-date-format="dd/mm/yyyy" />');
	        var dbnt = $('<span class="input-group-btn"><button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button></span>');
	        dateDIV.append(input).append(dbnt);//.append($('<span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>'));
	        
//	        $(function(){
//	        	dateDIV.datepicker();
//	        });
	        
	        return dateDIV;
	    };
	    
	    /**
	     * {type:'between',input:'date',rangeName:['startDate','endDate'],name:'coupon.expiryDate',label:'有效期',value:coupon.expiryDate,required:true},
	     * **/
	    this.betweenDate = function(option){
	    	var div = $('<div class="input-group input-large date-picker input-daterange" data-provide="datepicker" data-date-format="yyyy-mm-dd">');
	    	var to = $('<span class="input-group-addon"> to </span>');
	    	
	    	option.input = "text";
	    	var els = this._super(option);
	    	return div.append(els[0]).append(to).append(els[1]);
	    };
	    
	    
	    /**
	     * <div class="input-group">
        <span class="input-group-btn">
            <button class="btn red" type="button">-</button>
        </span>
        <input type="text" class="form-control">
        <span class="input-group-btn">
            <button class="btn red" type="button">+</button>
        </span>
    </div>
	     * **/
	    this.number2 = function(option){
	    	var newOption = utils.copy(option,null,['type','range']);
	    	newOption.type = 'text';
	    	var textElement = this._super(newOption);
	    	textElement.addClass('form-control');
	    	
	    	var min = option && option.min;
	    	var max = option && option.max;
	    	if(option && option.range && $.isArray(option.range) && option.range.length == 2){
	    		if(!min){
	    			min = option.range[0];
	    		}
	    		if(!max){
	    			max = option.range[1];
	    		}
	    	}
	    	
	    	/**
	    	 * minus button
	    	 * **/
	    	var minusSpan = $('<span class="input-group-btn"><button class="btn btn-default" type="button"> - </button></span>');
	    	$("button",minusSpan).on("click",function(){
	    		var val = Number(textElement.val());
	    		if(isNaN(val)){
	    			val = 1;
	    		}else {
	    			val--;
	    		}
	    		
	    		if(min){
		    		if(val < min) val = min;
	    		}
	    		if(max){
	    			if(val > max) val = max;
	    		}
	    		
	    		textElement.val(val);
	    		textElement.trigger("change");
	    	});
	    	
	    	/**
	    	 * add button
	    	 * **/
	    	var addSpan = $('<span class="input-group-btn"><button class="btn btn-default" type="button">+</button></span>');
	    	$("button",addSpan).on("click",function(){
	    		var val = Number(textElement.val());
	    		if(isNaN(val)){
	    			val = 1;
	    		}else {
	    			val++;
	    		}
	    		
	    		if(min){
		    		if(val < min) val = min;
	    		}
	    		if(max){
	    			if(val > max) val = max;
	    		}
	    		
	    		textElement.val(val);
	    		textElement.trigger("change");
	    	});
	    	
	    	return $("<div class='input-group' />").append(minusSpan).append(textElement).append(addSpan);
	    };
	    
	    /**
	     * {
	     * 	  type:'between',
	     *    name:'',
	     *    input:'',
	     *    vals:[],
	     *    options:
	     * }
	     * 
	     * {type:'between',layout:'vertical',input:'text',name:'ps.price',value:[100,1000]},
	     * **/
	    this.between = function(opts){
	    	if((opts && opts.input) == 'date' ){
	    		return this.betweenDate(opts);
	    	}
	    	
	    	var vertical = (opts && opts.layout) == "v";
	    	
	    	var els = this._super(opts);
	    	if($.isArray(els)){
	    		var fe = els[0];
	    		if(vertical){
	    			fe = $("<div class='margin-bottom-5' />").append(fe);
	    		}
	    		els[0] = fe;
	    	}
	    	
	    	return els;
	    };
	    
	    this.select2 = function(opts){
	    	opts.type = "select";
	    	
	    	var element = this._super(opts);
	    	element.addClass("form-control input-medium select2me select2-offscreen");
	    	element.attr("data-placeholder","Select...");
	    	element.attr("tabindex","-1");
	    	
	    	if ($().select2) {
	    		curr.doSelect2();
	        }else {
	        	$.getScript('/static/metronic/theme/assets/global/plugins/select2/select2.min.js').done(function(){
		    		curr.doSelect2();
	    		});
	        }
	    	
	    	return element;
	    };
	    
	    this.doSelect2 = function(){
	    	$('.select2me').select2({
                placeholder: "Select",
                allowClear: true
            });
	    };
	    
	    this.defaultClass = function(ele){
	    	if($.isArray(ele)){
	    		$.each(ele,function(){
	    			curr.defaultClass(this);
	    		});
	    	}else {
	    		if(ele.is("button")){
	        		ele.addClass("btn");
	        	}else {
	        		ele.addClass("form-control");
	        	}
	    	}
	    };
	    
	    /***
	     * 样式优先遵从 bootstrap
	     * 逐步移除metronic，icheck的特征样式
	     * metronic，icheck的实现将放于另外的独立文件中
	     * ***/
	    this.checkElement = function(options){
	    	var newOpts = utils.copy(options,null,['icheck','layout']);
	    	var element = this._super(newOpts);
	    	
	    	var goupDiv = $("<div class='input-group' />");
	    	var layoutDiv = $("<div />").append(element);
	    	goupDiv.append(layoutDiv);
	    	
	    	/**
	    	 * layout
	    	 * **/
	    	var layout = options && options.layout;
	    	if(layout === 'inline'){
	    		layout = 'icheck-inline';
	    	}else {
	    		layout = 'icheck-list';
	    	}
	    	layoutDiv.addClass(layout);
	    	
	    	return goupDiv;
	    };
	    
	    this.radio = function(options){
	    	var newOpts = utils.copy(options,null,['icheck','layout']);
	    	var element = this._super(newOpts);
	    	
	    	var inline = options && options.layout === 'inline';
	    	if(inline){
	    		if($.isArray(element)){
	    			$.each(element,function(){
	    				this.addClass('radio-inline');
		    		});
	    		}else {
	    			element.addClass('radio-inline');
	    		}
	    		
	    		return element;
	    	}else {
	    		if($.isArray(element)){
		    		var divs = [];
		    		$.each(element,function(){
		    			divs.push($("<div class='radio' />").append(this));
		    		});
		    		return divs;
		    	}
	    		
	    		return $("<div class='radio' />").append(this);
	    	}
	    	
	    	/*
	    	var ele = $("<div class='radio' />");
	    	ele.append(element);
	    	
	    	if(options && options.layout === 'inline'){
	    		ele.find("label").addClass('radio-inline');
	    	}
	    	
	    	return ele;
	    	*/
	    };
	    
	    this.radio22222 = function(options){
	    	var ele = this.checkElement(options);
	    	ele.addClass("radio");
	    	
	    	if(options && options.icheck){
	    		this.icheck($(':radio',ele),options.icheck);
	    	}
	    	
	    	ele.find("label").addClass('radio-inline');
	    	
	    	return ele;
	    };
	    
	    this.checkbox = function(options){
	    	var ele = this.checkElement(options);
	    	ele.addClass("checkbox");
	    	
	    	if(options && options.icheck){
	    		this.icheck($(':checkbox',ele),options.icheck);
	    	}
	    	ele.find("label").addClass('checkbox-inline');
	    	
	        return ele;
	    };
	    
	    this.icheck = function(check,options){
	    	if(options){
	    		check.addClass('icheck');
	    		
	    		if($().iCheck){
	    			curr.doICheck(check,options);
	    		}else {
	    			$.getScript('/static/icheck-1.x/icheck.js').done(function(){
		    			curr.doICheck(check,options);
		    		});
	    		}
	    	}
	    };
	    
	    this.doICheck = function(check,options){
	    	$.each(check,function(){
				var self = $(this);
				
				self.iCheck({
			        checkboxClass : 'icheckbox_square-blue',
			        radioClass : 'iradio_square-blue'
				});
				
				if(options.events){
    				$.each(options.events,function(eve,fun){
    					self.on(eve,$.isFunction(fun)?fun:eval(fun));
    				});
    			}
			});
	    };

	    this.summernote = function(options){
			
			$("head").append($('<link rel="stylesheet" type="text/css" href="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.css">'));
			//$("head").append($('<script src="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.min.js" type="text/javascript"></script>'));
			
			options.type = "textarea";
			if(!options.value)options.value = '';
			
			var textarea = this._super(options);
			var element = $('<div name=summernote_"'+options.name+'" class=".summernote" >'+options.value+'</div>');
			element.show("normal",function(){
				textarea.hide();
				
				$(this).summernote({height: 300});
				
				$(this).on('summernote.blur', function() {
					textarea.val($(this).summernote('code'));
				});
			});
			
			
			return [textarea,element];
		};

	    if(opts.type === "between"){
	    	return this.between(opts);
	    }
	    
	    if(opts.type === "date"){
	    	return this.date(opts);
	    }
	    
	    if(opts.type == "select2"){
	    	return this.select2(opts);
	    }
	    
	    if(opts.type == "radio"){
	    	return this.radio(opts);
	    }
	    
	    if(opts.type == "checkbox"){
	    	return this.checkbox(opts);
	    }
	    
	    if(opts.type == "summernote"){
	    	return this.summernote(opts);
	    }
	    
	    if(opts.type == "htmlEditor"){
	    	return this.summernote(opts);
	    }
	    
	    if(opts.type == "number2"){
	    	return this.number2(opts);
	    }
	    
	    var input = this._super(opts);
	    this.defaultClass(input);

	    if(opts && opts.icon){
	    	input.prepend($("<i class='fa fa-"+opts.icon+"' />"));
	    }
	    
		return input;
	},
	
    paginate:function(opts){
    	this.toPage = function(pno){
        	$("input[name='page.page']").val(pno);
        	$("input[name='page.page']").trigger("click");
        };
        
        this.validPage = function(pno){
        	return pno >= 1 && pno <= opts.total;
        };
        
        this.initPageButton = function(pno,bnt){
        	if(paginate.validPage(pno)){
            	bnt.click(function(){
                	paginate.toPage(pno);
                });
        	}else {
        		bnt.addClass("disabled");
        	}
        };
        
        var paginate = this;

        var el = $('<div class="dataTables_paginate paging_bootstrap_extended" />');
        var el2 = $('<div class="pagination-panel" />').appendTo(el);
        
        var pTxt = $("<span />").text(" Page ");
        var prev = $('<a href="javaScript:;" class="btn btn-sm default prev" title="Prev"><i class="fa fa-angle-left"></i></a>');
        var pinput = $('<input type="text" class="pagination-panel-input form-control input-mini input-inline input-sm" maxlenght="5" style="text-align:center; margin: 0 5px;" />');
        var next = $('<a href="javaScript:;" class="btn btn-sm default next" title="Next"><i class="fa fa-angle-right"></i></a>');
        var of = $("<span />").text(" of ");
        var total = $('<span class="pagination-panel-total" />').text(opts.total);
        
        this.initPageButton(opts.curr-1,prev);
        this.initPageButton(opts.curr+1,next);
        
        pinput.val(opts.curr);
        pinput.change(function(){
        	var pno = $(this).val();
        	if(pno < 1)pno = 1;
        	if(pno > opts.total)pno = opts.total;
        	
        	paginate.toPage(pno);
        });
        
        el2.append(pTxt).append(prev).append(pinput).append(next).append(of).append(total);
        
        var psize = $('<div class="dataTables_length" />');
        var sel = $('<label />').appendTo(psize);
        sel.html('<span class="seperator">|</span>');
        psize.append($("<span />").text(" View "));
        
        var selectOptions = {
        	type:'select',
            name:'',
            options:{
                '10':10,
                '20':20,
                '50':50,
                '100':100,
                '150':150,
                '-1':'ALL'
            }
        };
        
        var select = this.input(selectOptions);
        select.addClass("input-xsmall input-sm input-inline");
        select.prop("aria-controls","datatable_orders");
        
        select.appendTo(psize);
        
        return [el,psize];
    }
});

