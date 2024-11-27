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
public class AppAuthApiCountVo {

    /**
     * 租户下的API总数量
     */
    private Long totalCount;
    /**
     * 应用授权数量
     */
    private Long authedApiCountl;
    /**
     * 应用未授权数量
     */
    private Long unAuthApiCount;
}
