package com.wakedata.dw.open.model.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API标签信息
 *
 * @author chenshaopeng
 * @date 2021/11/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTagInfo {

    /**
     * 名称
     */
    private String name;

    /**
     * 说明
     */
    private String description;

    /**
     * 所属控制器
     */
    private String controller;


}
