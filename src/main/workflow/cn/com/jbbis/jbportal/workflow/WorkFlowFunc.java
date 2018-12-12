package cn.com.jbbis.jbportal.workflow;

import java.util.Random;
import cn.com.jbbis.util.UnikMap;

/**
 * <p>Title: 工作流公共函数库</p>
 * <p>Copyright: Copyright (c) 2008 </p>
 * <p>Company: 北京北大青鸟商用信息系统有限公司 </p>
 * @author kangsj@jbbis.com.cn
 * @version 1.0.0 2008-5-31 下午05:29:53
 */
public class WorkFlowFunc {
	/**
	 * 对int数组降序排列
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
	 * 对int数组升序排列
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
	 * 得到一组大于零的不重复随机数,参数位数digit必须小于最大值max
	 * @param digit 需要生成的随机数个数
	 * @param max 最大值
	 * @return int[] 数组
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
	 * 舍掉小数取整数,如果取出来值为0,则返回指定的值即arg1
	 * @param arg0 需要计算的值
	 * @param arg1 默认返回值
	 * @return int 返回取整的值
	 * @throws java.lang.Exception
	 */
	public static int floor(double arg0,int arg1) throws Exception{
		int n = 0;
		n = (int) Math.floor(arg0);
		return n == 0 ? arg1 : n;
	}
	
	/**
	 * 返回最小的（最接近负无穷大）double 值,
	 * 该值大于或等于参数，并且等于某个整数
	 * @param arg0
	 * @throws java.lang.Exception
	 */
	public static int ceil(double arg0) throws Exception{
		return (int)Math.ceil(arg0);
	}
	
	/**
	 * 检查字段的值,如果缺少字段就返回true
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
