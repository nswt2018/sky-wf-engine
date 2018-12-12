<procedure>
	<sql>
		select 
			bankid,
			postid,
			postbankid,
			superbankscope,
			case when bindprodid='' or bindprodid is null
				then '99'
				else bindprodid 
			end as bindprodid
		from 
		 	dwflownodepost
		where 		    
		    flowid = @flowid
		 	and nodeid = @nodeid
		 	and postauthority=@postauthority
		 	
	</sql>
</procedure>
