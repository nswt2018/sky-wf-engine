<procedure>
	<sql>
		select 		    
			userid,
			username,
			bankid
		from 
			cmuser 
		where 
			bankid in(@list:bankid)
			and userstate='0'
	
			and (postidset like @'%,{postidset},%'
			or postidset like @'{postidset},%'
			or postidset like @'%,{postidset}' 
			or postidset = @postidset)
			
	</sql>
</procedure>