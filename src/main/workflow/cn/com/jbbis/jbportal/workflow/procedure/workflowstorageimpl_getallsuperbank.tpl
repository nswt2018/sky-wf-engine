<procedure>
	<sql>

		WITH report( bankid,bankname,superbankid,banklevel ) 
		AS (
			SELECT bankid, bankname, superbankid,banklevel 
			FROM cmBank WHERE bankid = @bankid
			UNION all 
			SELECT a.bankid, a.bankname, a.superbankid,a.banklevel 
			FROM cmBank a, report b 
			WHERE a.bankid = b.superbankid AND a.banklevel!='1') 
		SELECT bankid 
		FROM report
		order by banklevel,bankid
				
	</sql>
</procedure>
