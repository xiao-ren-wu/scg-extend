package org.ywb.scgextend.common;

import com.google.common.base.Strings;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

@Getter
public class ResultVO<T> implements Serializable {

    /**
     * 相应状态码{@link ResultCode}
     */
    private String code;

    /**
     * 响应信息描述
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    private ResultVO(String message, String code) {
        this.message = message;
        this.code = code;
    }

    /**
     * 通过定义好的枚举创建VO
     *
     * @param resultCode {@link ResultCode}
     * @param <D>        data
     * @return ResultVo create By Enum
     */
    private static <D> ResultVO<D> buildByEnum(ResultCode resultCode) {
        return new ResultVO<>(resultCode.getMessage(), resultCode.getCode());
    }

    /**
     * 通过定义好的枚举创建VO
     *
     * @param resultCode {@link ResultCode}
     * @param <D>        data
     * @return ResultVo create By Enum
     */
    public static <D> ResultVO<D> buildFailure(ResultCode resultCode) {
        return buildByEnum(resultCode);
    }

    public static <D> ResultVO<D> buildFailure(String code, String message) {
        return new ResultVO<>(message, code);
    }

    /**
     * 创建成功返回对象
     *
     * @param data 返回数据
     * @param <D>  返回值类型
     * @return 统一返回对象
     */
    public static <D> ResultVO<D> buildSuccess(D data) {
        ResultVO<D> resultVO = buildByEnum(ResultCode.SUCCESS);
        resultVO.data = data;
        return resultVO;
    }

    /**
     * 创建成功返回对象
     *
     * @param <D> 返回值类型
     * @return 统一返回对象
     */
    public static <D> ResultVO<D> buildSuccess() {
        return buildByEnum(ResultCode.SUCCESS);
    }

    /**
     * 获取成功的数据，如果不成功，抛出{@link IllegalArgumentException}异常
     * 并携带{@see message}为异常信息
     *
     * @param resultVO resultVO
     * @param message  异常时抛出的信息
     * @param <D>      data
     * @return D
     */
    public static <D> D getDataIfSuccess(ResultVO<D> resultVO, String message) {
        assertSuccess(resultVO, message);
        return resultVO.data;
    }

    /**
     * 获取成功的数据，如果不成功，抛出{@link IllegalArgumentException}异常
     * 并携带{@see message}为异常信息
     *
     * @param resultVO        resultVO
     * @param messageSupplier 异常时抛出的信息
     * @param <D>             data
     * @return D
     */
    public static <D> D getDataIfSuccess(ResultVO<D> resultVO, Supplier<String> messageSupplier) {
        assertSuccess(resultVO, nullSafeGet(messageSupplier));
        return resultVO.data;
    }

    /**
     * 校验结果是否成功
     *
     * @param resultVO
     * @param <D>
     * @return
     */
    public static <D> boolean isSuccess(ResultVO<D> resultVO) {
        if (Objects.isNull(resultVO) || Strings.isNullOrEmpty(resultVO.code) || !resultVO.code.equals(ResultCode.SUCCESS.getCode())) {
            return false;
        }
        return true;
    }

    /**
     * 断言resultVO是否成功，如果不成功，抛出{@link IllegalArgumentException}异常
     * 并携带通过调用{@see messageSupplier}方法获取携带的异常信息
     *
     * @param resultVO resultVO
     * @param message  异常时抛出的信息
     * @param <D>      data
     */
    public static <D> void assertSuccess(ResultVO<D> resultVO, String message) {
        if (Objects.isNull(resultVO) || Strings.isNullOrEmpty(resultVO.code)) {
            throw new IllegalArgumentException(message);
        }
        if (!resultVO.code.equals(ResultCode.SUCCESS.getCode())) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言resultVO是否成功，如果不成功，抛出{@link IllegalArgumentException}异常
     * 并携带通过调用{@see messageSupplier}方法获取携带的异常信息
     *
     * @param resultVO        resultVO
     * @param messageSupplier 异常时抛出的信息
     * @param <D>             data
     */
    public static <D> void assertSuccess(ResultVO<D> resultVO, Supplier<String> messageSupplier) {
        if (Objects.isNull(resultVO) || Strings.isNullOrEmpty(resultVO.code)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
        if (!resultVO.code.equals(ResultCode.SUCCESS.getCode())) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }

    public static <D> ResultVO<D> buildFailure(String code, String message, D data) {
        ResultVO<D> resultVO = buildFailure(code, message);
        resultVO.data = data;
        return resultVO;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data +
                '}';
    }
}
