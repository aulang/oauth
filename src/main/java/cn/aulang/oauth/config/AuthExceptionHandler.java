package cn.aulang.oauth.config;


import cn.aulang.common.web.WebExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author wulang
 */
@RestControllerAdvice
public class AuthExceptionHandler extends WebExceptionHandler {
}
