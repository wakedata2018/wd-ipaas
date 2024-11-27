package com.wakedata.dw.open.liteflow.globalparams;

import lombok.Data;

/**
 * author Wangchensheng@wakedata.com
 * date 2023年02月21日 19:20:58
 */
@Data
public class GlobalParam {

    @GlobalParamDescription("惟客云租户id")
    public Integer tenantId;

    @GlobalParamDescription("惟客云应用id")
    public Integer appBuId;

}
