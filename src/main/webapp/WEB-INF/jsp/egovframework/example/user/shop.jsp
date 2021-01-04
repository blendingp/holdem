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
<html data-wf-page="5fe99ecbbd1d5613550cc51c" data-wf-site="5faa9fb759c008dd7db7c885">
<head>
<jsp:include page="../frame/userTop.jsp"></jsp:include>
</head>
<body>
	<div class="frame">
		<div class="bg-copy">
			<jsp:include page="../frame/userMenu.jsp"></jsp:include>
			<div class="body">
				<div class="w-form">
					<form id="submitForm" name="submitForm">
						<div class="notititlebox">
							<img src="/holdem/webflow/images/WINGAME.png" loading="lazy" alt=""
								class="notilogo">
							<div class="notititle">
								윈게임<span class="text-span-10">상점</span>센터입니다.
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
									<div class="listlink click">상점</div>
								</a>
							</div>
						</div>
						<div class="shopbox">
							<div class="snotice">보석은 구매 즉시 적용됩니다.</div>
							<div class="itemwrap">
								<c:forEach var="item" items="${pmList}">
									<c:if test="${fn:contains(item.product , 'Gem')}">
										<div class="itembox">
											<div class="iconwrap">
												<img src="/holdem/webflow/images/5fe997cb6dd2c7939c57ec84_quality.png" loading="lazy" alt="" class="image-7">
											</div>
											<div class="itemname">${item.title}</div>
											<div class="cashbox">
												<div class="div-block-18">
													<img src="/holdem/webflow/images/coin_icon-2.png" alt="" class="cashicon2">
													<div class="cash">${item.price}</div>
												</div>
												<a href="javascript:danalCard('${item.product}' , '${item.title}' , '${item.price}')" class="bbtn w-button">구매하기</a>
											</div>
										</div>									
									</c:if>
								</c:forEach>
								<!-- <div class="itembox">
									<div class="iconwrap">
										<img src="/holdem/webflow/images/5fe997cb6dd2c7939c57ec84_quality.png" loading="lazy" alt="" class="image-7">
									</div>
									<div class="itemname">보석 110</div>
									<div class="cashbox">
										<div class="div-block-18">
											<img src="/holdem/webflow/images/coin_icon-2.png" alt="" class="cashicon2">
											<div class="cash">1,100</div>
										</div>
										<a href="javascript:danalCard('110' , '100')" class="bbtn w-button">구매하기</a>
									</div>
								</div>
								<div class="itembox">
									<div class="iconwrap">
										<img src="/holdem/webflow/images/5fe997cb6dd2c7939c57ec84_quality.png" loading="lazy" alt="" class="image-7">
									</div>
									<div class="itemname">보석 550</div>
									<div class="cashbox">
										<div class="div-block-18">
											<img src="/holdem/webflow/images/coin_icon-2.png" alt="" class="cashicon2">
											<div class="cash">5,500</div>
										</div>
										<a href="javascript:danalCard('550' , '5500')" class="bbtn w-button">구매하기</a>
									</div>
								</div>
								<div class="itembox">
									<div class="iconwrap">
										<img src="/holdem/webflow/images/5fe997cb6dd2c7939c57ec84_quality.png" loading="lazy" alt="" class="image-7">
									</div>
									<div class="itemname">보석 1,100</div>
									<div class="cashbox">
										<div class="div-block-18">
											<img src="/holdem/webflow/images/coin_icon-2.png" alt="" class="cashicon2">
											<div class="cash">11,000</div>
										</div>
										<a href="javascript:danalCard('1100' , '11000')" class="bbtn w-button">구매하기</a>
									</div>
								</div>
								<div class="itembox">
									<div class="iconwrap">
										<img src="/holdem/webflow/images/5fe997cb6dd2c7939c57ec84_quality.png" loading="lazy" alt="" class="image-7">
									</div>
									<div class="itemname">보석 2,200</div>
									<div class="cashbox">
										<div class="div-block-18">
											<img src="/holdem/webflow/images/coin_icon-2.png" alt="" class="cashicon2">
											<div class="cash">22,000</div>
										</div>
										<a href="javascript:danalCard('2200' , '22000')" class="bbtn w-button">구매하기</a>
									</div>
								</div>
								<div class="itembox">
									<div class="iconwrap">
										<img src="/holdem/webflow/images/5fe997cb6dd2c7939c57ec84_quality.png" loading="lazy" alt="" class="image-7">
									</div>
									<div class="itemname">보석 5,500</div>
									<div class="cashbox">
										<div class="div-block-18">
											<img src="/holdem/webflow/images/coin_icon-2.png" alt="" class="cashicon2">
											<div class="cash">55,000</div>
										</div>
										<a href="javascript:danalCard('5500' , '55000')" class="bbtn w-button">구매하기</a>
									</div>
								</div>
								<div class="itembox">
									<div class="iconwrap">
										<img src="/holdem/webflow/images/5fe997cb6dd2c7939c57ec84_quality.png" loading="lazy" alt="" class="image-7">
									</div>
									<div class="itemname">보석 9,900</div>
									<div class="cashbox">
										<div class="div-block-18">
											<img src="/holdem/webflow/images/coin_icon-2.png" alt="" class="cashicon2">
											<div class="cash">99,000</div>
										</div>
										<a href="javascript:danalCard('9900' , '99000')" class="bbtn w-button">구매하기</a>
									</div>
								</div> -->
							</div>
						</div>
						<a href="/holdem/user/main.do" class="deletebtn-7 a w-inline-block">
							<div class="deletebtntext">홈으로</div>
						</a>
					</form>
				</div>
			</div>
		</div>
		<jsp:include page="../frame/userBottom.jsp"></jsp:include>
	</div>
	<script>
		function danalCard(code , prdtNm , money){
			console.log('danalCard');
			$.ajax({
				type:'post',
				url:'/holdem/user/checkCashLimit.do?m='+money,
				success:function(data){
					if(data == 'success')
					{
			 		    var idx = "${midx}";
					    if(idx == null || idx == '')
					    {
					    	location.href="/holdem/user/main.do?re=2";
					    }
					    window.open("/holdem/danal/order.do?code="+code+"&prdtNm="+prdtNm+"&money="+money , "DANAL" , "toolbar=yes,scrollbars=yes,resizable=yes,width=700,height=500") ; 
					}
					else
					{
						alert("<월 충전한도 초과>\n관계 법령에 의거하여 월 충전 한도 금액(50만원)을 초과하여 충전할 수 없습니다.");
						return;
					}
				}
			})
		}
	</script>
</body>
</html>