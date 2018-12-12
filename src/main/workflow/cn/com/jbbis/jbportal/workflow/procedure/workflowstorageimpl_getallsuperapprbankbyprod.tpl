<procedure>
	<sql>
		WITH t1 (i, bankid, superapprbankid)
		     AS (SELECT 1, a.BANKID, a.superapprbankid
		           FROM CMBANKPRODSUPERBANKMAP a
		          WHERE a.bankid = @bankid AND a.bindprodid = @bindprodid
		         UNION ALL
		         SELECT i + 1, a.BANKID, a.superapprbankid
		           FROM CMBANKPRODSUPERBANKMAP a, t1 b
		          WHERE     i &lt; 20
		                AND a.bindprodid = @bindprodid
		                AND a.bankid = b.superapprbankid
		                AND a.bankid != a.superapprbankid)
		SELECT bankid
		  FROM t1
	</sql>
</procedure>
