package com.wakedata.dw.open.controller.dto;

import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 校验过后需要返回的参数
 *
 * @author zhengqinghui@wakedata.com
 * @date 2022/10/12 15:39
 */
@Data
public class VerifyDTO {
    /**
     * 校验状态
     */
    private Boolean verifyStatus;
    /**
     * 到期时间
     */
    private String expirationTime;

    /**
     * 剩余天数
     */
    private long remainDay;

    public VerifyDTO() {
    }

    public VerifyDTO(Boolean verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public void settingTime(Date beforeTime, Date afterTime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.expirationTime = format.format(afterTime);
        //算出两个时间间隔天数
        this.remainDay = (afterTime.getTime() - beforeTime.getTime()) / (24 * 60 * 60 * 1000);
    }
}
