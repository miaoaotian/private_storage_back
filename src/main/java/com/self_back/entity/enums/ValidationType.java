package com.self_back.entity.enums;

import com.self_back.Exception.BusinessException;

public enum ValidationType {
    EMAIL("^[A-Za-z0-9+_.-]+@(.+)$", "不符合邮箱格式"),
    PASSWORD("^[a-zA-Z0-9]{6,12}$", "密码需为6到12位的字符"),
    USERNAME("^[a-zA-Z0-9]{1,10}$", "用户名必须要满足1到10位");

    private final String pattern;
    private final String errorMessage;

    ValidationType(String pattern, String errorMessage) {
        this.pattern = pattern;
        this.errorMessage = errorMessage;
    }
    public void validate(String value) {
        if (!value.matches(pattern)) {
            throw new BusinessException(errorMessage);
        }
    }
}
