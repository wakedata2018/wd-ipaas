package com.wakedata.dw.lowcode.model;

import lombok.*;

import javax.persistence.Table;

/**
 * 页面详情 - 实体类
 *
 * @author chenshaopeng
 * @date 2021/11/24
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dw_open_lowcode_page_detail")
public class LowCodePageDetailPo extends AppPo {

    /**
     * 基础信息Id
     */
    private Integer simpleId;

    /**
     * 页面内容 - 压缩
     */
    private String compressedContent;

    /**
     * 页面内容
     */
    private String content;

}

