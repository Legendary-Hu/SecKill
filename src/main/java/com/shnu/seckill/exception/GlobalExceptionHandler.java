package com.shnu.seckill.exception;

import com.shnu.seckill.utils.RespBean;
import com.shnu.seckill.utils.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * Author:RonClaus
 * Date:2022/6/30
 * Description:None
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean handler(Exception e){
        if (e instanceof GlobalException){ //判断是否为GlobalException
            GlobalException e1 = (GlobalException) e;
            return RespBean.error(e1.getRespBeanEnum());
        }else if(e instanceof BindException){
            BindException e2 = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BINDING_ERROR);
            respBean.setMessage("参数校验异常："+((BindException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
