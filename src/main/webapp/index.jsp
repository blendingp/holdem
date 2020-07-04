<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ui"     uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>
</head>

<body>

<script src="//code.jquery.com/jquery-1.11.0.min.js"></script> 
    <script type="text/javascript">
	var wsUri = "ws://localhost:8080/holdem/websocket/server.do";
         function init() {            
            websocket = new WebSocket(wsUri);

            websocket.onopen = function(evt) {
            	console.log("test");
                onOpen(evt);
            };

            websocket.onmessage = function(evt) {
                onMessage(evt);
            };

            websocket.onerror = function(evt) {
                onError(evt);
            };
        }

        function onOpen(evt) {            
            console.log("onOpen!");
            var obj = new Object();
            obj.protocol = "chatinit";
            obj.useridx = useridx; 
            doSend(JSON.stringify(obj));
        }
     
        function onError(evt) {
            writeToScreen('ERROR: ' + evt.data);
        }

        function doSend(message) {
            websocket.send(message);
        }                 

        function onMessage(evt) {
            console.log("onMessage");
            var obj;
            obj = JSON.parse(evt.data);
            if(obj.protocol == "createresult"){
                console.log("createresult!:"+obj.room);         
            }        
        }
        
        init();
    </script>
</body>
</html>