<%@ page pageEncoding="utf-8"%>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span id="mainTitle">${result['title']}</span>
</h2>

<form method="post" action="/admin/dict/save.htm" role="form" layout="h" class="form-horizontal" id="editForm">

	<div class="form-body">

		<input type="hidden" name="dict.id" value="${result['dict']['id']}">
		
		<div class="form-group row">
			<label class="col-md-2">名称</label>
			<div class="col-md-10">
				<input type="text" name="dict.name" value="${result['dict']['name']}" class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2">类别</label>
			<div class="col-md-10">
				<select name="dict.dictType.id" class="form-control"></select>
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2">简介</label>
			<div class="col-md-10">
				<input type="text" name="dict.summary" value="${result['dict']['summary']}" class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2"> </label>
			<div class="col-md-10"><button type="submit" class="btn">保存</button></div>
		</div>
	</div>
</form>
<script>
var dict = ${result['dict']};
var types = ${result['types']};
var dType = dict && dict.dictType && dict.dictType.id;

$(function() {
	initSelect("dict.dictType.id", types,dType);
});

function initSelect(sname, items,delVal) {
	if(!$.isArray(items)){
		return;
	}
	
	$.each(items, function() {
		var select = $("select[name='" + sname + "']");
		
		var val = this.id;
		var option = $("<option>").val(val).text(this.name);
		if(delVal == val){
			option.prop("selected",true);
		}

		select.append(option);
	});
}
</script>


