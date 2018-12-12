<procedure>
	<sql>
		insert into dwtaskvars(
	 		wfid,
	 		taskser,
	 		nodeid,
	 		varname,
	 		varvalue,
	 		lastchgtime,
	 		lastchgdate
	 	) values(
	 		@wfid,
	 		@number:taskser,
	 		@nodeid,
	 		@varname,
	 		@varvalue,
	 		@lastchgtime,
	 		@date:lastchgdate
	 	)
	</sql>
</procedure>
