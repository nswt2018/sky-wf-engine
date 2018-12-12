<procedure>
	<sql>
		select
			bankid, serid, sername, serno
		from
			cmserno
		where
			bankid=@bankid and serid=@serid
	</sql>
</procedure>
