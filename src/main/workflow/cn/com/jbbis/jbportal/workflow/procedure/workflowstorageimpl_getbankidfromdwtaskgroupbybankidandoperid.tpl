<procedure>
	<sql db="sqlserver,sybase">
		select 
			rtrim(bankid)+'/'+operid as userid,
			count(bankid) as num 
		from 
			dwtask 
		group by 
			bankid,operid 
		order by 2
	</sql>
	<sql db="oracle,informix">
		select 
			rtrim(bankid)||'/'||operid as userid,
			count(bankid) as num 
		from 
			dwtask 
		group by 
			bankid,operid 
		order by 2
	</sql>
</procedure>