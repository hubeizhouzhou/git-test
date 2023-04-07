package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*
全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})   //拦截这些
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {




    /*
       异常处理方法
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)  //处理这个SQL异常
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
       // log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){                // contains包含，拦截信息里包含Dup..的error
        String []split = ex.getMessage().split(" ");             //以空格分隔 拿数组第三个数据
        String msg = split[2]+"已经有了喔";
        return R.error(msg);
        }
        return R.error("出现了鼠鼠也不知道的错误了捏");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        return R.error(ex.getMessage());
    }


}
