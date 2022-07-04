package com.shnu.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.pojo.Goods;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huxiang
 * @since 2022-07-02
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsInfo> getGoods();

    GoodsInfo findGoodById(Long goodsId);
}
