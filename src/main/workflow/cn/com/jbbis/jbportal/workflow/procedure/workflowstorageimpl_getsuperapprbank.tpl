<procedure> 
	<sql>
		 SELECT bankid,  
              bankname,
              superapprbankid,
              banklevel
       FROM cmBank
       WHERE bankid = @bankid
				
	</sql>
</procedure>
