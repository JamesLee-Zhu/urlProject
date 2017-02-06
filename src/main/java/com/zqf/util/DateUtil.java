package com.zqf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public final static String DATE_FORMAT = "yyyy-MM-dd";
	public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 时间轴格式化
	 * 
	 * @param d
	 * @param pattern
	 * @return
	 */
	public static String formatTimeline(Date d, String pattern) {
		if (d == null)
			return "";
		Date toDay = new Date();
		long[] differTime = differDate(d, toDay);
		long day = differTime[0];
		long hour = differTime[1];
		long min = differTime[2];
		long sec = differTime[3];
		String s = "";
		if (day == 0) {
			if (hour == 0) {
				if (min == 0) {
					if (sec <= 5) {
						s = "刚刚";
					} else {
						s = sec + "秒前";
					}
				} else {
					s = min + "分钟前";
				}
			} else {
				s = hour + "小时前";
			}
		} else if (day == 1) {
			s = "昨天";
		} else if (day == 2) {
			s = "前天";
		} else if (day < 7) {
			s = day + "天前";
		} else {
			s = getFormatDate(d, DATE_FORMAT);
		}
		return s;
	}

	public static String getCHNWeekOfDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date date = new Date();
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	public static String getCHNWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/**
	 * * 获取指定日期是星期几
	 * 
	 * 参数为null时表示获取当前日期是星期几
	 * 
	 * @param date
	 * 
	 * @return
	 */

	public static String getNumWeekOfDate(Date date) {
		String[] weekOfDays = { "7", "1", "2", "3", "4", "5", "6" };
		Calendar calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
		}
		int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		return weekOfDays[w];
	}

	/**
	 * 根据格式得到格式化后的日期
	 * 
	 * @param currDate
	 *            要格式化的日期
	 * @param format
	 *            日期格式，如yyyy-MM-dd
	 * @see java.text.SimpleDateFormat#format(java.util.Date)
	 * @return String 返回格式化后的日期，格式由参数<code>format</code>
	 *         定义，如yyyy-MM-dd，如2006-02-15
	 */
	public static String getFormatDate(Date currDate, String format) {
		SimpleDateFormat dtFormatdB = null;
		try {
			dtFormatdB = new SimpleDateFormat(format);
			return dtFormatdB.format(currDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据格式得到格式化后的日期
	 * 
	 * @param currDate
	 *            要格式化的日期
	 * @param format
	 *            日期格式，如yyyy-MM-dd
	 * @see java.text.SimpleDateFormat#parse(java.lang.String)
	 * @return Date 返回格式化后的日期，格式由参数<code>format</code>定义，如yyyy-MM-dd，如2006-02-15
	 */
	public static Date getFormatDate(String currDate, String format) {
		SimpleDateFormat dtFormatdB = null;
		try {
			dtFormatdB = new SimpleDateFormat(format);
			return dtFormatdB.parse(currDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到日期的前或者后几天
	 * 
	 * @param iDate
	 *            如果要获得前几天日期，该参数为负数； 如果要获得后几天日期，该参数为正数
	 * @see java.util.Calendar#add(int, int)
	 * @return Date 返回参数<code>curDate</code>定义日期的前或者后几天
	 */
	public static Date getDateBeforeOrAfter(Date curDate, int iDate) {
		Calendar cal = Calendar.getInstance();
		if (curDate != null) {
			cal.setTime(curDate);
		}
		cal.add(Calendar.DAY_OF_MONTH, iDate);
		return cal.getTime();
	}

	/**
	 * 得到日期的前或者后几小时
	 * 
	 * @param iHour
	 *            如果要获得前几小时日期，该参数为负数； 如果要获得后几小时日期，该参数为正数
	 * @see java.util.Calendar#add(int, int)
	 * @return Date 返回参数<code>curDate</code>定义日期的前或者后几小时
	 */
	public static Date getDateBeforeOrAfterHours(Date curDate, int iHour) {
		Calendar cal = Calendar.getInstance();
		if (curDate != null) {
			cal.setTime(curDate);
		}
		cal.add(Calendar.HOUR_OF_DAY, iHour);
		return cal.getTime();
	}

	/**
	 * 日期天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int daysBetween(Date startDate, Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(endDate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 返回指写日期的星期一和星期日所在的日期
	 * 
	 * @param d
	 * @return
	 */
	public static Date[] getMonAndSunDate(Date d) {
		Date mon = null;
		Date sun = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		// 获得当前日期是一个星期的第几天
		int day = cal.get(Calendar.DAY_OF_WEEK);
		// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
		mon = cal.getTime();
		cal.add(Calendar.DATE, 6);
		sun = cal.getTime();
		return new Date[] { mon, sun };
	}

	/**
	 * 日期相差天时分秒
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long[] differDate(Date startDate, Date endDate) {
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long diff = Math.abs(endDate.getTime() - startDate.getTime());
		day = diff / (24 * 60 * 60 * 1000);
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		return new long[] { day, hour, min, sec };
	}

	/**
	 * 日期比较
	 * 
	 * @param DATE1
	 * @param DATE2
	 * @return
	 */
	public static boolean compare_date(Date DATE1, Date DATE2) {
		try {
			if (DATE1.getTime() > DATE2.getTime()) {
				return true;
			} else if (DATE1.getTime() < DATE2.getTime()) {
				return false;
			} else {
				return false;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	/**
	 * 获得指定日期的前一天
	 * 
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */
	public static Date getPrevDay(Date specifiedDay) {// 可以用new
		Calendar c = Calendar.getInstance();
		Date dayBefore = null;
		try {
			c.setTime(specifiedDay);
			int day = c.get(Calendar.DATE);
			c.set(Calendar.DATE, day - 1);
			dayBefore = c.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayBefore;
	}

	/**
	 * 获得指定日期的后一天
	 * 
	 * @param specifiedDay
	 * @return
	 */
	public static Date getNextDay(Date specifiedDay) {
		Calendar c = Calendar.getInstance();
		Date dayAfter = null;
		try {
			c.setTime(specifiedDay);
			int day = c.get(Calendar.DATE);
			c.set(Calendar.DATE, day + 1);
			dayAfter = c.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayAfter;
	}

	/**
	 * 凌晨
	 * 
	 * @param date
	 * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
	 *       1 返回yyyy-MM-dd 23:59:59日期
	 * @return
	 */
	public static Date getMinOrMaxDate(Date date, boolean isMinTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if (isMinTime) {
			return cal.getTime();
		} else {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MILLISECOND, -1);
		}
		return cal.getTime();
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年11月30日上午10:30:06
	 * @description 某年第一天
	 * @param TODO
	 * @return TODO
	 */
	public static Date yearOfFirstDay(Integer year) {
		Calendar calendar = Calendar.getInstance();
		int now = calendar.get(Calendar.YEAR);
		if (year == null || year.intValue() < 1900) {
			year = now;
		}
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		return calendar.getTime();
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年11月30日上午10:31:34
	 * @description 某年最后一天
	 * @param TODO
	 * @return TODO
	 */
	public static Date yearOfLastDay(Integer year) {
		Calendar calendar = Calendar.getInstance();
		int now = calendar.get(Calendar.YEAR);
		if (year == null || year.intValue() < 1900) {
			year = now;
		}
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();
		String formatDate = getFormatDate(currYearLast, "yyyy-MM-dd");
		return getFormatDate(formatDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年9月20日上午11:38:17
	 * @description 本月第一天凌晨0点0分0秒
	 */
	public static Date monthOfFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		// 将小时至0
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		calendar.set(Calendar.MINUTE, 0);
		// 将秒至0
		calendar.set(Calendar.SECOND, 0);
		// 将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		// 获得当前月第一天
		return calendar.getTime();
	}

	public static Date monthOfFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		// 将小时至0
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		calendar.set(Calendar.MINUTE, 0);
		// 将秒至0
		calendar.set(Calendar.SECOND, 0);
		// 将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		// 获得当前月第一天
		return calendar.getTime();
	}

	/**
	 * 得到某年某月的第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date monthOfFirstDay(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
		return cal.getTime();
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年9月20日上午11:38:59
	 * @description 本月最后一天23点59分59秒
	 */
	public static Date monthOfLastDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		// 将小时至0
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		calendar.set(Calendar.MINUTE, 0);
		// 将秒至0
		calendar.set(Calendar.SECOND, 0);
		// 将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		// 将当前月加1；
		calendar.add(Calendar.MONTH, 1);
		// 在当前月的下一月基础上减去1毫秒
		calendar.add(Calendar.MILLISECOND, -1);
		// 获得当前月最后一天
		return calendar.getTime();
	}

	public static Date monthOfLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		// 将小时至0
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		calendar.set(Calendar.MINUTE, 0);
		// 将秒至0
		calendar.set(Calendar.SECOND, 0);
		// 将毫秒至0
		calendar.set(Calendar.MILLISECOND, 0);
		// 将当前月加1；
		calendar.add(Calendar.MONTH, 1);
		// 在当前月的下一月基础上减去1毫秒
		calendar.add(Calendar.MILLISECOND, -1);
		// 获得当前月最后一天
		return calendar.getTime();
	}

	/**
	 * 得到某年某月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date monthOfLastDay(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return cal.getTime();
	}

	public static Date weekOfFirstDay(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		int d = 0;
		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			d = -6;
		} else {
			d = 2 - cal.get(Calendar.DAY_OF_WEEK);
		}
		cal.add(Calendar.DAY_OF_WEEK, d);
		// 将小时至0
		cal.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		cal.set(Calendar.MINUTE, 0);
		// 将秒至0
		cal.set(Calendar.SECOND, 0);
		// 将毫秒至0
		cal.set(Calendar.MILLISECOND, 0);
		// 所在周开始日期
		return cal.getTime();
	}

	public static Date weekOfLastDay(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		int d = 0;
		if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
			d = -6;
		} else {
			d = 2 - cal.get(Calendar.DAY_OF_WEEK);
		}
		cal.add(Calendar.DAY_OF_WEEK, d);
		cal.add(Calendar.DAY_OF_WEEK, 6);
		// 将小时至0
		cal.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		cal.set(Calendar.MINUTE, 0);
		// 将秒至0
		cal.set(Calendar.SECOND, 0);
		// 将毫秒至0
		cal.set(Calendar.MILLISECOND, 0);
		// 将当前月加1；
		cal.add(Calendar.DAY_OF_MONTH, 1);
		// 在当前月的下一月基础上减去1毫秒
		cal.add(Calendar.MILLISECOND, -1);
		return cal.getTime();
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年12月8日上午10:03:35
	 * @description 获取参数日期最小时间，参数为空则返回今天的最小时间
	 * @param TODO
	 * @return TODO
	 */
	public static Date getDateMintime(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * @author ZhuQiFeng
	 * @addDate 2016年12月8日上午10:03:53
	 * @description 获取参数日期最大时间，参数为空则返回今天的最大时间
	 * @param TODO
	 * @return TODO
	 */
	public static Date getDateMaxtime(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MILLISECOND, -1);
		return cal.getTime();
	}

}
