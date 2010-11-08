package org.openpoi.server.backend.file;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openpoi.server.api.annotations.OpenPoiPluginModule;
import org.openpoi.server.api.plugin.PluginModule;

import com.google.inject.AbstractModule;

@OpenPoiPluginModule
public class FileBasedPoiModule extends AbstractModule implements PluginModule {
	private static final Pattern LAYER_MAPPING_PATTERN = Pattern.compile("^layerFile-(.+)$");
	private Map<String, Object> initParameters;
	
	@Override
	protected void configure() {
		LayerFileMapping layerFileMapping = new LayerFileMapping();
		for (String paramName : initParameters.keySet()) {
			Matcher match = LAYER_MAPPING_PATTERN.matcher(paramName);
			if (match.matches()) {
				layerFileMapping.put(match.group(1), (String) initParameters.get(paramName));
			}
		}
		
		bind(FileBasedPoiManager.class);
		bind(PoiMemoryDatabase.class);
		bind(LayerFileMapping.class).toInstance(layerFileMapping);
	}

	@Override
	public void setInitParameters(Map<String, Object> initParameters) {
		this.initParameters = initParameters;
	}

}
