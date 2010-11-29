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

package org.openpoi.server.cache.memcached;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.openpoi.server.api.Cache;

public class MemcachedCache implements Cache {
	private MemcachedClient client;
	private int cacheExpirationSeconds;
	private long queryTimeoutSeconds;
	
	public MemcachedCache(
			@MemcachedAddresses String memcachedAddresses, 
			@QueryTimeout long timeoutSeconds, 
			@CacheExpiration int cacheExpirationSeconds) throws IOException {
		client = new MemcachedClient(AddrUtil.getAddresses(memcachedAddresses));
		this.queryTimeoutSeconds = timeoutSeconds;
		this.cacheExpirationSeconds = cacheExpirationSeconds;
	}
	
	@Override
	public void clear() {
		client.flush();
	}

	@Override
	public String get(String key) {
		Future<Object> f = client.asyncGet(key);
		try {
			return (String) f.get(queryTimeoutSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			// TODO: maybe log something here
			f.cancel(false);
			return null;
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			// TODO: maybe log something here
			return null;
		}
	}

	@Override
	public void put(String key, String value) {
		client.set(key, cacheExpirationSeconds, value);
	}
}
