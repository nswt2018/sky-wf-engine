<procedure>
	<sql>
	  	select 
	 		* 
	 	from 
	 		cmFieldInfo
	 	where
	 		getsql is null
			or getsql = ''
	 	order by FieldTab
	</sql>
</procedure>