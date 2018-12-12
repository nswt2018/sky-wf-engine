<procedure>
	<sql>
		select 
			*
		from 
			dwtask
		where
		 	wfid = @wfid
		 	#AND =@bankid
		 	#AND =@operid
	</sql>
</procedure>
