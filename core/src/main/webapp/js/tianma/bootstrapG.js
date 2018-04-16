
var BootstrapGenerator = HTMLGenerator.extend({
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
		var _ = this;

		var action = "/flows/flow.htm";
		if(options && options.action){
			action = options.action;
		}
		var form = _.build(utils.copy(options,{tag:"form",action:action,role:"form"},['type','body']));
		if(_.isInlineLayout(options)){
			form.addClass("form-inline");
		}
		if(_.isHorizontalLayout(options)){
			form.addClass("form-horizontal");
		}
		if(options && options.body){
			if(!options.body.layout){
				options.body.layout = options.layout;
			}
			form.append(_.build(options.body));
		}
		
		return form;
	},
	
	isInlineLayout:function(options){
		return options && options.layout && (options.layout === 'inline' || options.layout === 'i'); 
	},
	
	isHorizontalLayout:function(options){
		return options && options.layout && (options.layout === 'horizontal' || options.layout === 'h');
	},
	
	icon:function(icon){
		return $('<span class="glyphicon glyphicon-'+icon+'" aria-hidden="true"></span>');
	},
	
	/*
	 * editForm:{
        type:'formBody',
        groups:[
            {type:'hidden',name:'bid',value:bid},
            {type:'hidden',name:'fid',value:2},
            {type:'hidden',name:'task.id',value:task.id},
            
            {
                label:'收货地址',
                type:'formBody',
                layout:[1],

                groups:[
                    [
                        {type:'text',name:'task.title',label:'收件人'},
                        {type:'text',name:'task.dateDue',label:'手机号码'}
                    ],
                    [
                        {type:'text',name:'task.title',label:'省份'},
                        {type:'text',name:'task.dateDue',label:'城市'},
                        {type:'text',name:'task.title',label:'区'}
                    ],
                    {type:'text',name:'task.title',label:'详细地址'}
                ]
            },
            
            {type:'text',name:'task.title',label:'主题',placeholder:'主题',value:task.title,
                help:'任务的主题，非空'
            },
            {type:'radio',name:'task.type',label:'问题类型',layout:'inline',
                    value:task.type,options:each(taskTypes){{key:this.id,label:this.name}},
                help:'请选择问题类型'
            },
            
            {type:'checkbox',layout:'inline',name:'task.priority',label:'优先级',
                    value:task.priority,options:each(taskPrioritys){{key:this.id,label:this.name}}},
            {type:'date',name:'task.dateDue',label:'到期日',value:task.dateDue.df('-')},
            {type:'select',name:'task.operator',label:'经办人',value:task.operator,options:each(users){{key:this.id,label:this.name}}},
            {type:'select',name:'task.reporter',label:'报告人',value:task.reporter,options:each(users){{key:this.id,label:this.name}}},
            {type:'text',name:'task.environment',label:'环境',value:task.environment},
            {type:'text',name:'task.description',label:'描述',value:task.description},
            {type:'text',name:'task.estimatedTime',label:'预估时间',value:task.estimatedTime},
            {type:'text',name:'task.remainingTime',label:'剩余时间',value:task.remainingTime},
            {type:'text',name:'task.remark',label:'备注',value:task.remark},
            {type:'select',name:'task.parentTask',label:'父任务',value:task.parentTask,options:each(tasks){{key:this.id,label:this.title}}},
            {type:'text',name:'task.workLogs',label:'工作日志',value:task.workLogs},
            {type:'text',name:'task.t1',label:'测试1',value:task.t1},
            {type:'text',name:'task.t2',label:'测试2',value:task.t2}
    ]},
	 * **/
	formBody:function(options){
		
		/**
		 * options: {type:'text',name:'task.title',label:'主题',placeholder:'主题',value:task.title,help:'任务的主题，非空'},
		 * <div class="form-group">
		 * 		<label>主题</label>
		 * 		<input type="text" name="task.title" help="任务的主题，非空" placeholder="主题" class="form-control">
		 * 		<small class="text-muted form-text">任务的主题，非空</small>
		 * </div>
		 * **/
		this.basicFormGroup = function(options){
			var _ = this;
			var excludeFields = ['label','cols','isInline','isHorizontalLayout','help'];
			
			if($.isArray(options)){
				var elements = [];
				$.each(options,function(){
					elements.push(_.basicFormGroup(this));
				});
				return elements;
			}
			
			if(options.type === 'hidden'){
				var hidden = _.build(utils.copy(options,{},excludeFields));
				return hidden;
				//return curr.appendHidden(utils.copy(options,{},excludeFields));
			}
			
			var fg = $('<div class="form-group" />');
			if(!$.isArray(options) && !$.isPlainObject(options)){
				return fg.text(options);
			}
			
			var labelText = utils.readValue(options,'label',null);
			var label = null;
			if(labelText){
				label = $('<label />').text(labelText);
				if(options.type === 'radio' || options.type === 'checkbox'){
					if(!options.isInline){
						label.css("display","block");
					}
				}
			}

			var opts = utils.copy(options,{},excludeFields);
			var inputDiv = curr.build(opts);
			
			if(label){
				if(options && options.postposition){
					fg.append(inputDiv).append(label);
				}else {
					fg.append(label).append(inputDiv);
				}
			}else {
				fg.append(inputDiv);
			}

			var help = options && options.help;
			if(help){
				fg.append($('<small class="text-muted" />').text(help));
			}
			return fg;
		};

		/**
		 * options: {type:'text',name:'task.title',label:'主题',placeholder:'主题',value:task.title,help:'任务的主题，非空'},
		 * <div class="form-group row">
		 * 		<label class="col-md-2">主题</label>
		 * 		<div class="col-md-10">
		 * 			<input type="text" name="task.title" help="任务的主题，非空" placeholder="主题" class="form-control">
		 * 			<small class="text-muted form-text">任务的主题，非空</small>
		 * 		</div>
		 * </div>
		 * **/
		this.formGroup = function(options){
			var group = curr.basicFormGroup(options);
			if(!group || !group.is(".form-group")){
				return group;
			}
			
			if($.isArray(group)){
				var elements = [];
				
				$.each(group,function(){
					var e = curr.doLayout(this,options);
					elements.push(e);
				});
				
				return elements;
			}
			return curr.doLayout(group,options);
		};
		
		this.doLayout = function(group,options){
			if(options.isHorizontalLayout){
				return this.doHorizontalLayout(group,options && options.cols);
			}
			
			if(options.isInline){
				return this.doInlineLayout(group);
			}

			return group;
		};
		
		this.doInlineLayout = function(group){
			return group;
		};

		this.doHorizontalLayout = function(group,cols){
			/***
			 * columns
			 * **/
			var label = group.children('label');
			var labelCol = "col-md-2";
			var inputCol = "col-md-10";
			if($.isArray(cols)){
				if(cols.length > 0){
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
			
			group.addClass('row');
			label.addClass(labelCol);
			label.nextAll().wrapAll("<div class='"+inputCol+"'></div>");
			group.find('.text-muted').addClass('form-text');
			return group;
		};
		
		this.formRow = function(rowOpts){
			var rows = rowOpts && rowOpts.groups;
			if(!$.isArray(rows) || rows.length == 0){
				return null;
			}

			var pre = 'col-md-';
			var defaultCss = "col";
			var layout = rowOpts && rowOpts.layout
			var rowElements = [];
			$.each(rows,function(k,v){
				var cl = 1;
				if($.isArray(layout)){
					if(layout.length > 0 && $.isNumeric(layout[0])){
						cl = layout;
					}else if(layout.length > k){
						cl = layout[k];
					}
				}
				
				var cnames = defaultCss;
				if($.isArray(cl)){
					cnames = [];
					$.each(cl,function(){
						if($.isNumeric(this)){
							cnames.push(pre+this);
						}else {
							cnames.push(this);
						}
					});
				}
				
				console.log("cnames : " + cnames);

				var els = curr.basicFormGroup(v);
				if($.isArray(els)){
					var row = $("<div class='form-row' />");
					rowElements.push(row);
					
					var skipGroup = 0;
					$.each(els,function(idx,group){
						if(group.is(".form-group")){
							var css = defaultCss;
							var cssIdx = idx-skipGroup;
							if($.isArray(cnames) && cnames.length > cssIdx){
								css = cnames[cssIdx];
							}
							
							group.addClass(css.toString());
						}else {
							skipGroup++;
						}
						row.append(group);
					});
				}else {
					rowElements.push(els);
				}
			});
			
			return rowElements;
		};
		
		var curr = this;
		//var fbody = $('<div class="form-body" />');
		
		var fbody = curr.build(utils.copy(options,{tag:"div"},['groups','type','layout','label']));
		fbody.addClass('form-body');
		
		var isHorizontalLayout = this.isHorizontalLayout(options);
		var isInline = this.isInlineLayout(options);
		
		if(options && options.groups && $.isArray(options.groups)){
			var layout = options && options.layout;
			if($.isArray(layout)){
				var res = curr.formRow(options);
				if(res){
					fbody.append(res);
				}
			}else {
				$.each(options.groups,function(){
					this.isHorizontalLayout = isHorizontalLayout;
					this.isInline = isInline;
					
					var group = curr.formGroup(this);
					if(group){
						fbody.append(group);
					}
				});
			}
		}
		
		/*
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
		}*/

		return fbody;
	},
	
	/**
	 * {
	 * 		
	 * }
	 * **/
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
	
	appendTo:function(jqObj,options){
		if(options){
			if($.isArray(options) || $.isPlainObject(options)){
				jqObj.append(this.build(options));
			}else {
				jqObj.text(options);
			}
		}
		return jqObj;
	},
	
	/**
	 * {
          type:'column',
          css:'col-sm-3',
          body:{type:'text',name:'price',value:49}
       }
	 * ***/
	column:function(options){
		var column = $("<div />");
		
		var css = options && options.css;
		if(!css){
			css = "col-md-auto";
		}
		column.addClass(css);

		this.appendTo(column,options && options.body);
		
		return column;
	},
	
	/**
	 * {
      type:'row',
      column-css:['col-sm-2','col-sm-4','col-sm-2','col-sm-4'],
      body:[
        {type:'text',name:'price',value:49},
        {
          type:'column',
          css:'col-sm-3',
          body:{type:'text',name:'price',value:49}
        },
        {
          type:'column',
          css:'col-sm-5 col-md-6',
          body:'.col-sm-5 .col-md-6'
        },
        {type:'select',name:'price',value:49}
      ]
    }
	 * **/
	row:function(options){
		var _ = this;
		
		var body = options && options.body;
		var columnCss = options && options['column-css'];
		var rows = [];
		if($.isArray(body)){
			$.each(body,function(){
				var row = $("<div class='row' />");
				rows.push(row);
				
				var idx = 0;
				$.each(this,function(){
					var colOpts = this;
					var type = this && this.type;
					if(!(type === 'column')){
						colOpts = {
							type:'column',
							css:'col',
							body:this
						};
						
						if($.isArray(columnCss)){
							if(idx >= columnCss.length){
								idx = idx%columnCss.length;
							}
							colOpts['css'] = columnCss[idx]; 
						}else if(utils.isString(columnCss)){
							colOpts['css'] = columnCss;
						}
					}
					
					_.appendTo(row,colOpts);
					idx++;
				});
			});
			
		}
		return rows;
	},
	
	
	
	
	/**
	 * <div class="row">
    <div class="col-sm">
      One of three columns
    </div>
    <div class="col-sm">
      One of three columns
    </div>
    <div class="col-sm">
      One of three columns
    </div>
  </div>
	 * 
	 * ***/
	row222ddd:function(options){
		var _ = this;
		
		var row = $("<div class='row' />");
		_.attr(row,utils.copy(options,null,['body']));
		
		var body = options && options.body;
		if($.isArray(body)){
			$.each(body,function(){
				var columns = this;
				if($.isArray(columns)){
					$.each(columns,function(){
						var col = $("<div />");
						col.addClass('col');
						
						col.append();
					});
				}
			})
		}
	},
	
	/**
	 * <div class="row static-info">
    <div class="col-md-5 name">Order #:</div>
    <div class="col-md-7 value">
        12313232 <span class="label label-info label-sm">Email confirmation was sent </span>
    </div>
</div>
	 * **/
	row222:function(options){
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
	
	doText:function(jqObj,text){
		if(text == null || text == undefined){
			text = '';
		}
		
		console.log(' text : ' + text);
		jqObj.text(text);
	},
	
	table:function (options){
		var _ = this;
		
		var table = $("<table class='table' />");
		_.attr(table,utils.copy(options,null,['head','body']));
		
		this.tr = function(trDatas,colTag){
			var tr = $('<tr />');
			
			if($.isArray(trDatas)){
				$.each(trDatas,function(){
					if($.isArray(this) || $.isPlainObject(this)){
						$("<"+colTag+" />").appendTo(tr).append(_.build(this));
					}else {
						_.doText($("<"+colTag+" />").appendTo(tr),this);
					}
				});
			}else {
				if($.isPlainObject(this)){
					$("<"+colTag+" />").appendTo(tr).append(_.build(this));
				}else {
					_.doText($("<"+colTag+" />").appendTo(tr),this);
				}
			}

			return tr;
		};
		
		var head = options && options.head;
		if($.isArray(head)){
			$('<thead />').appendTo(table).append(_.tr(head,"th"));
		}
		
		var body = options && options.body;
		if($.isArray(body)){
			var tbody = $('<tbody />').appendTo(table);
			
			$.each(body,function(){
				tbody.append(_.tr(this,"td"));
			});
		}
		
		return table;
	},
	
	table22222:function (options){
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
	        	
	        	if(opts.html){
	        		return ele.append(this.build(opts));
	        	}
	        	
	        	if(opts.text){
	        		ele.text(opts.text);
	        	}
	        	
	        	if(opts.class){
	        		ele.addClass(opts.class);
	        	}
	        	
	        	var newOpts = utils.copy(opts,{},['type','text','html','class']);
	            ele.attr(newOpts);
	        }else {
	        	if(opts == null || opts == undefined){
	        		opts = "";
	        	}
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
	        
	        var head = opts;
	        //head = opts.heading;
	        
	        $.each(head,function(){
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
	        
	        //return dateDIV;
	        
	        return $("<input class='form-control' type='date' />");
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
	   
	    
	    this.addCustomCheckClass = function(checkDiv,inline){
	    	checkDiv.addClass('custom-control');
			if(inline){
				checkDiv.addClass('custom-control-inline');
			}
			checkDiv.find('input').addClass('custom-control-input');
			checkDiv.find('label').addClass('custom-control-label');
			
			return checkDiv;
	    };
	    
	    this.addDefaultCheckClass = function(checkDiv,inline){
	    	checkDiv.addClass('form-check');
			if(inline){
				checkDiv.addClass('form-check-inline');
			}
			checkDiv.find('input').addClass('form-control-input');
			checkDiv.find('label').addClass('form-control-label');
			
			return checkDiv;
	    };
	    
	    this.addCheckClass = function(checkDiv,options){
	    	var inline = options && options.layout === 'inline';
	    	var notCustom = options && options.custom === false;
	    	var isRadio = options && options.type === 'radio';
	    	var isCheckbox = options && options.type === 'checkbox';
	    	
	    	if(notCustom){
	    		checkDiv = this.addDefaultCheckClass(checkDiv,inline);
			}else {
				checkDiv = this.addCustomCheckClass(checkDiv,inline);
				
				if(isRadio){
					checkDiv.addClass('custom-radio');
				}
				if(isCheckbox){
					checkDiv.addClass('custom-checkbox');
				}
			}
			
			return checkDiv;
	    };
	    
	    this.check = function(options){
	    	var _ = this;
	    	var newOpts = utils.copy(options,null,['custom','layout']);
	    	var inputs = this._super(newOpts);

	    	if($.isArray(inputs)){
	    		var divs = [];
	    		$.each(inputs,function(){
	    			var div = _.addCheckClass(this,options);
	    			divs.push(div);
	    		});
	    		return divs;
	    	}
    		
    		return _.addCheckClass(inputs,options);
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
		
		this.pend = function(input,options){
			var prepend = options && options.prepend;
			var append = options && options.append;
			if(!prepend && !append){
				return input;
			}
			
			var inputDiv = $('<div class="input-group mb-3" />');
			
			if(prepend){
				var preDiv = $('<div class="input-group-prepend" />').append(this.doPend(prepend));
				inputDiv.append(preDiv);
			}
			
			inputDiv.append(input);
			
			if(append){
				var appDiv = $('<div class="input-group-append" />').append(this.doPend(append));
				inputDiv.append(appDiv);
			}
			
			return inputDiv;
		};
		
		this.doPend = function(body){
			var _ = this;
			if($.isArray(body)){
				var eles = [];
				$.each(body,function(){
					eles.push(_.doPend(this));
				});
				return eles;
			}
			
			if(utils.isString(body)){
				return $("<span class='input-group-text' />").text(body);
			}
			return this.build(body);
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
	    	return this.check(opts);
	    }
	    
	    if(opts.type == "checkbox"){
	    	return this.check(opts);
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
	    input = this.pend(input,opts);

	    /**
	     *if(opts && opts.icon){
	    	input.prepend($("<i class='fa fa-"+opts.icon+"' />"));
	    } 
	     * **/
	    
	    
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

