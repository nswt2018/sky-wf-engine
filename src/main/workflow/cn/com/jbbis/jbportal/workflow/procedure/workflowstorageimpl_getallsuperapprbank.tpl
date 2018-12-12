<procedure>
	<sql>
	
		WITH report(bankid, 
		            bankname,
		            superapprbankid, 
		            banklevel) AS (
		             SELECT bankid,  
		                                  bankname,
		                                  superapprbankid,
		                                  banklevel
		                           FROM cmBank
		                           WHERE banklevel = 1
		            UNION all
		            SELECT bankid,  
		                                  bankname,
		                                  superapprbankid,
		                                  banklevel
		                           FROM cmBank
		                           WHERE bankid = @bankid
		                           UNION ALL
		                           SELECT a.bankid,
		                                  a.bankname,
		                                  a.superapprbankid,
		                                  a.banklevel
		                           FROM cmBank a,
		                                report b
		                           WHERE a.bankid = b.superapprbankid
		                           AND   a.banklevel != '1')
		SELECT bankid
		FROM report
		ORDER BY banklevel,
		         bankid
				
	</sql>
</procedure>
