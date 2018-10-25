<%@ page pageEncoding="utf-8"%>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span id="mainTitle">${result['title']}</span>
</h2>

<form method="post" action="/admin/save.htm" role="form" layout="h" class="form-horizontal" id="editForm">

	<div class="form-body">

		<input type="hidden" name="admin.id" value="${result['admin']['id']}">
		
		<div class="form-group row">
			<label class="col-md-2">账号</label>
			<div class="col-md-10">
				<input type="text" name="admin.account" value="${result['admin']['account']}" class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2">密码</label>
			<div class="col-md-10">
				<input type="password" name="admin.password" value="${result['admin']['password']}" class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2"> </label>
			<div class="col-md-10"><button type="submit" class="btn">保存</button></div>
		</div>
	</div>
</form>


