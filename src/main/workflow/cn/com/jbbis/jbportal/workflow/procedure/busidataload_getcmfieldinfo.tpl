<procedure>
	<sql>
		  select 
		 		* 
		 	from 
		 		cmFieldInfo
		  where 
		    getsql is not null
			 	or getsql != ''
		 	order by FieldTab
	</sql>
</procedure>