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
 * ʵ�ִ˽ӿڵĶ�����һ�� Map �����ṩ�ߣ������߱���������������
 *	get �� containsKey���ֱ��ǰ�����ȡֵ����Դ����ԡ�
 *
 * ����ӿ��Ǵ� Map �ӿ��а�������ģ��������������� Map �еĶ�Ӧ�������ݡ�
 */
public interface MapProvider
{
    public Object get(Object key);

    public boolean containsKey(Object key);
}
