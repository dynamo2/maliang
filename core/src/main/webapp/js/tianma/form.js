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
	var htmlType = options.type;
	if(htmlType === "list"){
		return newListEditor(options);
	}
	
	var newOpt = eliminate(options,["label"]);
	if(newOpt.prefix && newOpt.name){
		newOpt.name = newOpt.prefix + '.' + newOpt.name;
	}
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

function eliminate(map,excls){
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

/**
 * List Editor
 * **/
function newListEditor(itemData){
	var divObj = $("<div></div>").addClass("ul-list");

	var options = initOptions(itemData);
	addNewButton(options,divObj);
	newListHeader(options.header).appendTo(divObj);
	
	if(itemData.value){
		$.each(itemData.value,function(k,item){
			newListItem(item,options).appendTo(divObj);
		});
	}
	
	newInputElement(options.maxIndex).appendTo(divObj);
	
	return divObj;
};

function newListItem(inputItem,options){
	var ulObj = $("<ul></ul>").attr("row",options.maxIndex.value);
	
	var col = 0;
	if(options.header){
		$.each(options.header,function(idx){
			var fieldInput = inputItem[idx];
		
			var liObj = $("<li></li>").attr("column",col++).appendTo(ulObj);
			if(fieldInput instanceof Array){
				$.each(fieldInput,function(k,opt){
					if(!opt)return true;
					
					newItemInput(opt,options).appendTo(liObj);
				});
				//options.emptyItem[idx] = fieldInput;
			}else if(fieldInput){
				newItemInput(fieldInput,options).appendTo(liObj);
				//options.emptyItem[idx] = fieldInput;
			}
		});
	}

	addItemDeleteButton(ulObj,col);
	options.maxIndex.value++;
	
	return ulObj;
};

function newItemInput(itemOpt,options){
	var oldName = itemOpt.name;
	itemOpt.name = options.maxIndex.value+'.'+itemOpt.name;
	if(!itemOpt.prefix)itemOpt.prefix = options.prefix;
	
	var inputObj = newInputElement(itemOpt).css("margin-left","5px");
	
	itemOpt.name = oldName;
	itemOpt.value = null;
	
	return inputObj;
}

function initOptions(items){
	return {
		label:items.label,
		header:items["item-labels"],
		prefix:items["item-prefix"],
		emptyItem:items.template,
		maxIndex:{
			value:0,
			type:'hidden',
			prefix:items["item-prefix"],
			name:'maxIndex'
		}
	};
}

function addNewButton(options,divObj){
	return $("<input type='button' id='addNewItemRow' />").val('添加'+options.label)
			.click(function(){
				newListItem(options.emptyItem,options).appendTo(divObj);
			}).appendTo($("<li />").appendTo($("<ul />").addClass("operator").appendTo(divObj)));
}

function newListHeader(header){
	var itemHeaders = [];
	$.each(header,function(key,label){
		itemHeaders.push({'name':key,'label':label});
	});
	itemHeaders.push({'name':'operator','label':'操作'});
	
	var ulObj = $("<ul></ul>").addClass("header");
	var col = 0;
	if(itemHeaders){
		$.each(itemHeaders,function(key,value){
			if(value){
				$("<li></li>").attr("column",col++).text(value.label).appendTo(ulObj);
			}
		});
	}
	
	return ulObj;
};

function addItemDeleteButton(ulObj,col){
	return $("<input type='button' value='删除' />").click(function(){
			ulObj.remove();
		}).appendTo($("<li></li>").attr("column",col).appendTo(ulObj));
}

/*
function UIListBuilder(){
	var builder = this;
	
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
}*/
