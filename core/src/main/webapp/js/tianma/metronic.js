function MetronicForm(json){
	var options = readForm(json);

	var mi = new MetronicInputs(options.inputs);
	var box = mi.build();
	
	var form = $("<form class='form-horizontal form-row-seperated' action='/flows/flow.htm' />");
	form.prop("id",options.id);
	form.append(box);
	
	//Test
	return box;
	
	//return form;
}

function MetronicInputs(inputs) {
	InputsGenerator.call(this,{
		row : "<div class='form-group' />",
		box : "<div class='form-body' />",
		columnInput : "<div class='col-md-10' />",
		afterInput : function(input, options) {
			input.addClass("form-control");
		},
		afterColumnLabel : function(label, options) {
			label.addClass("col-md-2 control-label");

			if (options.request) {
				label.append("<span class='request'>*</span>");
			}
		},
		inputs:inputs
	});
}

var pagniate = {
};

var productTable = {
    head:{
        heading:[
            {width:'10%',text:'ID'},
            {width:'15%',text:'Product&nbsp;Name'},
            {width:'15%',text:'Category'},
            {width:'10%',text:'Price'},
            {width:'10%',text:'Quantity'},
            {width:'15%',text:'Date&nbsp;Created'},
            {width:'10%',text:'Status'},
            {width:'10%',text:'Actions'}
        ],
        filter:[
                [],
                {type:'input.text',name:'order[number]'},
                {type:'input.between',name:'order[date]',input:'date'},
                {type:'input.text',name:'order[customer][name]'},
                {type:'input.text',name:'order[ship][to]'},
                {type:'input.between',name:'order[basic][price]',input:'text'},
                {type:'input.between',name:'order[purchase][price]',input:'text'},
                {type:'input.select',name:'order[status]',options:{}},
                {type:'input.submit',click:''}
            ]
    },
    body:[
        ['ID','Product&nbsp;Name','Category','Price','','','Status','Actions'],
        ['ID','Product&nbsp;Name','Category','Price','','','Status','Actions'],
        ['ID','Product&nbsp;Name','Category','Price','','','Status','Actions']
    ]
};




/***
 * 
 * 
 * {
  title: '产品列表',
  data: ${data},
  ht:{
    template:'${theme.metronic}',
    options:{
      content: [
        ${G.NAV},${NAV},
        ['tableList', 
          ['名称', '价格','订单库存', '实际库存','邮费', '上架状态', '操作'], 
          each(products) {[
            this.name, this.price, this.orderStock, this.stock, this.postage.freight+'元',this.grounding, 
            [['a', '查看', {id: this.id,fid: 5}],
              ['a', '编辑', {id: this.id,fid: 1}],
              ['a', '删除', {id: this.id,fid: 4}]]
          ]}
        ]
    ]}
}}
 * 
 * 
 * **/
