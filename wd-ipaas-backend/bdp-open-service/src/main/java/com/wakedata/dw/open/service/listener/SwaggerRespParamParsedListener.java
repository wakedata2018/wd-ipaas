package com.wakedata.dw.open.service.listener;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.ApiResponseParamPo;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.ApiResponseParamService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.swagger.SwaggerConstants;
import com.wakedata.wd.permission.login.dto.AdminUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author wanghu@wakedata.com
 * @title Swagger响应参数解析完成监听器
 * @date 2021/12/10
 * @since v1.0.0
 */
@Slf4j
@Component
public class SwaggerRespParamParsedListener {

    /*** 顶级类型*/
    private static final int TOP = 0;

    /*** 有响应*/
    private static final int HAD_RESPONSE = 0;

    /*** json {}*/
    private static final String JSON_BRACKETS = "{}";

    /*** 逗号分隔符*/
    private static final char COMMA = ',';

    /**
     * 响应体字段名称
     */
    private static final String COLUM_BODY = "body";

    @Resource
    private ApiResponseParamService apiResponseParamService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @EventListener
    public void onParsedListen(SwaggerRespParamParsedEvent event) {
        final AdminUserDTO currentUser = AuthUtils.getCurrentUserInfo();
        //异步执行
        CompletableFuture.runAsync(() -> {
            //编程式事务
            transactionTemplate.execute(transactionStatus -> {

                List<AbstractApiAttr> httpApiAttrs = event.getHttpApiAttrs();
                if (CollectionUtils.isEmpty(httpApiAttrs)) {
                    log.error("SwaggerRespParamParsed apiAttrs is null");
                    return null;
                }

                for (AbstractApiAttr httpApiAttr : httpApiAttrs) {
                    if (!(httpApiAttr instanceof HttpExternalApiAttr)) {
                        continue;
                    }

                    parsedJsonResultAndSave((HttpExternalApiAttr) httpApiAttr, currentUser);
                }

                return null;
            });

        });

    }

    private void parsedJsonResultAndSave(HttpExternalApiAttr httpApiAttr, AdminUserDTO currentUser) {
        String resultExample = httpApiAttr.getResponseResult();
        if (StringUtils.isBlank(resultExample)) {
            return;
        }

        Integer apiId = httpApiAttr.getApiId();
        if (Objects.isNull(apiId)) {
            return;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(resultExample);

            //删除旧的数据
            ApiResponseParamPo query = new ApiResponseParamPo();
            query.setDataAssetId(apiId);
            apiResponseParamService.delete(query);

            buildResponse(httpApiAttr.getResponseJsonSchema(), currentUser.getAccount(), httpApiAttr.getApiId(), TOP, currentUser.getTenantId());
//            parseJson(
//                jsonObject, currentUser.getAccount(), httpApiAttr.getApiId(), TOP, currentUser.getTenantId());
        } catch (Exception ex) {
            log.error("SwaggerRespParamParsedListener parse json error", ex);
        }

    }

    /**
     * 组装响应体并入库
     *
     * @param responseJsonSchema responseJsonSchema
     * @param creatUserId        creatUserId
     * @param apiId              apiId
     * @param parentId           parentId
     * @param tenantId           tenantId
     */
    private void buildResponse(String responseJsonSchema, String creatUserId, Integer apiId, Integer parentId, Long tenantId) {
        ApiResponseParamPo param = new ApiResponseParamPo();
        param.setBusinessType(DataAssetEnums.ApiResponseBusinessType.HTTP_RESULT);
        param.setUpdateBy(creatUserId);
        param.setCreateBy(creatUserId);

        param.setAssetColumns(COLUM_BODY);
        param.setDataAssetId(apiId);
        param.setParentId(parentId);
        param.setLesseeId(tenantId);

        param.setType(HttpParamKind.BODY);
        param.setResponded(HAD_RESPONSE);
        param.setAssetDataType(SwaggerConstants.JSON_VAL);
        param.setResponsePostData(responseJsonSchema);
        param.setIsSchema((StringUtils.isNotEmpty(responseJsonSchema) && JSONUtil.isJson(responseJsonSchema)) ? Boolean.TRUE : Boolean.FALSE);

        apiResponseParamService.add(param);
    }

    private void parseJson(JSONObject jsonObject, String creatUserId, Integer apiId, Integer parentId, Long tenantId) {
        if (MapUtils.isEmpty(jsonObject)) {
            return;
        }

        for (Entry<String, Object> entry : jsonObject.entrySet()) {
            ApiResponseParamPo param = new ApiResponseParamPo();
            param.setBusinessType(DataAssetEnums.ApiResponseBusinessType.HTTP_RESULT);
            param.setUpdateBy(creatUserId);
            param.setCreateBy(creatUserId);

            param.setAssetColumns(entry.getKey());
            param.setDataAssetId(apiId);
            param.setParentId(parentId);
            param.setLesseeId(tenantId);

            param.setType(HttpParamKind.BODY);
            param.setResponded(HAD_RESPONSE);

            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                param.setAssetDataType(SwaggerConstants.OBJECT);
                //入库
                apiResponseParamService.add(param);
                //递归解析
                parseJson((JSONObject) value, creatUserId, apiId, param.getId(), tenantId);
            } else if (value instanceof JSONArray) {
                param.setAssetDataType(SwaggerConstants.ARRAY);
                //入库
                apiResponseParamService.add(param);
                //递归解析
                parseJson((JSONObject) ((JSONArray) value).get(0), creatUserId, apiId, param.getId(), tenantId);
            } else if (value instanceof String) {
                //字段类型
                String[] splitResult = StringUtils.split((String) value, COMMA);
                param.setAssetDataType(JSON_BRACKETS.equals(splitResult[0]) ? SwaggerConstants.OBJECT : splitResult[0]);

                //描述
                if (splitResult.length > 1 && StringUtils.isNotBlank(splitResult[1])) {
                    param.setDescription(splitResult[1]);
                }
                //入库
                apiResponseParamService.add(param);
            }

        }

    }

}
