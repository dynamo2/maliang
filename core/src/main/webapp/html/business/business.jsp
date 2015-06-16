<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <script src="../js/angular.js"></script>
		<script src="../js/jquery-2.1.3.min.js"></script>
		
		<script src="../html/business/tianma.js"></script>
		<link href="../html/business/style.css" rel="stylesheet" type="text/css"/> 
    </head>
    <body>
		<script>
		var resultModel = ${resultJson};
		//{"contents":[{"enctype":"","html_parent":"search_form","children":{"ul-list":{"data":[{"price":{"text":850,"type":"label"},"name":{"href":"/detail.htm?id=1111","text":"Bioeffect EGF生长因子精华","type":"a"},"brand":{"text":"Bioeffect","type":"label"},"picture":{"src":"0-item_pic.jpg","type":"img"},"operator":[{"href":"/edit.htm?id=1111","text":"编辑","type":"a"},{"href":"/delete.htm?id=1111","text":"删除","type":"a"}]},{"price":{"text":275,"type":"label"},"name":{"href":"/detail.htm?id=2222","text":"Aminogenesis活力再生胶囊 ","type":"a"},"brand":{"text":"Aminogenesis","type":"label"},"picture":{"src":"0-item_pic.jpg","type":"img"},"operator":[{"href":"/edit.htm?id=2222","text":"编辑","type":"a"},{"href":"/delete.htm?id=2222","text":"删除","type":"a"}]}],"header":[{"name":"name","label":"名称"},{"name":"brand","label":"品牌"},{"name":"price","label":"价格"},{"name":"picture","label":"图片"},{"name":"operator","label":"操作"}]},"inputs":[{"name":"product.name","label":"名称","type":"text","value":"Product"},{"name":"product.brand","options":[{"label":"雪花秀","key":"aaaa"},{"label":"希思黎","key":"bbbb"},{"label":"Pola","key":"cccc"}],"label":"品牌","type":"select","value":"aaaa"},{"name":"product.category","options":[{"label":"雪花秀","key":"aaaa"},{"label":"希思黎","key":"bbbb"},{"label":"Pola","key":"cccc"}],"label":"分类","type":"radio","value":"aaaa"},{"name":"product.label","options":[{"name":"雪花秀","id":"aaaa"},{"name":"希思黎","id":"bbbb"},{"name":"Pola","id":"cccc"}],"label":"标签","type":"checkbox","value":"aaaa"},{"min":{"name":"product.min_price","type":"number","value":null},"max":{"name":"product.max_price","type":"number","value":null},"label":"价格","type":"between"},{"name":"product.description","label":"描述","type":"textarea","value":"product.description"},{"name":"product.expiry_date","label":"有效期","type":"date","value":"2015-03-26"},{"name":"product.picture","label":"图片","type":"picture","value":null}]},"name":"product.edit.form","action":"edit.html","type":"form"}],"html_template":"<div id='search_form'><\/div>"};
		//{"data":[{"price":{"text":850,"type":"label"},"name":{"href":"/detail.htm?id=1111","text":"Bioeffect EGF生长因子精华","type":"a"},"brand":{"text":"Bioeffect","type":"label"},"picture":{"src":"0-item_pic.jpg","type":"img"},"operator":[{"href":"/edit.htm?id=1111","text":"编辑","type":"a"},{"href":"/delete.htm?id=1111","text":"删除","type":"a"}]},{"price":{"text":275,"type":"label"},"name":{"href":"/detail.htm?id=2222","text":"Aminogenesis活力再生胶囊 ","type":"a"},"brand":{"text":"Aminogenesis","type":"label"},"picture":{"src":"0-item_pic.jpg","type":"img"},"operator":[{"href":"/edit.htm?id=2222","text":"编辑","type":"a"},{"href":"/delete.htm?id=2222","text":"删除","type":"a"}]}],"header":[{"name":"name","label":"名称"},{"name":"brand","label":"品牌"},{"name":"price","label":"价格"},{"name":"picture","label":"图片"},{"name":"operator","label":"操作"}]};
		//{"contents":[{"enctype":"","html_parent":"edit_form","inputs":[{"name":"product.id","type":"hidden","value":"111111"},{"name":"product.name","label":"名称","type":"text","value":"Product"},{"name":"product.brand","options":[{"label":"雪花秀","key":"aaaa"},{"label":"希思黎","key":"bbbb"},{"label":"Pola","key":"cccc"}],"label":"品牌","type":"select","value":"aaaa"},{"name":"product.category","options":[{"label":"雪花秀","key":"aaaa"},{"label":"希思黎","key":"bbbb"},{"label":"Pola","key":"cccc"}],"label":"分类","type":"radio","value":"aaaa"},{"name":"product.label","options":[{"label":"雪花秀","key":"aaaa"},{"label":"希思黎","key":"bbbb"},{"label":"Pola","key":"cccc"}],"label":"标签","type":"checkbox","value":"aaaa"},{"name":"product.price","label":"价格","type":"number","value":335.8},{"name":"product.description","label":"描述","type":"textarea","value":"product.description"},{"name":"product.expiry_date","label":"有效期","type":"date","value":"2015-03-26"},{"name":"product.picture","label":"图片","type":"picture","value":null}],"name":"product.edit.form","type":"form"}],"html_template":"<div id='edit_form'><\/div>"};
		
		$(function(){
			$("#main").html(resultModel.html_template);
			
			for(var idx in resultModel.contents){
				var htmlData = resultModel.contents[idx];
				
				buildHtml(htmlData);
			}
		});
		
		function buildHtml(htmlData){
			if(htmlData.type == 'form'){
				var formObj = TM_formBuilder.newForm(htmlData);
				//alert("hh: "+hh);
				
				var subObj = $("<input type='submit' value='保存' />");
				formObj.append(subObj);
				
				formObj.attr("method","post");
				
				$("#"+htmlData.html_parent).append(formObj);
			}
		}
		</script>
		<div id="main"></div>
		
	</body>
</html>