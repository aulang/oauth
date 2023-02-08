package cn.aulang.oauth.controller;

import cn.aulang.oauth.model.WebResponse;
import cn.aulang.oauth.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * 本地开发跨域临时配置
 * TODO 上线发布删除或注释该文件
 *
 * @author wulang
 */
@RestController
@RequestMapping("cache")
public class CacheController {

    private final CacheService cacheService;

    @Autowired
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("names")
    public Collection<String> get() {
        return cacheService.getCacheNames();
    }

    @GetMapping("{name}/{key}")
    public ResponseEntity<?> get(@PathVariable("name") String name, @PathVariable("key") String key) {
        Object obj = cacheService.get(name, key);

        if (obj == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(obj);
    }

    @GetMapping("clear/{name}")
    public WebResponse<?> clear(@PathVariable("name") String name) {
        cacheService.clear(name);
        return WebResponse.success();
    }

    @GetMapping("delete/{name}/{key}")
    public WebResponse<?> evict(@PathVariable("name") String name, @PathVariable("key") String key) {
        cacheService.evict(name, key);
        return WebResponse.success();
    }
}
