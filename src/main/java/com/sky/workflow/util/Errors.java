/*
 * Errors.java
 * modico.net (lihw@jbbis.com.cn), 2004-12-18, 2010-04-21
 *
 * Revisions:
 * 2008-12-11, modico:
 *		Enhance support for parameter filter of getStackString().
 * 2009-05-05, modico:
 *		RENAME: getInitCause => getInitialCause
 *		BUG FIX: getInitialCause() 没有考虑到同时有 getCause/get*Exception 的情况.
 * 2010-04-20, modico:
 *		Remove methods Assert;
 * 2010-04-21, modico:
 *		Rename method getInitialCause to rootOf;
 *		Rename method getStackString to filterStackTrace;
 *		Add specific support for SQLException in method filterStackTrace;
 */

package com.sky.workflow.util;

import java.lang.reflect.Method;

/**
 * 异常对象 Throwable 相关的辅助工具类. rootOf 方法用于获取给定异常的第一现场, 比 JDK 提供的方法更具兼容性;
 * filterStackTrace 方法用于过滤异常的调用栈;
 */
public class Errors {
	/**
	 * Get the initial cause of the given Throwable object. More compatible than
	 * Throwable.getCause() in JDK 1.4 .
	 */
	public static Throwable rootOf(Throwable e) {
		Throwable t1 = e, t2;
		for (; (t2 = getCause(t1)) != null; t1 = t2)
			;
		return t1;
	}

	/**
	 * Get the direct cause of the given Throwable object.
	 */
	private static Throwable getCause(Throwable e) {
		Throwable t;

		try {
			t = e.getCause();

			if (t != null) {
				return t;
			}

			String n = e.getClass().getName();
			if (n.startsWith("java.") || n.startsWith("javax.")) {
				return t;
			}
		} catch (Throwable letitbe) {
		}

		Method[] methods = e.getClass().getMethods();
		Class<?> rt = Throwable.class;

		for (int i = methods.length; --i >= 0;) {
			Method m = methods[i];
			if (rt.isAssignableFrom(m.getReturnType()) && m.getParameterTypes().length == 0) {
				String name = m.getName();
				if (name.startsWith("get")
						&& (name.endsWith("Cause") || name.endsWith("Exception") || name.endsWith("Throwable"))) {
					try {
						Object o = m.invoke(e);
						if (o != null && o != e) {
							return (Throwable) o;
						}
					} catch (Throwable letitbe) {
					}
				}
			}
		}

		return null;
	}

	public static void Assert(boolean exp, String errid) throws NamedException {
		if (!exp) throw new NamedException(errid);
	}
}
