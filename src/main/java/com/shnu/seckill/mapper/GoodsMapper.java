package com.shnu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shnu.seckill.info.GoodsInfo;
import com.shnu.seckill.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author huxiang
 * @since 2022-07-02
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    /**
     * 获取商品列表
     * @return
     */
    List<GoodsInfo> findGoods();

    GoodsInfo findGoodById(Long goodsId);
}
