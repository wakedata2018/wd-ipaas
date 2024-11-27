package com.wakedata.dw.open.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用授权API数量VO
 * @author 佟蕊
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppIdAndAuthApiCountVo {

    /**
     * App Id
     */
    private Integer appId;
    /**
     * 应用授权API数量
     */
    private Integer authApiCount;
}
