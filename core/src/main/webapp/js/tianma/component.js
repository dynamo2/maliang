function buildComponent(compt,parentObj){
	var compObj = null;
	if(compt.type == 'datatables'){
		compObj = buildDatatables(compt,parentObj);
	}else if(compt.type == 'formInputs'){
		compObj = buildFormInputs(compt).appendTo(parentObj);
	}else {
		compObj = buildHtmlElement(compt.htmlProps).appendTo(parentObj);
	}
	
	if(compt.components){
		$.each(compt.components,function(){
	        buildComponent(this,compObj);
		});
	}
	
	return compObj;
}