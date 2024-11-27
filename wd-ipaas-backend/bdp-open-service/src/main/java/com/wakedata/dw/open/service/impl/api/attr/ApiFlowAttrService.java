package com.wakedata.dw.open.service.impl.api.attr;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.function.custom.CustomFunctionUtil;
import com.wakedata.dw.open.liteflow.DataAssetApiLiteflowService;
import com.wakedata.dw.open.mapper.api.ApiFlowRelationMapper;
import com.wakedata.dw.open.mapper.api.CustomFunctionRelationApiMapper;
import com.wakedata.dw.open.mapper.api.attr.ApiFlowAttrMapper;
import com.wakedata.dw.open.mapper.api.attr.EventReceiveApiMapper;
import com.wakedata.dw.open.mapper.api.attr.EventSendApiMapper;
import com.wakedata.dw.open.model.api.ApiLiteFlowChainPo;
import com.wakedata.dw.open.model.api.CustomFunctionRelationApiPo;
import com.wakedata.dw.open.model.api.event.EventReceiveApiAttr;
import com.wakedata.dw.open.model.api.event.EventSendApiAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.ApiFlowRelation;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.attr.ApiAttrService;
import com.wakedata.dw.open.utils.JsonUtil;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.dw.openapi.service.EventReceiveAttrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author ZhangXueJun
 * @title ApiFlowAttrService
 * @date 2021/3/1 16:11
 * @projectName dw-open
 * @description
 */
@Service("apiFlowAttrService")
@Slf4j
public class ApiFlowAttrService implements ApiAttrService<ApiFlowAttr> {

    @Resource
    private ApiFlowAttrMapper apiFlowAttrMapper;
    @Resource
    private ApiFlowRelationMapper apiFlowRelationMapper;
    @Resource
    private EventSendApiMapper eventSendApiMapper;
    @Resource
    private EventReceiveApiMapper eventReceiveApiMapper;
    @Resource
    private EventReceiveAttrService eventReceiveAttrService;
    @Resource
    private ApiLiteFlowChainHelper apiLiteFlowChainHelper;

    @Resource
    private CustomFunctionRelationApiMapper customFunctionRelationApiMapper;

    @Resource
    private DataAssetApiLiteflowService dataAssetApiLiteflowService;


    @Override
    public ApiFlowAttr getApiAttr(Integer apiId, DataAssetEnums.DataApiType apiKind) {
        ApiFlowAttr apiFlowAttr = new ApiFlowAttr();
        apiFlowAttr.setApiId(apiId);

        apiFlowAttr = apiFlowAttrMapper.selectOne(apiFlowAttr);
        if (apiFlowAttr != null) {
            apiFlowAttr = apiFlowAttr.convert();
        }
        return apiFlowAttr;
    }

    @Override
    public ApiFlowAttr saveOrUpdateApiAttr(Integer apiId, ApiFlowAttr apiFlowAttr) {
        apiFlowAttr.setApiId(apiId);
        saveLiteFlowIfNecessary(apiFlowAttr, true);
        apiFlowAttr.setDagJson(JsonUtil.toJson(apiFlowAttr));

        if (apiFlowAttr.getId() == null) {
            apiFlowAttrMapper.insert(apiFlowAttr);
        } else {
            apiFlowAttrMapper.updateByPrimaryKey(apiFlowAttr);
        }

        // 设置引用关系
        ApiFlowRelation apiFlowRelation = new ApiFlowRelation();
        apiFlowRelation.setGraphId(apiId);
        apiFlowRelationMapper.delete(apiFlowRelation);

        //添加关联api记录
        dataAssetApiLiteflowService.addOperatorRelationApi(apiFlowAttr,apiFlowRelation);


        Long lesseeId = IpaasUserContext.getUserInfo().getLesseeId();
        Example example = new Example(CustomFunctionRelationApiPo.class);
        example.createCriteria().andEqualTo("dataAssetApiId",apiId);
        example.createCriteria().andEqualTo("lesseeId",lesseeId);
        customFunctionRelationApiMapper.deleteByExample(example);
        saveApiRelationCustomFuncName(lesseeId,apiId,apiFlowAttr.getDagJson());
        return apiFlowAttr;
    }

