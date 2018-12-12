<procedure>
	<sql>
		select 
			bankid,userid
		from 
			dwtaskhis
		where
		    tasktype='2'
		 	and wfid = @wfid
		 	#AND = @nodeid
	</sql>
</procedure>