package com.self_back.Handler;

import com.self_back.Exception.*;
import com.self_back.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException e) {
        log.info(e.getMessage());
        return Result.error(e.getMessage());
    }
    @ExceptionHandler(value = NoUserException.class)
    public Result<?> noUserExceptionHandler(NoUserException e) {
        log.info("未找到用户"+e);
        return Result.error(e.getMessage());
    }
    @ExceptionHandler(value = PassErrException.class)
    public Result<?> passErrExceptionHandler(PassErrException e) {
        log.info("密码错误");
        return Result.error(e.getMessage());
    }
    @ExceptionHandler(value = UnAuthException.class)
    public Result<?> unAuthException(UnAuthException e) {
        log.info("鉴权失败，请登录");
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = CodeErrException.class)
    public Result<?> CodeErr(CodeErrException e) {
        log.info("验证码错误");
        return Result.error(e.getMessage());
    }
    @ExceptionHandler(value = Exception.class)
    public Result<?> Exception(Exception e) {
        log.info(e.getMessage());
        return Result.error(e.getMessage());
    }
}
