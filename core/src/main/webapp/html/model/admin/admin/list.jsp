<%@ page pageEncoding="utf-8" %>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span>管理员列表</span>
	<a role="button" href="/admin/admin/edit.htm" class="btn ml-4">新增</a>
</h2>

          
<div class="row" id="adminList"></div>
                            
<script language="javaScript">
var admins = ${result['admins']};

$(function(){
	$("#adminList").append(table(admins,{
		account:'账号',
		password:'密码'
	}));
});

function listOperas(id){
	return [
		$("<a>").attr("href","/admin/edit.htm?id="+id).text("编辑"),operaSpec(),
		$("<a>").attr("href","/admin/delete.htm?id="+id).text("删除")
	]
}



</script>


