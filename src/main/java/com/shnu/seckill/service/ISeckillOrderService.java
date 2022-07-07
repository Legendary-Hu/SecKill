package com.shnu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shnu.seckill.pojo.SeckillOrder;
import com.shnu.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huxiang
 * @since 2022-07-02
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
