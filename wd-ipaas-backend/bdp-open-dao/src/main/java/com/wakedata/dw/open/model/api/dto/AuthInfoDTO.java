package com.wakedata.dw.open.model.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 授权的第三方应用信息
 *
 * @author wujunqiang
 * @date 2021/12/16 15:21
 */
@Data
@AllArgsConstructor
public class AuthInfoDTO {

    /**
     * 第三方授权应用ID
     */
    private Integer authId;

    /**
     * 第三方授权应用名称
     */
    private String name;

}
