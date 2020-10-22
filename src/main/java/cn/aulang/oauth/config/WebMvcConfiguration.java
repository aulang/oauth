package cn.aulang.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 18:00
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebContentInterceptor contentInterceptor = new WebContentInterceptor();
        contentInterceptor.setCacheSeconds(0);
        registry.addInterceptor(contentInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ALL)
                .allowCredentials(true);
    }
}
