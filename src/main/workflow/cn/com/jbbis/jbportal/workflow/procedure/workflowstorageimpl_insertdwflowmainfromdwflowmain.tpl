<procedure>
	<sql>
		insert into dwflowmain 
			(flowid,
			flowname,
			flowdesc,
			flowstate,
			effdate,
			enddate,
			manusql,
			execsql,
			isWriteBusiWFMap,
			isstarttempauth,
			fieldtabs)
		select 
			@keyno as flowid,
			#if @flowname
				@flowname as 
			#end
			flowname,
			flowdesc,
			'1' as flowstate,
			effdate,
			enddate,
			manusql,
			execsql,
			isWriteBusiWFMap,
			isstarttempauth,
			fieldtabs
		from 
			dwflowmain 
		where 
			flowid = @flowid
	</sql>
</procedure>