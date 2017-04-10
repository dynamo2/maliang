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

var HTMLGenerator = Class.extend({
	inputTypes:["text","htmlEditor","hidden","between","radio","date","select","select2","checkbox","button","submit","reset"],
	
	isInput:function(tn){
		return utils.hasName(this.inputTypes,tn);
	},
	
	isButton:function(type){
    	return utils.hasName(['button','submit','reset'],type);
    },
	
	build:function(opts){
		var curr = this;
		
		if($.isArray(opts)){
			return curr.buildArray(opts);
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

			var ve = g.build(v);
			
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
	    	
	    	var fopts = {
	    		name:name+'from',
	    		placeholder:'From',
	    		type:opts.input,
	    	};
	    	var topts = {
	    		type:opts.input,
	            name:name+'to',
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
	    			if(opts.value && opts.value.from){
		    			fopts.value = opts.value.from;
		    		}
	    			if(opts.value && opts.value.to){
	    				topts.value = opts.value.to;
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

		return this._super(opts);
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
		var form = this.build(utils.copy(options,{tag:"form",action:action},['type','body']));
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
	
	ScrollableTable:function (options){
	    var _ = this;
	    
	    this.table = function(opts){
	        var table = $("<table />");
	        
	        this.full(table,this.readWithDefaultConfig(opts.table,this.config.table));
	        
	        table.append(this.thead(opts.head));
	        table.append(this.tbody(opts.body));
	        
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
	            "class":"table table-striped table-bordered table-hover dataTable no-footer",
	            "id":"datatable_products",
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
	
	/**
	 * <div class="select2-container form-control input-medium select2me" id="s2id_autogen5">
		    <a href="javascript:void(0)" class="select2-choice select2-default" tabindex="-1"> 
		        <span class="select2-chosen" id="select2-chosen-6">Select...</span>
		        <abbr class="select2-search-choice-close"></abbr> 
		        <span class="select2-arrow" role="presentation"><b role="presentation"></b></span>
		    </a>
		    <label for="s2id_autogen6" class="select2-offscreen"></label>
		    <input class="select2-focusser select2-offscreen" type="text" aria-haspopup="true" role="button" aria-labelledby="select2-chosen-6" id="s2id_autogen6" />
		</div>
		<select class="form-control input-medium select2me select2-offscreen" data-placeholder="Select..." tabindex="-1" title="">
		    <option value=""></option>
		    <option value="AL">Alabama</option>
		    <option value="WY">Wyoming</option>
		</select>
	 * 
	 * **/
	input:function(opts){
		var curr = this;
		
		this.date = function(opts){
	        var de = $('<div class="input-group date date-picker margin-bottom-5" data-date-format="dd/mm/yyyy" />');
	        var dinput = $('<input type="text" class="form-control form-filter input-sm" readonly="" />').prop('name',opts.name).prop('placeholder',opts.placeholder);
	        var dbnt = $('<span class="input-group-btn"><button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button></span>');
	        
	        de.append(dinput).append(dbnt);

	        return de;
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

	    	return element;
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
	    
	    var input = this._super(opts);
	    if(input.is("button")){
    		input.addClass("btn");
    	}else {
    		input.addClass("form-control");
    	}

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

