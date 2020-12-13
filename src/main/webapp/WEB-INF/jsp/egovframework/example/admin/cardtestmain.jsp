<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
	<script src="/holdem/vendor/jquery/jquery.min.js"></script>
	<style>
		.card{
			display:inline-block;margin:1px;width:20px;border:1px solid black;cursor:pointer;
			position:relative;
			}
		.card:hover{background:red;
			display:inline-block;margin:1px;width:20px;border:1px solid black;cursor:pointer;
			}
		.cdn{position:absolute;top:1px;left:1px;}
		body{padding:20px;}
	</style>
</head>
<body oncontextmenu="return false" onselectstart="return false" ondragstart="return false">
<button onclick="shuffle()">Shuffle</button>
<button onclick="sendcard()">전송</button>
<button onclick="basemode()">전송끄기</button>
<br><br>

<div id="cardpan" style="width:300px;border:1px solid gray;">
</div>
<script>
var card=[];
	function cardpattern(v){
		switch(parseInt(v/13) ){
		case 0:return '<div style="opacity:0.5;color:black;">♠</div>';
		case 1:return '<div style="opacity:0.5;color:red;">◇</div>';
		case 2:return '<div style="opacity:0.5;color:red;">♡</div>';
		case 3:return '<div style="opacity:0.5;color:black;">♣</div>';
		}
	}
	function cardnum(v){
		switch( v%13 ){
		case 0:return 'A';
		case 10: return 'J';
		case 11: return 'Q';
		case 12: return 'K';
		default:
			return ""+((v%13)+1);
		}
	}
	function shuffle(){
		card=[];
		for(let i=0;i<52;i++)
			card[i]=i;
		for(let i=0;i<100000;i++)
		{
			let pick1 =  parseInt(Math.random()*52);
			let tmp = card[i%52];
			card[i%52] = card[pick1];
			card[pick1] = tmp;
		}
		$("#cardpan").html("");
		for(let i=0;i<52;i++)
		{
			$("#cardpan").append("<div class='card' onclick='change(this)' value='"+card[i]+"'>"
					+cardpattern(card[i])+"<div class='cdn'>"+cardnum(card[i])+"</div></div>");
		}
	}
	function change(obj){
		let a1 = $(obj).clone();
		$(obj).remove();
		$("#cardpan").prepend(a1);
	}
	function sendcard(){
		let value='';
		$("#cardpan div").each(function(){
			let v = $(this).attr('value');
			if( value.length != 0 ) value+=",";
			value+=v;
		});
		jQuery.ajax({
			type:"POST",
			url :"/holdem/cardtest/change.do?mode=1&cards=" +value,
			dataType:"json",
			success : function(data) {
				console.log("data:"+data);
			},
			complete : function(data) { },
			error : function(xhr, status , error){console.log("ajax ERROR!!! : " );}
		});
	}
	function basemode(){
		jQuery.ajax({
			type:"POST",
			url :"/holdem/cardtest/change.do?mode=0&cards=1,1",
			dataType:"json",
			success : function(data) {
				console.log("data:"+data);
			},
			complete : function(data) { },
			error : function(xhr, status , error){console.log("ajax ERROR!!! : " );}
		});
	}
	shuffle();
	
</script>
</body>
</html>