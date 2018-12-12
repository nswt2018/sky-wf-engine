<procedure>
	<sql>
		select 
			wfid,
			taskser,
			nodeid,
			varname,
			varvalue
		from 
			 dwtaskvars 
		where 
			 wfid = @wfid
			 #AND = @number:taskser
			 #AND = @varname
		#if @order
			 order by taskser asc
		#end 
	</sql>
</procedure>
