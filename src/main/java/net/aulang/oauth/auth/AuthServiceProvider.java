package net.aulang.oauth.auth;

import net.aulang.oauth.entity.ThirdServer;
import net.aulang.oauth.server.core.AuthService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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

    public AuthService get(ThirdServer server) {
        Optional<AuthService> optional =
                getServices()
                        .parallelStream()
                        .filter(e -> e.supports(server))
                        .findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }
}