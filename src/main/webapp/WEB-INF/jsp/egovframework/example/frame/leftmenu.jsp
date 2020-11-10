<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="java.util.*"%>

<!-- Navigation -->
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
	<div class="navbar-header">
		<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
			<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span>
			<span class="icon-bar"></span> <span class="icon-bar"></span>
		</button>
		<a class="navbar-brand" href="/holdem/admin/main.do">WINGAME</a>
	</div>

	<ul class="nav navbar-top-links navbar-right">
		<li class="dropdown">
			<a class="dropdown-toggle" data-toggle="dropdown" href="#"> 
				<i class="fa fa-user fa-fw"></i>
				<i class="fa fa-caret-down"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li class="divider"></li>
				<li><a href="/holdem/admin/logout.do"><i class="fa fa-sign-out fa-fw"></i>Logout</a></li>
			</ul>
		</li>
	</ul>

	<div class="navbar-default sidebar" role="navigation">
		<div class="sidebar-nav navbar-collapse">
			<ul class="nav" id="side-menu">
				<li><a href="/holdem/admin/main.do"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a></li>
				<li><a href="/holdem/admin/gamelogp.do"><i class="fa fa-gamepad fa-fw"></i> 게임로그</a></li>
				<li><a href="/holdem/admin/chat.do"><i class="fa fa-comment fa-fw"></i> 채팅관리</a></li>
				<li>
					<a href="#"><i class="fa fa-thumb-tack fa-fw"></i> 메인<span class="fa arrow"></span></a>
					<ul class="nav nav-second-level">
						<li><a href="/holdem/admin/youtube.do"><i class="fa fa-video-camera fa-fw"></i> 유튜브 설정</a></li>
						<li><a href="/holdem/admin/image.do"><i class="fa fa-image fa-fw"></i> 이미지 설정</a></li>
					</ul>
				</li>
				<li><a href="/holdem/admin/notice.do"><i class="fa fa-exclamation-circle fa-fw"></i> 공지사항</a></li>
				<li><a href="/holdem/admin/inquiry.do"><i class="fa fa-question-circle fa-fw"></i> 1:1문의</a></li>
				<li><a href="/holdem/admin/gameDescription.do"><i class="fa fa-gamepad fa-fw"></i> 게임설명</a></li>
				<li><a href="/holdem/admin/provision.do"><i class="fa fa-file-text fa-fw"></i> 약관</a></li>
			</ul>
		</div>
	</div>
</nav>