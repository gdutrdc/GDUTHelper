package com.rdc.gduthelper.net;

/**
 * Created by seasonyuu on 15/8/28.
 */
public abstract class BaseRunnable implements Runnable {
	protected GGCallback callback;
	protected String requestUrl;

	public interface GGCallback{
		void onCall(Object obj);
	}
}
