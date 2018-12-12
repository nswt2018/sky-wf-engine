<procedure>
	<sql>
		select 
			bankid,
			prodid,
			flowid,
			flowtype,
			iselecapprflag,
			submtrancode,
			flowbindstyle
		from 
			cmbankelecscope 
		where
			1=1
			#AND =@submtrancode
	</sql>
</procedure>