///prototype
var MG = {
	"ScrollableTable":function (options){
	    var _ = this;
	    var rdc = _.readWithDefaultConfig;
	    
	    this.build = function(){
	        var div = $('<div class="table-scrollable" />');
	        div.append(_.table(options));
	        
	        return div;
	    };
	    
	    this.table = function(opts){
	        var table = $("<table />").prop(_.readWithDefaultConfig(opts.table,_.config.table));
	        
	        table.append(_.thead(opts.head));
	        table.append(_.tbody(opts.body));
	        
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
	        if($.type(opts) == 'string'){
	            ele.text(opts);
	        }
	        
	        if($.isArray(opts)){
	        	ele.append(build(opts));
	        }
	        
	        if($.isPlainObject(opts)){
	        	if(opts.text){
	        		ele.text(opts.text);
	        	}
	            ele.prop(opts);
	        }
	        return ele;
	    };
	    
	    this.thead = function(opts){
	        var thead = $("<thead />");
	        var row = $('<tr role="row" class="heading" />').appendTo(thead);
	        
	        $.each(opts.heading,function(){
	            if(this.type == 'checker'){
	            }
	            
	            _.full($('<th />'),_.readWithDefaultConfig(this,_.config.thead.heading.th)).appendTo(row);
	        });
	        
	        return thead;
	    };
	    
	    
	    this.config = {
	        "table":{
	            "class":"table table-striped table-bordered table-hover dataTable no-footer",
	            "id":"datatable_products",
	            "aria-describedby":"datatable_products_info",
	            "role":"grid"
	        },
	        "thead":{
	            "heading":{
	                "th":{
	                    "class":"sorting",
	                    "tabindex":"0",
	                    "aria-controls":"datatable_products",
	                    "rowspan":"1",
	                    "colspan":"1"
	                }
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
	    
	    
	    return _.build();
	},
	
	///////////////////////////////////////////// portlet //////////////////////////////////////////////////
	
	'portlet-json':{
	    type:'portlet',
	    title:{
	        t:'Order Listing',
	        i:'shopping-cart',
	        links:[
	            {href:'javascript:;',t:'New Order',i:'plus'}
	        ],
	        tools:true,
	        buttons:[
	            {t:'Back',i:'angle-left'},
	            {t:'Reset',i:'reply'},
	            {t:'Save',i:'check',color:'green',click:''},
	            {t:'Save &amp; Continue Edit',i:'check-circle',color:'green',click:''}
	        ],
	        dropdownButton:{
	            t:'Tools',
	            menus:[
	                {href:'javascript:;',t:'Export to Excel'},
	                {href:'javascript:;',t:'Export to CSV'},
	                {href:'javascript:;',t:'Export to XML'},
	                'divider',
	                {href:'javascript:;',t:'Print Invoices'}
	            ]
	        }
	    },
	    body:{
	        type:'HT',
	        template:'',
	        places:{
	            info:{},
	            paginate:{},
	            select:{},
	            scrollableTable:{}
	        }
	    }
	},
	
	/********
	 * 
	 * 
	 * portlet
	 * 
	 * ***********/
	"fa-icon":function(icon){
        return $('<i class="fa fa-'+icon+'" />');
    },

    portlet:function(opts){
        var _ = this;
        
        this.g = function(){
            var p = $('<div class="portlet" />');
            if(opts.class){
                p.addClass(opts.class);
            }
            
            p.append(_.title(opts.title));
            p.append(_.body(opts.body));
        };
        
        this.title = function(opts){
            var t = $('<div class="portlet-title" />');
            if(opts.type){
                return t.append(build(opts));
            }
            
            //caption
            var c = $('<div class="caption">').text(opts.caption).append(fa-icon(opts.icon));
            t.append(c);
            
            //actions
            _.actions(opts.actions).appendTo(t);
            
            return t;
        };
        
        this.actions = function(opts){
            var da = $('<div class="actions" />');
            
            if($.isArray(opts.links)){
                $.each(opts.links,function(){
                    var a = $('<a class="btn default yellow-stripe">').prop('href',this.href).appendTo(da);
                    a.append(fa-icon(this.icon));
                    a.append($('<span class="hidden-480" />').text(this.text));
                });
            }
            
            if($.isArray(opts.buttons)){
                da.append(_.buttons(opts.buttons));
            }
            
            _.dropdownButton(opts.dropdownButton).appendTo(da);
            return da;
        };
        
        
        
        this.buttons = function(bnts){
            var els = [];
            $.each(bnts,function(){
                var be = $('<button class="btn"><i class="fa fa-'+this.icon+'"></i>'+this.text+'</button>');
                var co = this.color?this.color:'default';
                be.addClass(co);
                
                els.push(be);
            });
            return els;
        };
        
        this.dropdownButton = function(opts){
            var bg = $('<div class="btn-group">');
            var ba = $('<a class="btn default yellow-stripe dropdown-toggle" href="javascript:;" data-toggle="dropdown" />').appendTo(bg);
            var icon = opts.icon?opts.icon:'share';
            ba.append(fa-icon(icon));
            ba.append($('<span class="hidden-480">').text(opts.text));
            icon = opts.icon-right?opts.icon-right:'angle-down';
            ba.append(fa-icon(icon));
            bg.append(dropdownMenu({menus:opts.menus}));
            
            return bg;
        };
        
        this.body = function(opts){
            var b = $('<div class="portlet-body" />');
            if(opts.type){
                return b.append(build(opts));
            }
            
            return b;
        };
        
        return _.g();
    },
    dropdownMenu:function(opts){
        var _ = this;
        
        var ul = $('<ul class="dropdown-menu pull-right" />');
        
        $.each(opts.menus,function(){
            var l = $("<li />").appendTo(ul);
            
            if(this === 'divider'){
                l.addClass('divider');
            }else {
                var a = $("<a />").prop("href",this.href).appendTo(l);
                if(this.icon){
                    a.append($('<i class="icon-'+this.icon+'" />'));
                }
                a.text(this.text);
            }
        });
        
        return ul;
    },
    note:function(opts){
        var el = $('<div class="note" />');
        var text = '';
        if(opts.danger){
            el.addClass('note-danger');
            text = opts.danger;
        }
        
        $('<p />').text(text).appendTo(el);
        return el;
    },
    alert:function(opts){
        var el = $('<div class="Metronic-alerts alert fade in">');
        var text = '';
        var icon = '';
        if(opts.danger){
            el.addClass('alert-danger');
            text = opts.danger;
            icon = 'warning';
        }
        
        $('<button type="button" class="close" data-dismiss="alert" aria-hidden="true" />').appendTo(el);
        $('<i class="fa-lg fa fa-'+icon+'" />').appendTo(el);
        el.text(text);
        
        return el;
    },
    paginate:function(opts){
        var el = $('<div class="dataTables_paginate paging_bootstrap_extended" />');
        var el2 = $('<div class="pagination-panel" />').appendTo(el);
        
        var prev = $('<a href="#" class="btn btn-sm default prev disabled" title="Prev"><i class="fa fa-angle-left" /></a>');
        var pinput = $('<input type="text" class="pagination-panel-input form-control input-mini input-inline input-sm" maxlenght="5" style="text-align:center; margin: 0 5px;" />');
        var next = $('<a href="#" class="btn btn-sm default next disabled" title="Next"><i class="fa fa-angle-right" /></a>');
        var total = $('<span class="pagination-panel-total" />');
        
        el2.text('Page').append(prev).append(pinput).append(next).text('of').append(total);
        
        var psize = $('<div class="dataTables_length" />');
        var sel = $('<label />').appendTo(psize);
        sel.append($('<span class="seperator">|</span>')).text(View);
        
        var selectOptions = {
            name:'page[pageSize]',
            'class':"form-control input-xsmall input-sm input-inline",
            'aria-controls':"datatable_orders",
            options:{
                '10':10,
                '20':20,
                '50':50,
                '100':100,
                '150':150,
                '-1':'ALL'
            }
        };
        HTML.select(selectOptions).appendTo(psize);
        psize.text('records');
        
        return [el,psize];
    },
    
    input:{
    	testCode:function(){
    		var fromDate = {
    		    type:'date',
    		    name:'product[price][from]',
    		    placeholder:'From'
    		};
    		var toDate = {
    				type:'date',
        		    name:'product[price][to]',
        		    placeholder:'To'
        	};
    		
    		var db = {
    			    type:'input.between',
    			    name:'product[price]',
    			    input:'date'
    			};

    		input.date(fromDate);
    		input.date(toDate);
    		
    	},
    	
        date:function(opts){
            var de = '<div class="input-group date date-picker margin-bottom-5" data-date-format="dd/mm/yyyy" />';
            var dinput = $('<input type="text" class="form-control form-filter input-sm" readonly="" />').prop('name',opts.name).prop('placeholder',opts.placeholder);
            var dbnt = $('<span class="input-group-btn"><button class="btn btn-sm default" type="button"><i class="fa fa-calendar"></i></button></span>');
            
            de.append(dinput).append(dbnt);

            return de;
        },
        
        text:function(opts){
        	var input = this._super(opts);
        	input.addClass("form-control");
        	return input;
        },
        
        between:function(opts){
            var fopts = {
                name:opts.name+'[from]',
                placeholder:'From'
            };
            
            var topts = {
                name:opts.name+'[to]',
                placeholder:'To'
            };
            
            var f = eval(opts.input+"(fopts)");
            var t = eval(opts.input+"(topts)");
            
            return [f,t];
        }
    }
};


