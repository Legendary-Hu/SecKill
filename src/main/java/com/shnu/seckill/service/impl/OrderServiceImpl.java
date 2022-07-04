package com.shnu.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.mapper.OrderMapper;
import com.shnu.seckill.pojo.Order;
import com.shnu.seckill.pojo.SeckillGoods;
import com.shnu.seckill.pojo.SeckillOrder;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IOrderService;
import com.shnu.seckill.service.ISeckillGoodsService;
import com.shnu.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huxiang
 * @since 2022-07-02
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    /**
     * 秒杀订单
     * @param user
     * @param good
     * @return
     */
    @Override
    public Order seckill(User user, GoodsInfo good) {
        //秒杀商品库存减一
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", good.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        seckillGoodsService.updateById(seckillGoods);
        //生成Order订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(good.getId());
        order.setDeliveryAddrId(0l);
        order.setGoodsName(good.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(good.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrderService.save(seckillOrder);
        return order;
    }
}
