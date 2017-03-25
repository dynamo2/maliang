function appendToForm(formPrefix,options){
	var inputs = readInputs(options,formPrefix);
	
	var formId = formPrefix+"\\.form";
	var table = $("#"+formId).find("table");
	
	pt(ts(inputs));
	
	var ftable = new FormTable();
	ftable.appendToTable(table,inputs);
}

function Form(json){
	var options = readForm(json);
	pt(ts(options));
	var ft = new FormTable();
	ft.init(options);
	
	return ft.form;
}

function readFormDatas(form) {
	var inputs = form.find(":input");

	var reqDatas = {};
	$.each(inputs, function() {
		if ($(this).attr("type") == "radio" 
				|| $(this).attr("type") == "checkbox") {
			if (!this.checked) {
				return;
			}
		}
		
		var key = $(this).attr("name");
		var oldVal = reqDatas[key];
		if(oldVal){
			if(!$.isArray(oldVal)){
				oldVal = [oldVal];
				reqDatas[key] = oldVal;
			}
			oldVal.push($(this).val());
		}else {
			reqDatas[key] = $(this).val();
		}
	});

	return reqDatas;
}



/***
 * 构建以<table>排版的form表单
 * ***/
function FormTable(){
	var _ = this;
	var table;
	var hiddenTd;
	var form;
	
	this.init = function(options){
		_.form = $('<form action="/flows/flow.htm" method="post" />');
		_.form.attr("id",options.id);
		
		_.newTable();
		_.addInputs(options.inputs);
		
		_.form.append(_.table);
		return _.form;
	};
	
	this.appendToTable = function(table,inputs){
		_.table = table;
		_.addInputs(inputs);
	};
	
	this.addInputs = function(inputs){
		var lastTd = null;
		$.each(inputs,function(){
			if(_.isHidden(this)){
				_.addHidden(this);
				return;
			}else if(_.isNewLine(this) || !(lastTd)){
				lastTd = _.newLine(this);
				return;
			}else {
				_.append(lastTd,this);
			}
		});
	};
	
	this.newLine = function(opts){
		var tr = $("<tr />");
		var ltd = $("<td class='label' />");
		var etd = $("<td />");
		_.table.append(tr.append(ltd).append(etd));
		
		ltd.text(_.readLabel(opts));
		if(_.isGroup(opts)){
			_.append(etd,opts);
		}else if(_.isList(opts)){
			_.buildListInputs(opts).appendTo(etd);
		}else {
			_.appendInput(etd,opts);
		}
		
		return etd;
	};
	
	/**
	 * 生成列表编辑模式
	 * ***/
	this.buildListInputs = function(opts){
		var listTable = $("<table class='tableList' cellpadding='0' cellspacing='1' />");
		var tr = $("<tr />").appendTo(listTable);
		
		if($.isArray(opts.type.header)){
			$.each(opts.type.header,function(){
				$("<th class='header' />").text(this).appendTo(tr);
			});
		}
		
		listTable.data("index",0);
		var newTemplate = null;
		$.each(opts.type.inputs,function(){
			if(newTemplate == null){
				newTemplate = _.readNewInputs(this);
			}
			_.addListRow(listTable,this);
		});
		
		if(newTemplate){
			tr = $("<tr />").prependTo(listTable);
			var td = $("<td colspan='"+newTemplate.length+"' />").appendTo(tr);
			var addButton = $("<input type='button' value='添加' />").appendTo(td);
			addButton.click(function(){
				_.addListRow(listTable,newTemplate);
			});
		}
		return listTable;
	};
	
	/***
	 * 根据原始input组生成[‘新增’input组]
	 * **/
	this.readNewInputs = function(inputs){
		var newInputs = [];
		if($.isArray(inputs)){
			$.each(inputs,function(){
				newInputs.push(utils.copy(this,{},['value']));
			});
		}
		return newInputs;
	};
	
	/***
	 * 根据inputs添加[编辑row]到指定table
	 * **/
	this.addListRow = function(listTable,inputs){
		var tr = $("<tr />").appendTo(listTable);
		var hidden = $("<td class='hidden' />").appendTo(tr);
		var idx = listTable.data("index");
		$.each(inputs,function(){
			var oldName = this.name;
			
			_.addListIndex(this,idx);
			
			if(_.isHidden(this)){
				_.append(hidden,this);
			}else {
				_.append($("<td />").insertBefore(hidden),this);
			}
			
			this.name = oldName;
		});
		listTable.data("index",idx+1);
	};
	
	/***
	 * 将input.name转换成数组格式：
	 *    如：provice.city 转换成 province.0.city
	 * **/
	this.addListIndex = function(opts,idx){
		if(!opts || !opts.name 
				|| !utils.isString(opts.name))return;
		
		if(opts.name.charAt(opts.name.length-1) == '.'){
			opts.name = opts.name.substr(0,opts.name.length-1);
			return;
		}
		
		var n = '';
		var ns = opts.name.split(".");
		if(ns.length > 1){
			for(var i = 0; i<ns.length-1;i++){
				if(n.length > 0){
					n += '.';
				}
				n += ns[i];
			}
		}
		
		if(n.length > 0){
			n += '.';
		}
		n += idx+'.'+ns[ns.length-1];
		
		opts.name = n;
	};
	
	/**
	将opts解析出来的元素[label,input]添加到参数td中
	***/
	this.append = function(td,opts){
		if(_.isGroup(opts)){
			var groupInps = opts.type && opts.type.inputs;
			_.append(td,groupInps);
		}else if($.isArray(opts)){
			var lastLine = $("<div />").appendTo(td);
			$.each(opts,function(){
				if(_.isNewLine(this)){
					lastLine = $("<div />").appendTo(td);
				}
				_.append(lastLine,this);
			});
		}else {
			if(_.isPreLabel(opts)){
				_.appendLabel(td,opts);
			}
			_.appendInput(td,opts);
			
			if(_.isPostLabel(opts)){
				_.appendLabel(td,opts);
			}
		}

		return td;
	};
	
	this.isPreLabel = function(opts){
		if(opts && opts.label && utils.isString(opts.label)){
			var start = opts.label.slice(0,1);
			return !(start === '?');
		}
		return false;
	};
	
	this.isPostLabel = function(opts){
		if(opts.label && utils.isString(opts.label)){
			var start = opts.label.slice(0,1);
			if(start === '?'){
				opts.label = opts.label.slice(1);
				return true;
			}
		}
		return false;
	};
	
	this.appendLabel = function(td,opts){
		var label = $("<label />");
		label.text(_.readLabel(opts));
		td.append(label);
		return td;
	};
	
	this.appendInput = function(td,opts){
		if(_.canSelect(opts)){
			var selOpts = opts.type;
			if($.isPlainObject(selOpts)){
				opts.type = selOpts.name;
				utils.copy(selOpts,opts,['name']);
			}
		}
		td.append(_.input(opts));
		return td;
	};
	
	this.isHidden = function(opts){
		return opts && opts.type === 'hidden';
	};
	
	this.isNewLine = function(opts){
		return opts && (opts.newLine === true || opts.newLine === LINE_BREAK);
	};
	
	this.isGroup = function(opts){
		return _.isType(opts,'group');
	};
	
	this.isList = function(opts){
		return _.isType(opts,'list');
	};
	
	this.canSelect = function(opts){
		return _.isType(opts,'select') || _.isType(opts,'radio') || _.isType(opts,'checkbox');
	};
	
	this.isType = function(opts,tname){
		if(opts && opts.type){
			if($.isPlainObject(opts.type)){
				return opts.type.name === tname;
			}
			
			return opts.type === tname;
		}
		return false;
	};
	
	this.newTable = function(){
		_.table =  $("<table cellspacing='0' />");
		var htr = $("<tr class='hidden' />");
		_.hiddenTd = $("<td />");
		htr.append(_.hiddenTd);
		
		_.table.append(htr);
	};
	
	this.addHidden = function(opts){
		_.hiddenTd.append(_.input(opts));
	};
	
	this.input = function(opts){
		return TM_formBuilder.newInputElement(_.readInputOptions(opts));
	};
	
	
	this.readLabel = function(opts){
		return (opts && opts.label)?opts.label:'';
	};
	
	this.readInputOptions = function(opts){
		var inpOpts = {};
		utils.copy(opts,inpOpts,['newLine','label']);
		
		return inpOpts;
	};
}


