package com.highgo.cloud.auth.util;


import com.highgo.cloud.result.ResultCode;

/**
 * @Description: 统一接口返回值
 * @Author: yanhonghai
 * @Date: 2018/11/14 18:28
 */
public class ResultUtil {
    public static Result success() {
        return success(null);
    }

    public static Result success(Object object) {
        Result result = new Result();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage("成功");
        result.setData(object);
        return result;
    }

    public static Result success(Integer code, Object object) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage("成功");
        result.setData(object);
        return result;
    }

    public static Result success(Integer code, String msg, Object object) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(msg);
        result.setData(object);
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode(ResultCode.INTERNAL_SERVER_ERROR.getCode());
        result.setMessage(msg);
        return result;
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }
}
