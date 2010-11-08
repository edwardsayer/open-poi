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
