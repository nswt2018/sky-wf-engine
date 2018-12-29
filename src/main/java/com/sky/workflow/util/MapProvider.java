/*
 * MapProvider.java
 * modico.net (lihw@jbbis.com.cn), 2006-3-15
 *
 * Revisions:
 * 2008-12-12, modico:
 *	1. Rename ProvideNamedItem to MapProvider;
 *	2. Rename getNamedItem() to get().
 */

package com.sky.workflow.util;

/**
 * 实现此接口的对象是一个 Map 内容提供者，即它具备两个基本方法：
 *	get 和 containsKey，分别是按名字取值或测试存在性。
 *
 * 这个接口是从 Map 接口中剥离出来的，它的两个方法与 Map 中的对应方法兼容。
 */
public interface MapProvider
{
    public Object get(Object key);

    public boolean containsKey(Object key);
}
