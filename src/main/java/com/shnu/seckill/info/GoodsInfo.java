package com.shnu.seckill.info;

import com.shnu.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.attoparser.trace.MarkupTraceEvent;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Author:RonClaus
 * Date:2022/7/2
 * Description:None
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfo extends Goods {

    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
