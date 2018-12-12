<procedure>
	<sql>
		delete from
			dwtaskvars
		where 
			wfid = @wfid
			#if @taskser
				and taskser = @number:taskser
			#end
			#if @varname
				and varname in (@list:varname)
			#end
	</sql>
</procedure>