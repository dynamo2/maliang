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

function FormBuilder(){
	var builder = this;
	
	this.newForm = function(formData){
		var formObj = $("<form />");
		formObj.attr("action",formData.action);
		formObj.attr("enctype",formData.enctype);
	
		for(var i = 0; i < formData.inputs.length; i++){
			var divObj = builder.newInputDiv(formData.inputs[i]);
			formObj.append(divObj);
		}
		
		return formObj;
	};
	
	this.newInputDiv = function(inputData){
		var divObj = $("<div></div>");
		var labelObj = $("<label></label>");
		labelObj.text(inputData.label);
		divObj.append(labelObj).append(newInput(inputData.info));
		return divObj;
	};
	
	this.newInput = function(dinfo){
		var newObj;
		var inputType = dinfo.type;
		if(inputType == "text"){
			newObj = builder.newText(dinfo);
		}else if(inputType == "select"){
			newObj = builder.newSelect(dinfo);
		}else if(inputType == "hidden"){
			newObj = builder.newHiddenText(dinfo);
		}else if(inputType == "label"){
			newObj = builder.newLabel(dinfo);
		}
		
		return newObj;
	};
	
	
	this.newSelect = function(data){
		var selObj = $("<select></select>");
		selObj.attr("name",data.name);
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
	
	this.newLabel = function(data){
		var labelObj = $("<label></label>");
		labelObj.attr("name",data.name);
		labelObj.text(data.value);
		return labelObj;
	};
	
	this.newText = function(data){
		var txtObj = $("<input type='text' />");

		txtObj.attr("value",data.value);
		txtObj.attr("name",data.name);
		
		return txtObj;
	};
	
	this.newHiddenText = function(data){
		var txtObj = $("<input type='hidden' />");

		txtObj.attr("value",data.value);
		txtObj.attr("name",data.name);
		
		return txtObj;
	};
};

var TM_tableListBuilder = new TableListBuilder();
var TM_formBuilder =  new FormBuilder();