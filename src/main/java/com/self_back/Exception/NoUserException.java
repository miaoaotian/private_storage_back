package com.self_back.Exception;

public class NoUserException extends RuntimeException{
    public NoUserException(String msg) {
        super(msg);
    }
}
