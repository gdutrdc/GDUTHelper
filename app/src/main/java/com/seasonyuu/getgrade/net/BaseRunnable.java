package com.seasonyuu.getgrade.net;

/**
 * Created by seasonyuu on 15/8/28.
 */
public abstract class BaseRunnable implements Runnable {
	protected GGCallback callback;
	public interface GGCallback{
		void onCall(Object obj);
	}
}
