package com.wakedata.dw.open.service.impl.exportapi;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.Markdown;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.ApiGroupDTO;
import com.wakedata.dw.open.service.connector.ConnectorApiGroupService;
import com.wakedata.dw.open.service.connector.ConnectorApiService;
import com.wakedata.dw.open.service.connector.ConnectorService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDetailDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiGroupDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorDTO;
import com.wakedata.dw.open.service.exportapi.ExportApiService;
import com.wakedata.dw.open.service.exportapi.dto.connector.ConnectorExportInfoDTO;
import com.wakedata.dw.open.service.exportapi.dto.connector.ExportConnectorParamDTO;
import com.wakedata.dw.open.service.exportapi.dto.openapi.ExportOpenApiParamDTO;
import com.wakedata.dw.open.service.exportapi.dto.openapi.OpenApiExportInfoDTO;
import com.wakedata.dw.open.service.impl.api.gateway.MarkdownWriter;
import com.wakedata.dw.open.service.impl.api.helper.ApiGroupHelper;
import com.wakedata.dw.open.service.utils.DocumentMarkDownUtil;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.service.vo.DocumentApiDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 导出API业务接口实现类
 *
 * @author wujunqiang
 * @since 2023/1/12 11:38
 */
@Slf4j
@Service
public class ExportApiServiceImpl implements ExportApiService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private DataAssetApiService dataAssetApiService;

    @Autowired
    private ConnectorApiService connectorApiService;

    @Autowired
    private ConnectorApiGroupService connectorApiGroupService;

    @Resource
    private ApiGroupService apiGroupService;

    @Resource
    private ApiGroupHelper apiGroupHelper;

    /**
     * 连接器API导出文件名模版
     */
    private static final String EXPORT_CONNECTOR_FILE_NAME_TEMPLATE = "connector_export_%s.json";

    /**
     * 开放平台API导出json文件名模版
     */
    private static final String EXPORT_OPEN_API_FILE_NAME_TEMPLATE = "open_api_export_%s.json";

    /**
     * 开放平台API导出markdown文件名模版
     */
    private static final String EXPORT_OPEN_API_MARKDOWN_NAME_TEMPLATE = "open_api_export_md_%s";

    @Override
    public void exportConnectorApiToFile(ExportConnectorParamDTO dto, HttpServletResponse response) {
        List<Long> connectorIdList = dto.getConnectorIdList();
        List<ConnectorDTO> connectorDtoList = queryConnectorList(connectorIdList);
        // 导出文件
        exportJsonFile(response, new ConnectorExportInfoDTO(connectorDtoList), EXPORT_CONNECTOR_FILE_NAME_TEMPLATE);
    }

    @Override
    public void exportOpenApiToFile(ExportOpenApiParamDTO dto, HttpServletResponse response) {
        List<Integer> apiGroupIdList = dto.getApiGroupIdList();
        // 查询API分组下的API信息并根据分组聚合
        List<ApiDetailVo> apiDetailList = dataAssetApiService.queryApiListByGroupIdsAndTypes(apiGroupIdList, dto.getApiTypeList(), true);
        if (CollectionUtil.isEmpty(apiDetailList)) {
            throw new OpenException(MsgCodeEnum.w_no_data_open_api_to_export);
        }
        // 导出文件
        exportJsonFile(response, new OpenApiExportInfoDTO(apiDetailList), EXPORT_OPEN_API_FILE_NAME_TEMPLATE);
    }

    @Override
    public void exportOpenApiToMarkdown(ExportOpenApiParamDTO dto, HttpServletResponse response) {

        if (CollectionUtil.isEmpty(dto.getApiGroupIdList())) {
            List<ApiGroupDTO> defaultApiGroupList = apiGroupService.queryApiGroupAndApi();
            dto.setApiGroupIdList(defaultApiGroupList.stream().map(ApiGroupDTO::getId).collect(Collectors.toList()));
        }

        List<ApiDetailVo> apiDetailList = dataAssetApiService.queryApiListByGroupIdsAndTypes(dto.getApiGroupIdList(), dto.getApiTypeList(), false);
        if (CollectionUtil.isEmpty(apiDetailList)) {
            throw new OpenException(MsgCodeEnum.w_no_data_open_api_to_export);
        }

        Map<Integer, String> apiGroupNameMap = apiGroupHelper.getGroupNameMap(dto.getApiGroupIdList());
        Map<Integer, List<DocumentApiDetailVo>> structureTargetMap = new LinkedHashMap<>();
        List<DocumentApiDetailVo> exportTargetApiList = apiDetailList.stream().filter(detailVo ->
                    DataAssetEnums.PublicEnums.PUBLIC.equals(detailVo.getDataAssetApi().getSecret()) &&
                    DataAssetPublishStatusEnum.PUBLISH.equals(detailVo.getDataAssetApi().getDataAssetPublishStatus()))
                .map(detailVo -> dataAssetApiService.parseApiDetailOfDocument(detailVo)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(exportTargetApiList)) {
            throw new OpenException(MsgCodeEnum.w_no_data_open_api_to_export);
        }
        Map<Integer, List<DocumentApiDetailVo>> structureMap = exportTargetApiList.stream().collect(Collectors.groupingBy(apiDetailVo -> apiDetailVo.getDataAssetApi().getApiGroupId()));
        structureMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> structureTargetMap.put(x.getKey(), x.getValue()));

        List<Element> elements = new ArrayList<>();
        Markdown markdown = new Markdown(String.format(EXPORT_OPEN_API_MARKDOWN_NAME_TEMPLATE, System.currentTimeMillis()), elements);
        DocumentMarkDownUtil.buildDirectory(markdown);
        for (Integer apiGroupId : structureTargetMap.keySet()) {
            DocumentMarkDownUtil.buildApiGroup(apiGroupNameMap.get(apiGroupId), markdown);
            List<DocumentApiDetailVo> currentGroupApi = structureMap.get(apiGroupId).stream().sorted(Comparator.comparing(apiId -> apiId.getDataAssetApi().getDataAssetApiId())).collect(Collectors.toList());
            for (DocumentApiDetailVo documentApiDetailVo : currentGroupApi) {
                DocumentMarkDownUtil.buildApiDetail(documentApiDetailVo, markdown);
            }
        }
        MarkdownWriter.write(markdown, response);
    }

    /**
     * 导出json文件
     *
     * @param response         HttpServletResponse
     * @param obj              导出数据对象
     * @param fileNameTemplate 导出文件名模版
     */
    private void exportJsonFile(HttpServletResponse response, Object obj, String fileNameTemplate) {
        try (OutputStream outputStream = response.getOutputStream()) {
            //fastjson不能够序列化这么多的数据
            byte[] bytes = objectMapper.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(String.format(fileNameTemplate, System.currentTimeMillis()), "UTF-8"));
            response.setHeader("Accept-Ranges", "bytes");
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_export_api_fail);
        }
    }

    /**
     * 根据连接器ID集合，查询连接器相关信息，返回导出集合JSON
     *
     * @return 连接器信息集合
     */
    private List<ConnectorDTO> queryConnectorList(List<Long> connectorIds) {
        // 根据连接器ID集合查询连接器API详情信息，并根据连接器接口分组ID聚合
        List<ConnectorApiDetailDTO> connectorApiDetailList = connectorApiService.queryByConnectorIds(connectorIds);
        Map<Long, List<ConnectorApiDetailDTO>> groupByApiGroupIdMap = connectorApiDetailList.stream().collect(Collectors.groupingBy(x -> x.getConnectorApi().getApiGroupId()));
        // 查询连接器信息
        List<ConnectorDTO> connectorList = connectorService.queryByIds(connectorIds);
        // 查询连接器api分组信息
        List<ConnectorApiGroupDTO> connectorApiGroupList = connectorApiGroupService.queryByConnectorIds(connectorIds);
        Map<Long, List<ConnectorApiGroupDTO>> connectorApiGroupMap = connectorApiGroupList.stream().collect(Collectors.groupingBy(ConnectorApiGroupDTO::getConnectorId));
        // 将连接器api分组、连接器api归类到对应的连接器信息中
        for (ConnectorDTO connectorDTO : connectorList) {
            Long connectorId = connectorDTO.getId();
            List<ConnectorApiGroupDTO> apiGroupList = connectorApiGroupMap.get(connectorId);
            if (CollectionUtil.isNotEmpty(apiGroupList)) {
                for (ConnectorApiGroupDTO connectorApiGroupDTO : apiGroupList) {
                    Long apiGroupId = connectorApiGroupDTO.getId();
                    connectorApiGroupDTO.setApiDetailList(groupByApiGroupIdMap.get(apiGroupId));
                }
            }
            connectorDTO.setConnectorGroupList(apiGroupList);
        }
        return connectorList;
    }

}
