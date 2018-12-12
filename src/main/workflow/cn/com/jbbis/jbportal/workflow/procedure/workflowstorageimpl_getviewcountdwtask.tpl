<procedure>
	<sql>
		select 
			count(*) as num
		from 
			dwtask
		where
		    tasktype='1'
		 	and wfid = @wfid
		 	#AND = @nodeid
	</sql>
</procedure>