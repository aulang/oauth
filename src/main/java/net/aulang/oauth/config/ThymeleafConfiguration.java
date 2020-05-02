package net.aulang.oauth.config;

import net.aulang.oauth.manage.BeiAnBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.PostConstruct;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-05-02 12:27
 */
@Configuration
public class ThymeleafConfiguration {
    @Autowired
    private BeiAnBiz beiAnBiz;
    @Autowired
    private ThymeleafViewResolver viewResolver;

    @PostConstruct
    public void addStaticVariable() {
        viewResolver.addStaticVariable("beiAn", beiAnBiz.get());
    }
}
