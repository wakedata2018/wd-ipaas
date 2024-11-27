package com.wakedata.dw.open.service.impl.swagger.helper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.enums.*;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.swagger.SwaggerApiDO;
import com.wakedata.dw.open.model.swagger.SwaggerInfoPo;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.SwaggerApiDTO;
import com.wakedata.dw.open.service.swagger.SwaggerApiService;
import com.wakedata.dw.open.service.swagger.SwaggerInfoService;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月03日 17:55:32
 */
@Component
public class SwaggerInfoHelper {

    @Resource
    private SwaggerInfoService swaggerInfoService;

    @Resource
    private SwaggerApiService swaggerApiService;

    @Resource
    private DataAssetApiService dataAssetApiService;


    /**
     * 导入主表时，根据临时APIId获取临时API并校验相关参数
     *
     * @param id            临时API主键id
     * @param swaggerApiMap swaggerApiMap
     * @return SwaggerApiDTO
     */
    public SwaggerApiDTO checkSwaggerApiDtoById(Integer id, Map<Integer, SwaggerApiDTO> swaggerApiMap, Boolean isConvert) {
        SwaggerApiDTO swaggerApiInfo = swaggerApiMap.get(id);
        if (Objects.isNull(swaggerApiInfo)) {
            throw new OpenException(MsgCodeEnum.w_swagger_api_is_not_empty);
        }

        String apiInfo = swaggerApiInfo.getApiInfo();
        if (StringUtils.isEmpty(apiInfo)) {
            throw new OpenException(MsgCodeEnum.w_not_exists_api);
        }

        Integer parseStatus = swaggerApiInfo.getParseStatus();
        if (Objects.equals(parseStatus, SwaggerApiParseStatusEnum.FAIL_PARSE.getValue())) {
            throw new OpenException(MsgCodeEnum.w_swagger_api_parse_is_fail);
        }

        // 覆盖导入无需校验导入结果 普通导入需要校验导入结果
        if (isConvert) {
            return swaggerApiInfo;
        }

        Integer importStatus = swaggerApiInfo.getImportStatus();
        if (!Objects.equals(importStatus, SwaggerApiImportStatusEnum.UN_IMPORT.getValue())) {
            throw new OpenException(MsgCodeEnum.w_swagger_api_import_is_fail);
        }
        return swaggerApiInfo;
    }

    /**
     * 将swaggerApi接口导入正式表(新增)
     *
     * @param apiDetailVo            swaggerApi的全部信息
     * @param swaggerApiId           swaggerApiID
     * @param temporaryApiImportEnum 导入类型
     * @return true/false
     */
    public SwaggerApiDO addApiFromSwagger(ApiDetailVo apiDetailVo, Integer swaggerApiId, TemporaryApiImportEnum temporaryApiImportEnum) {
        SwaggerApiDO swaggerApiDO = new SwaggerApiDO();

        addOrUpdateApiAndBuildDesc(apiDetailVo, swaggerApiDO, temporaryApiImportEnum);
        swaggerApiDO.setId(swaggerApiId);
        swaggerApiDO.setUpdateBy(apiDetailVo.getDataAssetApi().getInCharge());

        // 同步swaggerApi的导入状态
        swaggerApiService.updateImportStatus(swaggerApiDO);
        return swaggerApiDO;
    }

    /**
     * 将swaggerApi接口导入正式表(更新)
     *
     * @param apiDetailVo  apiDetailVo
     * @param swaggerApiId swaggerId
     * @return true/false
     */
    public SwaggerApiDO updateApiFromSwagger(ApiDetailVo apiDetailVo, Integer swaggerApiId) {
        SwaggerApiDO swaggerApiDO = new SwaggerApiDO();

        addOrUpdateApiAndBuildDesc(apiDetailVo, swaggerApiDO, TemporaryApiImportEnum.CONVERT_IMPORT_UPDATE);
        swaggerApiDO.setId(swaggerApiId);
        swaggerApiDO.setUpdateBy(apiDetailVo.getDataAssetApi().getInCharge());

        // 同步swaggerApi的导入状态
        swaggerApiService.updateImportStatus(swaggerApiDO);
        return swaggerApiDO;
    }

