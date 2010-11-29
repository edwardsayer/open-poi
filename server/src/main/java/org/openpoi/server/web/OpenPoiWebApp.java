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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.openpoi.server.MissingLayerException;
import org.openpoi.server.SimpleJsonPoiSerializer;
import org.openpoi.server.SimpleMemoryCache;
import org.openpoi.server.api.Cache;
import org.openpoi.server.api.PoiManager;
import org.openpoi.server.api.PoiSerializer;
import org.openpoi.server.api.annotations.CacheMaxSize;
import org.openpoi.server.api.plugin.PluginModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public class OpenPoiWebApp extends GuiceServletContextListener {
	private static final Pattern LAYER_MAPPING_PATTERN = Pattern.compile("^layerPoiManager-(.+)$");
	
	private Map<String, Object> initParameters;
	private int cacheMaxSize;

	private Set<Class<PluginModule>> pluginModuleClasses;
	private static Map<String, Class<PoiManager>> layerMapping;
	private static Injector injector;
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		initParameters = new HashMap<String, Object>();
		ServletContext servletContext = servletContextEvent.getServletContext();
		for (Enumeration<?> paramEnum = servletContext.getInitParameterNames(); paramEnum.hasMoreElements();) {
			String param = (String) paramEnum.nextElement();
			initParameters.put(param, servletContext.getInitParameter(param));
		}
		
		initializeSettings(servletContext);
		try {
			pluginModuleClasses = getPluginModuleClasses(servletContext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.contextInitialized(servletContextEvent);
	}
	
	public static Set<String> getLayerNames() {
		return Collections.unmodifiableSet(layerMapping.keySet());
	}
	
	public static PoiManager getPoiManager(String layer) throws MissingLayerException {
		return injector.getInstance(PoiManagerFactory.class).create(layer);
	}

	private Set<Class<PluginModule>> getPluginModuleClasses(
			ServletContext servletContext) throws IOException {
		Set<Class<PluginModule>> result = new HashSet<Class<PluginModule>>();
		Set<?> libPaths = servletContext.getResourcePaths("/WEB-INF/lib");
		for (Object jar : libPaths) {
			try {
				JarInputStream jis = new JarInputStream(servletContext.getResource((String) jar).openStream());
				JarEntry jarEntry = null;
				try {
					while ((jarEntry = jis.getNextJarEntry()) != null) {
						if (!jarEntry.isDirectory()) {
							DataInputStream dis = new DataInputStream(
									new BufferedInputStream(jis));
							try {
							ClassFile classFile = new ClassFile(dis);
							AnnotationsAttribute aa = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
							boolean isPluginModule = aa != null 
								&& aa.getAnnotation("org.openpoi.server.api.annotations.OpenPoiPluginModule") != null;
							if (isPluginModule) {
								try {
									result.add((Class<PluginModule>) Class
											.forName(classFile.getName()));
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ClassCastException e) {
									servletContext.log("Class " + classFile.getName() 
											+ " is annotated as OpenPoiPluginModule, "
											+ "but does not implement PluginModule. "
											+ "Plugin " + jar + " will not be loaded.");
								}
							}
							} catch (IOException e) {
								// Probably not a class
							}
						}
					}
				} catch (IOException e) {
					servletContext.log("IOException while examining JAR \"" + jar + "\"; file \"" + jarEntry.getName() + "\".");
					throw e;
				} catch (RuntimeException e) {
					servletContext.log("IOException while examining JAR \"" + jar + "\"; file \"" + jarEntry.getName() + "\".");
					throw e;
				} finally {
					jis.close();
				}
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		return result;
	}

	private void initializeSettings(ServletContext servletContext) {
		layerMapping = new HashMap<String, Class<PoiManager>>();
		
		for (String paramName : initParameters.keySet()) {
			Matcher match = LAYER_MAPPING_PATTERN.matcher(paramName);
			if (match.matches()) {
				layerMapping.put(match.group(1), (Class<PoiManager>) tryGetClass((String) initParameters.get(paramName), null));
			}
		}
		
		layerMapping = Collections.unmodifiableMap(layerMapping); 
						
		try {
			cacheMaxSize = Integer.parseInt(servletContext.getInitParameter("cacheMaxSizeKb")) * 1024;
		} catch (Exception e) {
			cacheMaxSize = 64 * 1024;
		}
	}

	private Class<?> tryGetClass(String className, Class<?> defaultClass) {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			if (defaultClass != null) {
				return defaultClass;				
			} else {
				// TODO: what's sensible here?
				throw new RuntimeException(e);
			}
		}
	}

	@Override
    protected Injector getInjector() {
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(new PoiServerModule());
		modules.add(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/Pois/*").with(GetPoisServlet.class);
            }
        });
		for (Class<PluginModule> pluginModuleClass : pluginModuleClasses) {
			try {
				PluginModule pluginModule = pluginModuleClass.newInstance();
				pluginModule.setInitParameters(initParameters);
				modules.add(pluginModule);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
        injector = Guice.createInjector(modules);
        
        return injector;
    }

    private class PoiServerModule extends AbstractModule {
        @Override
        protected void configure() {
        	bind(PoiManagerFactory.class).to(DefaultPoiManagerFactory.class).in(Scopes.SINGLETON);        	
            bind(Cache.class).to(SimpleMemoryCache.class).in(Scopes.SINGLETON);
            bind(PoiSerializer.class).to(SimpleJsonPoiSerializer.class);
            
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
}
