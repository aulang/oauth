package cn.aulang.oauth.restcontroller;

import cn.aulang.oauth.entity.BeiAn;
import cn.aulang.oauth.manage.BeiAnBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-05-03 19:54
 */
@RestController
@RequestMapping("/api/beian")
public class BeiAnRestController {

    private final BeiAnBiz beiAnBiz;

    @Autowired
    public BeiAnRestController(BeiAnBiz beiAnBiz) {
        this.beiAnBiz = beiAnBiz;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BeiAn> get() {
        BeiAn beiAn = beiAnBiz.get();
        return ResponseEntity.ok(beiAn);
    }
}
