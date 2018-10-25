<%@ page pageEncoding="utf-8"%>

<h2 class="border-bottom pb-1 mt-2 mb-4">
	<span id="mainTitle">${result['title']}</span>
</h2>

<form method="post" action="/admin/device/save.htm" enctype="multipart/form-data" role="form" layout="h" class="form-horizontal" id="editForm">
	<input type="hidden" name="deviceModel.id" value="${result['device']['id']}" class="form-control">
	
	<div class="form-body">

		<div class="form-group row">
			<label class="col-md-2">名称</label>
			<div class="col-md-10">
				<input type="text" name="deviceModel.name" value="${result['device']['name']}" class="form-control">
			</div>
		</div>

		<div class="form-group row">
			<label class="col-md-2">类别</label>
			<div class="col-md-10">
				<select name="deviceModel.type.id" class="form-control"></select>
			</div>
		</div>

		<div class="form-group row">
			<label class="col-md-2">封面</label>
			<div class="col-md-10">
				<input type="text" name="deviceModel.cover" value="${result['device']['cover']}" class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2">照片集</label>
			<div class="col-md-10">
				<input type="file" name="files" value="" multiple class="form-control">
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2">照片集</label>
			<div class="col-md-10">
				<div class="form-body" id="photosBody">
					<div class="row">
						<div class="form-group col-md-8">
							<button type="button" class="btn btn-primary" onclick="addRow()">新增</button>
						</div>
					</div>
					<div class="row">
						<div class="form-group col-md-8">图片</div>
						<div class="form-group col-md-2">操作</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2">系统配置</label>
			<div class="col-md-10">
				<div class="form-body" id="dictBody">
				</div>
			</div>
		</div>
		
		<div class="form-group row">
			<label class="col-md-2"> </label>
			<div class="col-md-10"><button type="submit" class="btn">保存</button></div>
		</div>
	</div>
</form>

<script>
	var types = ${result['types']};
	var dictTypes = ${result['dictTypes']};
	var dictMap = ${result['dictMap']};
	var device = ${result['device']};
	var dmType = device && device.type && device.type.id;
	var photos = device && device.photos;
	var deviceDicts = device && device.deviceDicts;

	$(function() {
		initSelect($("select[name='deviceModel.type.id']"),types,dmType);
		initPhotos(photos,$("#photosBody"));
		
		var i = 0;
		var row = null;
		$.each(dictTypes,function(idx,dtype){
			if((i++)%2 == 0){
				row = $("<div>").addClass("row");
			}
			
			var label = $("<label>").addClass("col-2 mb-2").text(dtype.name);
			var column = $("<div>").addClass("col-4 mb-2");
			
			var dictType = $("<input>").attr({
				type:"hidden",
				value:dtype.id,
				name:"deviceModel.deviceDicts."+idx+".dictType.id"
			});
			var dict = $("<select>").addClass("form-control").attr("name","deviceModel.deviceDicts."+idx+".dict.id");
			
			var dictId = readDict(device.deviceDicts,dtype.id);
			var selOpts = [{id:"",name:"-请选择-"}];
			selOpts = selOpts.concat(dictMap[this.id]);

			initSelect(dict,selOpts,readDict(device.deviceDicts,dtype.id));
			
			row.append(label).append(column.append(dictType).append(dict));
			$("#dictBody").append(row);
		});
	});
	
	function readDict(deviceDicts,dictTypeId){
		if(!$.isArray(deviceDicts)){
			return null;
		}
		
		var dictId = "";
		$.each(deviceDicts,function(){
			if(this.dictType && this.dictType.id == dictTypeId){
				dictId = this.dict && this.dict.id;
				return;
			}
		});
		return dictId;
	}
	
	function initPhotos(photos,pbody){
		if(!$.isArray(photos) || photos.length === 0){
			photos = [""];
		}
		
		$.each(photos,function(idx,val){
			var row = $("<div>").addClass("row").attr("for","items");
			var c1 = $("<div>").addClass("form-group col-md-8");
			var input = $("<input>").addClass("form-control").attr({
				type:"file",
				name:"deviceModel.photos."+idx,
				value:val
			});
			c1.append(input);
			
			var c2 = $("<div>").addClass("form-group col-md-2");
			var button = $("<button>").addClass("btn btn-danger").attr({
				type:"button",
				name:"deleteBtn",
				onclick:"deleteRow()"
			}).text("删除");
			c2.append(button);
			
			row.append(c1).append(c2);
			pbody.append(row);
		});
	}

	function initSelect(select, items,delVal) {
		if(!$.isArray(items)){
			return;
		}
		
		$.each(items, function() {
			var val = this.id;
			var option = $("<option>").val(val).text(this.name);
			if(delVal == val){
				option.prop("selected",true);
			}

			select.append(option);
		});
	}

	function addRow() {
		var em = $(event.target);
		var formBody = em.closest(".form-body");
		var lastRow = formBody.find(".row[for='items']:last");

		var newRow = $(lastRow.prop("outerHTML"));
		formBody.append(newRow);

		var inputs = newRow.find(":input");

		$.each(inputs, function() {
			doArrayName($(this), 0);
		});

		var rows = formBody.find(".row[for='items']");
		$.each(rows, function() {
			$(this).find(":button[name='deleteBtn']").prop("disabled", false);
		});
	}

	function doArrayName(input, num) {
		var iName = input.prop("name");

		iName = utils.nextArrayName(iName, num);

		input.prop("name", iName);
		input.val("");
		return input;
	}

	function deleteRow() {
		var em = $(event.target);
		var row = em.closest(".row");
		var formBody = em.closest(".form-body");

		if (formBody.find(".row[for='items']").length <= 1) {
			alert('最后一行，不能删除');

			em.prop('disabled', 'true');
			return;
		}

		row.remove();

		if (formBody.find(".row[for='items']").length <= 1) {
			formBody.find(".row[for='items']")
					.find(":button[name='deleteBtn']").prop("disabled", true);
		}
	}

	function saveRow(dftDatas) {
		var em = $(event.target);
		var row = em.closest(".row");

		var datas = readFormDatas(row);

		var reqDatas = $.extend(dftDatas, datas);

		$.ajax("/flows/flow.htm", {
			data : reqDatas,
			dataType : 'json',
			type : 'POST',
			success : function(result, textStatus) {
				console.log("row ajax save ok");
			}
		});

		em.prop("disabled", true);
	}

	function canSave() {
		var row = $(event.target).closest(".row");

		var saveBtn = row.find("[name='saveBtn']");
		saveBtn.prop("disabled", false);
	}
</script>


