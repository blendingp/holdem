<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
			<img src="/holdem/webflow/images/top_logo.png" loading="lazy"
				sizes="(max-width: 767px) 92vw, 100vw"
				srcset="/holdem/webflow/images/top_logo-p-500.png 500w, /holdem/webflow/images/top_logo-p-800.png 800w, /holdem/webflow/images/top_logo-p-1080.png 1080w, /holdem/webflow/images/top_logo-p-1600.png 1600w, /holdem/webflow/images/top_logo.png 1867w"
				alt="" class="toplogoicon"><img src="/holdem/webflow/images/line.png"
				loading="lazy" sizes="(max-width: 1920px) 100vw, 1920px"
				srcset="/holdem/webflow/images/line-p-500.png 500w, /holdem/webflow/images/line-p-800.png 800w, /holdem/webflow/images/line-p-1080.png 1080w, /holdem/webflow/images/line-p-1600.png 1600w, /holdem/webflow/images/line.png 1920w"
				alt="" class="line">
			<div class="chipsbg">
				<div class="monititlebox">
					<img src="/holdem/webflow/images/pngwing.com---2020-11-11T111932.785.png" loading="lazy" alt="" class="motitleback">
					<div class="mititle">
						HOLDEM<br>
						<span class="mitotitle">윈게임<strong class="motibold">카지노 홀덤</strong></span>
					</div>
					<div class="mititlebotbox">
						<div class="mititle2">
							Casino <strong class="mititlebold">WINGAME</strong>
						</div>
						<div class="mititle3">지루할 틈이 없는 수많은 유저들! 유저들의 칩은 다 내꺼! 홀덤의 왕이 되어 보세요.</div>
					</div>
				</div>
			</div>
			<div class="tbwrap">
				<div class="textback1">
					<div class="explain">
						<strong class="bold-text-8">수많은</strong> 유저
					</div>
					<img src="/holdem/webflow/images/pngwing.com---2020-11-11T112200.991.png" loading="lazy" alt="" class="textback1lmage">
				</div>
				<div class="textback1">
					<div class="explain">
						<strong class="bold-text-7">심리</strong>게임
					</div>
					<img src="/holdem/webflow/images/pngwing.com---2020-11-11T112200.991.png" loading="lazy" alt="" class="textback1lmage">
				</div>
				<div class="textback1">
					<div class="explain">
						<strong class="bold-text-5">칩게임</strong>은<br>연습이지?
					</div>
					<img src="/holdem/webflow/images/pngwing.com---2020-11-11T112200.991.png" loading="lazy" alt="" class="textback1lmage">
				</div>
				<div class="textback1">
					<div class="explain">
						<strong class="bold-text-6">골드대전</strong><br>진검승부
					</div>
					<img src="/holdem/webflow/images/pngwing.com---2020-11-11T112200.991.png" loading="lazy" alt="" class="textback1lmage">
				</div>
			</div>
			<div class="div-block">
				<div style="padding-top: 56.17021276595745%"
					class="video w-video w-embed">
					<iframe class="embedly-embed"
						src="https://www.youtube.com/embed/${youtube.saveNm}" scrolling="no" title="YouTube embed" frameborder="0"
						allow="autoplay; fullscreen" allowfullscreen="true"></iframe>
				</div>
			</div>
			<div data-animation="slide" data-duration="500" data-infinite="1" class="slider w-slider">
				<div class="w-slider-mask">
					<c:forEach var="item" items="${imageList}" >
					<div class="w-slide">
						<img src="/filePath/holdem/photo/${item.saveNm}" loading="lazy"
							sizes="(max-width: 767px) 50vw, (max-width: 936px) 100vw, 936px"
							alt="" class="slideimg">
					</div>
					</c:forEach>
				</div>
				<div class="left-arrow w-slider-arrow-left">
					<img src="/holdem/webflow/images/◀.png" loading="lazy" alt="" class="leftarrowicon">
				</div>
				<div class="right-arrow w-slider-arrow-right">
					<img src="/holdem/webflow/images/▶.png" loading="lazy" alt=""
						class="rightarrowicon">
				</div>
				<div class="slide-nav w-slider-nav w-round"></div>
			</div>
			<div class="b_back">
				<div class="down_btnwrap">
					<a href="/holdem.zip" style="text-decoration:none;" download>
						<div class="wingamewrap">
							<img src="/holdem/webflow/images/game_logo.png" loading="lazy" alt=""
								class="game_logo">
							<div class="wingameholdem">
								WINGAME HOLDEM<br>
								<span class="text-span">윈게임<strong class="bold-text-3">홀덤</strong></span>
							</div>
						</div>
					</a>
					<div>
						<a href="#" class="googleplay_btn w-inline-block"><img
							src="/holdem/webflow/images/googleplay_button.png" loading="lazy" alt=""
							class="googleplay_btnicon"></a> <a href="#"
							class="onstore_btn w-inline-block"><img
							src="/holdem/webflow/images/onestore_button.png" loading="lazy" alt=""
							class="onstore_btnicon"></a>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
</body>
</html>