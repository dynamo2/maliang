<%@ page pageEncoding="utf-8" %>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span>配置数据列表</span>
	<a role="button" href="/admin/dict/edit.htm" class="btn ml-4">新增</a>
</h2>
          
<div class="row" id="dictList"></div>
                            
<script language="javaScript">
var dicts = ${result['dicts']};

$(function(){
	$("#dictList").append(table(dicts,{
		name:'名称',
		dictType:'类型',
		summary:'简介'
	}));
});


function listOperas(id){
	return [
		$("<a>").attr("href","/admin/dict/edit.htm?id="+id).text("编辑"),operaSpec(),
		$("<a>").attr("href","/admin/dict/delete.htm?id="+id).text("删除")
	]
}
</script>


