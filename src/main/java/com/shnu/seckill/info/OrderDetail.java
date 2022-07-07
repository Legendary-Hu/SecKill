package com.shnu.seckill.info;

import com.shnu.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author:RonClaus
 * Date:2022/7/5
 * Description:None
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    private Order order;
    private GoodsInfo goodsInfo;
}
