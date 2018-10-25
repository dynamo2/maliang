<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script src="/js/jquery-2.1.3.min.js"></script>
		
		<!-- bootstrap -->
		<script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
		<script src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js"></script>
		<link href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
		<script>
		var types = ${types};
		
		$.each(types,function(){
			console.log("id: " + this.id+", name: " + this.name);
		});
		</script>
    </head>
    <body class="page-header-fixed page-quick-sidebar-over-content">

<style>
.box-shadow {
    box-shadow: 0 0.25rem 0.75rem rgba(0, 0, 0, .05);
}

#webNav {
    position: -webkit-sticky;
    position: sticky;
    top: 0;
    z-index: 1020;
},

.personal-nav {
    position: -webkit-sticky;
    position: sticky;
    top: 0;
    z-index: 1020;
}
</style>
<div class="p-4">
    <div id="webNav" class="d-flex flex-column flex-md-row align-items-center p-3 px-md-4 mb-3 bg-white border-bottom box-shadow">
      <h5 class="my-0 mr-md-auto font-weight-normal">医疗族模型网站</h5>
      <nav class="my-2 my-md-0 mr-md-3">
        <a class="p-2 text-dark" href="#">Features</a>
        <a class="p-2 text-dark" href="/flows/flow.htm?bid=5b1f48139f7b0317f386cd16&amp;fid=3">后台管理</a>
        <a class="p-2 text-dark" href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;fid=1">族模型</a>
        <a class="p-2 text-dark" href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=3">个人中心</a>
      </nav>
      
      <a class="btn btn-outline-primary mr-2" href="/flows/flow.htm?bid=5b1e75079f7b03127f4e2019">注册</a>
      <a class="btn btn-outline-primary mr-2" href="/flows/flow.htm?bid=5b1e75079f7b03127f4e2019&amp;fid=3">登录</a>
      <a class="btn btn-outline-primary" href="/flows/flow.htm?bid=5b1e75079f7b03127f4e2019&amp;fid=6">退出</a>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-3 personal-nav">
            
                <div class="list-group mb-4"><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1e16299f7b03127f4e1fb9" class="list-group-item list-group-item-action active">建筑</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1e16369f7b03127f4e1fbb" class="list-group-item list-group-item-action">柱</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1e163c9f7b03127f4e1fbd" class="list-group-item list-group-item-action">门</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1f63079f7b0317f386cdb2" class="list-group-item list-group-item-action">窗</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1f630f9f7b0317f386cdb4" class="list-group-item list-group-item-action">家具</a></div><div class="list-group mb-4"><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fa42e9f7b0317f326ad69" class="list-group-item list-group-item-action active">结构</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fa43e9f7b0317f326ad6b" class="list-group-item list-group-item-action">结构柱</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fa4509f7b0317f326ad6d" class="list-group-item list-group-item-action">结构梁</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fa4689f7b0317f326ad6f" class="list-group-item list-group-item-action">基础</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fa46f9f7b0317f326ad71" class="list-group-item list-group-item-action">其他</a></div><div class="list-group mb-4"><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fb38b9f7b0317f326ad79" class="list-group-item list-group-item-action active">园林景观</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fb3ae9f7b0317f326ad7b" class="list-group-item list-group-item-action">场地构件</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fb3b89f7b0317f326ad7d" class="list-group-item list-group-item-action">小品</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fb3c09f7b0317f326ad7f" class="list-group-item list-group-item-action">绿化</a><a href="/flows/flow.htm?bid=5b1f8a589f7b0317f386cdde&amp;search.type=5b1fb3d29f7b0317f326ad81" class="list-group-item list-group-item-action">室外照明</a></div>
            </div>
            <div class="col-9">
                <p></p><h2 class="border-bottom">所有</h2><p></p>
                <div class="container border p-2">
                	<form method="post" action="/flows/flow.htm" role="form">
                		<div class="row"><div class="col"><div class="form-body"><input type="hidden" value="5b1f8a589f7b0317f386cdde" name="bid" class="form-control"><input type="hidden" value="1" name="fid" class="form-control"><div class="form-group row"><label class="col-md-2">名称</label><div class="col-md-10"><input type="text" name="search.name" class="form-control"></div></div><div class="form-group row"><label class="col-md-2">分类</label><div class="col-md-10"><select name="search.type" class="form-control"><option value="all">所有</option><option value="5b1e16299f7b03127f4e1fb9">建筑</option><option value="5b1e16369f7b03127f4e1fbb">柱</option><option value="5b1e163c9f7b03127f4e1fbd">门</option><option value="5b1e16499f7b03127f4e1fbf">装饰柱</option><option value="5b1e16579f7b03127f4e1fc1">旋转门</option><option value="5b1f63079f7b0317f386cdb2">窗</option><option value="5b1f630f9f7b0317f386cdb4">家具</option><option value="5b1f63319f7b0317f386cdb7">平拉门</option><option value="5b1f63479f7b0317f386cdb9">推拉门</option><option value="5b1f64969f7b0317f386cdbf">折叠门</option><option value="5b1fa42e9f7b0317f326ad69">结构</option><option value="5b1fa43e9f7b0317f326ad6b">结构柱</option><option value="5b1fa4509f7b0317f326ad6d">结构梁</option><option value="5b1fa4689f7b0317f326ad6f">基础</option><option value="5b1fa46f9f7b0317f326ad71">其他</option><option value="5b1fb38b9f7b0317f326ad79">园林景观</option><option value="5b1fb3ae9f7b0317f326ad7b">场地构件</option><option value="5b1fb3b89f7b0317f326ad7d">小品</option><option value="5b1fb3c09f7b0317f326ad7f">绿化</option><option value="5b1fb3d29f7b0317f326ad81">室外照明</option><option value="5b1fb3e49f7b0317f326ad83">植物</option><option value="5b1fb3ee9f7b0317f326ad85">水景</option></select></div></div><div class="form-group row"><label class="col-md-2">型号</label><div class="col-md-10"><select name="search.model" class="form-control"><option value="all">所有</option></select></div></div></div></div><div class="col"><div class="form-body"><div class="form-group row"><label class="col-md-2">厂商</label><div class="col-md-10"><select name="search.vendor" class="form-control"><option value="all">所有</option><option value="5b1f52529f7b0317f386cd37">厂商1</option><option value="5b1f53489f7b0317f386cd3f">厂商2</option></select></div></div><div class="form-group row"><label class="col-md-2">品牌</label><div class="col-md-10"><select name="search.brand" class="form-control"><option value="all">所有</option><option value="5b1f54db9f7b0317f386cd62">品牌1</option></select></div></div><div class="form-group row"><label class="col-md-2"> </label><div class="col-md-10"><button type="submit" class="btn btn-primary">搜索</button></div></div></div></div></div></form></div><div class="row"><div class="col-4"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/13095402ufd0.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=4&amp;id=5b1f34b49f7b0317f386ccf1">向日葵</a></h5>
                                <div class="mb-2 price"> 分类: 植物 </div>
                                <a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f34b49f7b0317f386ccf1" class="btn btn-primary">下载</a><span> </span><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f34b49f7b0317f386ccf1" class="btn btn-danger">收藏</a>
                              </div>
                            </div></div>
                            
                            
                            <div class="col-4">
                            	<div class="card m-2" style="width: 16rem;">
	                            	<img src="http://assets.bimto.cn/img/detailDraw/201601/06104920ay66.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
	                            	<div class="card-body">
	                                	<h5 class="card-title"><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=4&amp;id=5b1f36019f7b0317f386ccfb">花草</a></h5>
	                                	<div class="mb-2 price"> 分类: 植物 </div>
	                                	<a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f36019f7b0317f386ccfb" class="btn btn-primary">下载</a>
	                                	<span> </span>
	                                	<a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f36019f7b0317f386ccfb" class="btn btn-danger">收藏</a>
	                              	</div>
                            	</div>
                            </div>
                            
                            
                            <div class="col-4"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/06104928n9eq.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=4&amp;id=5b1f363f9f7b0317f386ccfd">RPC-热带植物</a></h5>
                                <div class="mb-2 price"> 分类: 植物 </div>
                                <a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f363f9f7b0317f386ccfd" class="btn btn-primary">下载</a><span> </span><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f363f9f7b0317f386ccfd" class="btn btn-danger">收藏</a>
                              </div>
                            </div></div><div class="col-4"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/06105001xti0.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=4&amp;id=5b1f394c9f7b0317f386cd01">花架</a></h5>
                                <div class="mb-2 price"> 分类: null </div>
                                <a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f394c9f7b0317f386cd01" class="btn btn-primary">下载</a><span> </span><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1f394c9f7b0317f386cd01" class="btn btn-danger">收藏</a>
                              </div>
                            </div></div><div class="col-4"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201512/0200124626eh.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=4&amp;id=5b1fa0799f7b0317f326ad5c">支柱-正方形带球</a></h5>
                                <div class="mb-2 price"> 分类: 装饰柱 </div>
                                <a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1fa0799f7b0317f326ad5c" class="btn btn-primary">下载</a><span> </span><a href="/flows/flow.htm?bid=5b1f2f9a9f7b0317f386ccb8&amp;fid=1&amp;id=5b1fa0799f7b0317f326ad5c" class="btn btn-danger">收藏</a>
                              </div>
                            </div></div></div>
            </div>
        </div>
    </div>
</div></body>
</html>