package org.ywb.scgextend.advice;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.ywb.scgextend.common.ResultCode;
import org.ywb.scgextend.common.ResultVO;
import org.ywb.scgextend.exceptions.GatewayException;
import org.ywb.scgextend.handler.ExceptionHandlerCore;

import java.net.ConnectException;

import static org.ywb.scgextend.common.ResultCode.SERVICE_NOT_EXIST;


/**
 * @version v1.0.0
 * <p>
 * [注意！！！！]
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * 这个不是SpringMVC统一异常处理！！！
 * 这个不是SpringMVC统一异常处理！！！
 * 这个不是SpringMVC统一异常处理！！！
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * {具体说明详见：}
 * {@link ExceptionHandlerCore}
 * 一定要看！！！
 * </p>
 * @date 2019/9/10 15:55
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler(GatewayException.class)
    public ResultVO<Void> handler(GatewayException e) {
        return ResultVO.buildFailure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResultVO<Void> handler(ResponseStatusException e) {
        return ResultVO.buildFailure(SERVICE_NOT_EXIST.getCode(), e.getReason());
    }

    @ExceptionHandler(ConnectException.class)
    public ResultVO<Void> handler(ConnectException e) {
        log.error(Throwables.getStackTraceAsString(e));
        return ResultVO.buildFailure(ResultCode.SERVICE_OUT_TIME.getCode(), "网络异常,请稍候再试!");
    }

    @ExceptionHandler(Throwable.class)
    public ResultVO<Void> handler(Throwable e) {
        log.error(Throwables.getStackTraceAsString(e));
        return ResultVO.buildFailure(ResultCode.SERVICE_EXCEPTION.getCode(), "服务器异常");
    }

}
