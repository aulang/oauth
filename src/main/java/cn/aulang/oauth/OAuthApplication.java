package cn.aulang.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-11-24 22:50
 */
@EnableScheduling
@SpringBootApplication
@EnableMongoRepositories("cn.aulang.oauth.repository")
public class OAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthApplication.class, args);
    }

}
