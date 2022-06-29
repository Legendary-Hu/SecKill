package com.shnu.seckill.info;

import com.shnu.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Author:RonClaus
 * Date:2022/6/28
 * Description:None
 */
@Data
public class LogInfo {
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;
}
