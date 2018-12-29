/*
 * MapCollector.java
 * modico.net (lihw@jbbis.com.cn), 2008-3-19
 */

package com.sky.workflow.util;

/**
 * 实现此接口的对象可以用作存放 Map 数据的容器。它只有一个基本方法：
 *	put。
 *
 * 这个接口是从 Map 接口中剥离出来的，它的方法与 Map 对应的方法兼容。
 */
public interface MapCollector
{
	public Object put(Object key, Object value);
}
