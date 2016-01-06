package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.net.BaseRunnable;

/**
 * Created by seasonyuu on 16/1/4.
 */
public class GetSchedule extends BaseRunnable {
	private String year;
	private String term;

	public GetSchedule(String year, String term, GGCallback callback) {
		this.callback = callback;
		this.year = year;
		this.term = term;
	}

	@Override
	public void run() {

	}
}
