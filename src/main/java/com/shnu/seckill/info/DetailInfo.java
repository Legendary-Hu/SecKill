package com.shnu.seckill.info;

import com.shnu.seckill.pojo.User;
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
public class DetailInfo {
    private User user;
    private GoodsInfo goodsInfo;
    private int secKillStatus;
    private int remainSeconds;
}