function readForm(json) {
	var _ = this;
	var source = json;

	var tag = 'form';
	var htmlOption = {
		tag : 'form'
	};

	var prefix = null;
	var inputs = null;

	this.read = function() {
		_.readHtmlOption();
		
		if ($.isArray(source) && source.length >= 3) {
			inputs = readInputs(source[2],prefix);
		}
	};

	this.readHtmlOption = function() {
		if ($.isArray(source) && source.length >= 2) {
			var obj = source[1];

			if ($.isPlainObject(obj)) {
				utils.copy(obj, htmlOption, null);
			} else if (utils.isString(obj)) {
				htmlOption.id = obj + '.' + tag;
				prefix = obj;
			}
		}
	};

	/*
	this.readInputs = function() {
		inputs = [];
		if ($.isArray(source) && source.length >= 3) {
			var obj = source[2];
			if ($.isArray(obj)) {
				$.each(obj, function(k, val) {
					if (val) {
						inputs.push(readInput(val, prefix));
					}
				});
			}
		}
	};
	*/

	_.read();
	htmlOption.inputs = inputs;
	return htmlOption;
}

function readInputs(options,prefix){
	var inputs = [];
	
	if ($.isArray(options) && options.length > 0) {
		var first = options[0];
		if($.isArray(first)){
			$.each(options, function() {
				if (this) {
					inputs.push(readInput(this, prefix));
				}
			});
		}else if(utils.isString(first)) {
			inputs.push(readInput(options, prefix));
		}
	}
	return inputs;
}

