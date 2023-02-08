package cn.aulang.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 缓存服务
 *
 * @author wulang
 */
@Service
public class CacheService {

    private final CacheManager cacheManager;

    @Autowired
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }

    public Object get(String name, String key) {
        Cache cache = cacheManager.getCache(name);

        if (cache == null) {
            return null;
        }

        Cache.ValueWrapper wrapper = cache.get(key);

        if (wrapper == null) {
            return null;
        }

        return wrapper.get();
    }

    public void evict(String name, String key) {
        Cache cache = cacheManager.getCache(name);

        if (cache != null) {
            cache.evict(key);
        }
    }

    public void clear(String name) {
        Cache cache = cacheManager.getCache(name);

        if (cache != null) {
            cache.clear();
        }
    }
}
