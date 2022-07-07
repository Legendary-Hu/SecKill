package com.shnu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.info.OrderDetail;
import com.shnu.seckill.pojo.Order;
import com.shnu.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huxiang
 * @since 2022-07-02
 */
public interface IOrderService extends IService<Order> {
    /**
     * 秒杀
     * @param user
     * @param good
     * @return
     */
    Order seckill(User user, GoodsInfo good);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetail getDetail(Long orderId);
}
