package com.shnu.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shnu.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author huxiang
 * @since 2022-06-28
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
