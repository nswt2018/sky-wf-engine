package cn.com.jbbis.jbportal.workflow;

import java.util.Random;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>Title: ����������������</p>
 * <p>Copyright: Copyright (c) 2008 </p>
 * <p>Company: ������������������Ϣϵͳ���޹�˾ </p>
 * @author kangsj@jbbis.com.cn
 * @version 1.0.0 2008-5-31 ����05:29:53
 */
public class WorkFlowFunc {
	/**
	 * ��int���齵������
	 * @throws java.lang.Exception
	 */
	public static void sortASC(int[] values) {
		int temp;
		for (int i = 0; i < values.length; ++i) {
			for (int j = 0; j < values.length - i - 1; ++j) {
				if (values[j] > values[j + 1]) {
					temp = values[j];
					values[j] = values[j + 1];
					values[j + 1] = temp;
				}
			}
		}
	}

	/**
	 * ��int������������
	 * @throws java.lang.Exception
	 */
	public static void sortDESC(int[] values) {
		int temp;
		for (int i = 0; i < values.length; ++i) {
			for (int j = 0; j < values.length - i - 1; ++j) {
				if (values[j] < values[j + 1]) {
					temp = values[j];
					values[j] = values[j + 1];
					values[j + 1] = temp;
				}
			}
		}
	}
	
	/**
	 * �õ�һ�������Ĳ��ظ������,����λ��digit����С�����ֵmax
	 * @param digit ��Ҫ���ɵ����������
	 * @param max ���ֵ
	 * @return int[] ����
	 * @throws java.lang.Exception
	 */
	public static int[] getRandom(int digit, int max) throws Exception{
		int[] r = new int[digit];
		Random rand = new Random();
		boolean[] bool = new boolean[max];
		int num = 0;
		for (int i = 0; i < digit; i++) {
			do {
				num = rand.nextInt(max);
			} while (bool[num]);
			bool[num] = true;
			r[i] = num;
		}
		return r;
	}
	
	/**
	 * ���С��ȡ����,���ȡ����ֵΪ0,�򷵻�ָ����ֵ��arg1
	 * @param arg0 ��Ҫ�����ֵ
	 * @param arg1 Ĭ�Ϸ���ֵ
	 * @return int ����ȡ����ֵ
	 * @throws java.lang.Exception
	 */
	public static int floor(double arg0,int arg1) throws Exception{
		int n = 0;
		n = (int) Math.floor(arg0);
		return n == 0 ? arg1 : n;
	}
	
	/**
	 * ������С�ģ���ӽ��������double ֵ,
	 * ��ֵ���ڻ���ڲ��������ҵ���ĳ������
	 * @param arg0
	 * @throws java.lang.Exception
	 */
	public static int ceil(double arg0) throws Exception{
		return (int)Math.ceil(arg0);
	}
	
	/**
	 * ����ֶε�ֵ,���ȱ���ֶξͷ���true
	 * @throws Exception:java.lang.Exception
	 */
	public static boolean checkField(UnikMap m,String[] fields) throws Exception{
		boolean flag = false;
		for (int i = 0; i < fields.length; i++) {
			
			System.out.println(fields[i] + "==" + m.getString(fields[i]));
			flag = m.getString(fields[i]) == null ? true : false;
			if(flag)
				break;
		}
		return flag;
	}
}
