package org.openpoi.server.cache.memcached;

import java.util.Map;

import org.openpoi.server.api.annotations.OpenPoiPluginModule;
import org.openpoi.server.api.plugin.PluginModule;

import com.google.inject.AbstractModule;

@OpenPoiPluginModule
public class MemcachedCacheModule extends AbstractModule implements
		PluginModule {
	private Map<String, Object> initParameters;
	
	@Override
	protected void configure() {
		bindConstant().annotatedWith(MemcachedAddresses.class).to((String)initParameters.get("memcachedAddresses"));
		bindConstant().annotatedWith(QueryTimeout.class).to((String)initParameters.get("memcachedQueryTimeoutSeconds"));
		bindConstant().annotatedWith(CacheExpiration.class).to((String)initParameters.get("memcachedCacheExpirationSeconds"));
	}

	@Override
	public void setInitParameters(Map<String, Object> initParameters) {
		this.initParameters = initParameters;
	}
}
