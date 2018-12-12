<procedure>
	<sql>
		delete from
	 		dwtask
	 	where
	 		wfid=@wfid
	 		and nodeid = @nodeid
	</sql>
</procedure>