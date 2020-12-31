<%@ page contentType="text/html; charset=euc-kr" %>
<%
	response.setHeader("Pragma", "No-cache");
	request.setCharacterEncoding("euc-kr");

	String BackURL = "./BackURL.jsp";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr"/>
<link href="/holdem/css/danal/style.css" type="text/css" rel="stylesheet" media="all"/>
<title>*** 다날 신용카드 결제 ***</title>
</head>
<body>
<form name="form" >
	<!-- popSize 530x430  -->
	<div class="popWrap">
		<h1 class="logo"><img src="/holdem/img/logo.gif" /></h1>
		<div class="tit_area">
			<p class="tit"><img src="/holdem/img/tit05.gif" alt="결제취소 Complete" /></p>
		</div>
		<div class="box">
			<div class="boxTop">
				<div class="boxBtm" style="height:136px;">
					<p class="txt_info">귀하의 결제가 취소되었습니다.</p>
				</div>
			</div>
		</div>
		<p class="btn">
			<a href="javascript:self.close();"><img src="/holdem/img/btn_confirm.gif" width="91" height="28" alt="확인" /></a>
		</p>
		<div class="popFoot">
			<div class="foot_top">
				<div class="foot_btm">
					<div class="noti_area">
						 다날 신용카드결제를 이용해주셔서 고맙습니다. [Tel] 1566-3355
					</div>
				</div>
			</div>
		</div>
	</div>
</form>
</body>
</html>