function readInput(opts, prefix) {
	var _ = this;
	var input = {};

	this.read = function() {
		var obj = opts;
		if ($.isArray(obj)) {
			if ($.isArray(obj[0])) {
				input = [];

				$.each(obj, function(k, val) {
					input.push(readInput(val, prefix));
				});
			} else {
				input.newLine = _.newLine();
				input.name = _.readName();
				input.label = _.readLabel();
				input.type = _.readType();
				input.value = _.readValue();

				var others = _.readOthers();
				if (others) {
					input.events = _.readEvents(others);
				}
			}
		} else if ($.isPlainObject(obj)) {
			input = {
				name : obj.name ? obj.name : obj.n,
				label : obj.label ? obj.label : obj.l,
				type : obj.type ? obj.type : obj.t,
				value : obj.value ? obj.value : obj.v,
				newLine : obj.newLine ? obj.newLine : obj.nl
			};
		}
	};

	this.readEvents = function(defOpts) {
		if (!defOpts)
			return EMPTY;

		var events = {};
		var eventNames = [ 'change', 'click', 'dbclick', 'blur', 'focus' ];
		$.each(eventNames, function() {
			if (defOpts[this]) {
				events[this] = defOpts[this];
				defOpts[this] = EMPTY;
			}
		});

		return events;
	};

	this.readType = function() {
		var type = opts[2];
		if (!type) {
			type = 'text';
		} else if ($.isArray(type)) {
			var topts = {};
			topts.name = type[0];

			var list = type.length >= 2 ? type[1] : null;
			if (topts.name == 'select' || topts.name == 'radio'
					|| topts.name == 'checkbox') {
				topts.options = list;

				if (type.length >= 3 && $.isPlainObject(type[2])) {
					var defOpts = type[2];
					topts.events = _.readEvents(defOpts);
					utils.copy(defOpts, topts, [ 'options' ]);
				}
			} else if (topts.name == 'group') {
				if ($.isArray(list)) {
					topts.inputs = [];
					$.each(list, function() {
						topts.inputs.push(readInput(this, input.name));
					});
				}
			} else if (topts.name == 'list') {
				topts.header = type[1];
				topts.inputs = [];
				$.each(type[2], function(idx, val) {
					topts.inputs[idx] = [];
					$.each(val, function() {
						topts.inputs[idx].push(readInput(this, input.name));
					});
				});
			}

			type = topts;
		}
		return type;
	};

	this.readName = function() {
		var name = opts[0];
		if (name && name.slice(0, 1) == '$') {
			return name.slice(1);
		}

		return prefix ? prefix + '.' + name : name;
	};

	this.readLabel = function() {
		return opts[1];
	};

	this.readValue = function() {
		return opts[3];
	};

	this.readOthers = function() {
		if (opts.length >= 4) {
			return opts[4];
		}

		return null;
	};

	this.newLine = function() {
		var last = opts[opts.length - 1];
		if (last == LINE_BREAK) {
			return opts.pop();
		}
		return opts[-1];
	};

	_.read();
	return input;
}


