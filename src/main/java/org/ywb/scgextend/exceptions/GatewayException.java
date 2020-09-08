package org.ywb.scgextend.exceptions;

import lombok.Getter;
import org.ywb.scgextend.common.ResultCode;

@Getter
public class GatewayException extends RuntimeException {

    private String code;
    private String message;

    public GatewayException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public GatewayException(String code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
