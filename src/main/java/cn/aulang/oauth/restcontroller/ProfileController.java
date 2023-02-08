package cn.aulang.oauth.restcontroller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.jwt.JwtHelper;
import cn.aulang.oauth.model.Profile;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wulang
 */
@RestController
public class ProfileController {

    private final JwtHelper jwtHelper;

    @Autowired
    public ProfileController(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @GetMapping(path = "/api/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authorization) {
        try {
            List<String> strs = StrUtil.splitTrim(authorization, " ");
            String jwt = strs.size() > 1 ? strs.get(1) : strs.get(0);
            Profile profile = jwtHelper.decode(jwt);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Constants.error(HttpStatus.UNAUTHORIZED.value(), "无效的access_token"));
        }
    }
}
