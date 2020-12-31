<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html data-wf-page="5fead5b30531f8fb18593f53" data-wf-site="5faa9fb759c008dd7db7c885">
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include>
</head>
<body>
<div class="frame">
		<div class="bg-copy">
			<jsp:include page="../frame/userMenu.jsp"></jsp:include>
			<div class="body">
				<div class="w-form">
					<form id="joinform" name="joinform">
						<div class="notititlebox">
							<img src="images/WINGAME.png" loading="lazy" alt="" class="notilogo">
							<div class="notititle">
								윈게임<span class="text-span-10">본인인증</span>센터입니다.
							</div>
							<div class="notibottitle">
								윈게임을 이용해 주셔서 감사합니다.<br>궁금하신 사항이 있으시다면 언제든지 문의해 주시기 바랍니다.
							</div>
						</div>
						<div class="togbox">
							<div class="listclick">
								<a href="/holdem/user/main.do" class="listhomelink w-inline-block">
									<div class="listlink">HOME</div>
								</a>
								<div class="listhome">&gt;</div>
								<a href="/holdem/user/verification.do" class="listhomelink w-inline-block">
									<div class="listlink click">본인인증</div>
								</a>
							</div>
						</div>
						<div class="verificationbox">
							<div class="vnotice">
								WINGAME은 [정보통신망 이용 촉진 및 정보보호 등에 관한 법률] 제 23조의 2(주민등록번호의 사용제한)에
								의거 회원님의 주민번호를 저장하지 않습니다.<br>또한 청소년 유해 매체 물을 포함하고 있어 정보통신망
								이용촉진 및 정보보호 등에 관한 법률 및 청소년 보호법의 규정에 의하여 <br>18세 미만의 청소년은 일부
								컨텐츠에 대해 이용할 수 없습니다.
							</div>
							<div class="vbox">
								<div class="vtxt">휴대폰으로 인증하기</div>
								<img
									src="/holdem/webflow/images/iconfinder_mobile-phone-smartphone-handphone-gadget_3643758.png"
									loading="lazy" alt="" class="phone"> 
								<a href="#" onclick="javascript:window.open('/holdem/user/joinNice.do','_blank', 'toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=400,height=400')" class="vbtn w-button">인증하기</a>
							</div>
							<div class="vnotice2">
								<span class="text-span-13">알아두세요!<br></span>- 본인인증은 이벤트 혜택
								중복 지급 체크 이외 어떠한 용도로도 사용되지 않습니다.<br>- 본인인증 시 성인인증도 동시에
								이루어집니다.<br>- 이벤트로 지급되는 캐시는 보너스캐시로 30일 유효기간이 있으며 경과 시 자동소멸
								됩니다.
							</div>
						</div>
						<a href="#" class="deletebtn-7 a w-inline-block">
							<div class="deletebtntext">다음에 하기</div>
						</a>
					</form>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
</body>
</html>