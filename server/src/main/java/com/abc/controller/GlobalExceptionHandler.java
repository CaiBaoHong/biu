package com.abc.controller;

import com.abc.vo.Json;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {

        System.out.println("-------" + ex.getClass().getSimpleName());

        HttpStatus status = getStatus(request);

        String message = ex.getMessage();
        Json resp = new Json();
        resp.succ(false);

        if (ex instanceof UnauthorizedException) {

            //未登录
            message = "Unauthorized";
            resp.code(HttpStatus.UNAUTHORIZED.value());
            status = HttpStatus.OK;//为了前台能接收到返回的异常信息

        } else if (ex instanceof UnauthenticatedException) {

            //没有权限访问
            message = "Unauthenticated";
            resp.code(HttpStatus.FORBIDDEN.value());
            status = HttpStatus.OK;

        } else {

            message = "system error";
            resp.code(HttpStatus.INTERNAL_SERVER_ERROR.value());
            logger.error(message, ex);

        }
        resp.msg(message);
        return new ResponseEntity<>(resp, status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}