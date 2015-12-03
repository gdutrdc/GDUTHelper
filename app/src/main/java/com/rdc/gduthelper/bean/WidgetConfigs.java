package com.rdc.gduthelper.bean;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by seasonyuu on 15/12/3.
 */
public class WidgetConfigs implements Serializable {
	private HashMap<Integer, String> widgetConfigs;

	public WidgetConfigs() {
	}

	public HashMap<Integer, String> getWidgetConfigs() {
		return widgetConfigs;
	}

	public void setWidgetConfigs(HashMap<Integer, String> widgetConfigs) {
		this.widgetConfigs = widgetConfigs;
	}

	public void putConfig(int widgetId, String selection) {
		if (widgetConfigs != null)
			widgetConfigs.put(widgetId, selection);
	}

	public String getConfig(int widgetId) {
		if (widgetConfigs != null)
			return widgetConfigs.get(widgetId);
		return null;
	}

}