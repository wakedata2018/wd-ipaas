package com.wakedata.dw.open.service.impl.swagger.helper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.SwaggerApiImportStatusEnum;
import com.wakedata.dw.open.enums.SwaggerApiParseStatusEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.swagger.SwaggerApiDO;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.dto.SwaggerApiDTO;
import com.wakedata.dw.open.service.vo.ApiDetailVo;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月02日 14:49:18
 */
public class TemporaryApiParseUtil {

    /**
     * 默认请求体JsonSchema
     */
    private static final String DEFAULT_REQUEST_JSON_SCHEMA = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{},\"rootRequired\":false}}";
    public static final String DATA_ASSET_API_KEY_STRING = "String";
    public static final String DATA_ASSET_API_KEY_JSON_OBJECT = "JSONObject";

    /**
     * 初始化临时Api信息
     *
     * @param apiDetailVo   apiDetailVo
     * @param swaggerApiDTO swaggerApiDTO
     * @return SwaggerApiDO
     */
    public static SwaggerApiDO initTemporaryApiDO(ApiDetailVo apiDetailVo, SwaggerApiDTO swaggerApiDTO) {
        SwaggerApiDO swagger = new SwaggerApiDO();

        swagger.setSwaggerId(swaggerApiDTO.getSwaggerId());
        swagger.setApiName(apiDetailVo.getDataAssetApi().getApiName());
        swagger.setApiType(apiDetailVo.getDataAssetApi().getApiType().getValue());
        swagger.setDataAssetApiMethod(apiDetailVo.getDataAssetApi().getDataAssetApiMethod());
        swagger.setApiDescription(apiDetailVo.getDataAssetApi().getApiDescription());
        swagger.setApiGroupId(apiDetailVo.getDataAssetApi().getApiGroupId());
        swagger.setCreateTime(new Date());
        swagger.setCreateBy(swaggerApiDTO.getInCharge());
        swagger.setApiInfo(buildApiInfo(apiDetailVo));
        swagger.setJsonSchema(buildRequestBody(apiDetailVo.getParameters()));
        swagger.setParseStatus(SwaggerApiParseStatusEnum.SUCCESS_PARSE.getValue());
        swagger.setImportStatus(SwaggerApiImportStatusEnum.UN_IMPORT.getValue());

        return swagger;
    }

    /**
     * 构造新增ApiDetailVo对象
     *
     * @param swaggerApiDTO   临时API
     * @param respMappingRule 响应参数映射规则
     * @param inCharge        负责人
     * @return ApiDetailVo对象
     */
    public static ApiDetailVo initApiDetailVo(SwaggerApiDTO swaggerApiDTO, Integer respMappingRule, String inCharge) {
        // 补充api信息表
        ApiDetailVo apiDetailVo = JSONObject.parseObject(swaggerApiDTO.getApiInfo(), ApiDetailVo.class);
        apiDetailVo.getDataAssetApi().setDataAssetApiMethod(apiDetailVo.getDataAssetApi().getDataAssetApiMethod().replaceAll("\\{|\\}", DwOpenConstant.BANK_STRING));
        apiDetailVo.getDataAssetApi().setApiGroupId(swaggerApiDTO.getApiGroupId());
        apiDetailVo.getDataAssetApi().setInCharge(inCharge);
        apiDetailVo.getDataAssetApi().setRespMappingRule(respMappingRule);
        apiDetailVo.getDataAssetApi().setApiAttr(convertApiInfo(swaggerApiDTO.getApiInfo(), HttpExternalApiAttr.class, DwOpenConstant.API_ATTR));
        apiDetailVo.getDataAssetApi().setSecret(DataAssetEnums.PublicEnums.PUBLIC);

        return apiDetailVo;
    }

