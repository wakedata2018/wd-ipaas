package com.wakedata.dw.open.service.impl.api.strategy.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.exception.OperatorOpenException;
import com.wakedata.dw.open.liteflow.LiteflowApiInvokeService;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.*;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.DataApiAccessServiceImpl;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import com.wakedata.dw.open.service.impl.api.strategy.AbstractApiInvokeStrategy;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.ColumnTypeConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZhangXueJun
 * @title LiteFlowApiInvokeStrategy
 * @date 2021/5/28 10:28
 * @projectName dw-open
 * @description
 */
@Slf4j
public class LiteFlowApiInvokeStrategy<T> extends AbstractApiInvokeStrategy<T> {

    private DataApiAccessServiceImpl dataApiAccessService;

    public LiteFlowApiInvokeStrategy(
            DataApiAccessServiceImpl dataApiAccessService,
            DataAssetEnums.DataApiType apiKind,
            DataAssetApiPo dataAssetApi,
            Integer appId,
            JSONObject params,
            HashSet<String> accessRuleFields) {
        super(apiKind, dataAssetApi, appId, params, accessRuleFields);
        this.dataApiAccessService = dataApiAccessService;
    }

    @Override
    public T invoke() {
        ApiFlowAttr apiAttr = (ApiFlowAttr) dataAssetApi.getApiAttr();
        return (T)GlobalApplicationContext.getBean(LiteflowApiInvokeService.class).invokeApi(apiAttr, dataAssetApi.getDataAssetApiId(), appId,requestParams);
    }

}