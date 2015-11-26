package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.net.BaseRunnable;

/**
 * Created by seasonyuu on 15/11/26.
 */
public class GetExamTime extends BaseRunnable {
	private String year;
	private String term;
	public GetExamTime(String year, String term, GGCallback callback) {
		this.callback = callback;
		this.year = year;
		this.term = term;
	}

	@Override
	public void run() {

	}
}
