<procedure>
	<sql name="getallcmbankbyprodid">
		select 
			 *
		from 
			 CMBANKPRODSUPERBANKMAP 
		where 
			 bankid = @bankid
			 and bindprodid =@bindprodid
	</sql>
</procedure>
