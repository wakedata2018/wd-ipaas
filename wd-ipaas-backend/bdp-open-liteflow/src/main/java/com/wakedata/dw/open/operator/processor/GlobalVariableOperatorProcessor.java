package com.wakedata.dw.open.operator.processor;

import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.api.StartNodeComponent;
import com.wakedata.dw.open.liteflow.globalparams.GlobalParam;
import com.wakedata.dw.open.liteflow.globalparams.GlobalParamDescription;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.global.GlobalOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import com.wakedata.dw.open.service.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * author Wangchensheng@wakedata.com
 * date 2023年02月21日 19:49:32
 */
@Service
public class GlobalVariableOperatorProcessor extends BaseOperatorProcessor {

    private static final String JSON_SCHEMA_OBJECT_ROOT = "{\"root\":{\"type\":\"object\",\"properties\":{},\"name\":\"root\"}}";
    private static final String GLOBAL_PARAM_NAME = "name";
    private static final String GLOBAL_PARAM_TYPE = "type";
    private static final String GLOBAL_PARAM_DESCRIPTION = "description";

    @Override
    protected String getOperatorClassName() {
        return GlobalOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return null;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildGlobalOperatorTemplate(operateId, operatorName);
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return StartNodeComponent.class;
    }

    /**
     * 填充信息到LiteFlowAllOperatorTemplateDTO
     */
    private LiteFlowAllOperatorTemplateDTO buildGlobalOperatorTemplate(String operateId, String operatorName) {
        JSONObject jsonObject = JSONObject.parseObject(JSON_SCHEMA_OBJECT_ROOT);
        JSONObject properties = jsonObject.getJSONObject(JsonUtils.SCHEMA_ROOT).getJSONObject(JsonUtils.SCHEMA_PROPERTIES);

        Class<GlobalParam> globalParamClass = GlobalParam.class;
        Field[] declaredFields = globalParamClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            addGlobalParam(properties,
                    declaredField.getName(),
                    StringUtils.substringAfterLast(declaredField.getType().getName().toLowerCase(Locale.ROOT), DwOpenConstant.JOIN_POINT),
                    declaredField.getDeclaredAnnotation(GlobalParamDescription.class).value());
        }
        return buildBaseOperatorTemplate(jsonObject.toJSONString(), operateId, operatorName, Operator.GLOBAL_OPERATOR_DESC);
    }

    /**
     * 构建全局参数字段信息
     */
    private static void addGlobalParam(JSONObject properties, String name, String columType, String description) {
        JSONObject param = new JSONObject();
        param.put(GLOBAL_PARAM_NAME, name);
        param.put(GLOBAL_PARAM_TYPE, columType);
        param.put(GLOBAL_PARAM_DESCRIPTION, description);
        properties.put(name, param);
    }

}
