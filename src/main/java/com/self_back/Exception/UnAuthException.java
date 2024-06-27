package com.self_back.Exception;

public class UnAuthException extends RuntimeException{
    public UnAuthException(String msg) {
        super(msg);
    }
}
