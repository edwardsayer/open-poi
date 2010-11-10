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

package org.openpoi.server;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openpoi.server.api.Cache;
import org.openpoi.server.api.annotations.CacheMaxSize;


import com.google.inject.Inject;

public class SimpleMemoryCache implements Cache {
    private Map<String, String> cache;
    private int size;
    
    @Inject
    public SimpleMemoryCache(@CacheMaxSize final int maxSize) {
        cache = new LinkedHashMap<String, String>(100, .75f, true) {
            @Override
			public String put(String arg0, String arg1) {
            	size += arg1.length();
				return super.put(arg0, arg1);
			}

			@Override
			public String remove(Object key) {
				String removedValue = super.remove(key);
				if (removedValue != null) {
					size -= removedValue.length();
				}
				
				return removedValue;
			}

			@Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size > maxSize;
            }

			@Override
			public void clear() {
				super.clear();
				size = 0;
			}
        };
        
    }
    
    @Override
    public String get(String key) {
        synchronized (cache) {
            return cache.get(key);
        }
    }

    @Override
    public void put(String key, String value) {
        synchronized (cache) {
            cache.put(key, value);
        }
    }

	@Override
	public void clear() {
		synchronized (cache) {
			cache.clear();
		}
	}
}
