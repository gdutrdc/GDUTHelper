package com.rdc.gduthelper.bean;

import java.util.HashMap;

/**
 * Created by seasonyuu on 15/12/3.
 */
public class WidgetConfigs extends HashMap<Integer, String> {

	public void putConfig(int widgetId, String selection) {
		put(widgetId, selection);
	}

	public String getConfig(int widgetId) {
		return get(widgetId);
	}

	@Override
	public String toString() {
		String s = "";
		Object[] keys = keySet().toArray();
		Object[] values = values().toArray();
		for (int i = 0; i < keys.length; i++) {
			s += keys[i].toString() + "." + values[i].toString();
			if (i < keys.length - 1)
				s += ",";
		}
		return s;
	}
}