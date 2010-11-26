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
