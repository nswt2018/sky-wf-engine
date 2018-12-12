<procedure>
	<sql>
	
		insert into dwflownodepost
			(flowid, 
			nodeid, 
			bankid, 
			postid, 
			postbankid,
			postauthority,
			superbankscope,
			bindprodid)
		select 
			@keyno as flowid,
			nodeid, 
			bankid, 
			postid, 
			postbankid,
			postauthority,
			superbankscope,
			bindprodid
		from
			dwflownodepost 
		where 
			flowid = @flowid
	</sql>
</procedure>