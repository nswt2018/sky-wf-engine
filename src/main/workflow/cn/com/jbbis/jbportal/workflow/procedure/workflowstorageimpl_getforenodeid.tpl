<procedure>
	<sql>
		select 
			* 
		from 
			dwtaskhis
		where 
			forenodeid in (
      			select 
             		forenodeid 
      			from 
             		dwtaskhis 
      			where 
      				nodeid &lt;&gt; forenodeid
      				#AND =@wfid
      				<!--and forenodeid=@nodeid-->)
			#AND =@wfid
			#AND =@nodeid
	</sql>
</procedure>