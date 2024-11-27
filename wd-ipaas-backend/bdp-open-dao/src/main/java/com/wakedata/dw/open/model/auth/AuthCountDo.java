package com.wakedata.dw.open.model.auth;

import lombok.Data;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/11/11 16:22
 */
@Data
public class AuthCountDo {
    /**
     * 第三方应用Id
     * */
    private Integer authId;
    /**
     * 已授权的API数量
     * */
    private Integer apiNum;
}
