package com.seasonyuu.getgrade.utils;

/**
 * Created by seasonyuu on 15/9/11.
 */
public class CheckPswUtils {
	public static final int LEVEL_SIMPLE = 0;
	public static final int LEVEL_MIDDLE = 1;
	public static final int LEVEL_SAFE = 2;

	private static final String PSW_LENGTH_NOT_MATCH = "密码长度不能小于 6 位！";
	private static final String PSW_NOT_SAFE = "密码应至少包含数字、字母、特殊符号中的两类！您的新密码为弱密码，请重新输入！";

	public static String getPswSafeRate(String password) {
		switch (getPswSafeLevel(password)) {
			case LEVEL_SIMPLE:
				return "当前密码等级为 " + "简单";
			case LEVEL_MIDDLE:
				return "当前密码等级为 " + "中";
			case LEVEL_SAFE:
				return "当前密码等级为 " + "困难";
		}
		return null;
	}

	private static int getPswSafeLevel(String password) {
		boolean[] flags  =new boolean[4];
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (c >= '0' && c <= '9') //数字
				flags[0] = true;
			else if (c >= 'A' && c <= 'Z') //大写字母
				flags[1] = true;
			else if (c >= 'a' && c <= 'z') //小写
				flags[2] = true;
			else
				flags[3] = true; //特殊字符
		}
		int mode = 0;
		for(boolean flag:flags){
			if(flag)
				mode ++;
		}
		if (mode <= 1)
			return LEVEL_SIMPLE;
		else if (mode <= 3)
			return LEVEL_MIDDLE;
		else
			return LEVEL_SAFE;
	}

	public static String getPswError(String password) {
		if (password.length() < 6)
			return PSW_LENGTH_NOT_MATCH;
		if (LEVEL_SIMPLE == getPswSafeLevel(password)) {
			return PSW_NOT_SAFE;
		}
		return null;
	}
}
