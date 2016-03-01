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
		var selObj = $("<select></select>");
		selObj.attr("name",builder.addPrefix(data.prefix)+data.name);
		var defaultValue = data.value;
		
		for(var i = 0; i < data.options.length;i++){
			var option = data.options[i];
			var optObj = $("<option></option>");
			selObj.append(optObj);
			
			optObj.attr("value",option.key);
			optObj.text(option.label);
			if(option.key == defaultValue){
				optObj.attr("selected",true);
			}
		}
		return selObj;
	};
	
	this.newImg = function(imgData){
		var imgObj = $("<img />");
		//imgObj.attr("src",imgData.src);
		imgObj.attr("src","http://img4.douban.com/view/commodity_story/imedium/public/p6668828.jpg");
		return imgObj;
	};
	
	this.newRadio = function(data){
		var radioSpan = $("<span></span>");
		
		for(var idx in data.options){
			var option = data.options[idx];
			
			var radioObj = $("<input type='radio'>");
			radioObj.attr("name",builder.addPrefix(data.prefix)+data.name);
			radioObj.attr("value",option.key);
			
			var radioText = $("<label></label>");
			radioText.text(option.label);
			
			if(option.key == data.value){
				radioObj.attr("selected",true);
			}
			
			radioSpan.append(radioObj);
			radioSpan.append(radioText);
		}
		
		return radioSpan;
	};
	
	this.newCheckbox = function(data){
		var checkboxSpan = $("<span></span>");
		
		for(var idx in data.options){
			var option = data.options[idx];
			
			var checkboxObj = $("<input type='checkbox'>");
			
			checkboxObj.attr("name",builder.addPrefix(data.prefix)+data.name);
			checkboxObj.attr("value",option.key);
			checkboxObj.attr("checked",false);
			if(option.key == data.value){
				checkboxObj.attr("checked",true);
			}
			
			var checkboxText = $("<label></label>");
			checkboxText.text(option.label);
			
			checkboxSpan.append(checkboxObj);
			checkboxSpan.append(checkboxText);
		}
		
		return checkboxSpan;
	};
	
	this.newInput = function(data){
		var txtObj = $("<input />");

		txtObj.attr("value",data.value);
		txtObj.attr("name",builder.addPrefix(data.prefix)+data.name);
		
		return txtObj;
	};
	
	this.addPrefix = function(prefix){
		if(prefix != "undefined" && prefix != null && prefix != ""){
			return prefix+".";
		}
		return '';
	}
	
	this.newTextarea = function(data){
		var txtObj = $("<textarea />");

		txtObj.text(data.value);
		txtObj.attr("name",builder.addPrefix(data.prefix)+data.name);
		
		return txtObj;
	};
	
	this.newText = function(data){
		var txtObj = builder.newInput(data);
		txtObj.attr("type","text");
		return txtObj;
	};
	
	this.newHiddenText = function(data){
		var txtObj = builder.newInput(data);
		txtObj.attr("type","hidden");
		return txtObj;
	};
	
	this.newFileText = function(data){
		var txtObj = builder.newInput(data);
		txtObj.attr("type","file");
		return txtObj;
	};
	
	this.newNumberInput = function(data){
		var txtObj = builder.newInput(data);
		txtObj.attr("type","number");
		return txtObj;
	};
	
	this.newDateInput = function(data){
		var txtObj = builder.newText(data);
		txtObj.datepicker();
		return txtObj;
	};
	
	this.newLabel = function(data){
		var labelObj = $("<label></label>");
		labelObj.attr("name",builder.addPrefix(data.prefix)+data.name);
		labelObj.text(data.text);
		return labelObj;
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
		var divObj = $("<div></div>");
		for(var idx in inputs){
			var inputData = inputs[idx];
			divObj.append(builder.newItem(inputData));
		}
		
		return divObj;
	};
	
	this.newItem = function(inputData){
		var divObj = $("<div></div>");
		var labelObj = $("<label></label>");
		labelObj.text(inputData.label);
		divObj.append(labelObj);
		
		var inputObj = builder.newInputElement(inputData);
		
		divObj.append(inputObj);
		return divObj;
	};
	
	this.newInputElement = function(inputData){
		if(!inputData.type){
			inputData.type = "text";
		}
		if(inputData.val){
			inputData.value = inputData.val;
		}
		
		
		if(inputData.type == "text"){
			return TM_htmlBuilder.newText(inputData);
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
		}
	};
	
	this.newLabel = function(data){
		var labelObj = $("<label></label>");
		labelObj.attr("name",data.name);
		labelObj.text(data.value);
		return labelObj;
	};
	
};
function UIListBuilder(){
	var builder = this;
	
	this.newInputs = function(itemData){
		var divObj = $("<div></div>").addClass("ul-list");
		
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
		
		TM_formBuilder.newInputElement(options.maxIndex).appendTo(divObj);
		
		addBntObj.click(function(){
			var ulObj = builder.newItemInputs(options.emptyItem,options);
			divObj.append(ulObj);
		});
		
		return divObj;
	};
	
	this.newItemInputs = function(inputItem,options){
		var ulObj = $("<ul></ul>");
		ulObj.attr("row",options.maxIndex.value);
		
		var col = 0;
		if(options.headers){
			$.each(options.headers,function(idx){
				var fieldInput = inputItem[idx];
				
				var liObj = $("<li></li>").attr("column",col++).appendTo(ulObj);
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
		var liObj = $("<li></li>");
		if(col){
			liObj.attr("column",col);
		}
		liObj.append(delBntObj);
		ulObj.append(liObj);
	}
	
	this.newUIList = function(listData){
		var divObj = $("<div></div>").addClass("ul-list");
		
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