    /**
     * 构造更新ApiDetailVo对象
     *
     * @param apiDetailVo     需要更新的apiDetailVo对象
     * @param swaggerApiDTO   重新拉取的Swagger临时API
     * @param respMappingRule 响应参数映射规则
     * @param inCharge        负责人
     */
    public static void buildConvertApiDetailVo(ApiDetailVo apiDetailVo, SwaggerApiDTO swaggerApiDTO, Integer respMappingRule, String inCharge) {
        apiDetailVo.getDataAssetApi().setInCharge(inCharge);
        apiDetailVo.getDataAssetApi().setRespMappingRule(respMappingRule);
        apiDetailVo.getDataAssetApi().setSecret(DataAssetEnums.PublicEnums.PUBLIC);
        apiDetailVo.getDataAssetApi().setApiDescription(swaggerApiDTO.getApiDescription());
        apiDetailVo.getDataAssetApi().setApiAttr(TemporaryApiParseUtil.convertApiInfo(swaggerApiDTO.getApiInfo(), HttpExternalApiAttr.class, DwOpenConstant.API_ATTR));
        apiDetailVo.getDataAssetApi().setReqMethod(DataAssetEnums.ReqMethod.valueOf(
                                                 TemporaryApiParseUtil.convertApiInfo(swaggerApiDTO.getApiInfo(), String.class, DwOpenConstant.REQ_METHOD)));
        apiDetailVo.setParameters(JSONObject.parseObject(swaggerApiDTO.getApiInfo(), ApiDetailVo.class).getParameters());
    }

    /**
     * 解析指定对象
     *
     * @param apiInfo     大json(apiDetailVo)
     * @param targetClass 指定对象类型
     * @param targetKey   DataAssetApiPo中某个key
     * @param <T>         指定类型
     * @return 指定类型的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertApiInfo(String apiInfo, Class<T> targetClass, String targetKey) {
        return Optional.ofNullable(apiInfo)
                .map(JSONObject::parseObject)
                .map(apiInfoJson -> apiInfoJson.getJSONObject(DwOpenConstant.DATA_ASSET_API).get(targetKey))
                .map(target -> {
                    String type = target.getClass().getSimpleName();
                    switch (type) {
                        case DATA_ASSET_API_KEY_STRING:
                            return (T) target;
                        case DATA_ASSET_API_KEY_JSON_OBJECT:
                            return JSONObject.parseObject(target.toString(), targetClass);
                        default:
                            return null;
                    }
                })
                .orElse(null);
    }

    /**
     * 根据模版路径解析json文件
     */
    public static String parseJsonByTempFilepath(String tempFilePath) {
        File tempFile = new File(tempFilePath);
        if (!FileUtil.exist(tempFile)) {
            FileUtil.del(tempFilePath);
            throw new OpenException(MsgCodeEnum.w_file_read_error);
        }
        String text = new FileReader(tempFilePath).readString();
        if (!JSONUtil.isJson(text)) {
            FileUtil.del(tempFilePath);
            throw new OpenException(MsgCodeEnum.w_import_text_is_not_json);
        }
        return text;
    }

    /**
     * 获取条件为body的jsonSchema
     */
    private static String buildRequestBody(List<ApiConditionPo> parameters) {

        // 防止出现存在空body时请求体不入库的情况
        return parameters.stream()
                .filter(parameter -> Objects.equals(parameter.getHttpParamKind(), HttpParamKind.BODY))
                .findFirst().map(ApiConditionPo::getJsonSchema).orElse(DEFAULT_REQUEST_JSON_SCHEMA);
    }

    /**
     * 构建ApiDetailVo对象并转换为json
     */
    private static String buildApiInfo(ApiDetailVo apiDetailVo) {

        // 防止出现存在空body时请求体不入库的情况
        List<ApiConditionPo> parameters = apiDetailVo.getParameters();
        parameters.stream()
                .filter(parameter -> Objects.equals(parameter.getHttpParamKind(), HttpParamKind.BODY))
                .filter(parameter -> Objects.isNull(parameter.getJsonSchema()))
                .forEach(parameter -> parameter.setJsonSchema(DEFAULT_REQUEST_JSON_SCHEMA));

        return JSONObject.toJSONString(apiDetailVo);
    }
}
