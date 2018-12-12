<procedure>
	<sql>
		select
			* 
		from
			dbapptempauthbook
		where
			authbankid = @authbankid
			and authperson = @authperson
			and @date:sysdate&gt;=begindate
			and @date:sysdate&lt;=enddate
			and busiscope like @'%{busiscope}%'
			and tempauthstat = '2'
	</sql>
</procedure>