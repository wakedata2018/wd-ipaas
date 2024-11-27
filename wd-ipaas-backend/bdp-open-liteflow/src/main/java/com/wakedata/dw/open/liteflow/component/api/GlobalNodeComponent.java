package com.wakedata.dw.open.liteflow.component.api;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.constant.ThirdAuthConstant;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.model.api.flow.operator.global.GlobalOperator;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.impl.api.GlobalParamServiceImpl;
import com.wakedata.dw.open.service.vo.AppAccessAuthConfig;

import java.util.Objects;

/**
 * author Wangchensheng@wakedata.com
 * date 2023年02月22日 11:08:55
 */
public class GlobalNodeComponent {

    public static void storeGlobalParams(NodeRunTimeContext nodeRunTimeContext) {
        JSONObject globalParams = new JSONObject();
        JSONObject wdInfoParams = buildWdInfoParams(nodeRunTimeContext.getAppId(), (GlobalParamServiceImpl) nodeRunTimeContext.getGlobalParamService());
        globalParams.put(HttpParamKind.BODY.name(), wdInfoParams);
        nodeRunTimeContext.getApiFlowSlot().storeStartResult(GlobalOperator.GLOBAL_OPERATOR_ID, globalParams);
    }

    /**
     * 构建惟客云应用信息
     */
    private static JSONObject buildWdInfoParams(Integer appId, GlobalParamServiceImpl globalParamService) {
        JSONObject bodyParams = new JSONObject();
        AppAccessAuthConfig appAccessAuthConfig = globalParamService.queryAppInfoByAppId(appId);

        if (Objects.nonNull(appAccessAuthConfig)) {
            bodyParams.put(ThirdAuthConstant.WdAuthParamEnum.COMMON_TENANT_ID.getCode(), appAccessAuthConfig.getTenantId());
            bodyParams.put(ThirdAuthConstant.WdAuthParamEnum.COMMON_APP_BU_ID.getCode(), appAccessAuthConfig.getAppBuId());
        }
        return bodyParams;
    }

}
