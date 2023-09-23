package cn.aulang.oauth.restcontroller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.jwt.JwtHelper;
import cn.aulang.oauth.model.JwtUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * profile
 *
 * @author wulang
 */
@RestController
@RequestMapping("/api")
public class ProfileController {

    private final JwtHelper jwtHelper;

    @Autowired
    public ProfileController(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @GetMapping(path = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authorization) {
        try {
            String[] strs = StringUtils.split(authorization);
            String jwt = strs.length > 1 ? strs[1] : strs[0];
            JwtUser jwtUser = jwtHelper.decode(jwt);
            return ResponseEntity.ok(jwtUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Constants.error(HttpStatus.UNAUTHORIZED.value(), "无效的access_token"));
        }
    }
}
