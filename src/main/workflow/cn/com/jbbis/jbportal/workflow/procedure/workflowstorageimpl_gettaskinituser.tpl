<procedure>
	<sql>
		select 
			a.*,
			d.username 
		from 
			dwtaskhis a,
			dwflownode b,
			dwflowinst c,
			cmuser d
		where 
			a.wfid = c.wfid
			and c.flowid = b.flowid
			and a.operid = d.userid
			and a.wfid = @wfid
			and b.nodetype= @nodetype
	</sql>
</procedure>