package com.self_back.aspect;

import com.self_back.annotation.ValidParam;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Aspect
@Component
public class ValidationAspect {
    @Before("execution(* *(.., @com.self_back.annotation.ValidParam (*), ..))")
    public void validateParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(ValidParam.class)) {
                ValidParam annotation = parameters[i].getAnnotation(ValidParam.class);
                if (args[i] instanceof String) {
                    annotation.type().validate((String) args[i]);
                }
            }
        }
    }

}
