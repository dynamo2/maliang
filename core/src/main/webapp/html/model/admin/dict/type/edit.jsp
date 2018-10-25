<%@ page pageEncoding="utf-8"%>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span id="mainTitle">${result['title']}</span>
</h2>

<form method="post" action="/admin/dict/type/save.htm" role="form" layout="h" class="form-horizontal" id="editForm">

	<div class="form-body">

		<input type="hidden" name="dictType.id" value="${result['dictType']['id']}">
		
		<div class="form-group row">
			<label class="col-md-2">名称</label>
			<div class="col-md-10">
				<input type="text" name="dictType.name" value="${result['dictType']['name']}" class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2">简介</label>
			<div class="col-md-10">
				<input type="text" name="dictType.summary" value="${result['dictType']['summary']}" class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2"> </label>
			<div class="col-md-10"><button type="submit" class="btn">保存</button></div>
		</div>
	</div>
</form>


