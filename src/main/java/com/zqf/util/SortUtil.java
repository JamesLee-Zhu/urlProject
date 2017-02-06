package com.zqf.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;

public class SortUtil {

	/**
	 * 对list进行排序
	 * 
	 * @param sortList
	 *            需要排序的list
	 * @param param1
	 *            排序的参数名称
	 * @param orderType
	 *            排序类型：正序-asc；倒序-desc
	 */
	@SuppressWarnings("unchecked")
	public static List<?> sort(List<?> sortList, String param1, String orderType) {
		Comparator<?> mycmp1 = ComparableComparator.getInstance();
		if ("desc".equals(orderType)) {
			mycmp1 = ComparatorUtils.reversedComparator(mycmp1); // 逆序（默认为正序）
		}
		ArrayList<Object> sortFields = new ArrayList<Object>();
		sortFields.add(new BeanComparator(param1, mycmp1)); // 主排序（第一排序）
		ComparatorChain multiSort = new ComparatorChain(sortFields);
		Collections.sort(sortList, multiSort);
		return sortList;
	}

	/**
	 * 对list进行排序
	 * 
	 * @param sortList
	 *            需要排序的list
	 * @param param1
	 *            排序的参数名称:参数长度
	 * @param param2
	 *            排序的参数名称:排序参数
	 * @param orderType
	 *            排序类型：正序-asc；倒序-desc
	 */
	@SuppressWarnings("unchecked")
	public static List<?> sortParam2(List<?> sortList, String param1, String param2, String orderType) {
		Comparator<?> mycmp1 = ComparableComparator.getInstance();
		Comparator<?> mycmp2 = ComparableComparator.getInstance();
		if ("desc".equals(orderType)) {
			mycmp1 = ComparatorUtils.reversedComparator(mycmp1); // 逆序（默认为正序）
		}
		ArrayList<Object> sortFields = new ArrayList<Object>();
		sortFields.add(new BeanComparator(param1, mycmp1)); // 主排序（第一排序）
		sortFields.add(new BeanComparator(param2, mycmp2)); // 主排序（第一排序）
		ComparatorChain multiSort = new ComparatorChain(sortFields);
		Collections.sort(sortList, multiSort);
		return sortList;
	}

}
