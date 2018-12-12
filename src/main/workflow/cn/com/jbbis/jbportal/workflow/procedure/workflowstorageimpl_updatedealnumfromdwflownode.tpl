<procedure>
	<sql>
		update 
	 		dwflownode
	 	set
	 		mindealnum = @number:mindealnum
	 	where
	 		flowid = @flowid
	 		and nodeid = @nodeid
	</sql>
</procedure>