<procedure>
	<sql>
		select 		    
			userid,
			username,
			bankid
		from 
			cmuser 
		where 
			bankid = @bankid
			and userstate='0'
		<!--	#AND = @userloginstate
			and userid != @operid --><!--liuxj-->
			and (postidset like @'%,{postidset},%'
			or postidset like @'{postidset},%'
			or postidset like @'%,{postidset}' 
			or postidset = @postidset)
	</sql>
</procedure>