<procedure>
	<sql>
		insert into 
			dwtaskhis (
			wfid,
			taskser,
			bankid,
			operid,
			recetime,
			dealtime,
			dealsystime,
			nodeid,
			nodename,
			exectrancode,
			submtrancode,
			looktrancode,
			taskdesc,
			forenodeid,
			tasktype,
			nodephase,
			isAllowGet,
			isAllowReturn,
			taskRound,
			isPrimaryAuditNode,
			busiOperateStyle,
			lastchgdate,
			lastchgtime
			)
		select 
			wfid,
			taskser,
			bankid,
			operid,
			#if @recetime
			@recetime recetime,
			#else
			recetime,
			#end
			@dealtime as dealtime,
			@dealsystime as dealsystime,
			nodeid,
			nodename,
			exectrancode,
			submtrancode,
			looktrancode,
			@taskdesc as taskdesc,
			forenodeid,
			tasktype,
			nodephase,
			isAllowGet,
			isAllowReturn,
			taskRound,
			isPrimaryAuditNode,
			busiOperateStyle,
			@date:lastchgdate,
			@lastchgtime
		from 
			dwtask 
		where 
			wfid = @wfid
			#AND = @number:taskser
	</sql>
</procedure>