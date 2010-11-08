/*
 * Copyright (C) 2010 Per Liedman (per@liedman.net)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Lesser GNU General Public License for more details.
 * You should have received a copy of the Lesser GNU General Public License
 * along with this program. If not, see < http://www.gnu.org/licenses/>.
 *
 */

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
