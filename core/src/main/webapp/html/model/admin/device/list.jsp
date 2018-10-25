<%@ page pageEncoding="utf-8" %>
<!DOCTYPE html>
<html ng-app="">
	<head> 
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
        
         
        <!-- jquery --> 
		<script src="/js/jquery-2.1.4.js"></script> 
		
		
		<link rel="stylesheet" type="text/css" href="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.css">
		<script src="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.min.js" type="text/javascript"></script>
	
		<link rel="stylesheet" type="text/css" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css">
	    <script type="text/javascript" src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js"></script>
	    <script type="text/javascript" src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

		<!-- 
		<script src="http://localhost:8080/static/metronic/theme/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
		 -->
		 
		<style>
		.page-content {
			min-height:1000px;
		}
		</style>
    </head>
    <body class="page-header-fixed page-quick-sidebar-over-content">

<style>
.navbar .form-control {
    padding: .75rem 1rem;
    border-width: 0;
    border-radius: 0;
}
.form-control-dark {
    color: #fff;
    background-color: rgba(255, 255, 255, .1);
    border-color: rgba(255, 255, 255, .1);
}
.sidebar-sticky {
    position: -webkit-sticky;
    position: sticky;
    top: 48px;
    height: calc(100vh - 48px);
    padding-top: .5rem;
    overflow-x: hidden;
    overflow-y: auto;
}
.sidebar .nav-link {
    font-weight: 500;
    color: #333;
}
.nav-link {
    display: block;
    padding: .5rem 1rem;
}
</style>

<%@ include file="../include/head.jsp" %>

<div class="container-fluid">
      <div class="row">
        
		<%@ include file="../include/leftNav.jsp" %>
		
        <main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
        
        <div class="chartjs-size-monitor" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px; overflow: hidden; pointer-events: none; visibility: hidden; z-index: -1;"><div class="chartjs-size-monitor-expand" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;"><div style="position:absolute;width:1000000px;height:1000000px;left:0;top:0"></div></div><div class="chartjs-size-monitor-shrink" style="position:absolute;left:0;top:0;right:0;bottom:0;overflow:hidden;pointer-events:none;visibility:hidden;z-index:-1;"><div style="position:absolute;width:200%;height:200%;left:0; top:0"></div></div></div>
          <p></p><h2 class="border-bottom pb-4"><span>族模型列表</span><a role="button" href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82" class="btn ml-4">新增</a></h2><p></p>
          <div class="container border p-2"><form method="post" action="/flows/flow.htm" role="form"><div class="row"><div class="col"><div class="form-body"><input type="hidden" value="5b1e0ea79f7b03127f4e1f82" name="bid" class="form-control"><input type="hidden" value="1" name="fid" class="form-control"><div class="form-group row"><label class="col-md-2">名称</label><div class="col-md-10"><input type="text" name="search.name" class="form-control"></div></div><div class="form-group row"><label class="col-md-2">分类</label><div class="col-md-10"><select name="search.type" class="form-control"><option value="all">所有</option></select></div></div><div class="form-group row"><label class="col-md-2">型号</label><div class="col-md-10"><select name="search.model" class="form-control"><option value="all">所有</option></select></div></div></div></div><div class="col"><div class="form-body"><div class="form-group row"><label class="col-md-2">厂商</label><div class="col-md-10"><select name="search.vendor" class="form-control"><option value="all">所有</option></select></div></div><div class="form-group row"><label class="col-md-2">品牌</label><div class="col-md-10"><select name="search.brand" class="form-control"><option value="all">所有</option></select></div></div><div class="form-group row"><label class="col-md-2"> </label><div class="col-md-10"><button type="submit" class="btn btn-primary">搜索</button></div></div></div></div></div></form></div><div class="row"><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/13095402ufd0.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1f34b49f7b0317f386ccf1">向日葵</a></h5>
                                <div class="mb-2 price"> 分类: 植物 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=7&amp;id=5b1f34b49f7b0317f386ccf1" class="btn btn-danger">撤销发布</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/13095512kfbw.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1f34ea9f7b0317f386ccf3">盆栽002</a></h5>
                                <div class="mb-2 price"> 分类: 植物 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b1f34ea9f7b0317f386ccf3" class="btn btn-primary">编辑</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=6&amp;id=5b1f34ea9f7b0317f386ccf3" class="btn btn-info">发布</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b1f34ea9f7b0317f386ccf3" class="btn btn-danger">删除</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/06104920ay66.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1f36019f7b0317f386ccfb">花草</a></h5>
                                <div class="mb-2 price"> 分类: 植物 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=7&amp;id=5b1f36019f7b0317f386ccfb" class="btn btn-danger">撤销发布</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/06104928n9eq.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1f363f9f7b0317f386ccfd">RPC-热带植物</a></h5>
                                <div class="mb-2 price"> 分类: 植物 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=7&amp;id=5b1f363f9f7b0317f386ccfd" class="btn btn-danger">撤销发布</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201601/06105001xti0.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1f394c9f7b0317f386cd01">花架</a></h5>
                                <div class="mb-2 price"> 分类: null </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=7&amp;id=5b1f394c9f7b0317f386cd01" class="btn btn-danger">撤销发布</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201511/30191239fw86.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1f64fa9f7b0317f386cdd5">平开门-铝合金双扇镶玻璃门002</a></h5>
                                <div class="mb-2 price"> 分类: 平拉门 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b1f64fa9f7b0317f386cdd5" class="btn btn-primary">编辑</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=6&amp;id=5b1f64fa9f7b0317f386cdd5" class="btn btn-info">发布</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b1f64fa9f7b0317f386cdd5" class="btn btn-danger">删除</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201609/14113724yytx.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1fa0039f7b0317f326ad58">旋转门-双翼玻璃002</a></h5>
                                <div class="mb-2 price"> 分类: 旋转门 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b1fa0039f7b0317f326ad58" class="btn btn-primary">编辑</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=6&amp;id=5b1fa0039f7b0317f326ad58" class="btn btn-info">发布</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b1fa0039f7b0317f326ad58" class="btn btn-danger">删除</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201512/0200124626eh.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b1fa0799f7b0317f326ad5c">支柱-正方形带球</a></h5>
                                <div class="mb-2 price"> 分类: 装饰柱 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=7&amp;id=5b1fa0799f7b0317f326ad5c" class="btn btn-danger">撤销发布</a>
                              </div>
                            </div></div><div class="col-2"><div class="card m-2" style="width: 16rem;">
                              <img src="http://assets.bimto.cn/img/detailDraw/201511/30195139u125.jpg" style="height: 180px;width:auto;display: block;" class="card-img-top">
                              <div class="card-body">
                                <h5 class="card-title"><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=5&amp;id=5b2119459f7b0366f8d152c3">休闲椅006</a></h5>
                                <div class="mb-2 price"> 分类: 家具 </div>
                                <a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b2119459f7b0366f8d152c3" class="btn btn-primary">编辑</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=6&amp;id=5b2119459f7b0366f8d152c3" class="btn btn-info">发布</a><span> </span><a href="/flows/flow.htm?bid=5b1e0ea79f7b03127f4e1f82&amp;fid=1&amp;id=5b2119459f7b0366f8d152c3" class="btn btn-danger">删除</a>
                              </div>
                            </div></div></div>
        </main>
      </div>
    </div>
    </body>
</html>