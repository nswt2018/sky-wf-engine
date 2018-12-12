<procedure>
	<sql>
		select 
			a.bankid,
			a.operid,
			b.nodetype 
		from 
			dwtaskhis a,
			dwflownode b,
			dwflowinst c 
		where 
			a.wfid = c.wfid 
			and a.nodeid = b.nodeid 
			and b.flowid = c.flowid
			and a.wfid= @wfid
			and a.nodeid= @nodeid
	</sql>
</procedure>