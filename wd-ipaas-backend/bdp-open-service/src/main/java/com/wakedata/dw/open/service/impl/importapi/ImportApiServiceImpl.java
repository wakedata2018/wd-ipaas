package com.wakedata.dw.open.service.impl.importapi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.SwaggerApiImportStatusEnum;
import com.wakedata.dw.open.enums.SwaggerApiParseStatusEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.swagger.SwaggerApiDO;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.connector.ConnectorApiGroupService;
import com.wakedata.dw.open.service.connector.ConnectorApiService;
import com.wakedata.dw.open.service.connector.ConnectorGroupService;
import com.wakedata.dw.open.service.connector.ConnectorService;
import com.wakedata.dw.open.service.connector.dto.*;
import com.wakedata.dw.open.service.exportapi.dto.openapi.ImportOpenApiResultDTO;
import com.wakedata.dw.open.service.impl.swagger.helper.TemporaryApiParseUtil;
import com.wakedata.dw.open.service.importapi.ImportApiService;
import com.wakedata.dw.open.service.importapi.dto.connector.ConnectorImportInfoDTO;
import com.wakedata.dw.open.service.importapi.dto.openapi.OpenApiImportInfoDTO;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 导入API业务接口实现类
 *
 * @author wujunqiang
 * @since 2023/2/3 11:55
 */
