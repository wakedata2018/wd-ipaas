package com.wakedata.dw.openapi.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.dw.open.function.custom.CustomFunction;
import com.wakedata.dw.open.function.custom.CustomFunctionHelper;
import com.wakedata.dw.open.function.custom.CustomFunctionStatus;
import com.wakedata.dw.open.mapper.api.CustomFunctionMapper;
import com.wakedata.dw.open.model.api.CustomFunctionPo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义函数方法实现
 * @author luomeng
 * @date 2022/11/1 18:14
 */
@Service
public class CustomFunctionHelperImpl implements CustomFunctionHelper {

    @Resource
    private CustomFunctionMapper customFunctionMapper;

    @Override
    public CustomFunction getCustomFunctionByName(String functionName, Long lesseeId) {
        CustomFunctionPo functionPo = new CustomFunctionPo();
        functionPo.setFuncName(functionName);
        functionPo.setLesseeId(lesseeId);
        CustomFunctionPo customFunctionPo = customFunctionMapper.selectOne(functionPo);
        if(customFunctionPo != null){
            return getCustomFunction(customFunctionPo);
        }
        return null;
    }

    private CustomFunction getCustomFunction(CustomFunctionPo customFunctionPo) {
        CustomFunction customFunction = new CustomFunction();
        customFunction.setName(customFunctionPo.getFuncName());
        customFunction.setLanguage(customFunctionPo.getFuncLanguage());
        customFunction.setDesc(customFunctionPo.getFuncDesc());
        customFunction.setParamList(JSONUtil.toList(customFunctionPo.getFuncParam(), CustomFunction.FunctionParam.class));
        customFunction.setReturnType(customFunctionPo.getFuncReturn());
        customFunction.setCode(customFunctionPo.getFuncCode());
        return customFunction;
    }

    @Override
    public List<CustomFunction> getPublishCustomFunctionList(Long lesseeId) {
        CustomFunctionPo functionPo = new CustomFunctionPo();
        functionPo.setLesseeId(lesseeId);
        functionPo.setStatus(CustomFunctionStatus.StatusEnum.ONLINE.getCode());
        List<CustomFunctionPo> customFunctionPoList = customFunctionMapper.select(functionPo);
        if(CollUtil.isNotEmpty(customFunctionPoList)){
            return customFunctionPoList.stream().map(this::getCustomFunction).collect(Collectors.toList());
        }
        return null;
    }


}
