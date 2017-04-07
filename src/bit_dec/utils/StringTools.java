package bit_dec.utils;

import java.util.HashSet;
import java.util.Iterator;

public class StringTools {
	
	/**
	 * �ж�һ���ַ���tmp�ǲ��ǰ����ַ���������
	 * @param strs
	 * @param tmp
	 * @return
	 */
	public static boolean isInStrings(String[] strs,String tmp){
		if (strs != null && tmp != null) {
			for(String str:strs){
				if (tmp.startsWith(str)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * �ж���������hashset���Ԫ���Ƿ�ȫ����ͬ
	 * @param v
	 * @param w
	 * @return
	 */
	public static boolean isTequal(HashSet<String> v,HashSet<String> w){
		if (v.size() != w.size()) {
			return false;
		}
		Iterator<String> it = v.iterator();
		while (it.hasNext()) {
			String obj = it.next();
		    if (!w.contains(obj))
		    	return false;
		}
		Iterator<String> it2 = w.iterator();
		while (it2.hasNext()) {
			String obj = it2.next();
		    if (!v.contains(obj))
		    	return false;
		}
		return true;
	}
}
