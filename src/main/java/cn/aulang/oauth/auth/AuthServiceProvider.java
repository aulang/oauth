package cn.aulang.oauth.auth;

import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.server.core.AuthService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
            services = applicationContext.getBeansOfType(AuthService.class).values();
        }
        return services;
    }

    public AuthService get(ThirdServer server) {
        Optional<AuthService> optional = getServices()
                .parallelStream()
                .filter(e -> e.supports(server))
                .findFirst();
        return optional.orElse(null);
    }
}