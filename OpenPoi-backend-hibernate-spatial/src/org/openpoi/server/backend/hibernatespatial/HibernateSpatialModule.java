package org.openpoi.server.backend.hibernatespatial;

import java.util.Map;

import org.hibernate.Session;
import org.openpoi.server.api.annotations.OpenPoiPluginModule;
import org.openpoi.server.api.plugin.PluginModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

@OpenPoiPluginModule
public class HibernateSpatialModule extends AbstractModule implements
		PluginModule {

	@Override
	protected void configure() {
		bind(Session.class).toProvider(new SessionProvider());
	}

	@Override
	public void setInitParameters(Map<String, Object> initParameters) {
		// Do nothing
	}
    
    private static class SessionProvider implements Provider<Session> {
		@Override
		public Session get() {
			return HibernateUtil.getSessionFactory().getCurrentSession();
		}
    }
}
