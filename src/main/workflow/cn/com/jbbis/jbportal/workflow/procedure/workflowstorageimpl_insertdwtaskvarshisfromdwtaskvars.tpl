<procedure>
	<sql>
		insert into 
			dwtaskvarshis
			(WFID,
			TASKSER,
			NODEID,
			VARNAME,
			VARVALUE,
			LASTCHGDATE,
			LASTCHGTIME			
			)
		select 
			WFID,
			TASKSER,
			NODEID,
			VARNAME,
			VARVALUE,
			LASTCHGDATE,
			LASTCHGTIME 
		from 
			dwtaskvars 
		where 
			wfid = @wfid
	</sql>
</procedure>