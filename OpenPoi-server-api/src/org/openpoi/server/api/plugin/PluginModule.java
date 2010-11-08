package org.openpoi.server.api.plugin;

import java.util.Map;

import com.google.inject.Module;

public interface PluginModule extends Module {
	void setInitParameters(Map<String, Object> initParameters);
}
