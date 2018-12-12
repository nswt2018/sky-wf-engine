<procedure>
	<sql>
		select 
			bankid, superapprbankid, superbankid
		from 
			cmbankprodsuperbankmap 
		where 
			bankid = @bankid
			and bindprodid=@bindprodid
	</sql>
</procedure>
