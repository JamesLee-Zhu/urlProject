package com.zqf.util;

import java.io.UnsupportedEncodingException;

//******取出数值前的字符串******
public class RMBUtil {

	public float parserStatement(String statement) throws Exception {
		float result = 0;
		try {
			byte[] bytes = null;
			bytes = statement.getBytes("gb2312");
			for (int i = bytes.length - 1; i >= 0; i--) {
				String unit = queryUnit(bytes[i - 1], bytes[i]);
				// 无关键字结尾
				if (unit.equals("")) {
					if (bytes[i] > 0) {
						// 以阿拉伯数字结尾
						float num = numberYuan1(bytes, i + 1);
						result = num;
					} else {
						int num2 = queryNumber(bytes[i - 1], bytes[i]);
						if (num2 == -1) {
							i--;
							continue;
						}
						if (i < 3) {
							result = num2;
						} else {
							unit = queryUnit(bytes[i - 3], bytes[i - 2]);
							if (unit.equals("角")) {
								// 关键字"角"后跟汉字数字
								int num1 = queryNumber(bytes[i - 5], bytes[i - 4]);
								float num = numberYuan1(bytes, i - 6) + (float) (num1 * 0.1) + (float) (num2 * 0.01);
								result = num;
							} else if (unit.equals("元")) {
								// 关键字"元"后跟汉字数字
								float num = numberYuan1(bytes, i - 2) + (float) (num2 * 0.1);
								result = num;
							} else {
								// "零"后跟汉字数字
								float num = numberYuan1(bytes, i + 2);
								result = num;
							}
						}

					}
					break;
				} else if (unit.equals("分")) {
					// 关键字"分"结尾 关键字"分"前一般为一位汉字数字
					int num2 = queryNumber(bytes[i - 3], bytes[i - 2]);
					// 往前推 取"角""元"关键字前数字
					unit = queryUnit(bytes[i - 5], bytes[i - 4]);
					if (unit.equals("角")) {
						int num1 = queryNumber(bytes[i - 7], bytes[i - 6]);
						float num = numberYuan1(bytes, i - 8) + (float) (num1 * 0.1) + (float) (num2 * 0.01);
						result = num;
					} else {
						float num = numberYuan1(bytes, i - 6) + (float) (num2 * 0.01);
						result = num;
					}
					break;
				} else if (unit.equals("角")) {
					// 关键字"角"结尾 关键字"角"前为一位汉字数字
					int num1 = queryNumber(bytes[i - 3], bytes[i - 2]);
					// 取完"角"关键字前数字 往前推取"元"关键字前数字
					float num = numberYuan1(bytes, i - 4) + (float) (num1 * 0.1);
					result = num;
					break;
				} else if (unit.equals("元")) {
					// 关键字"元"结尾
					unit = queryUnit(bytes[i - 3], bytes[i - 2]);
					if (unit.equals("万")) {
						float num = numberYuan1(bytes, i - 2) * 10000;
						if (num < 0)
							num = numberYuan1(bytes, i - 2);
						result = num;
						break;
					} else {
						float num = numberYuan1(bytes, i);
						result = num;
						break;
					}
				}
				if (unit.equals("万")) {
					float num = numberYuan1(bytes, i) * 10000;
					if (num < 0)
						num = numberYuan1(bytes, i - 2);
					result = num;
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	// i值为汉字"元"的第二个字节的下标值
	// 关键字"元"前面的字符串有两种情况：1.一位汉字数字
	// 2.多位阿拉伯数字
	public float numberYuan(byte[] bytes, int i) {
		float num = 0;
		int index = 0;
		int flag = 0;
		if (bytes[i - 2] < 0) {
			// 为一位汉字数字情况： 从汉字数字转为阿拉伯数字
			num = queryNumber(bytes[i - 3], bytes[i - 2]);
			index = i - 3;
		} else {
			// 为多位阿拉伯数字情况： 从i-2开始向前推到出现汉字为止 中间截出阿拉伯数字字符串
			for (int j = i - 2; j >= 0; j--) {
				int number = queryLoopNumber(bytes[j]);
				if (number == -1) {
					byte[] byteNum = new byte[100];
					System.arraycopy(bytes, j + 1, byteNum, 0, i - 2 - j);
					String numStr = new String(byteNum);
					numStr = String.copyValueOf(numStr.toCharArray(), 0, bytes.length);
					num = Float.valueOf(numStr);
					index = j + 1;
					flag = 0;
					break;
				}
				if (j == 0) {
					byte[] byteNum = new byte[100];
					System.arraycopy(bytes, 0, byteNum, 0, i - 1);
					String numStr = new String(byteNum);
					numStr = String.copyValueOf(numStr.toCharArray(), 0, bytes.length);
					num = Float.valueOf(numStr);
					index = j;
					flag = 1;
					break;
				}
			}
		}
		// 截取数字前的字符串
		byte[] otherStrByte = new byte[index];
		System.arraycopy(bytes, 0, otherStrByte, 0, index);
		try {
			String otherString = new String(bytes, 0, index, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return num;
	}

	// i值为汉字"元"的第二个字节的下标值
	// 关键字"元"前面的字符串有两种情况：1.多位汉字数字
	// 2.多位阿拉伯数字
	public float numberYuan1(byte[] bytes, int i) {
		float num = 0;
		float num1 = 0;
		float num2 = 0;
		int index = 0;
		int flag = 0;
		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		if (i - 2 < 0) {
			num = queryLoopNumber(bytes[i - 1]);
		} else if ((i > bytes.length) || (bytes[i - 2] < 0 && bytes[i - 1] < 0)) {
			// 为一位汉字数字情况：
			// 从汉字数字转为阿拉伯数字
			for (int j = i - 2; j >= 0; j = j - 2) {
				byte[] bytes1 = new byte[2];
				bytes1[0] = bytes[j - 1];
				bytes1[1] = bytes[j];
				String numStr = "";
				try {
					numStr = new String(bytes1, 0, bytes1.length, "gb2312");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (numStr.equals("零")) {
					flag1 = 1;
					flag2 = 1;
					flag3 = 1;
					continue;
				}
				if (numStr.equals("十") || numStr.equals("拾")) {
					if ((j - 3) < 0) {
						num = 10;
						if (num1 != 0)
							num = num + num1;
						index = 0;
						break;
					}
					bytes1[0] = bytes[j - 3];
					bytes1[1] = bytes[j - 2];
					flag1 = 1;
					numStr = "";
					try {
						numStr = new String(bytes1, 0, bytes1.length, "gb2312");
						if (!numStr.equals("一") && !numStr.equals("二") && !numStr.equals("三") && !numStr.equals("四") && !numStr.equals("五") && !numStr.equals("六") && !numStr.equals("七")
								&& !numStr.equals("八") && !numStr.equals("九")) {
							num = 10 + num1;
							index = j - 1;
							continue;
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (numStr.equals("百") || numStr.equals("佰")) {
						if (flag1 == 0)
							num1 = num1 * 10;
						num = 10 + num1;
						num1 = num;
						index = j - 3;
					} else {
						num = queryNumber(bytes[j - 3], bytes[j - 2]) * 10 + num1;
						num1 = num;
						index = j - 3;
						j = j - 2;
					}
				} else if (numStr.equals("百") || numStr.equals("佰")) {
					if (flag1 == 0)
						num1 = num1 * 10;
					num = queryNumber(bytes[j - 3], bytes[j - 2]) * 100 + num1;
					num1 = num;
					index = j - 3;
					j = j - 2;
					flag2 = 1;
				} else if (numStr.equals("千") || numStr.equals("仟")) {
					if (flag2 == 0)
						num1 = num1 * 100;
					num = queryNumber(bytes[j - 3], bytes[j - 2]) * 1000 + num1;
					num1 = num;
					index = j - 3;
					j = j - 2;
					flag3 = 1;
				} else if (numStr.equals("万")) {
					for (int k = j; k >= 0; k = k - 2) {
						bytes1[0] = bytes[k - 1];
						bytes1[1] = bytes[k];
						numStr = "";
						try {
							numStr = new String(bytes1, 0, bytes1.length, "gb2312");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if (numStr.equals("零")) {
							flag1 = 1;
							flag2 = 1;
							flag3 = 1;
							continue;
						}
						if (numStr.equals("十") || numStr.equals("拾")) {
							bytes1[0] = bytes[k - 3];
							bytes1[1] = bytes[k - 2];
							numStr = "";
							try {
								numStr = new String(bytes1, 0, bytes1.length, "gb2312");
								if (!numStr.equals("一") && !numStr.equals("二") && !numStr.equals("三") && !numStr.equals("四") && !numStr.equals("五") && !numStr.equals("六") && !numStr.equals("七")
										&& !numStr.equals("八") && !numStr.equals("九")) {
									num = 10 + num1;
									index = j - 1;
									continue;
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							if (numStr.equals("百") || numStr.equals("佰")) {
								num = 100000 + num1;
								num1 = num;
								index = k - 3;
								// j = j - 2;
							} else {
								num = queryNumber(bytes[k - 3], bytes[k - 2]) * 10 * 10000 + num1;
								num1 = num;
								index = k - 3;
								k = k - 2;
								j = k;
							}
						} else if (numStr.equals("百") || numStr.equals("佰")) {
							num = queryNumber(bytes[k - 3], bytes[k - 2]) * 100 * 10000 + num1;
							num1 = num;
							index = k - 3;
							k = k - 2;
							j = k;
						} else if (numStr.equals("千") || numStr.equals("仟")) {
							num = queryNumber(bytes[k - 3], bytes[k - 2]) * 1000 * 10000 + num1;
							num1 = num;
							index = k - 3;
							k = k - 2;
							j = k;
						} else {
							num2 = queryNumber(bytes[k - 3], bytes[k - 2]) * 10000;
							if (num2 < 0) {
								j = k;
								break;
							}
							if (flag3 == 0)
								num1 = num1 * 1000;
							num = num2 + num1;
							num1 = num2 + num1;
							index = k - 3;
							k = k - 2;
							j = k;
						}
					}
				} else {
					num1 = queryNumber(bytes[j - 1], bytes[j]);
					if (num1 < 0)
						break;
					num = num1;
					index = j - 1;
				}
			}
		} else {
			// 为多位阿拉伯数字情况： 从i-2开始向前推到出现汉字为止 中间截出阿拉伯数字字符串
			int k = 0;
			if (bytes[i - 1] > 0)
				i++;
			for (int j = i - 2; j >= 0; j--) {
				int number = queryLoopNumber(bytes[j]);
				k++;
				if (number == -1) {
					byte[] byteNum = new byte[100];
					System.arraycopy(bytes, j + 1, byteNum, 0, k - 1);
					String numStr = new String(byteNum);
					numStr = String.copyValueOf(numStr.toCharArray(), 0, bytes.length);
					num = Float.valueOf(numStr);
					index = j + 1;
					flag = 0;
					break;
				}
				// *********************
				if (j == 0) {
					byte[] byteNum = new byte[100];
					System.arraycopy(bytes, 0, byteNum, 0, k);
					String numStr = new String(byteNum);
					numStr = String.copyValueOf(numStr.toCharArray(), 0, bytes.length);
					num = Float.valueOf(numStr);
					index = j;
					flag = 1;
					break;
				}
			}
		}
		// 截取数字前的字符串
		byte[] otherStrByte = new byte[index];
		System.arraycopy(bytes, 0, otherStrByte, 0, index);
		try {
			String otherString = new String(bytes, 0, index, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return num;
	}

	private String queryUnit(byte first, byte second) {
		String unit = "";
		if (first == -73 && second == -42) {
			unit = "分";
		} else if (first == -67 && second == -57) {
			unit = "角";
		} else if (first == -61 && second == -85) {
			unit = "角";
		} else if (first == -44 && second == -86) {
			unit = "元";
		} else if (first == -65 && second == -23) {
			unit = "元";
		} else if (first == -44 && second == -78) {
			unit = "元";
		} else if (first == -51 && second == -14) {
			unit = "万";
		}
		return unit;

	}

	private int queryNumber(byte first, byte second) {
		int number = 0;
		if (second < 0) {
			// 第一个字节小于0 为汉字数字
			number = transNumber(first, second);
		} else {
			// 为阿拉伯数字
			number = second - 48;
		}
		return number;
	}

	private int queryLoopNumber(byte first) {
		int number = 0;
		if (first < 0) {
			return -1;
		} else {
			// 直接取数值
			number = first - 48;
			//
		}
		return number;
	}

	// 将汉字数字转为阿拉伯数字
	private int transNumber(byte first, byte second) {
		int number = 0;
		byte[] bytes = new byte[2];
		bytes[0] = first;
		bytes[1] = second;
		String numStr = "";
		try {
			numStr = new String(bytes, 0, bytes.length, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (numStr.equals("零")) {
			number = 0;
		} else if (numStr.equals("一") || numStr.equals("壹")) {
			number = 1;
		} else if (numStr.equals("二") || numStr.equals("贰") || numStr.equals("两")) {
			number = 2;
		} else if (numStr.equals("三") || numStr.equals("叁")) {
			number = 3;
		} else if (numStr.equals("四") || numStr.equals("肆")) {
			number = 4;
		} else if (numStr.equals("五") || numStr.equals("伍")) {
			number = 5;
		} else if (numStr.equals("六") || numStr.equals("陆")) {
			number = 6;
		} else if (numStr.equals("七") || numStr.equals("柒")) {
			number = 7;
		} else if (numStr.equals("八") || numStr.equals("捌")) {
			number = 8;
		} else if (numStr.equals("九") || numStr.equals("玖")) {
			number = 9;
		} else if (numStr.equals("十") || numStr.equals("拾")) {
			number = 10;
		} else if (numStr.equals("百") || numStr.equals("佰")) {
			number = 100;
		} else if (numStr.equals("千") || numStr.equals("仟")) {
			number = 1000;
		} else {
			number = -1;
		}
		return number;
	}

}