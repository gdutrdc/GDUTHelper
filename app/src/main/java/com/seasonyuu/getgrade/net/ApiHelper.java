package com.seasonyuu.getgrade.net;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class ApiHelper {
	private static final String URL_XYW = "http://jwgl.gdut.edu.cn/"; //校园网地址
	private static final String URL_DX = "http://jwgldx.gdut.edu.cn/"; //电信地址

	public static final String LOGIN = "default2.aspx";
	public static final String INTO_GRADE="";

	public static String getURl(boolean isXYW){
		if(isXYW)
			return URL_XYW;
		else
			return URL_DX;
	}



}
