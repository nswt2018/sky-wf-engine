<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="jquery-1.11.3.min.js"></script>
<script type="text/javascript">
    $(function(){
    	debugger;
    	$("#btn").click(function(){
    		var url="http://192.168.43.82:8080/workflow/wf0001s";
    		var params="{\"flowid\":\"0000000001\"}";
    		debugger;
    		alert(params);
    		
    		/*  $(this).load(url,params,function(d){
    			
    			alert(d);
    		});  */
    		
    		/* $.post(url,params,function(d){
    			alert(d.msg);
    			
    		},"json"); */
    		$.ajax({
    			  url:url,
    		      type:"POST",
    		      data:{"flowid":"0000000001"},
    		      processData: false, 
    		      success:function(d){
    		    	  alert(d);
    		      },
    		      error:function(){    		    	  
    		      },
    		      dataType:"application/json;charset=UTF-8"
    		});
    	});

    });  
</script>
</head>
<body>
 <input type="button" id="btn" value="点我">
</body>
</html>