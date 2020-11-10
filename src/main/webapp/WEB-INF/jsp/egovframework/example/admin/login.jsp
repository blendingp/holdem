<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../frame/admintop.jsp" flush="true" />
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-md-4 col-md-offset-4">
				<div class="login-panel panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">Wingame Admin Login</h3>
					</div>
					<div class="panel-body">
						<form role="form" id="loginForm">
							<fieldset>
								<div class="form-group">
									<input class="form-control" placeholder="ID" id="id" name="id" type="text" autofocus>
								</div>
								<div class="form-group">
									<input class="form-control" onkeypress="if(event.keyCode==13) {javascript:adminLogin(); return false;}" placeholder="Password" id="pw" name="pw" type="password">
								</div>
								<a href="javascript:adminLogin()" class="btn btn-lg btn-success btn-block">Login</a>
							</fieldset>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../frame/adminbottom.jsp" flush="true" />
	
	<script>
		function adminLogin()
		{
			var id = $("#id").val();
			var pw = $("#pw").val();
			if(id == "")
			{
				alert("ID를 입력하세요.");
				$("#id").focus();
				return;
			}
			if(pw == "")
			{
				alert("비밀번호를 입력하세요.");
				$("#pw").focus();
				return;
			}
			$.ajax({
				type : 'post' , 
				url : '/holdem/admin/loginChk.do',
				data : $("#loginForm").serialize(),
				success:function(data){
					if(data.result == 'success')
					{
						location.href="/holdem/admin/main.do";
					}
					else
					{
						alert(data.msg);
						return;
					}
				},
				error:function(e){
					console.log('ajax Error');
				}
				
			});
		}
	</script>
</body>
</html>