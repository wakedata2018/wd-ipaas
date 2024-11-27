package com.wakedata.dw.open.service.exportapi;

import com.wakedata.dw.open.service.exportapi.dto.connector.ExportConnectorParamDTO;
import com.wakedata.dw.open.service.exportapi.dto.openapi.ExportOpenApiParamDTO;

import javax.servlet.http.HttpServletResponse;

/**
 * 导出API业务接口
 *
 * @author wujunqiang
 * @since 2023/1/12 11:35
 */
public interface ExportApiService {

    /**
     * 将连接器API导出成json文件
     *
     * @param dto      导出连接器需要传的参数DTO
     * @param response HttpServletResponse
     */
    void exportConnectorApiToFile(ExportConnectorParamDTO dto, HttpServletResponse response);

    /**
     * 将开放平台API导出成json文件
     *
     * @param dto      导出API需要传的参数DTO
     * @param response HttpServletResponse
     */
    void exportOpenApiToFile(ExportOpenApiParamDTO dto, HttpServletResponse response);

    /**
     * 将开发平台API导出成markDown文件
     *
     * @param dto 导出API需要传的参数DTO
     * @param response 响应体
     */
    void exportOpenApiToMarkdown(ExportOpenApiParamDTO dto, HttpServletResponse response);
}
