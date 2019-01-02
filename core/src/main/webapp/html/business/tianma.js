function TableListBuilder(){
	var builder = this; 
		
	this.newTable = function(collectionList){
		var tableObj = $("<table />");
		tableObj.append(builder.newHeader(collectionList.order,collectionList.label));
		
		for(var i = 0; i < collectionList.data.length; i++){
			tableObj.append(builder.newTD(collectionList.order,collectionList.data[i]));
		}
		return tableObj;
	};
	
	this.newHeader = function (orders,labels){
		var trObj = $("<tr />");
		for(var i = 0; i < orders.length; i++){
			var thObj = $("<th />");
			thObj.text(labels[orders[i]]);
			trObj.append(thObj);
		}
		return trObj;
	};
	
	this.newTD = function (orders,data){
		var trObj = $("<tr />");
		for(var i = 0; i < orders.length; i++){
			var tdObj = $("<td />");
			tdObj.text(data[orders[i]]);
			trObj.append(tdObj);
		}
		return trObj;
	};
};

function HtmlBuilder(){
	var builder = this;
	this.newHtmlElement = function(htmlData){
		if(htmlData.type == "a"){
			return builder.newA(htmlData);
		}
		if(htmlData.type == "label"){
			return builder.newLabel(htmlData);
		}
		if(htmlData.type == "img"){
			return builder.newImg(htmlData);
		}
	};

	this.newSelect = function(data){
		var selObj = $("<select />");
		selObj.attr("name",builder.addPrefix(data.prefix)+data.name);
		
		var options = builder.formalizeSelectOptions(data.options);
		selObj.append(builder.buildOptions(options,data.value));
		
		var newProps = utils.copy(data,{},['name','type','options']);
		this.props(selObj,newProps);
		
		return selObj;
	};
	
	this.buildOptions = function(data,selectedVal){
		if($.isArray(data)){
			var list = [];
			$.each(data,function(){
				list.push(builder.newOption(this,selectedVal));
			});
			return list;
		}else {
			return builder.newOption(data,selectedVal);
		}
	};
	
	this.newOption = function(opts,selectedVal){
		if(!opts)return null;
		
		if(!$.isPlainObject(opts)){
			opts = {key:opts,label:opts};
		}
		
		var optObj = $("<option />");
		optObj.val(opts.key);
		optObj.text(opts.label);
		
		if(selectedVal){
			if(optObj.val() == selectedVal){
				optObj.attr("selected",true);
			}
		}
		
		return optObj;
	}
	
	this.newImg = function(imgData){
		var imgObj = $("<img />");
		//imgObj.attr("src",imgData.src);
		imgObj.attr("src","http://img4.douban.com/view/commodity_story/imedium/public/p6668828.jpg");
		return imgObj;
	};
	
	this.newRadio = function(data){
		var eles = [];
		var options = builder.formalizeSelectOptions(data.options);
		$.each(options,function(){
			var val = $.trim(this.key);
			var radio = builder.newInput(data)
				.prop("type","radio")
				.prop("id",data.name+'_'+val)
				.val(val)
				.prop("checked",utils.eq(val,data.value));
			
			
			if(data.checked){
				radio.prop("checked",data.checked);
			}
			
			//eles.push(radio);
			
			var label = $("<label />").prop("for",radio.prop('id'));
			if($.isPlainObject(this.label)){
				label.append(generator.build(this.label));
			}else {
				label.text(this.label);
			}
			eles.push($("<div />").append(radio).append(label));
		});
		
		return eles;
	};
	
	this.newCheckbox = function(data){
		var eles = [];
		var options = builder.formalizeSelectOptions(data.options);
		var nidx = 0;
		$.each(options,function(){
			var val = $.trim(this.key);
			var box = builder.newInput(data)
						.val(val)
						.prop("id",data.name+'_'+val)
						.prop("type","checkbox");
			
			if(utils.isString(data.value)){
				box.prop("checked",utils.eq(val,data.value));
			}else if($.isArray(data.value)){
				box.prop("checked",$.inArray(val,data.value) >= 0);
			}
			
			//eles.push($("<label />").append(box).append(this.label));
			
			var label = $("<label />").append(this.label).prop("for",box.prop('id'));
			eles.push($("<div />").append(box).append(label));
		});
		
		return eles;
	};
	
	this.newRadio_bak = function(data){
		var ul = builder.newBoxUL(data)
					.addClass('ul-radio');
		
		var options = builder.formalizeSelectOptions(data.options);
		$.each(options,function(){
			var li = $("<li />").appendTo(ul);
			var val = $.trim(this.key);
			builder.newInput(data)
				.prop("type","radio")
				.val(val)
				.prop("checked",val == $.trim(data.value))
				.appendTo(li);

			builder.newLabel2(this.label).appendTo(li);
			
		});
		
		return ul;
	};
	
	this.newCheckbox_bak = function(data){
		var ul = builder.newBoxUL(data)
					.addClass('ul-checkbox');
		
		var options = builder.formalizeSelectOptions(data.options);
		$.each(options,function(){
			var li = $("<li />").appendTo(ul);
			var val = $.trim(this.key);
			var box = builder.newInput(data)
						.val(val)
						.prop("type","checkbox")
						.appendTo(li);
			
			if(utils.isString(data.value)){
				box.prop("checked",val == $.trim(data.value));
			}else if($.isArray(data.value)){
				box.prop("checked",$.inArray(val,data.value) >= 0);
			}
		
			builder.newLabel2(this.label).appendTo(li);
		});
		
		return ul;
	};
	
	
	
	this.formalizeSelectOptions = function(options){
		if($.isPlainObject(options)){
			var temp = [];
			$.each(options,function(k,v){
				temp.push({
					key:k,
					label:v
				});
			});
			return temp;
		}
		return options;
	};
	
	this.newBoxUL = function(data){
		var ul = $("<ul />");
		if(data.layout && $.trim(data.layout) == 'vertical'){
			ul.addClass("vertical");
		}else {
			ul.addClass("horizontal");
		}
		return ul;
	}
	
	this.newLabel2 = function(options){
		var label = {tag:'label'};
		if(utils.isString(options)){
			label.text = options;
		}else if($.isPlainObject(options)) {
			utils.copy(options,label,null);
		}
		return buildHtmlElement(label);
	};
	
	this.newButton = function(data){
		if(!(data && data.type == "button")){
			return null;
		}
	
		var options = utils.copy(data,null,["type"]);
		return this.props($("<button type='button' />"),options);
	};
	
	this.newSubmit = function(data){
		if(!(data && data.type == "submit")){
			return null;
		}
	
		var options = utils.copy(data,null,["type"]);
		return this.props($("<button type='submit' />"),options);
	};
	
	this.newReset = function(data){
		if(!(data && data.type == "reset")){
			return null;
		}
	
		var options = utils.copy(data,null,["type"]);
		return this.props($("<button type='reset' />"),options);
	};
	
	this.props = function(ele,options){
		if(!options){
			return ele;
		}
		
		if(options.text){
			ele.text(options.text);
		}
		
		if(options.html){
			ele.html(options.html);
		}
		
		if(options.class){
			ele.addClass(options.class);
		}
		
		if($.isPlainObject(options.events)){
			$.each(options.events,function(k,v){
				var fun = eval(v);
				
				console.log(" on event : " + fun);
				
				ele.on(k,eval(v));
			});
		}
		
		options = utils.copy(options,null,["text","html","class","events"]);
		if(!$.isPlainObject(options)){
			options = {};
		}
		
		return ele.prop(options) && ele.attr(options);
	};
	
	this.newInput = function(data){
		var input = $("<input />");
		if(data){
			input.prop("value",data.value)
				.prop("type",data.type)
				.prop("name",builder.readName(data));
			
			if(data && data.class){
				input.addClass(data.class);
			}
			
			if($.isPlainObject(data.events)){
				for(x in data.events){
					var fun = data.events[x];
					input.on(x,eval(fun));
				}
			}
			
			var newOpts = utils.copy(data,null,['options','value','type','name','class','events']);
			input.attr(newOpts);
		}
		
		return input;
	};
	
	this.readName = function(data){
		return builder.addPrefix(data.prefix)+data.name;
	};
	
	this.addPrefix = function(prefix){
		if(prefix != undefined && prefix != null && prefix != ""){
			return prefix+".";
		}
		return '';
	}
	
	this.newTextarea = function(data){
		return $("<textarea />").text(data.value)
					.attr("name",builder.addPrefix(data.prefix)+data.name);
	};

	this.newFileText = function(data){
		var txtObj = builder.newInput(data);
		txtObj.attr("type","file");
		return txtObj;
	};
	
	/*
	this.newText = function(data){
		return builder.newInput(data).attr("type","text");
	};
	
	this.newHiddenText = function(data){
		var txtObj = builder.newInput(data);
		txtObj.attr("type","hidden");
		return txtObj;
	};
	
	this.newNumberInput = function(data){
		return builder.newInput(data).attr("type","number");
	};
	
	this.newDateInput = function(data){
		return builder.newText(data).attr("type","date");
		//txtObj.datepicker();
	};
	
	this.newSubmit = function(data){
		return builder.newInput(data).prop("type","submit");
	};
	
	this.newButton = function(data){
		return builder.newInput(data).prop("type","button");
	};
	*/
	
	this.newLabel = function(data){
		return $("<label />").attr("name",builder.readName(data)).text(data.text);
	};
	
	this.newA = function(data){
		var aObj = $("<a></a>");
		aObj.attr("href",data.href);
		aObj.text(data.text);
		return aObj;
	};
}		
function FormBuilder(){
	var builder = this;
	
	this.newForm = function(formData){
		var formObj = $("<form />");
		formObj.attr("method","post");
		formObj.attr("action",formData.action);
		formObj.attr("enctype",formData.enctype);
		formObj.attr("name",formData.name);
		
		for(var idx in formData.children){
			var obj;
			var htmlData = formData.children[idx];
			if(idx == "inputs"){
				obj = builder.newInputsDiv(htmlData);
			}else if(idx == "ul-list"){
				obj = TM_ulListBuilder.newUIList(htmlData);
			}
			formObj.append(obj);
		}
		
		/*
		var bntDiv = $("<div></div>").append($("<input type='button' value='返回'>"))
				.append($("<input type='reset' value='重置'>"))
				.append($("<input type='submit' value='保存'>"));
		formObj.append(bntDiv);*/
		
		
		return formObj;
	};
	
	this.newInputsDiv = function(inputs){
		var divObj = $("<div />");
		for(var idx in inputs){
			var inputData = inputs[idx];
			divObj.append(builder.newItem(inputData));
		}
		
		return divObj;
	};
	
	this.newItem = function(inputData){
		var divObj = $("<div />");
		
		$("<label />").text(inputData.label).appendTo(divObj);
		builder.newInputElement(inputData).appendTo(divObj);
		
		return divObj;
	};
	
	this.newInputElement = function(inputData){
		if(!inputData.type){
			inputData.type = "text";
		}
		if(inputData.val){
			inputData.value = inputData.val;
		}
		
		
		if(inputData.type == "select"){
			return TM_htmlBuilder.newSelect(inputData);
		}else if(inputData.type == "picture"){
			return TM_htmlBuilder.newFileText(inputData);
		}else if(inputData.type == "file"){
			return TM_htmlBuilder.newFileText(inputData);
		}else if(inputData.type == "radio"){
			return TM_htmlBuilder.newRadio(inputData);
		}else if(inputData.type == "checkbox"){
			return TM_htmlBuilder.newCheckbox(inputData);
		}else if(inputData.type == "textarea"){
			return TM_htmlBuilder.newTextarea(inputData);
		}else if(inputData.type == "button"){
			return TM_htmlBuilder.newButton(inputData);
		}else if(inputData.type == "submit"){
			return TM_htmlBuilder.newSubmit(inputData);
		}else if(inputData.type == "reset"){
			return TM_htmlBuilder.newReset(inputData);
		}else if(inputData.type == "label"){
			return builder.newLabel(inputData);
		}else if(inputData.type == "between"){
			var spanObj = $("<span></span>");
			var minObj = builder.newInputElement(inputData.min);
			var maxObj = builder.newInputElement(inputData.max);
			var speObj = $("<label>-</label>");
			
			spanObj.append(minObj).append(speObj).append(maxObj);
			
			return spanObj;
		}else if(inputData.type == "list"){
			return TM_ulListBuilder.newInputs(inputData);
		}else if(inputData.type == "htmlEditor"){
			return builder.newHtmlEditor(inputData);
		}else {
			return TM_htmlBuilder.newInput(inputData);
		}
		
		/*
		if(inputData.type == "text"){
			return TM_htmlBuilder.newText(inputData);
		}else if(inputData.type == "submit"){
			return TM_htmlBuilder.newSubmit(inputData);
		}else if(inputData.type == "button"){
			return TM_htmlBuilder.newButton(inputData);
		}else if(inputData.type == "select"){
			return TM_htmlBuilder.newSelect(inputData);
		}else if(inputData.type == "hidden"){
			return TM_htmlBuilder.newHiddenText(inputData);
		}else if(inputData.type == "date"){
			return TM_htmlBuilder.newDateInput(inputData);
		}else if(inputData.type == "number"){
			return TM_htmlBuilder.newNumberInput(inputData);
		}else if(inputData.type == "picture"){
			return TM_htmlBuilder.newFileText(inputData);
		}else if(inputData.type == "file"){
			return TM_htmlBuilder.newFileText(inputData);
		}else if(inputData.type == "radio"){
			return TM_htmlBuilder.newRadio(inputData);
		}else if(inputData.type == "checkbox"){
			return TM_htmlBuilder.newCheckbox(inputData);
		}else if(inputData.type == "textarea"){
			return TM_htmlBuilder.newTextarea(inputData);
		}else if(inputData.type == "label"){
			return builder.newLabel(inputData);
		}else if(inputData.type == "between"){
			var spanObj = $("<span></span>");
			var minObj = builder.newInputElement(inputData.min);
			var maxObj = builder.newInputElement(inputData.max);
			var speObj = $("<label>-</label>");
			
			spanObj.append(minObj).append(speObj).append(maxObj);
			
			return spanObj;
		}else if(inputData.type == "list"){
			return TM_ulListBuilder.newInputs(inputData);
		}else if(inputData.type == "html"){
			return builder.newHtmlEditor(inputData);
		}*/
	};
	
	this.newHtmlEditor = function(options){
		var divObj = $("<div style='width:600px;margin:10px;' />");
		var areaObj = $("<textarea />").prop("name",options.name)
							.text(options.value).appendTo(divObj);
		areaObj.wysiwyg(wysiwygFullBars);
		return divObj;
	};
	
	this.newLabel = function(data){
		return $("<label />").attr("name",data.name).text(data.value);
	};
	
};
function UIListBuilder(){
	var builder = this;
	
	this.newInputs = function(itemData){
		var divObj = $("<div />").addClass("ul-list");
		
		var addBntObj = $("<input type='button' id='addNewItemRow' />").val('添加新'+itemData.label).appendTo(divObj);
		$("<input type='submit' />").val('保存').appendTo(divObj);
		
		/***
		 * Build list header
		 * ***/
		var itemHeaders = [];
		$.each(itemData["item-labels"],function(key,label){
			itemHeaders.push({'name':key,'label':label});
		});
		itemHeaders.push({'name':'operator','label':'操作'});
		divObj.append(builder.newListHeader(itemHeaders));

		var options = {};
		options.maxIndex = {};
		options.emptyItem = {};
		options.headers = itemData["item-labels"];
		options.maxIndex.value = 0;
		options.maxIndex.type = "hidden";
		options.maxIndex.prefix = itemData["item-prefix"];
		options.maxIndex.name = "maxIndex";
		
		for(var idx in itemData.value){
			var ulObj = builder.newItemInputs(itemData.value[idx],options);
			divObj.append(ulObj);
		}
		
//		TM_formBuilder.newInputElement(options.maxIndex).appendTo(divObj);
		
		addBntObj.click(function(){
			var ulObj = builder.newItemInputs(options.emptyItem,options);
			divObj.append(ulObj);
		});
		
		return divObj;
	};
	
	this.newItemInputs = function(inputItem,options){
		var ulObj = $("<ul />");
		ulObj.attr("row",options.maxIndex.value);
		
		var col = 0;
		if(options.headers){
			$.each(options.headers,function(idx){
				var fieldInput = inputItem[idx];
				
				var liObj = $("<li />").attr("column",col++).appendTo(ulObj);
				if(fieldInput instanceof Array){
					$.each(fieldInput,function(key,value){
						if(!value)return true;
						
						var oldName = value.name;
						value.name = options.maxIndex.value+'.'+value.name;
						TM_formBuilder.newInputElement(value).css("margin-left","5px").appendTo(liObj);
						
						value.name = oldName;
						value.value = null;
					});
					
					options.emptyItem[idx] = fieldInput;
				}else if(fieldInput){
					var oldName = fieldInput.name;
					fieldInput.name = options.maxIndex.value+'.'+fieldInput.name;
					TM_formBuilder.newInputElement(fieldInput).css("margin-left","5px").appendTo(liObj);
					
					fieldInput.name = oldName;
					options.emptyItem[idx] = fieldInput;
					options.emptyItem[idx].value = null;
				}
			});
		}

		builder.addDeleteButton(ulObj,col);
		options.maxIndex.value++;
		
		return ulObj;
	};
	
	this.addDeleteButton = function(ulObj,col){
		var delBntObj = $("<input type='button' value='删除' />");
		delBntObj.click(function(){
			ulObj.remove();
		});
		var liObj = $("<li />");
		if(col){
			liObj.attr("column",col);
		}
		liObj.append(delBntObj);
		ulObj.append(liObj);
	}
	
	this.newUIList = function(listData){
		var divObj = $("<div />").addClass("ul-list");
		
		divObj.append(builder.newListHeader(listData.header));

		var uls = builder.newListData(listData);
		for(var idx in uls){
			divObj.append(uls[idx]);
		}
		return divObj;
	};
	
	this.newListData = function(listData){
		var headerIndex = [];
		var idx = 0;
		for(var i in listData.header){
			headerIndex[idx++] = listData.header[i].name;
		}
		
		var ulAry = [];
		idx = 0;
		for(var i in listData.data){
			var modelData = listData.data[i];
			
			var ulObj = $("<ul></ul>");
			for(var ii in headerIndex){
				var key = headerIndex[ii];
				var htmlData = modelData[key];
				
				var liObj = $("<li></li>");
				var htmlObj;
				if(htmlData instanceof Array){
					for(var iii in htmlData){
						htmlObj = TM_htmlBuilder.newHtmlElement(htmlData[iii]);
						liObj.append(htmlObj);
					}
				}else {
					htmlObj = TM_htmlBuilder.newHtmlElement(htmlData);
					liObj.append(htmlObj);
				}
				
				ulObj.append(liObj);
			}
			
			ulAry[idx++] = ulObj;
		}
		
		return ulAry;
	};
	
	this.newListHeader = function(headers){
		var ulObj = $("<ul></ul>").addClass("header");
		
		//alert(JSON.stringify(headers));
		var col = 0;
		if(headers){
			$.each(headers,function(key,value){
				if(value){
					$("<li></li>").attr("column",col++).text(value.label).appendTo(ulObj);
				}
			});
		}
		
		return ulObj;
	};
}





var TM_formBuilder =  new FormBuilder();
var TM_htmlBuilder =  new HtmlBuilder();
var TM_ulListBuilder = new UIListBuilder();
var TM_tableListBuilder = new TableListBuilder();

/*
function readFormDatas(form){
	var inputs = form.find(":input");
	
	var reqDatas = {};
	$.each(inputs,function(){
		if($(this).attr("type") == "radio"){
			if(!this.checked){
				return;
			}
		}
		reqDatas[$(this).attr("name")] = $(this).val();
	});

	return reqDatas;
}
*/