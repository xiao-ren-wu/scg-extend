package org.ywb.scgextend.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @version v1.0.0
 * <p>
 * 全局异常处理
 * </p>
 * @date 2019/9/1 13:28
 */
@Slf4j
public class GlobalExceptionHandler extends DefaultErrorWebExceptionHandler {

    @Resource
    private ExceptionHandlerCore handlerCore;


    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 统一处理异常信息
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Throwable error = super.getError(request);
        //调用处理异常的方法，并将对象转换成map
        return handlerCore.handlerException(error);
    }

    /**
     * Extract the error attributes from the current request, to be used to populate error
     * views or JSON payloads.
     * @param request the source request
     * @param options options to control error attributes
     * @return the error attributes as a Map
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = super.getError(request);
        return handlerCore.handlerException(error);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        boolean includeStackTrace = isIncludeStackTrace(request, MediaType.ALL);
        Map<String, Object> error = getErrorAttributes(request, includeStackTrace);
        return ServerResponse
                .status(getHttpStatus(error))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(error.get("result")));
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        HttpStatus status = (HttpStatus) errorAttributes.get("status");
        if (Objects.isNull(status)) {
            return HttpStatus.OK.value();
        }
        return status.value();
    }
}
