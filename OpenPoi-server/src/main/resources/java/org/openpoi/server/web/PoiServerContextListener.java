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

package org.openpoi.server.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;


import org.hibernate.Session;
import org.openpoi.server.DefaultPoiManager;
import org.openpoi.server.MissingLayerException;
import org.openpoi.server.PoiManagerFactory;
import org.openpoi.server.SimpleJsonPoiSerializer;
import org.openpoi.server.SimpleMemoryCache;
import org.openpoi.server.api.Cache;
import org.openpoi.server.api.PoiManager;
import org.openpoi.server.api.PoiSerializer;
import org.openpoi.server.api.annotations.CacheMaxSize;
import org.openpoi.server.util.HibernateUtil;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.ServletScopes;

public class PoiServerContextListener extends GuiceServletContextListener {
	private static final Pattern LAYER_MAPPING_PATTERN = Pattern.compile("^layerPoiManager-(.+)$");
	
	private Class<PoiManager> poiManagerClass;
	private Class<PoiSerializer> poiSerializerClass;
	private Class<Cache> cacheClass;
	private int cacheMaxSize;
	public static Map<String, Class<PoiManager>> layerMapping;
	private static Injector injector;
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		initializeSettings(servletContextEvent.getServletContext());

		super.contextInitialized(servletContextEvent);
	}

	private void initializeSettings(ServletContext servletContext) {
		layerMapping = new HashMap<String, Class<PoiManager>>();
		
		for (Enumeration paramEnum = servletContext.getInitParameterNames(); paramEnum.hasMoreElements();) {
			String paramName = (String) paramEnum.nextElement();
			Matcher match = LAYER_MAPPING_PATTERN.matcher(paramName);
			if (match.matches()) {
				layerMapping.put(match.group(1), (Class<PoiManager>) tryGetClass(servletContext.getInitParameter(paramName), null));
			}
		}
		poiManagerClass = (Class<PoiManager>) tryGetClass(servletContext.getInitParameter("poiManager"), DefaultPoiManager.class);
		poiSerializerClass = (Class<PoiSerializer>) tryGetClass(servletContext.getInitParameter("poiSerializer"), SimpleJsonPoiSerializer.class);
		cacheClass = (Class<Cache>) tryGetClass(servletContext.getInitParameter("cache"), SimpleMemoryCache.class);
		
		try {
			cacheMaxSize = Integer.parseInt(servletContext.getInitParameter("cacheMaxSizeKb")) * 1024;
		} catch (Exception e) {
			cacheMaxSize = 64 * 1024;
		}
	}

	private Class<?> tryGetClass(String className, Class<?> defaultClass) {
		if (className != null) {
			try {
				return Class.forName(className);
			} catch (Exception e) {
				return defaultClass;
			}
		} else {
			return defaultClass;
		}
	}

	@Override
    protected Injector getInjector() {
        injector = Guice.createInjector(new PoiServerModule(), new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/Pois/*").with(GetPoisServlet.class);
            }
        });
        
        return injector;
    }

    private class PoiServerModule extends AbstractModule {
        @Override
        protected void configure() {
        	bind(PoiManager.class).to(poiManagerClass);
        	bind(Session.class).toProvider(new SessionProvider()).in(ServletScopes.REQUEST);
        	bind(PoiManagerFactory.class).to(DefaultPoiManagerFactory.class).in(Scopes.SINGLETON);        	
            bind(Cache.class).to(cacheClass).in(Scopes.SINGLETON);
            bind(PoiSerializer.class).to(poiSerializerClass);
            
            bindConstant().annotatedWith(CacheMaxSize.class).to(cacheMaxSize);
        }
    }
    
    private static class DefaultPoiManagerFactory implements PoiManagerFactory {
		@Override
		public PoiManager create(String layer) throws MissingLayerException {
			Class<PoiManager> poiManagerClass = layerMapping.get(layer);
			if (poiManagerClass != null) {
				return injector.getInstance(poiManagerClass);
			} else {
				throw new MissingLayerException("No PoiManager defined for layer \"" + layer + "\".");
			}
		}
    }
    
    private static class SessionProvider implements Provider<Session> {
		@Override
		public Session get() {
			return HibernateUtil.getSessionFactory().getCurrentSession();
		}
    }
}
