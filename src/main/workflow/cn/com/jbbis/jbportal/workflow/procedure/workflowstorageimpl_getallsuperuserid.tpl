<procedure>
	<sql>
		select 	distinct	    
			userid,
			username,
			bankid
		from 
			cmuser 
		where 
			bankid in (@list:bankid)
			and userstate='0'
		<!--	#AND = @userloginstate
			and userid != @operid --><!--liuxj-->
			and (postidset like @'%,{postidset},%'
			or postidset like @'{postidset},%'
			or postidset like @'%,{postidset}' 
			or postidset = @postidset)
	</sql>
</procedure>