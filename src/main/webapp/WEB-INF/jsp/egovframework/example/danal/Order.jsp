<%@ page contentType="text/html; charset=euc-kr" pageEncoding="euc-kr"%>
<%
	response.setHeader("Pragma", "No-cache");
	request.setCharacterEncoding("euc-kr");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr">
<link href="/holdem/css/danal/style.css" type="text/css" rel="stylesheet" media="screen" />
<title>*** 신용카드 결제 요청 ***</title>
<script language="javascript">
	function confirmEvent() {
		//document.form.action = "./Ready.jsp";
		var em = document.getElementById('email').value;
		document.getElementById('item').value = encodeURIComponent(document.getElementById('item').value); 
		if(em == '' || em == null)
		{
			alert("이메일을 입력해주세요.");
			document.getElementById('email').focus();
			return;
		}
		document.form.setAttribute("accept-charset", "euc-kr");
		document.form.action = "/holdem/danal/ready.do";
		document.form.method = "POST";
		document.form.submit();
	}
	function init_orderid() {
		var today = new Date();
		var year = today.getFullYear();
		var month = today.getMonth() + 1;
		var date = today.getDate();
		var time = today.getTime();

		if (parseInt(month) < 10) {
			month = "0" + month;
		}

		if (parseInt(date) < 10) {
			date = "0" + date;
		}

		var order_idxx = "WINGAME_" + year + "" + month + "" + date + "" + time;

		document.form.orderid.value = order_idxx;
	}

	function InstallEvent() {
		var tmp = window.open('https://web.teledit.com/Danal/EDI/IKAuth/InstallPlugin.php','', 'width=150px,height=80px;');
	}
</script>
</head>
<%@ include file="/inc/function.jsp"%>
<body onload="javascript:init_orderid();">
	<form name="form">
		<div class="paymentPop">
			<div class="titArea">
				<a href="#" class="logo"><img src="/holdem/img/logo2.gif" width="75"
					height="34" alt="Danal" /></a> <span class="txtArea"><emclass="point">[결제요청]</em>
					이 페이지는 신용카드결제를 요청하는 페이지입니다.</span>
			</div>
			<div class="contenBox">
				<div class="grayBox">
					<div class="grayBox_top">
						<div class="grayBox_btm">
							이 페이지는 신용카드결제를 요청하는 페이지입니다. <br>

						</div>
					</div>
				</div>
				<div class="payInfo">
					<p class="payTitle">신용카드 주문정보</p>
					<div class="payDev">
						<dl>
							<dt>* 주문번호</dt>
							<dd>
								<input type="text" class="it1" value="" name="orderid" />
							</dd>
						</dl>
						<dl>
							<dt>
								* 상품명
								<dd>
									<input type="text" class="it1" value="보석 ${cash}개" name="itemname" id="item"/>
								</dd>
							</dt>
						</dl>
						<dl>
							<dt>* 주문자명</dt>
							<dd>
								<input type="text" class="it" value="${nick}" name="username" />
							</dd>
						</dl>
						<dl>
							<dt>* E-mail</dt>
							<dd>
								<input type="text" class="it3" value="" name="useremail" id="email" />
							</dd>
						</dl>
						<dl>
							<dt>* 주문자 ID</dt>
							<dd>
								<input type="text" class="it" value="${muserid}" name="userid" />
							</dd>
						</dl>
						<dl>
							<dt>* 사용자환경</dt>
							<dd>
								<input type="text" class="it" value="WP" name="useragent" readonly/> "WP/WM/WA/WI" 중 하나.
							</dd>
						</dl>
						<dl>
							<dt>* 가격</dt>
							<dd>
								<input type="text" class="it" name="amount" value="${money}"/> 
							</dd>
						</dl>

					</div>
				</div>
				<div class="btnSet">
					<a href="#"><img src="/holdem/img/btn_payment.gif" width="112"
						height="27" alt="결제요청" onclick="javascript:confirmEvent();" /></a> <a
						href="#"><img src="/holdem/img/btn_first.gif" width="112"
						height="27" alt="처음으로" /></a>
				</div>
			</div>
		</div>
	</form>
</body>
</html>