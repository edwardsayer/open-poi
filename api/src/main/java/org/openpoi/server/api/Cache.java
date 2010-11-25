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

package org.openpoi.server.api;

/**
 * <p>The interface to be implemented by a cache for POI query responses.</p>
 * 
 * <p>Note that implementations of this class must be implemented in a
 * thread safe manner.</p> 
 * @author Per Liedman (per@liedman.net)
 *
 */
public interface Cache {
	/**
	 * Returns the cached response for a query's key.
	 * @param key the key of the query
	 * @return the serialized response of the query, if it has been cached, or
	 * <code>null</code> if the query's response is not in the cache.
	 */
    String get(String key);
    
    /**
     * Puts a query's response in to the cache.
     * @param key the query's key, which the cached result can later be fetched by
     * @param value the query's response, which is to be cached
     */
    void put(String key, String value);
    
    /**
     * Clears (invalidates) the cache's current contents.
     */
    void clear();
}
