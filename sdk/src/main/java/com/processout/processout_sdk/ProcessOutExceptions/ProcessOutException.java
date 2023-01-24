package com.processout.processout_sdk.ProcessOutExceptions;

/**
 * Created by jeremylejoux on 11/18/18.
 */

public class ProcessOutException extends Exception {
    private String message;

    public ProcessOutException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String toString() {
        return super.toString();
    }
}
