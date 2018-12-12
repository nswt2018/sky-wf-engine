<procedure>
	<sql>
		select
			*
		from
			dwflowroute
		where
			flowid=@flowid 
			and nodeid=@nodeid
		order by flowid,nodeid,routeid
	</sql>
</procedure>
