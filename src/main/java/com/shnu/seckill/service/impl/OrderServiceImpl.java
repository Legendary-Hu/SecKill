package com.shnu.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shnu.seckill.exception.GlobalException;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.info.OrderDetail;
import com.shnu.seckill.mapper.OrderMapper;
import com.shnu.seckill.pojo.Order;
import com.shnu.seckill.pojo.SeckillGoods;
import com.shnu.seckill.pojo.SeckillOrder;
import com.shnu.seckill.pojo.User;
import com.shnu.seckill.service.IGoodsService;
import com.shnu.seckill.service.IOrderService;
import com.shnu.seckill.service.ISeckillGoodsService;
import com.shnu.seckill.service.ISeckillOrderService;
import com.shnu.seckill.utils.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 秒杀订单
     * @param user
     * @param good
     * @return
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsInfo good) {
        //秒杀商品库存减一
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", good.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        boolean seckillres = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count="+"stock_count-1"
                ).eq("goods_id", good.getId()).gt("stock_count", 0));
        if (seckillGoods.getStockCount()<1){
            //判断是否还有库存
            redisTemplate.opsForValue().set("isStockEmpty:"+seckillGoods.getGoodsId(),"0");
            return null;
        }
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
        //秒杀成功则将秒杀订单存入redis，方便下次判断是否重复抢购
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+good.getId(),seckillOrder);
        return order;
    }

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetail getDetail(Long orderId) {
        if (orderId==null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXISI);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsInfo good = goodsService.findGoodById(order.getGoodsId());
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setGoodsInfo(good);
        return orderDetail;
    }
}
