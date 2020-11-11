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
<meta charset="UTF-8">
<title>WinGame</title>
</head>
<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.css"/>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/slick-carousel@1.8.1/slick/slick.min.js"></script>
<body>
userMain<br/>
<a href="/holdem/user/notice.do">공지사항</a><br/>
<a href="/holdem/user/game.do">게임설명</a><br/>
<a href="/holdem/user/provision.do">약관</a><br/>
<a href="/holdem/user/inquiry.do">1:1문의</a><br/>
<iframe style="width:50%; height:500px;" src="https://www.youtube.com/embed/${youtube.saveNm}" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" ></iframe>
<br/>
<div id="imageList" style="width:50%;">
<c:forEach var="item" items="${imageList}" >
	<img src="/filePath/holdem/photo/${item.saveNm}" style="max-height:450px;"/>
</c:forEach>
</div> 
<script>
$('#imageList').slick({
	 infinite: true,
	 slidesToShow: 1,
	 slidesToScroll: 1 ,
});	 

</script>
</body>
</html>