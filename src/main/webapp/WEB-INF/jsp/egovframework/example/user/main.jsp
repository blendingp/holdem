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
			<div class="body-top" style="width:100%;">
				<img src="/holdem/webflow/images/top_logo.png" loading="lazy"
					sizes="(max-width: 767px) 92vw, 100vw"
					srcset="/holdem/webflow/images/top_logo-p-500.png 500w, /holdem/webflow/images/top_logo-p-800.png 800w, /holdem/webflow/images/top_logo-p-1080.png 1080w, /holdem/webflow/images/top_logo-p-1600.png 1600w, /holdem/webflow/images/top_logo.png 1867w"
					alt="" class="toplogoicon"><img src="/holdem/webflow/images/line.png"
					loading="lazy" sizes="(max-width: 1920px) 100vw, 1920px"
					srcset="/holdem/webflow/images/line-p-500.png 500w, /holdem/webflow/images/line-p-800.png 800w, /holdem/webflow/images/line-p-1080.png 1080w, /holdem/webflow/images/line-p-1600.png 1600w, /holdem/webflow/images/line.png 1920w"
					alt="" class="line">
			</div>
			<div class="body-bot" style="width:100%;">
				<div class="chipsbg">
					<img src="/holdem/webflow/images/holdem_text.png" loading="lazy" alt="" class="holdemicon">
					<img src="/holdem/webflow/images/middle_text.png" loading="lazy" sizes="(max-width: 767px) 66vw, 766px"
						srcset="/holdem/webflow/images/middle_text-p-500.png 500w, /holdem/webflow/images/middle_text.png 766w"
						alt="" class="holdemtxt">
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
						<div style="padding-top: 56.17021276595745%" class="video w-video w-embed">
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
				</div>
				<div class="b_back">
					<div class="down_btnwrap">
						<div class="wingamewrap">
							<img src="/holdem/webflow/images/game_logo.png" loading="lazy" alt="" class="game_logo">
							<div class="wingameholdem">
								HOLDEM<br>
								<span class="text-span">윈게임<strong class="bold-text-3">홀덤</strong></span>
							</div>
						</div>
						<div>
							<a href="/holdemM.zip" class="googleplay_btn w-inline-block" download>
								<img src="/holdem/webflow/images/googleplay_button.png" loading="lazy" alt="" class="googleplay_btnicon">
							</a> 
							<%-- <a href="/holdem.zip" class="pcdownload_btn w-inline-block" download>  
							     <a href="/holdem/user/ldPlayerGuide.do" class="pcdownload_btn w-inline-block">
								<img src="/holdem/webflow/images/pc_button.png" loading="lazy" alt="" class="pcdownload_btnicon">
							</a>--%>
						</div>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
</body>
</html>