<procedure>
	<sql>
	 	select
	 		flowname,
	 		flowstate,
	 		flowdesc
	 	from 
	 		dwflowmain
	 	where 
	 		flowid = @flowid
	</sql>
</procedure>