    /**
     * 构建临时API的导入状态和导入描述
     * 并新增/更新临时API
     */
    public void addOrUpdateApiAndBuildDesc(ApiDetailVo apiDetailVo, SwaggerApiDO swaggerApiDO, TemporaryApiImportEnum type) {
        switch (type) {
            case IMPORT_ADD:
                Boolean isApiNameExit = dataAssetApiService.checkApiName(apiDetailVo.getDataAssetApi().getApiName());
                Boolean isApiPathExit = dataAssetApiService.checkApiPath(apiDetailVo.getDataAssetApi().getDataAssetApiMethod());
                accept(apiDetailVo, swaggerApiDO, !isApiNameExit && !isApiPathExit,
                        vo -> dataAssetApiService.addApi(vo),
                        MsgCodeEnum.w_swagger_api_import_add_success.getDesc(),
                        isApiNameExit ? MsgCodeEnum.w_swagger_api_import_add_fail_api_name.getDesc() : MsgCodeEnum.w_swagger_api_import_add_fail_api_path.getDesc());
                break;
            case CONVERT_IMPORT_ADD:
                Boolean isNameExit = dataAssetApiService.checkApiName(apiDetailVo.getDataAssetApi().getApiName());
                Boolean isPathExit = dataAssetApiService.checkApiPath(apiDetailVo.getDataAssetApi().getDataAssetApiMethod());
                accept(apiDetailVo, swaggerApiDO, !isNameExit && !isPathExit,
                        vo -> dataAssetApiService.addApi(vo),
                        MsgCodeEnum.w_swagger_api_convert_import_add_success.getDesc(),
                        isNameExit ? MsgCodeEnum.w_swagger_api_convert_import_add_error_api_name.getDesc() : MsgCodeEnum.w_swagger_api_convert_import_add_error_api_path.getDesc());
                break;
            case CONVERT_IMPORT_UPDATE:
                accept(apiDetailVo, swaggerApiDO, Objects.equals(apiDetailVo.getDataAssetApi().getDataAssetPublishStatus(), DataAssetPublishStatusEnum.UN_PUBLISH),
                        vo -> dataAssetApiService.updateApi(vo),
                        MsgCodeEnum.w_swagger_api_convert_import_update_success.getDesc(),
                        MsgCodeEnum.w_swagger_api_convert_import_update_error.getDesc());
                break;
            default:
                throw new OpenException(MsgCodeEnum.w_empty_argument);
        }
    }

    /**
     * 检查是否存在
     */
    public SwaggerInfoPo checkForExist(Integer swaggerInfoId) {
        SwaggerInfoPo result = swaggerInfoService.detail(swaggerInfoId);
        if (Objects.isNull(result)) {
            throw new OpenException(MsgCodeEnum.w_swagger_info_not_exists);
        }
        return result;
    }

    /**
     * 获取Swagger Api解析结果
     */
    public void getSwaggerApiCount(Integer swaggerInfoId, SwaggerInfoPo swaggerInfoPo) {
        if (Objects.isNull(swaggerInfoPo)) {
            throw new OpenException(MsgCodeEnum.w_swagger_info_not_exists);
        }

        WeekendSqls<SwaggerApiDO> weekendSqls = WeekendSqls.custom();
        weekendSqls.andEqualTo(SwaggerApiDO::getSwaggerId, swaggerInfoId);
        Example example = Example.builder(SwaggerApiDO.class)
                .select("id", "errorDetail")
                .where(weekendSqls).build();
        List<SwaggerApiDTO> swaggerApiDTOList = swaggerApiService.selectByExample(example);
        if (CollectionUtil.isEmpty(swaggerApiDTOList)) {
            swaggerInfoPo.setApiSuccessAmount(SwaggerInfoPo.DEFAULT_API_AMOUNT);
            swaggerInfoPo.setApiAmount(SwaggerInfoPo.DEFAULT_API_AMOUNT);
            return;
        }

        List<SwaggerApiDTO> successApiList = swaggerApiDTOList.stream()
                .filter(swaggerApiDTO -> StringUtils.isEmpty(swaggerApiDTO.getErrorDetail())).collect(Collectors.toList());
        swaggerInfoPo.setApiSuccessAmount(successApiList.size());
        swaggerInfoPo.setApiAmount(swaggerApiDTOList.size());
    }

    /**
     * SwaggerInfo的导入状态
     */
    public Integer executeStatusHandle(List<SwaggerApiDTO> swaggerApiDTOList, List<Integer> idList) {
        // 导入前:解析成功的SwaggerApi数量
        List<SwaggerApiDTO> unImportList = swaggerApiDTOList.stream()
                .filter(swaggerApiDTO -> ObjectUtil.equal(swaggerApiDTO.getImportStatus(), SwaggerApiImportStatusEnum.UN_IMPORT.getValue()) &&
                        ObjectUtil.equal(swaggerApiDTO.getParseStatus(), SwaggerApiParseStatusEnum.SUCCESS_PARSE.getValue()))
                .collect(Collectors.toList());

        boolean isPartial = unImportList.size() > idList.size();
        return isPartial ? SwaggerExecuteStatusEnum.PARTIAL_EXECUTE.getValue() : SwaggerExecuteStatusEnum.ALL_EXECUTE.getValue();
    }

    private void accept(ApiDetailVo apiDetailVo, SwaggerApiDO swaggerApiDO, boolean isAddOrUpdate,
                        Function<ApiDetailVo, Boolean> function,
                        String successDesc, String errorDesc) {
        if (isAddOrUpdate) {
            Boolean isSuccess = function.apply(apiDetailVo);
            swaggerApiDO.setImportStatus((isSuccess ? SwaggerApiImportStatusEnum.SUCCESS_IMPORT : SwaggerApiImportStatusEnum.FAIL_IMPORT).getValue());
            swaggerApiDO.setErrorDetail(isSuccess ? successDesc : MsgCodeEnum.w_dao_execute_error.getDesc());
            return;
        }
        swaggerApiDO.setImportStatus(SwaggerApiImportStatusEnum.FAIL_IMPORT.getValue());
        swaggerApiDO.setErrorDetail(errorDesc);
    }

}
