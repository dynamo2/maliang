function buildFormInputs(options){
	return newInputsDiv(options.inputs);
}

function newInputsDiv(inputs){
	var container = $("<ul />");

	defaultButtons().appendTo(container);
	
	$.each(inputs,function(){
		newInputItem(this).appendTo(container);
	});
	
	return container;
}

function defaultButtons(){
	var item = $("<li />");
	
	$("<input type='reset' value='重置' />").appendTo(item);
	$("<input type='submit' value='保存' />").appendTo(item);
	
	return item;
}

function newInputItem(options){
	var item = $("<li />");
	
	if(options.label){
		$("<label />").text(options.label).appendTo(item);
	}else {
		item.prop("className","hidden");
	}
	newInputElement(options).appendTo(item);
	
	return item;
}

function newInputElement(options){
	var newOpt = copyOptions(options,["label"]);

	var htmlType = newOpt.type;
	if(htmlType == "select"){
		return newHtmlSelect(newOpt);
	}else if(htmlType == "html"){
		return newHtmlEditor(newOpt);
	}
	
	newOpt.tag = "input";
	return buildHtmlElement(newOpt);
}

function newHtmlEditor(options){
	var divObj = $("<div />").css("width","830px").css("margin","10px");
	var areaObj = $("<textarea />").prop("name",options.name).
					text(options.text).appendTo(divObj);
	areaObj.wysiwyg(wysiwygFullBars);
	return divObj;
}

function newHtmlSelect(options){
	var selObj = $("<select />");
	selObj.prop("name",options.name);
	
	var defaultValue = options.value;
	for(idx in options.options){
		var option = options.options[idx];
		option.tag = "option";
		
		var optObj = buildHtmlElement(option).appendTo(selObj);
		if(option.value == defaultValue){
			optObj.prop("selected",true);
		}
	}
	return selObj;
}

function copyOptions(map,excls){
	var newMap = {};
	$.each(map,function(k,v){
		for(idx in excls){
			if(k == excls[idx]){
				return true;
			}
		}

		newMap[k] = v;
	});
	
	return newMap;
}


/*
function newInputElement(inputData){
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
	};*/