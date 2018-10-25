BootstrapGenerator.prototype.form = function(options){
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
		
		var fbody = _.build(options.body);
		form.append(fbody);
		
		var validate = null;
		if(typeof (fbody && fbody.data) === 'function'){
			validate = fbody.data("validate");
		}
		if(validate){
			form.validate(validate);
		}
	}
	
	return form;
}


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
BootstrapGenerator.prototype.formBody = function(options){
	this.readAllValidation = function(opts){
		var _ = this;
		$.each(validateCmds,function(){
			if(opts && opts[this]){
				_.readValidation(this,opts);
			}
		});
	};
	
	this.readValidation = function(validateCmd,opts){
		var validateMsg = opts && opts[validateCmd];
		if(validateMsg){
			var fname = opts && opts.name;
			
			var rule = validate.rules[fname];
			if(!rule){
				rule = {};
				validate.rules[fname] = rule;
			}
			rule[validateCmd] = true;
			
			var msg = validate.messages[fname];
			if(!msg){
				msg = {};
				validate.messages[fname] = msg;
			}
			msg[validateCmd] = validateMsg;
		}
	};
	
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
		
		/********************************************
		 * validate start
		 * ******************************************/
		_.readAllValidation(opts);
		/********************************************
		 * validate end
		 * ******************************************/
		
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
		if(!group || !$(group).is(".form-group")){
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
				var row = $("<div class='row' />");
				rowElements.push(row);
				
				var skipGroup = 0;
				$.each(els,function(idx,group){
					if($(group).is(".form-group")){
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
	var validate = {rules:{},messages:{}};
	//var fbody = $('<div class="form-body" />');
	
	var fbody = curr.build(utils.copy(options,{tag:"div"},['groups','type','layout','label']));
	var validateCmds = ["required","email","remote","minlength","maxlength","rangelength",
		"min","max","range","step","email","url","date","dateISO","number","digits","equalTo"];
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
	
	fbody.data('validate',validate);
	return fbody;
}