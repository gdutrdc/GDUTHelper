package com.rdc.gduthelper.bean;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by seasonyuu on 15/12/3.
 */
public class WidgetConfigs implements Serializable {
	private HashMap<Integer, String> widgetConfigs;

	public WidgetConfigs() {
		widgetConfigs = new HashMap<>();
	}

	public HashMap<Integer, String> getWidgetConfigs() {
		return widgetConfigs;
	}

	public void setWidgetConfigs(HashMap<Integer, String> widgetConfigs) {
		this.widgetConfigs = widgetConfigs;
	}

	public void putConfig(int widgetId, String selection) {
		if (widgetConfigs == null)
			widgetConfigs = new HashMap<>();
		widgetConfigs.put(widgetId, selection);
	}

	public String getConfig(int widgetId) {
		if (widgetConfigs != null)
			return widgetConfigs.get(widgetId);
		return null;
	}

	@Override
	public String toString() {
		String s = "";
		if (widgetConfigs == null)
			s = "null";
		else {
			Object[] keys = widgetConfigs.keySet().toArray();
			Object[] values = widgetConfigs.values().toArray();
			for (int i = 0; i < keys.length; i++) {
				s += keys[i].toString() + "." + values[i].toString();
				if (i < keys.length - 1)
					s += ",";
			}
		}
		return s;
	}
}