@Slf4j
@Service
public class ImportApiServiceImpl implements ImportApiService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiGroupService apiGroupService;

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectorApiService connectorApiService;

    @Autowired
    private DataAssetApiService dataAssetApiService;

    @Autowired
    private ConnectorGroupService connectorGroupService;

    @Autowired
    private ConnectorApiGroupService connectorApiGroupService;

    @Override
    public ImportOpenApiResultDTO importOpenApiFromFile(String tempFilePath, Integer apiGroupId, IpaasUserInfo userInfo) {
        String jsonText = readImportJsonTextFromFile(tempFilePath);
        OpenApiImportInfoDTO importInfo;
        try {
            importInfo = objectMapper.readValue(jsonText, OpenApiImportInfoDTO.class);
        } catch (JsonProcessingException e) {
            FileUtil.del(tempFilePath);
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_import_api_text_data_structure_wrong);
        }
        List<ApiDetailVo> apiDetailList = importInfo.getApiDetailList();
        if (CollectionUtil.isEmpty(apiDetailList)) {
            FileUtil.del(tempFilePath);
            throw new OpenException(MsgCodeEnum.w_parse_import_data_fail);
        }
        String groupPath = apiGroupService.checkForExist(apiGroupId).getGroupPath();
        AtomicInteger successTotal = new AtomicInteger();
        AtomicInteger failTotal = new AtomicInteger();
        for (ApiDetailVo apiDetailVo : apiDetailList) {
            if (apiDetailVo.getResults() == null) {
                apiDetailVo.setResults(new ArrayList<>());
            }
            DataAssetApiPo dataAssetApiPo = apiDetailVo.getDataAssetApi();
            try {
                dataAssetApiService.checkApiMethodExists(dataAssetApiPo);
                initImportOpenApiParam(dataAssetApiPo, userInfo, groupPath, apiGroupId);
                dataAssetApiService.addApi(apiDetailVo);
                successTotal.incrementAndGet();
            } catch (OpenException e) {
                failTotal.incrementAndGet();
            }
        }
        FileUtil.del(tempFilePath);
        return new ImportOpenApiResultDTO(apiDetailList.size(), successTotal.get(), failTotal.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportOpenApiResultDTO importConnectorFromFile(String tempFilePath, Long connectorGroupId, IpaasUserInfo userInfo) {
        String jsonText = readImportJsonTextFromFile(tempFilePath);
        ConnectorImportInfoDTO importInfo;
        try {
            importInfo = objectMapper.readValue(jsonText, ConnectorImportInfoDTO.class);
        } catch (JsonProcessingException e) {
            FileUtil.del(tempFilePath);
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_import_connector_data_structure_wrong);
        }
        List<ConnectorDTO> connectorList = importInfo.getConnectorList();
        if (CollectionUtil.isEmpty(connectorList)) {
            FileUtil.del(tempFilePath);
            throw new OpenException(MsgCodeEnum.w_parse_import_data_fail);
        }
        // 校验连接器分类是否存在
        ConnectorGroupDTO connectorGroupDTO = connectorGroupService.checkForExist(connectorGroupId);
        Long groupId = connectorGroupDTO.getId();
        Date now = new Date();
        AtomicInteger successTotal = new AtomicInteger();
        AtomicInteger failTotal = new AtomicInteger();
        for (ConnectorDTO connectorDTO : connectorList) {
            // 保存连接器基础信息、环境地址、鉴权参数
            initImportConnectorDto(connectorDTO, groupId);
            if (!connectorService.checkConnectorNameExist(connectorDTO)) {
                failTotal.incrementAndGet();
                continue;
            }
            Long connectorId = connectorService.create(connectorDTO).getData();
            successTotal.incrementAndGet();
            // 保存连接器接口分组
            List<ConnectorApiGroupDTO> apiGroupList = connectorDTO.getConnectorGroupList();
            if (CollectionUtil.isEmpty(apiGroupList)) {
                continue;
            }
            for (ConnectorApiGroupDTO apiGroupDTO : apiGroupList) {
                apiGroupDTO.setId(null);
                apiGroupDTO.setConnectorId(connectorId);
                Long apiGroupId = connectorApiGroupService.createOfModify(apiGroupDTO).getData();
                // 保存连接器API
                List<ConnectorApiDetailDTO> apiDetailList = apiGroupDTO.getApiDetailList();
                if (CollectionUtil.isEmpty(apiDetailList)) {
                    continue;
                }
                for (ConnectorApiDetailDTO apiDetailDTO : apiDetailList) {
                    initImportConnectorApiDetailDto(apiDetailDTO, apiGroupId, connectorId, userInfo, now);
                    connectorApiService.create(apiDetailDTO);
                }
            }
        }
        FileUtil.del(tempFilePath);
        return new ImportOpenApiResultDTO(connectorList.size(), successTotal.get(), failTotal.get());
    }

    /**
     * 初始化导入连接器数据
     *
     * @param connectorDTO 连接器DTO
     * @param groupId      连接器分组ID
     */
    private void initImportConnectorDto(ConnectorDTO connectorDTO, Long groupId) {
        connectorDTO.setId(null);
        connectorDTO.setGroupId(groupId);
        List<ConnectorEnvironmentAddressDTO> environmentAddressList = connectorDTO.getConnectorEnvironmentAddressDTOList();
        if (CollectionUtil.isNotEmpty(environmentAddressList)) {
            environmentAddressList.forEach(x -> {
                x.setId(null);
                x.setConnectorId(null);
            });
        }
        List<ConnectorParamsDTO> connectorParamList = connectorDTO.getConnectorParamsDTOList();
        if (CollectionUtil.isNotEmpty(connectorParamList)) {
            connectorParamList.forEach(x -> {
                x.setId(null);
                x.setConnectorId(null);
            });
        }
    }

    /**
     * 初始化导入连接器API数据
     *
     * @param apiDetailDto 连接器API详情DTO
     * @param apiGroupId   连接器api分组id
     * @param connectorId  连接器id
     * @param userInfo     用户上下文
     * @param createTime   创建时间
     */
    private void initImportConnectorApiDetailDto(ConnectorApiDetailDTO apiDetailDto, Long apiGroupId, Long connectorId
            , IpaasUserInfo userInfo, Date createTime) {
        ConnectorApiDTO connectorApi = apiDetailDto.getConnectorApi();
        connectorApi.setId(null);
        connectorApi.setApiGroupId(apiGroupId);
        connectorApi.setConnectorId(connectorId);
        connectorApi.setCreateBy(userInfo.getUserIdentification());
        connectorApi.setUpdateBy(userInfo.getUserIdentification());
        connectorApi.setLesseeId(userInfo.getLesseeId());
        connectorApi.setCreateTime(createTime);
        connectorApi.setUpdateTime(createTime);
        apiDetailDto.setRequestParams(apiDetailDto.getRequestParams() == null ? new ArrayList<>() : apiDetailDto.getRequestParams());
        apiDetailDto.setResponseParams(apiDetailDto.getResponseParams() == null ? new ArrayList<>() : apiDetailDto.getResponseParams());
    }

    /**
     * 初始化需要导入的API参数
     *
     * @param dataAssetApiPo DataAssetApiPo
     * @param userInfo       用户上下文
     * @param groupPath      导入的分组路径
     * @param apiGroupId     导入的分组id
     */
    private void initImportOpenApiParam(DataAssetApiPo dataAssetApiPo, IpaasUserInfo userInfo, String groupPath, Integer apiGroupId) {
        dataAssetApiPo.setInCharge(userInfo.getName());
        dataAssetApiPo.setLesseeId(userInfo.getLesseeId());
        dataAssetApiPo.setDataAssetApiId(null);
        dataAssetApiPo.setDataAssetApiMethod(groupPath + dataAssetApiPo.getDataAssetApiMethod());
        dataAssetApiPo.setApiGroupId(apiGroupId);
    }

    /**
     * 读取上传的文件，获取导入信息
     *
     * @param tempFilePath 已经上传的导入文件路径
     * @return 文件文本
     */
    private String readImportJsonTextFromFile(String tempFilePath) {
        return TemporaryApiParseUtil.parseJsonByTempFilepath(tempFilePath);
    }

    /**
     * 构建api导入结果数据
     *
     * @param apiDetailVo API信息
     * @param parseStatus 解析结果：1 成功 2 失败，对应枚举类SwaggerApiImportStatusEnum
     * @param errorDetail 错误详情，导入成功传null
     * @param apiGroupId  导入的api分组id
     * @return SwaggerApiDO
     */
    private SwaggerApiDO buildSwaggerApiDo(ApiDetailVo apiDetailVo, Integer parseStatus, String errorDetail, Integer apiGroupId) {
        SwaggerApiDO swaggerApiDo = new SwaggerApiDO();
        DataAssetApiPo dataAssetApi = apiDetailVo.getDataAssetApi();
        swaggerApiDo.setApiName(dataAssetApi.getApiName());
        swaggerApiDo.setApiType(dataAssetApi.getApiType().getValue());
        swaggerApiDo.setDataAssetApiMethod(dataAssetApi.getDataAssetApiMethod());
        swaggerApiDo.setApiDescription(dataAssetApi.getApiDescription());
        swaggerApiDo.setParseStatus(parseStatus);
        if (SwaggerApiParseStatusEnum.FAIL_PARSE.getValue().equals(parseStatus)) {
            swaggerApiDo.setErrorDetail(errorDetail);
        }
        swaggerApiDo.setApiInfo(JSONUtil.toJsonStr(apiDetailVo));
        swaggerApiDo.setApiGroupId(apiGroupId);
        swaggerApiDo.setImportStatus(SwaggerApiImportStatusEnum.SUCCESS_IMPORT.getValue());
        Date now = new Date();
        swaggerApiDo.setCreateTime(now);
        swaggerApiDo.setUpdateTime(now);
        String inCharge = dataAssetApi.getInCharge();
        swaggerApiDo.setCreateBy(inCharge);
        swaggerApiDo.setUpdateBy(inCharge);
        String jsonSchema = apiDetailVo.getParameters().stream()
                .filter(parameter -> Objects.equals(parameter.getHttpParamKind(), HttpParamKind.BODY))
                .findFirst().map(ApiConditionPo::getJsonSchema).orElse(null);
        swaggerApiDo.setJsonSchema(jsonSchema);
        return swaggerApiDo;
    }

}
