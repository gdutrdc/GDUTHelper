package com.seasonyuu.getgrade.net;

import com.seasonyuu.getgrade.app.GGApplication;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class ApiHelper {
	private static final String URL_XYW = "http://jwgl.gdut.edu.cn/"; //校园网地址
	private static final String URL_DX = "http://jwgldx.gdut.edu.cn/"; //电信地址

	private static final String HOST_XYW = "jwgl.gdut.edu.cn/"; //校园网地址
	private static final String HOST_DX = "jwgldx.gdut.edu.cn/"; //电信地址

	public static final String LOGIN = "default2.aspx";
	public static final String INTO_GRADE="";

	public static final int LOGIN_SUCCESS = 0x1;
	public static final int LOGIN_FAILED = 0x2;
	public static final int GET_CHECK_CODE_SUCCESS = 0x3;
	public static final int GET_PAGE_RESULT = 0x4;
	public static final int GET_INFORMATION_SUCCESS = 0x5;
	public static final int GET_INFORMATION_AVATAR = 0x6;

	public static String getURl(){
		if(GGApplication.getInstance().isUseDx())
			return URL_DX;
		else
			return URL_XYW;
	}

	public static String getHost(){
		if(GGApplication.getInstance().isUseDx())
			return HOST_DX;
		else
			return HOST_XYW;
	}




}
