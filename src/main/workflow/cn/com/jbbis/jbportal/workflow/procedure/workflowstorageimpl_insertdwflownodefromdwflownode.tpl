<procedure>
	<sql>
		insert into dwflownode 
			(flowid, 
			nodeid, 
			nodename, 
			nodephase,
			nodedesc, 
			nodetype, 
			isunit, 
			unitflowid, 
			mindealnum, 
			autodisuserflag, 
			taskoverpolicy, 
			assignminnum, 
			overpercent, 
			selectoperflag, 
			exectrancode, 
			submtrancode, 
			looktrancode, 
			tranterm, 
			nextnodemode,
			nextnode, 
			processmode,
			hisflag,
			ISALLOWGET,
			ISALLOWRETURN,
			ISPRIMARYAUDITNODE,
			assignmindealnumstyle) 
		select 
			@keyno as flowid,
			nodeid, 
			nodename, 
			nodephase,
			nodedesc, 
			nodetype, 
			isunit, 
			unitflowid, 
			mindealnum, 
			autodisuserflag, 
			taskoverpolicy, 
			assignminnum, 
			overpercent, 
			selectoperflag, 
			exectrancode, 
			submtrancode, 
			looktrancode, 
			tranterm, 
			nextnodemode, 
			nextnode, 
			processmode,
			hisflag,
			ISALLOWGET,
			ISALLOWRETURN,
			ISPRIMARYAUDITNODE,
			nvl(assignmindealnumstyle,'2')
		from 
			dwflownode 
		where 
			flowid = @flowid
	</sql>
</procedure>