/****************************
 * 
 * Old code
 * 
 * **************************/
function buildFormInputs(options) {
	return newInputsDiv(options.inputs);
}

function newInputsDiv(inputs) {
	var container = $("<ul />");

	defaultButtons().appendTo(container);

	$.each(inputs, function() {
		newInputItem(this).appendTo(container);
	});

	return container;
}

function defaultButtons() {
	var item = $("<li />");

	$("<input type='reset' value='重置' />").appendTo(item);
	$("<input type='submit' value='保存' />").appendTo(item);

	return item;
}

function newInputItem(options) {
	var item = $("<li />");

	if (options.label) {
		$("<label />").text(options.label).appendTo(item);
	}

	if (options.type == 'hidden') {
		item.prop("className", "hidden");
	}

	newInputElement(options).appendTo(item);
	return item;
}

function newInputElement(options) {
	var htmlType = options.type;
	if (htmlType === "list") {
		return newListEditor(options);
	}

	var newOpt = eliminate(options, [ "label" ]);
	if (newOpt.prefix && newOpt.name) {
		newOpt.name = newOpt.prefix + '.' + newOpt.name;
	}
	if (htmlType == "select") {
		return newHtmlSelect(newOpt);
	} else if (htmlType == "html") {
		return newHtmlEditor(newOpt);
	}

	newOpt.tag = "input";
	return buildHtmlElement(newOpt);
}

function newHtmlEditor(options) {
	var divObj = $("<div />").css("width", "830px").css("margin", "10px");
	var areaObj = $("<textarea />").prop("name", options.name).text(
			options.text).appendTo(divObj);
	areaObj.wysiwyg(wysiwygFullBars);
	return divObj;
}

function newHtmlSelect(selOpts) {
	var selObj = $("<select />");
	selObj.prop("name", selOpts.name);

	var dVal = selOpts.value;
	$.each(selOpts.options, function() {
		var opts = this;
		if (!$.isPlainObject(this)) {
			opts = {
				label : this,
				value : this
			};
		}
		opts.tag = 'option';

		var optObj = buildHtmlElement(opts).appendTo(selObj);
		if (opts.value == dVal) {
			optObj.prop("selected", true);
		}
	});

	/*
	 * for(idx in options.options){ var option = options.options[idx];
	 * option.tag = "option";
	 * 
	 * var optObj = buildHtmlElement(option).appendTo(selObj); if(option.value ==
	 * defaultValue){ optObj.prop("selected",true); } }
	 */
	return selObj;
}

function eliminate(map, excls) {
	var newMap = {};
	$.each(map, function(k, v) {
		for (idx in excls) {
			if (k == excls[idx]) {
				return true;
			}
		}

		newMap[k] = v;
	});

	return newMap;
}

/*******************************************************************************
 * List Editor
 ******************************************************************************/
function newListEditor(itemData) {
	var divObj = $("<div></div>").addClass("ul-list");

	var options = initOptions(itemData);
	addNewButton(options, divObj);
	newListHeader(options.header).appendTo(divObj);

	if (itemData.value) {
		$.each(itemData.value, function(k, item) {
			newListItem(item, options).appendTo(divObj);
		});
	}

	newInputElement(options.maxIndex).appendTo(divObj);

	return divObj;
};

