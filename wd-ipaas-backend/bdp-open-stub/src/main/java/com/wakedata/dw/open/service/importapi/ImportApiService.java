package com.wakedata.dw.open.service.importapi;

import com.wakedata.dw.open.service.exportapi.dto.openapi.ImportOpenApiResultDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;

/**
 * 导入API业务接口
 *
 * @author wujunqiang
 * @since 2023/2/3 11:53
 */
public interface ImportApiService {

    /**
     * 通过文件方式导入开放平台API
     *
     * @param tempFilePath 已经上传的导入文件路径
     * @param apiGroupId   导入的api分组id
     * @param userInfo     用户上下文
     * @return 导入开放平台API结果统计DTO
     */
    ImportOpenApiResultDTO importOpenApiFromFile(String tempFilePath, Integer apiGroupId, IpaasUserInfo userInfo);

    /**
     * 通过文件方式导入连接器
     *
     * @param tempFilePath     已经上传的导入文件路径
     * @param connectorGroupId 导入的连接器分类id
     * @param userInfo         用户上下文
     * @return 导入连接器结果统计DTO
     */
    ImportOpenApiResultDTO importConnectorFromFile(String tempFilePath, Long connectorGroupId, IpaasUserInfo userInfo);

}