    /**
     * 保存api关联的自定义函数
     * @param lesseeId
     * @param apiId
     * @param content
     */
    public void saveApiRelationCustomFuncName(Long lesseeId,Integer apiId,String content){
        //关联自定义函数
        Set<String> customFuncNames = CustomFunctionUtil.extractCustomFuncNames(content);
        if(CollUtil.isNotEmpty(customFuncNames)){
            CustomFunctionRelationApiPo customFunctionRelationApiPo = null;
            for(String funcName:customFuncNames) {
                customFunctionRelationApiPo = new CustomFunctionRelationApiPo();
                customFunctionRelationApiPo.setLesseeId(lesseeId);
                customFunctionRelationApiPo.setCustomFuncName(funcName);
                customFunctionRelationApiPo.setDataAssetApiId(apiId);
                customFunctionRelationApiPo.setCreateTime(new Date());
                customFunctionRelationApiPo.setUpdateTime(new Date());
                customFunctionRelationApiMapper.insert(customFunctionRelationApiPo);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteApiAttrByApiId(Integer apiId) {
        if (ObjectUtil.isEmpty(apiId)) {
            throw new OpenException(MsgCodeEnum.w_api_id_is_not_empty);
        }
        //对应删除事件发送配置表的数据
        EventSendApiAttr eventSendApiAttr = new EventSendApiAttr();
        eventSendApiAttr.setApiId(apiId);
        List<EventSendApiAttr> list = eventSendApiMapper.select(eventSendApiAttr);
        for (EventSendApiAttr eventSendApiAttr1 : list) {
            //删除事件发送配置表的数据
            eventSendApiMapper.deleteByPrimaryKey(eventSendApiAttr1.getId());
        }

        //对应删除事件接收配置表的数据
        EventReceiveApiAttr eventReceiveApiAttr = new EventReceiveApiAttr();
        eventReceiveApiAttr.setApiId(apiId);
        List<EventReceiveApiAttr> eventReceiveApiAttrList = eventReceiveApiMapper.select(eventReceiveApiAttr);
        for (EventReceiveApiAttr eventReceiveApiAttr1 : eventReceiveApiAttrList) {
            eventReceiveApiMapper.deleteByPrimaryKey(eventReceiveApiAttr1.getId());
            eventReceiveAttrService.deleteSubscribe(eventReceiveApiAttr1.getId());
        }


        ApiFlowAttr apiAttr = new ApiFlowAttr();
        apiAttr.setApiId(apiId);

        ApiFlowRelation apiFlowRelation = new ApiFlowRelation();
        apiFlowRelation.setApiId(apiId);
        apiFlowRelationMapper.delete(apiFlowRelation);
        return apiFlowAttrMapper.delete(apiAttr);
    }

    /**
     * 生成api执行链
     *
     * @param apiFlowAttr
     * @param force
     * @return
     */
    public String saveLiteFlowIfNecessary(ApiFlowAttr apiFlowAttr, boolean force) {
        if (!force) {
            String liteFlowChain = apiLiteFlowChainHelper.getLiteFlowChain(apiFlowAttr.getApiId().longValue());
            if (StrUtil.isNotBlank(liteFlowChain)) {
                return liteFlowChain;
            }
        }

        String json = ParamMappingsUtils.prettyFormatJson(dataAssetApiLiteflowService.generateApiLiteflowChain(apiFlowAttr));

        ApiLiteFlowChainPo chainPo = new ApiLiteFlowChainPo();
        chainPo.setDataAssetApiId(apiFlowAttr.getApiId().longValue());
        chainPo.setChain(json);
        chainPo.setCreateTime(new Date());
        chainPo.setUpdateTime(new Date());
        apiLiteFlowChainHelper.saveLiteFlowChain(chainPo);
        return json;
    }

}