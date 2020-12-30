<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include>
</head>
<body>
	<div class="frame">
		<div class="bg">
			<jsp:include page="../frame/userMenu.jsp"></jsp:include>
			<div class="body-top">
				<img src="/holdem/webflow/images/top_logo.png" loading="lazy" sizes="(max-width: 767px) 92vw, 100vw" alt="" class="toplogoicon">
				<img src="/holdem/webflow/images/line.png" loading="lazy" sizes="(max-width: 1920px) 100vw, 1920px"
					srcset="/holdem/webflow/images/line-p-500.png 500w, /holdem/webflow/images/line-p-800.png 800w, /holdem/webflow/images/line-p-1080.png 1080w, /holdem/webflow/images/line-p-1600.png 1600w, /holdem/webflow/images/line.png 1920w"
					alt="" class="line">
			</div>
			<div class="body-bot">
				<div class="chipsbg">
					<img src="/holdem/webflow/images/holdem_text.png" loading="lazy" alt="" class="holdemicon">
					<img src="/holdem/webflow/images/middle_text.png" loading="lazy" sizes="(max-width: 767px) 66vw, 766px" 
						srcset="/holdem/webflow/images/middle_text-p-500.png 500w, /holdem/webflow/images/middle_text.png 766w" alt="" class="holdemtxt">
				</div>
				<div class="tbwrap">
					<div class="textback1">
						<img src="/holdem/webflow/images/deco_1.png" loading="lazy" alt="" class="textback1lmage">
					</div>
					<div class="textback1">
						<img src="/holdem/webflow/images/deco_2.png" loading="lazy" alt="" class="textback1lmage">
					</div>
					<div class="textback1">
						<img src="/holdem/webflow/images/deco_3.png" loading="lazy" alt="" class="textback1lmage">
					</div>
					<div class="textback1">
						<img src="/holdem/webflow/images/deco_4.png" loading="lazy" alt="" class="textback1lmage">
					</div>
				</div>
				<div class="body-video">
					<div class="div-block">
						<div style="padding-top: 56.17021276595745%"
							class="video w-video w-embed">
							<iframe class="embedly-embed" src="https://www.youtube.com/embed/${youtube.saveNm}" scrolling="no" title="YouTube embed" frameborder="0" allow="autoplay; fullscreen" allowfullscreen="true"></iframe>
						</div>
					</div>
					<div data-animation="slide" data-duration="500" data-infinite="1"
						class="slider w-slider">
						<div class="w-slider-mask">
							<c:forEach var="item" items="${imageList}">
								<div class="w-slide">
									<img src="/filePath/holdem/photo/${item.saveNm}" loading="lazy" sizes="(max-width: 767px) 50vw, (max-width: 936px) 100vw, 936px" alt="" class="slideimg">
								</div>
							</c:forEach>
						</div>
						<div class="left-arrow w-slider-arrow-left">
							<img src="/holdem/webflow/images/◀.png" loading="lazy" alt="" class="leftarrowicon">
						</div>
						<div class="right-arrow w-slider-arrow-right">
							<img src="/holdem/webflow/images/▶.png" loading="lazy" alt="" class="rightarrowicon">
						</div>
						<div class="slide-nav w-slider-nav w-round"></div>
					</div>
				</div>
				<div class="b_back">
					<div class="down_btnwrap">
						<div class="wingamewrap">
							<img src="/holdem/webflow/images/game_logo.png" loading="lazy" alt="" class="game_logo">
							<div class="wingameholdem"> HOLDEM<br> <span class="text-span">윈게임<strong class="bold-text-3">홀덤</strong></span>
							</div>
						</div>
						<div class="div-block-13">
							<a href="/holdemM.zip" class="googleplay_btn w-inline-block" download>
								<img src="/holdem/webflow/images/googleplay_button1.png" loading="lazy" alt="" class="playbtnlmage">
								<div class="playbtntext"> Google Play 다운로드<br> <span class="playbtntext2">Download</span> </div>
							</a>
							<!-- <a href="/holdem.zip" class="googleplay_btn w-inline-block" download>
								<img src="/holdem/webflow/images/pc_button2.png" loading="lazy" alt="" class="playbtnlmage-2">
								<div class="playbtntext">PC 다운로드<br><span class="playbtntext2">Download</span></div> 
							</a> -->
						</div>
						<%-- <div>
							<a href="/holdemM.zip" class="googleplay_btn w-inline-block" download>
								<img src="/holdem/webflow/images/googleplay_button.png" loading="lazy" alt="" class="googleplay_btnicon">
							</a> 
							<a href="/holdem.zip" class="pcdownload_btn w-inline-block" download>  
							     <a href="/holdem/user/ldPlayerGuide.do" class="pcdownload_btn w-inline-block">
								<img src="/holdem/webflow/images/pc_button.png" loading="lazy" alt="" class="pcdownload_btnicon">
							</a>
						</div> --%>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
		<div class="w-form">
			<form action="/holdem/user/loginprocess.do" id="loginform" name="loginform">
				<input type="hidden" value="${item.midx}" name="midx">
				<c:if test="${re != null }">
					<div class="loginpop" id="loginpop" style="display:flex">
				</c:if>
				<c:if test="${re == null }">
					<div class="loginpop" id="loginpop" style="display:none">
				</c:if>
					<div class="login">
						<div class="logintitle">로그인</div>
						<div class="div-block-14">
							<input type="text" class="logininput w-input" maxlength="256" name="muserid" placeholder="ID" id="muserid" required="">
							<input type="password" class="logininput w-input" maxlength="256" name="muserpw" placeholder="Password" id="muserpw" required="">
						</div>
						<a href="javascript:loginAccess()" class="loginbtn w-button">로그인</a>
						<div class="loginsearchbox">
							<a href="#" class="searchlink w-inline-block">
								<div class="idpwsearch">아이디 찾기</div>
							</a>
							<div class="div-block-15"></div>
							<a href="#" class="searchlink w-inline-block">
								<div class="idpwsearch">비밀번호 찾기</div>
							</a>
						</div>
						<div class="div-block-17"></div>
						<div class="logincontent">WINGAME 회원이 아니신가요?</div>
						<a href="/holdem/user/join.do" class="loginjoinbtn w-button">회원가입</a>
						<a href="#" onclick="$('#loginpop').css('display','none');" class="link-block-20 w-inline-block">
							<img src="/holdem/webflow/images/pngwing.com-4.png" loading="lazy" alt="" class="image-6">
						</a>
					</div>
				</div>
			</form>
		</div>
	</div>
	<script>
		var re = "${re}";
		if(re == 1){
			alert('아이디나 비밀번호가 틀렸습니다.');
		}
		
		function loginAccess() {
			var muserid = document.getElementById("muserid").value;
			var muserpw = document.getElementById("muserpw").value;
			var fm = document.getElementById('loginform');
	
			if (muserid.length == 0) {
				alert('아이디를 입력해주세요.');
				return;
			}
			if (muserpw.length == 0) {
				alert('비밀번호를 입력해주세요.');
				return;
			}
			fm.submit();
		}
	</script>
</body>
</html>