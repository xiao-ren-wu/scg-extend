package org.ywb.scgextend.handler;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ywb.scgextend.common.SpringContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @version v1.0.0
 * <p>
 * 虽然spring webflux支持springMVC注解，
 * 但是，Filter抛出的异常不属于MVC层，所以，处理Filter抛出的异常依旧不能用SpringMVC那一套注解
 * 但是我们可以自己模仿做一套和MVC类似的注解
 * 该处理类使用SpringMVC的注解
 * 让用户可以无感知的像处理MVC层抛出的异常那样处理Filter抛出的异常
 * __________________________________________
 * |但是由于本人能力有限，暂时只支持            |
 * |入参有且只有一个并且类型只能是处理的异常类型|
 * |________________________________________|
 * 示例：
 * <code>
 * *@RestControllerAdvice
 * public class GlobalExceptionHandlerAdvice {
 * *@ExceptionHandler(Exception.class)
 * public ResponseResult handler(Exception e) {
 * return ResponseResult.createBySuccess("这是我的自定义异常");
 * }
 * }
 * </code>
 * </p>
 * @date 2019/9/10 11:15
 */
@Slf4j
@Component
public class ExceptionHandlerCore implements ApplicationRunner {

    /**
     * key是处理异常的类型
     * value是处理异常的方法
     */
    private LinkedHashMap<Class<? extends Throwable>, Node> exceptionHandlerMap;

    /**
     * 解析类上的注解
     * 将处理异常的方法注册到map中
     */
    private void register(Object exceptionAdvice) {
        Method[] methods = exceptionAdvice.getClass().getMethods();
        Arrays.stream(methods).forEach(method -> {
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            if (Objects.isNull(exceptionHandler)) {
                return;
            }
            ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
            HttpStatus status = null;
            if (Objects.nonNull(responseStatus)) {
                status = responseStatus.value();
            }
            HttpStatus finalStatus = status;
            Arrays.asList(exceptionHandler.value()).forEach(a -> exceptionHandlerMap.put(a, new Node(method, exceptionAdvice, finalStatus)));
        });
    }

    /**
     * 根据异常对象获取解决异常的方法
     *
     * @param throwable 异常对象
     * @return handler method
     */
    private Node getHandlerExceptionMethodNode(Throwable throwable) {
        ArrayList<Class<?>> superClass = this.getSuperClass(throwable.getClass());
        for (Class<?> aClass : superClass) {
            Node handlerNode = null;
            if ((handlerNode = exceptionHandlerMap.get(aClass)) != null) {
                return handlerNode;
            }
        }
        return null;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, Object> beans = SpringContextHolder.getBeansWithAnnotation(RestControllerAdvice.class);
        log.info("-------------异常处理对象获取完毕-------------");
        exceptionHandlerMap = Maps.newLinkedHashMapWithExpectedSize(beans.size());
        log.info("-------------异常处理容器内存分配完毕-------------");
        beans.keySet()
                .stream()
                .map(beans::get)
                .forEach(this::register);
        log.info("-------------异常处理方法注册完毕-------------");
    }

    /**
     * 对外暴露的处理异常的方法
     *
     * @param throwable 处理的异常
     * @return 调用异常后的返回值
     */
    public HashMap<String, Object> handlerException(Throwable throwable) {
        Node exceptionMethodNode = this.getHandlerExceptionMethodNode(throwable);
        if (Objects.isNull(exceptionMethodNode)) {
            log.error("未定义异常处理方法，处理异常失败,异常处理信息如下：{}", throwable.getMessage());
            throw new RuntimeException("未定义异常处理方法，处理异常失败");
        }
        /**
         * 本人水平有限。
         * 现在支持持一个入参类型类{@link Throwable}
         * 后人可以随意扩展~
         */
        Object returnResult = null;
        try {
            returnResult = exceptionMethodNode.method.invoke(exceptionMethodNode.thisObj, throwable);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> resultMap = Maps.newHashMapWithExpectedSize(2);
        resultMap.put("status", exceptionMethodNode.status);
        resultMap.put("result", returnResult);
        return resultMap;
    }

    /**
     * 用于存放方法和方法所在的实例
     */
    private static class Node {
        Node(Method method, Object thisObj, HttpStatus status) {
            this.method = method;
            this.thisObj = thisObj;
            this.status = status;
        }

        /**
         * 状态码
         */
        HttpStatus status;
        /**
         * 处理异常的方法
         */
        Method method;
        /**
         * 方法所在的实例
         */
        Object thisObj;
    }


    /**
     * 获取该类的class以及所有父的class
     *
     * @param clazz this.class
     * @return list
     */
    public ArrayList<Class<?>> getSuperClass(Class<?> clazz) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        classes.add(clazz);
        Class<?> suCl = clazz.getSuperclass();
        while (suCl != null) {
            classes.add(suCl);
            suCl = suCl.getSuperclass();
        }
        return classes;
    }
}
