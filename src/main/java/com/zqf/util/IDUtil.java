package com.zqf.util;

import java.util.Random;
import java.util.UUID;

public class IDUtil {
	
	private static String randomStub = "00";
	private static int lastInt = 0;
	private static long lastTimeMills = 0l;

	public static void main(String[] args) {
		initWithStationNo(1);
		System.out.println(createFixedSeqId(15));
	}

	public static String getId() {
		initWithStationNo(1);
		return createFixedSeqId(15);
	}

	/**
	 * 
	 * @Title initWithStationNo
	 * @Description 分布环境下，传入ID生成服务器的台号，防止序列号重复
	 * @param @param stationNo 最大支持99台服务器
	 * @return void
	 * @throws
	 * @author macsunny
	 * @date 2016年2月3日 下午3:01:59
	 */
	public static void initWithStationNo(int stationNo) {
		randomStub = String.format("%02d", stationNo % 100);
		lastTimeMills = System.currentTimeMillis();
	}

	private synchronized static int nextInt(long currentTimeMills) {
		if (lastTimeMills != currentTimeMills) {
			lastInt = 0;
		} else
			lastInt = ++lastInt % 10000;
		return lastInt;
	}

	/**
	 * 
	 * @Title createFixedSeqId
	 * @Description 根据27位以上指定长度生成纯数字的随机数（时间递增相关)
	 * @param @param length
	 * @param @return
	 * @return String
	 * @throws
	 * @author macsunny
	 * @date 2016年2月19日 下午6:06:32
	 */
	public static String createFixedSeqId(int length) {
		if (length < 15)
			return "-1";
		StringBuilder sb = new StringBuilder();
		long currentTimeMills = System.currentTimeMillis();
		sb.append(currentTimeMills);
		sb.append(randomStub);
		int currentInt = nextInt(currentTimeMills);
		int restLen = length - sb.length() - 3;
		if (restLen > 0) {
			String format = "%0" + restLen + "d";
			int bound = (int) Math.pow(10, restLen);
			sb.append(String.format(format, currentInt % bound));
		}
		sb.append(String.format("%02d", new Random().nextInt(100)));
		return sb.toString();
	}
	/**
	 * length位随机字母+数字
	 */
	public static String createId(int length) {
		StringBuffer id = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				id.append((char) (random.nextInt(26) + temp));
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				id.append(random.nextInt(10));
			}
		}
		return id.toString();
	}

	/**
	 * length位数字
	 */
	public static String createInviteCode(int length) {
		int offset = 4;
		if (length < offset) {
			offset = length;
		}
		if (offset < 0) {
			offset = 0;
		}
		StringBuffer inviteCode = new StringBuffer();
		Random random = new Random();
		String timeStr = String.valueOf(System.currentTimeMillis());
		int timeStrLen = timeStr.length();
		inviteCode.append(timeStr.substring(timeStrLen - offset, timeStrLen));
		for (int i = 0; i < length - offset; i++) {
			inviteCode.append(random.nextInt(10));
		}
		return inviteCode.toString();
	}

	/**
	 * 获取一个去掉-的UUID字符串
	 * 
	 * @return
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
	}

	final private static char[] chars = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();

	public static String generateVariableCode(int charCnt) {
		if (charCnt <= 0)
			return "";
		char[] ret = new char[charCnt];
		for (int i = 0; i < charCnt; i++) {
			int letterPos = (int) (Math.random() * 10000) % (chars.length);
			ret[i] = chars[letterPos];
		}
		String code = String.valueOf(ret);
		return code;
	}

	public static String generateInviteCode() {
		int charCnt = 5;
		char[] ret = new char[charCnt];
		for (int i = 0; i < charCnt; i++) {
			int letterPos = (int) (Math.random() * 10000) % (chars.length);
			ret[i] = chars[letterPos];
		}
		String code = String.valueOf(ret);
		return code;
	}

	public static String generateExchangeCode() {
		int charCnt = 12;
		char[] ret = new char[charCnt];
		for (int i = 0; i < charCnt; i++) {
			int letterPos = (int) (Math.random() * 10000) % (10);
			ret[i] = chars[letterPos];
		}
		String code = String.valueOf(ret);
		return code;
	}

	public static String generatePassword() {
		int charCnt = 6;
		char[] ret = new char[charCnt];
		for (int i = 0; i < charCnt; i++) {
			int letterPos = (int) (Math.random() * 10000) % (10);
			ret[i] = chars[letterPos];
		}
		String code = String.valueOf(ret);
		return code;
	}
}
