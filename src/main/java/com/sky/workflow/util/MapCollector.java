/*
 * MapCollector.java
 * modico.net (lihw@jbbis.com.cn), 2008-3-19
 */

package com.sky.workflow.util;

/**
 * ʵ�ִ˽ӿڵĶ������������� Map ���ݵ���������ֻ��һ������������
 *	put��
 *
 * ����ӿ��Ǵ� Map �ӿ��а�������ģ����ķ����� Map ��Ӧ�ķ������ݡ�
 */
public interface MapCollector
{
	public Object put(Object key, Object value);
}
