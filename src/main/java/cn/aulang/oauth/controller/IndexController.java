package cn.aulang.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 15:30
 */
@Controller
public class IndexController {

    @GetMapping({"/", "/index",})
    public String index() {
        return "index";
    }
}
