package com.processout.processout_sdk.ProcessOutExceptions;

/**
 * Created by jeremylejoux on 11/18/18.
 */

public class ProcessOutCardException extends ProcessOutException {

    private String code;

    public ProcessOutCardException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
