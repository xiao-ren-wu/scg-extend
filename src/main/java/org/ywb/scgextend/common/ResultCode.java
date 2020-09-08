package org.ywb.scgextend.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ResultCode {
    /**
     * 通用类
     */
    SUCCESS("00000", "成功"),
    SYSTEM_ERROR("10000", "系统异常"),
    /**
     * 服务类
     */
    SERVICE_NOT_EXIST("10001", "服务不存在"),
    SERVICE_OUT_TIME("10002", "服务调用超时"),
    SERVICE_EXCEPTION("10003", "服务调用异常"),
    /**
     * 用户类
     */
    USER_NOT_EXIST("20000", "用户不存在"),
    USER_NOT_LOGIN("20001", "用户未登录"),
    USER_PWD_ERROR("20002", "用户密码不正确"),
    USER_ACCESS_DENIED("20003", "用户无访问权限"),
    USER_PWD_NOT_SET("20004", "您尚未设置登录密码，可以切换快捷登录方式直接登录或通过忘记密码功能完成登录密码的设置。"),
    USER_PASSWORD_EMPTY("21010", "密码为空"),
    USER_PASSWORD_ERROR("21020", "密码格式错误"),
    USER_TOKEN_TIMEOUT("21030", "token已过期，请重新登录"),
    USER_TOKEN_REPEAT("21040", "异地登录"),
    USER_ACCOUNT_ERROR("21050", "账户异常"),
    USER_ACCOUNT_LOCK("21060", "账户被锁定"),
    /**
     * 参数类
     */
    PARAM_ERROR("30001", "参数异常"),
    PARAM_REPEAT_SUBMIT("30002", "数据重复提交"),
    PARAM_VALIDATOR_ERROR("30003", "验证码错误"),
    PARAM_VALIDATOR_TIMEOUT("30004", "验证码已过期，请重新获取验证码"),
    PARAM_WARMING("30006", "密码错误次数警告"),
    /**
     * DB类
     */
    INSERT_TABLE_ERROR("50001", "数据入库异常"),
    AMOUNT_CHANGE("50002", "金额发生变化"),
    ;

    private String message;

    private String code;

    ResultCode(String code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getCodeByMsg(String desc) {
        return Arrays.stream(ResultCode.values())
                .filter(a -> a.message.equals(desc))
                .findAny()
                .map(ResultCode::getCode)
                .orElse(null);
    }

    public static String getMsgByCode(String code) {
        return Arrays.stream(ResultCode.values())
                .filter(a -> a.code.equals(code))
                .findAny()
                .map(ResultCode::getMessage)
                .orElse(null);
    }
}
