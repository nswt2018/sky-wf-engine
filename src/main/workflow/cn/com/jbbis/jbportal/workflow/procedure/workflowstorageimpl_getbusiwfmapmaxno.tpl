<procedure>
	<sql>
		select max(transeq) as transeq
		from dbBusiWFMap where loanid=@loanid		 
	</sql>
</procedure>