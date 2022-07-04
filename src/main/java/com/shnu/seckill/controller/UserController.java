package com.shnu.seckill.controller;


import com.shnu.seckill.pojo.User;
import com.shnu.seckill.utils.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author huxiang
 * @since 2022-06-28
 */
@Controller
@RequestMapping("/user")
public class UserController {
    /**
     * 用户信息（测试）
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

}
