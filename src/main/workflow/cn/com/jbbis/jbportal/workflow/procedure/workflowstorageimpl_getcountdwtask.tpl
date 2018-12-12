<procedure>
	<sql>
		select 
			count(*) as num
		from 
			dwtask
		where
		    tasktype='2'
		 	and wfid = @wfid
		 	#AND = @nodeid
	</sql>
</procedure>