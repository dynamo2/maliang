function table(datas,options){
	var table = $("<table>").addClass("table");
	var thead = $("<thead>").appendTo(table);
	var tbody = $("<tbody>").appendTo(table);
	
	var keys = [];
	var row = $("<tr>").appendTo(thead);
	$.each(options,function(k,v){
		keys.push(k);
		$("<th>").text(v).appendTo(row);
	});
	$("<th>").text("操作").appendTo(row);
	
	$.each(datas,function(){
		var data = this;
		var id = data && data.id;
		row = $("<tr>").appendTo(tbody);
		
		$.each(keys,function(){
			var td = $("<td>").appendTo(row);
			var val = data[this];
			
			if($.isPlainObject(val)){
				td.text(val && val["name"]);
			}else {
				td.text(val);
			}
		});
		
		/**
		添加操作链接
		**/
		var btns = listOperas(id);
		$("<td>").appendTo(row).append(btns);
	});
	
	return table;
}

function reqOpera(options,name){
	return !((options && options[name]) === false);
}

/**
 * 操作链接
 * **/
function listOperas(id){
	return [
		$("<a>").attr("href","#").text("编辑"),operaSpec(),
		$("<a>").attr("href","#").text("删除")
	]
}

/**
 * 操作分隔符，默认为：' | '
 * **/
function operaSpec(spec){
	if(!spec){
		spec = " | ";
	}
	return $("<span>").text(spec);
}