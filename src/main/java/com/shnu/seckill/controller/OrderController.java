package com.shnu.seckill.controller;


import com.shnu.seckill.info.OrderDetail;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IOrderService;
import com.shnu.seckill.utils.RespBean;
import com.shnu.seckill.utils.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author huxiang
 * @since 2022-07-02
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;


    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user,Long orderId){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetail orderDetail = orderService.getDetail(orderId);
        if (orderDetail==null){
            return RespBean.error(RespBeanEnum.ORDER_NOT_EXISI);
        }
        return RespBean.success(orderDetail);
    }
}
