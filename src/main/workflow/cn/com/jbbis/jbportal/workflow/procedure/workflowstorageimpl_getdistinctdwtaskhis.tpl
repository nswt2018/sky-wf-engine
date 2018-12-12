<procedure>
	<sql>
	select
		distinct 
		a.bankid,
		a.operid,
        b.username
	from 
		dwtaskhis a,
		cmuser b 
	where 
		b.userstate='0'
		and a.operid = b.userid 
		and wfid = @wfid
		#AND = @nodeid
		
	<!--	#AND = @userloginstate-->
		#AND = @tasktype
		
	</sql>
</procedure>
