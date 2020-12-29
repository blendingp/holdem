<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<!--  This site was created in Webflow. http://www.webflow.com  -->
<!--  Last Published: Tue Dec 29 2020 04:01:34 GMT+0000 (Coordinated Universal Time)  -->
<html data-wf-page="5fe99ecbbd1d56dd7f0cc51a" data-wf-site="5faa9fb759c008dd7db7c885">
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include>
</head>
<body>
	<div class="frame">
		<div class="bg-copy">
			<jsp:include page="../frame/userMenu.jsp"></jsp:include>
			<div class="body">
				<div class="w-form">
					<form id="infoform" name="infoform" action="/holdem/user/myinfoupdate.do">
						<input type="hidden" value="${item.midx}" name="midx">
						<input type="hidden" name="muserid" value="${item.muserid}" />
						<div class="notititlebox">
							<img src="/holdem/webflow/images/WINGAME.png" loading="lazy" alt="" class="notilogo">
							<div class="notititle">
								윈게임<span class="text-span-10">내 정보</span>센터입니다.
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
								<a href="#" class="listhomelink w-inline-block">
									<div class="listlink click">내 정보</div>
								</a>
							</div>
						</div>
						<div class="myinfobox">
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">아이디</div>
								</div>
								<div class="infotxt">${item.muserid}</div>
							</div>
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">닉네임</div>
								</div>
								<div class="jnwrap">
									<div class="infotxt">${item.nickname}</div>
								</div>
							</div>
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">현재 비밀번호</div>
								</div>
								<div class="jnwrap">
									<input type="password" class="jinput w-input" maxlength="256" name="muserpw" placeholder="비밀번호를 입력해주세요" id="muserpw" required="">
									<div class="jnotice2" id="muserpwn" style="display:none">일치하지않습니다.</div>
								</div>
								<div class="jnotice" id="muserpwn2" style="display:none">비밀번호를 재입력 해주세요.</div>
							</div>
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">새 비밀번호</div>
								</div>
								<div class="jnwrap">
									<input type="password" class="jinput w-input" maxlength="256" name="muserpw2" placeholder="새 비밀번호를 입력해주세요 (8~15자리 영문,숫자,특수문자 조합)" id="muserpw2" required="">
									<div class="jnotice" id="muserpw2n" style="display:none">비밀번호를 재입력 해주세요.</div>
								</div>
							</div>
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">새 비밀번호 확인</div>
								</div>
								<div class="jnwrap">
									<input type="password" class="jinput w-input" maxlength="256" name="muserpw3" placeholder="새 비밀번호를 재입력해주세요" id="muserpw3" required="">
									<div class="jnotice2" id="muserpw3n">일치하지않습니다.</div>
								</div>
								<div class="jnotice" id="muserpw3n2" style="display:none">비밀번호를 재입력 해주세요.</div>
							</div>
							<div class="jrow">
								<div class="jbox">
									<div class="jtxt">생년월일</div>
								</div>
								<div class="infotxt">${item2.birthdate}</div>
							</div>
							<div class="notice">※ 회원님의 소중한 개인정보는 WINGAME 개인정보처리방침에 따라 안전하게 관리되고 있습니다.</div>
						</div>
						<a href="#" onclick="javascript:updateMyinfo()" class="deletebtn-7 a w-inline-block">
							<div class="deletebtntext">변경하기</div>
						</a>
					</form>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
	<script>
	var result = "${result}";
	if(result=="1") alert("패스워드가 틀렸습니다");
	if(result=="0") alert("패스워드가 변경되었습니다");
		function updateMyinfo() {
			var muserpw = document.getElementById("muserpw").value;
			var muserpw2 = document.getElementById("muserpw2").value;
			var muserpw3 = document.getElementById("muserpw3").value;
	
			if(muserpw.length == 0 || muserpw2.length == 0)
			{
				alert("비밀번호를 입력해주세요");
				return;
			}
			if(muserpw3 != muserpw2)
			{
				alert("비밀번호가 일치하지 않습니다");
				return;
			}

			var fm = document.getElementById('infoform');
			fm.submit();
		}
	</script>
</body>
</html>