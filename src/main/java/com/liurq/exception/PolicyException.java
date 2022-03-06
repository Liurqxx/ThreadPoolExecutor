package com.liurq.exception;

/**
 * @author liurq
 * @desc 自定义异常
 * @date 2022-03-05  19:40
 */
public class PolicyException extends RuntimeException {

    public PolicyException() {
        super();
    }

    public PolicyException(String message) {
        super(message);
    }

}
