package cn.aulang.oauth.thirdserver.core;

import cn.aulang.oauth.entity.ThirdServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:57
 */
@Service
public class AuthServiceProvider implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Collection<AuthService> services;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Collection<AuthService> getServices() {
        if (services == null) {
            Map<String, AuthService> serviceMap
                    = applicationContext.getBeansOfType(AuthService.class);
            services = serviceMap.values();
        }
        return services;
    }

    public Optional<AuthService> get(ThirdServer server) {
        return getServices().parallelStream().filter(e -> e.supports(server)).findAny();
    }
}