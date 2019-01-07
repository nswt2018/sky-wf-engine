package com.sky.workflow.util;

public final class NamedException extends RuntimeException
{
	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;

	public NamedException(String name)
	{
		this(name, null);
	}

	public NamedException(String name, Object data)
	{
		super(name);
	}

	public String getName()
	{
		return super.getMessage();
	}

	public String getMessage()
	{
		return msg != null ? msg : super.getMessage();
	}

	/**
	 * 标准格式为 "类名 + [: 详细消息]", 这里重定义为 "异常ID + [: 详细信息]".
	 */
	public String toString()
	{
		String s = getName();
		if (msg == null) {
			return s;
		}

		return s + ": " + msg;
	}
}
