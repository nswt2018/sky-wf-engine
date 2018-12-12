<procedure>
	<sql>
		insert into dwBusiScopeWFMap
				(busiscope,
				flowid,
				operid,
				bankid,
				createdate,
				lastchanperson,
				lastchanbankid,
				lastchandate,
				busiscopename,
				lastchangetime)
			select
				busiscope,
				@keyno AS flowid,
				operid,
				bankid,
				createdate,
				lastchanperson,
				lastchanbankid,
				lastchandate,
				busiscopename,
				lastchangetime
			from 
				dwBusiScopeWFMap 
			where 
				flowid = @flowid
	</sql>
</procedure>