<procedure>
	<sql>
		select 
			max(taskRound) as taskRound
		from 
			dwTaskRound
		where wfid = @wfid				 
	</sql>
</procedure>