function newListItem(inputItem, options) {
	var ulObj = $("<ul></ul>").attr("row", options.maxIndex.value);

	var col = 0;
	if (options.header) {
		$.each(options.header, function(idx) {
			var fieldInput = inputItem[idx];

			var liObj = $("<li></li>").attr("column", col++).appendTo(ulObj);
			if (fieldInput instanceof Array) {
				$.each(fieldInput, function(k, opt) {
					if (!opt)
						return true;

					newItemInput(opt, options).appendTo(liObj);
				});
				// options.emptyItem[idx] = fieldInput;
			} else if (fieldInput) {
				newItemInput(fieldInput, options).appendTo(liObj);
				// options.emptyItem[idx] = fieldInput;
			}
		});
	}

	addItemDeleteButton(ulObj, col);
	options.maxIndex.value++;

	return ulObj;
};

function newItemInput(itemOpt, options) {
	var oldName = itemOpt.name;
	itemOpt.name = options.maxIndex.value + '.' + itemOpt.name;
	if (!itemOpt.prefix)
		itemOpt.prefix = options.prefix;

	var inputObj = newInputElement(itemOpt).css("margin-left", "5px");

	itemOpt.name = oldName;
	itemOpt.value = null;

	return inputObj;
}

function initOptions(items) {
	return {
		label : items.label,
		header : items["item-labels"],
		prefix : items["item-prefix"],
		emptyItem : items.template,
		maxIndex : {
			value : 0,
			type : 'hidden',
			prefix : items["item-prefix"],
			name : 'maxIndex'
		}
	};
}

function addNewButton(options, divObj) {
	return $("<input type='button' id='addNewItemRow' />").val(
			'添加' + options.label).click(function() {
		newListItem(options.emptyItem, options).appendTo(divObj);
	}).appendTo(
			$("<li />").appendTo(
					$("<ul />").addClass("operator").appendTo(divObj)));
}

function newListHeader(header) {
	var itemHeaders = [];
	$.each(header, function(key, label) {
		itemHeaders.push({
			'name' : key,
			'label' : label
		});
	});
	itemHeaders.push({
		'name' : 'operator',
		'label' : '操作'
	});

	var ulObj = $("<ul></ul>").addClass("header");
	var col = 0;
	if (itemHeaders) {
		$.each(itemHeaders, function(key, value) {
			if (value) {
				$("<li></li>").attr("column", col++).text(value.label)
						.appendTo(ulObj);
			}
		});
	}

	return ulObj;
};

function addItemDeleteButton(ulObj, col) {
	return $("<input type='button' value='删除' />").click(function() {
		ulObj.remove();
	}).appendTo($("<li></li>").attr("column", col).appendTo(ulObj));
}

/*
 * function UIListBuilder(){ var builder = this;
 * 
 * this.newUIList = function(listData){ var divObj = $("<div></div>").addClass("ul-list");
 * 
 * divObj.append(builder.newListHeader(listData.header));
 * 
 * var uls = builder.newListData(listData); for(var idx in uls){
 * divObj.append(uls[idx]); } return divObj; };
 * 
 * this.newListData = function(listData){ var headerIndex = []; var idx = 0;
 * for(var i in listData.header){ headerIndex[idx++] = listData.header[i].name; }
 * 
 * var ulAry = []; idx = 0; for(var i in listData.data){ var modelData =
 * listData.data[i];
 * 
 * var ulObj = $("<ul></ul>"); for(var ii in headerIndex){ var key =
 * headerIndex[ii]; var htmlData = modelData[key];
 * 
 * var liObj = $("<li></li>"); var htmlObj; if(htmlData instanceof Array){
 * for(var iii in htmlData){ htmlObj =
 * TM_htmlBuilder.newHtmlElement(htmlData[iii]); liObj.append(htmlObj); } }else {
 * htmlObj = TM_htmlBuilder.newHtmlElement(htmlData); liObj.append(htmlObj); }
 * 
 * ulObj.append(liObj); }
 * 
 * ulAry[idx++] = ulObj; }
 * 
 * return ulAry; }; }
 */
