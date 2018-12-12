<procedure>
	<sql>		
		select 
			flowid, 
			nodeid,
			opincode,
			opindesc
		from 
			dwOpinionDefine 
		where 
			flowid = @flowid
			
	</sql>
</procedure>