<%@ page pageEncoding="utf-8" %>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span>配置类型列表</span>
	<a role="button" href="/admin/dict/type/edit.htm" class="btn ml-4">新增</a>
</h2>
          
<div class="row" id="dictTypeList"></div>

<script language="javaScript">
var dictTypes = ${result['dictTypes']};

$(function(){
	$("#dictTypeList").append(table(dictTypes,{
		name:'名称',
		summary:'简介'
	}));
});

function listOperas(id){
	return [
		$("<a>").attr("href","/admin/dict/type/edit.htm?id="+id).text("编辑"),operaSpec(),
		$("<a>").attr("href","/admin/dict/type/delete.htm?id="+id).text("删除"),operaSpec(),
		$("<a>").attr("href","/admin/dict/edit.htm?tid="+id).text("新增数据")
	]
}



</script>


