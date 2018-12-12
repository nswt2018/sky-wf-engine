<procedure>
	<sql>
		select 
		 	*
		from 
		 	dwtaskhis 
		where 
		 	wfid = @wfid
		 	#AND = @nodeid
			order by taskser
	</sql>
</procedure>
