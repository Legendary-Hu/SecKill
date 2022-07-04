package com.shnu.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.mapper.GoodsMapper;
import com.shnu.seckill.pojo.Goods;
import com.shnu.seckill.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author huxiang
 * @since 2022-07-02
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsInfo> getGoods() {
        return goodsMapper.findGoods();
    }

    @Override
    public GoodsInfo findGoodById(Long goodsId) {

        return goodsMapper.findGoodById(goodsId);
    }
}
