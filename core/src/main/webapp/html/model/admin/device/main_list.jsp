<%@ page pageEncoding="utf-8" %>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span>族模型列表</span>
	<a role="button" href="/admin/device/edit.htm" class="btn ml-4">新增</a>
</h2>

<div class="container border p-2">
	<form method="post" role="form" id="searchForm">
		<input type="hidden" name="curPage" id="curPage" value="1" />
		<div class="row">
			<div class="col">
				<div class="form-body">
					<div class="form-group row">
						<label class="col-md-2">名称</label>
						<div class="col-md-10">
							<input type="hidden" name="search.reset" value="false">
							<input type="text" name="search.key" class="form-control">
						</div>
					</div>
					<div class="form-group row">
						<label class="col-md-2"> </label>
						<div class="col-md-10"><button type="submit" class="btn btn-primary">搜索</button></div>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>
          
<div class="row" id="deviceModelList"></div>
<nav id="pageNav"></nav>
                            
<script language="javaScript">
var result = ${result};
var devices = result && result.devices;
var page = result && result.page;
var search = result && result.search;

$(function(){
	deviceList(devices);
	initSearchForm();
	
	$("#pageNav").append(pagination(page));
});

function turnToPage(e){
	var curPage = e && e.data && e.data.curPage;

	$("#curPage").val(curPage);
	ajaxDevices();
}

function ajaxDevices(){
	$.ajax({
		url:'/admin/device/ajax/list.htm',
		type: 'post',
		dataType:'json',
		data:$("#searchForm").serialize(),
		complete: function (data,status) {
			var result = JSON.parse(data.responseText);
			$("#deviceModelList").empty();
			$("#pageNav").empty();
			
			deviceList(result && result.devices);
			$("#pageNav").append(pagination(result && result.page));
		}
	});
}

function searchItemRow(label,items,key,formBody){
	if($.isArray(items) && items.length > 0){
		var row = $("<div>").addClass("row").appendTo(formBody);
		var label = $("<label>").addClass("col-md-2").text(label).appendTo(row);
		var column = $("<div>").addClass("col-md-10").appendTo(row);
		$.each(items,function(){
			var span = $("<span>").addClass("mr-2").appendTo(column);
			var hidden = $("<input>").attr({
				type:"hidden",
				name:'search.'+key,
				value:this.id
			}).appendTo(span);
			var button = $("<button>").addClass("btn btn-info mb-2").prop("type","button").text(this.name).appendTo(span);
			button.click(function(){

				//not hidden
				$("<input>").attr({
					type:"hidden",
					name:'not.'+key,
					value:hidden.val()
				}).appendTo(span);
				
				span.hide();
				ajaxDevices();
			});
		});
	}
}

function initSearchForm(){
	var form = $("#searchForm");
	var formBody = form.find(".form-body");
	
	form.find("input[name='search.key']").val(search && search.key);
	
	var searchTypes = search && search.types;
	searchItemRow("类型",searchTypes,"types",formBody);

	var searchDictTypes = search && search.dictTypes;
	if($.isArray(searchDictTypes)){
		$.each(searchDictTypes,function(){
			searchItemRow(this.name,this.dicts,"dicts",formBody);
		});
	}
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

function deviceList(devices){
	$.each(devices,function(){
		
		var column = $("<div />").addClass("col-2");
		var card = $("<div />").addClass("card m-2").css("width","16rem").appendTo(column);
		
		$("<img>").attr("src",this.cover).attr("style","height: 180px;width:auto;display: block;").addClass("card-img-top").appendTo(card);
		
		var title = $("<a>").prop("href","#").text(this.name);
		var cardBody = $("<div>").addClass("card-body").appendTo(card);
		cardBody.append($("<h5>").addClass('card-title').append(title));
		cardBody.append($("<div>").addClass("mb-2").text("分类："+(this.type && this.type.name)));
		
		/**
		** buttons
		**/
		if(this.published){
			$("<a />").prop("href","#").text("撤销发布").addClass("btn btn-danger mr-2").appendTo(cardBody);
		}else {
			$("<a />").prop("href","/admin/device/edit.htm?id="+this.id).text("编辑").addClass("btn btn-primary mr-2").appendTo(cardBody);
			$("<a />").prop("href","#").text("发布").addClass("btn btn-info mr-2").appendTo(cardBody);
			$("<a />").prop("href","#").text("删除").addClass("btn btn-danger mr-2").appendTo(cardBody);
		}
		
		$("#deviceModelList").append(column);
	});
}

function pagination(page){
	var pageUL = $("<ul>").addClass("pagination justify-content-center");
	var totalPage = 1;
	var curPage = 1;
	try {
		curPage = new Number(page && page.curPage);
		totalPage = new Number(page && page.totalPage);
	}catch(e){
		totalPage = 1;
	}
	
	var pre = curPage -1;
	var pageItem = $("<li>").addClass("page-item");
	var preLink = $("<a>").addClass("page-link").prop("aria-label","Previous");
	if(pre < 1){
		//preLink.prop("disabled",true);
		pre = 1;
	}
	preLink.prop("href","#");
	preLink.on("click",{curPage:pre},turnToPage);
	preLink.append($("<span>").prop("aria-hidden",true).html("&laquo;"));
	preLink.append($("<span>").addClass("sr-only",true).text("Previous"));
	pageItem.append(preLink);
	pageUL.append(pageItem);
	
	for(var i = 1; i <= totalPage; i++){
		var pageItem = $("<li>").addClass("page-item");
		var pageLink = $("<a>").addClass("page-link").prop("href","#").text(i);
		
		//pageLink.click([{curPage:i},turnToPage]);
		
		pageLink.on("click",{curPage:i},turnToPage);
		
		pageItem.append(pageLink);
		pageUL.append(pageItem);	
	}
	
	var next = curPage+1;
	var pageItem = $("<li>").addClass("page-item");
	var nextLink = $("<a>").addClass("page-link").prop("aria-label","Next");
	if(next > totalPage){
		nextLink.attr("disable",true);
		next = totalPage;
	}
	nextLink.prop("href","#");
	nextLink.on("click",{curPage:next},turnToPage);
	nextLink.append($("<span>").prop("aria-hidden",true).html("&raquo;"));
	nextLink.append($("<span>").addClass("sr-only",true).text("Next"));
	pageItem.append(nextLink);
	pageUL.append(pageItem);
	
	return pageUL;
	
	/*
	var pageNav = $("<nav>").attr("aria-label","Page navigation example");
	pageNav.append(pageUL);
	
	return pageNav;
	*/
}

</script>


