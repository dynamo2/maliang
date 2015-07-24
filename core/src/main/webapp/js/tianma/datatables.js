function buildDatatables(options,parentObj){
    var tableObj = $('<table cellpadding="0" cellspacing="0" data-page-length="5" border="0" class="display"></table>');
	setHtmlProperties(tableObj,options.htmlProps);
	parentObj.append(tableObj);

	/*
	if(options.cache){
		options.columnTemplate[0] += "<input type='hidden' value='${this}' />";
		parentObj.append(cacheColumnTemplate(options));
	}*/
	
	tableObj.dataTable({
      //"data": readDatatableDatas(options),
      "columns": readDatatablesTitles(options.header),
      "bPaginate":true,
      "bLengthChange":false,
      "ajax":options.ajax,
      "serverSide":true,
      "bFilter":true,
      "bSort":false
    });
	
	//document.write(JSON.stringify($.fn.dataTableSettings));
	return tableObj;
}

function readDatatablesTitles(header){
	var titles = [];
	var i = 0;
	for(var idx in header){
	  titles[i++] = {title:header[idx]};
	}
	return titles;
}

function readDatatableDatas(options){
    var datas = [];
	for(var idx in options.datas){
	  datas.push(parseColumn(options.columnTemplate,options.datas[idx]));
	}
	return datas;
}

function moveDatas(from,to,rowSelector){
    var fromTable = $("#"+from).DataTable();
	var toTable = $("#"+to).DataTable();
	var toColumn = getColumnTemplate(to);

	var delRows = [];
	fromTable.rows(rowSelector).every(function(){
		var fromRow = $(this.node());
		var colData = parseColumn(toColumn,getRowData(fromRow));
		
		print(colData);
		toTable.row.add(colData).draw();
		
		delRows.push(this);
	});
	
	for(var idx in delRows){
		delRows[0].remove().draw();
	}
}

function cacheColumnTemplate(options){
	var areaObj = $("<textarea></textarea>");
	areaObj.text(JSON.stringify(options.columnTemplate));
	//areaObj.prop("style","width:500px;height:100px;");
	areaObj.prop("id",options.htmlProps.id+"ColumnTemplate");
	areaObj.hide();
	
	return areaObj;
}

function parseColumn(columnTemplate,data){
    var dd = [];
	for(var idx in columnTemplate){
	    dd.push(replaceVar(columnTemplate[idx],data));
	}
	return dd;
}

function getColumnTemplate(tabId){
	return JSON.parse($("#"+tabId+"ColumnTemplate").text());
}

function getRowData(jqRow){
	var hio = jqRow.find("input[type='hidden']");
	return JSON.parse(hio.val());
}