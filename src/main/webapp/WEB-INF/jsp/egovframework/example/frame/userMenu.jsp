<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<div class="topmenu_back">
	<div class="topmenuwrap">
		<a href="/holdem/user/main.do" class="wingamebtn w-inline-block">
			<img src="/holdem/webflow/images/WINGAME.png" loading="lazy" alt="" class="wingamelogo">
			</a>
		<div class="div-block-19">
			<c:if test="${midx == null }">
				<a href="/holdem/user/notice.do" class="topmenu_btn w-inline-block">
					<div class="text-block-44">공지사항</div>
				</a>
				<a href="/holdem/user/etcB/game.do?type=G" class="topmenu_btn w-inline-block">
					<div class="text-block-44">게임설명</div>
				</a>
				<a href="/holdem/user/inquiry.do" class="topmenu_btn w-inline-block">
					<div class="text-block-44">1:1문의</div>
				</a>
				<!-- <a href="/holdem/user/main.do?re=2" class="topmenu_btn w-inline-block">
					<div class="text-block-44">로그인</div>
				</a> -->
			</c:if>
			<c:if test="${midx != null }">
				<a href="/holdem/user/notice.do" class="topmenu_btn w-inline-block">
					<div class="text-block-44">공지사항</div>
				</a>
				<a href="/holdem/user/etcB/game.do?type=G" class="topmenu_btn w-inline-block">
					<div class="text-block-44">게임설명</div>
				</a>
				<a href="/holdem/user/inquiry.do" class="topmenu_btn w-inline-block">
					<div class="text-block-44">1:1문의</div>
				</a>
				<a href="/holdem/user/shop.do" class="topmenu_btn w-inline-block">
					<div class="text-block-44">상점</div>
				</a>
				<a href="/holdem/user/myinfo.do" class="topmenu_btn w-inline-block">
					<div class="text-block-44">내정보</div>
				</a>
				<a href="/holdem/user/logoutprocess.do" class="topmenu_btn w-inline-block">
					<div class="text-block-44">로그아웃</div>
				</a>
			</c:if>
		</div>
	</div>
</div>