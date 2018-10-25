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
		
		<script src="/js/dm/util.js"></script>
		<script src="/js/dm/main.js?d"></script>
		
		
		<link rel="stylesheet" type="text/css" href="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.css">
		<script src="/static/metronic/theme/assets/global/plugins/bootstrap-summernote/summernote.min.js" type="text/javascript"></script>
	
		<link rel="stylesheet" type="text/css" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css">
	    <script type="text/javascript" src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js"></script>
	    <script type="text/javascript" src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

		<style>
		.page-content {
			min-height:1000px;
		}
		
		
		</style>
    </head>
    <body class="page-header-fixed page-quick-sidebar-over-content">

><style>
.text-center {
    text-align: center!important;
}
body {
    display: -ms-flexbox;
    display: -webkit-box;
    display: flex;
    -ms-flex-align: center;
    -ms-flex-pack: center;
    -webkit-box-align: center;
    align-items: center;
    -webkit-box-pack: center;
    justify-content: center;
    padding-top: 40px;
    padding-bottom: 40px;
    background-color: #f5f5f5;
}
html, body {
    height: 100%;
}
.form-signin {
    width: 100%;
    max-width: 330px;
    padding: 15px;
    margin: 0 auto;
}
.form-signin {
    width: 100%;
    max-width: 330px;
    padding: 15px;
    margin: 0 auto;
}
.form-signin .form-control {
    position: relative;
    box-sizing: border-box;
    height: auto;
    padding: 10px;
    font-size: 16px;
}
</style>

  
    <form class="form-signin" action="/admin/login/do.htm" method="post">
      <img class="mb-4" src="https://getbootstrap.com/assets/brand/bootstrap-solid.svg" alt="" width="72" height="72">
      <h1 class="h3 mb-3 font-weight-normal">医疗族后台管理-登录</h1>
      
      <div class="text-danger">${errorMessage}</div>
      
      <input type="hidden" name="bid" value="5b21eec69f7b03176f8625c8">
      <input type="hidden" name="fid" value="7">
      
      <label for="inputEmail" class="sr-only">账号</label>
      <input type="text" id="inputEmail" name="account" class="form-control" placeholder="账号" required="" autofocus="">
      
      <label for="inputPassword" class="sr-only">Password</label>
      <input type="password" id="inputPassword" name="password" class="form-control" placeholder="密码" required="">
      
      <div class="checkbox mb-3">
        <label>
          
        </label>
      </div>
      <button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
      <p class="mt-5 mb-3 text-muted">© 2018-2020</p>
    </form>
  </body>
  </html>
  
  
  