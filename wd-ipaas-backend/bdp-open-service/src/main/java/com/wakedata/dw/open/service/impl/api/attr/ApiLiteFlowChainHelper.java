package com.wakedata.dw.open.service.impl.api.attr;

import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.mapper.api.ApiLiteFlowChainMapper;
import com.wakedata.dw.open.model.api.ApiLiteFlowChainPo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author luomeng
 * @date 2022/10/13 19:03
 */
@Component
public class ApiLiteFlowChainHelper {

    @Resource
    private ApiLiteFlowChainMapper apiLiteFlowChainMapper;

    /**
     * 保存流程编排执行链
     * @param apiLiteFlowChainPo
     */
    @CacheEvict(value = DwOpenConstant.API_CHAIN_JSON_KEY + "#"+DwOpenConstant.API_CHAIN_JSON_EXPIRE_TIME
            ,key = "'getLiteFlowChain_'+#apiLiteFlowChainPo.dataAssetApiId")
    public void saveLiteFlowChain(ApiLiteFlowChainPo apiLiteFlowChainPo){
        ApiLiteFlowChainPo query = new ApiLiteFlowChainPo();
        query.setDataAssetApiId(apiLiteFlowChainPo.getDataAssetApiId());
        ApiLiteFlowChainPo chainPo = apiLiteFlowChainMapper.selectOne(query);
        if(chainPo != null){
            chainPo.setChain(apiLiteFlowChainPo.getChain());
            chainPo.setUpdateTime(new Date());
            apiLiteFlowChainMapper.updateByPrimaryKey(chainPo);
        }else{
            apiLiteFlowChainMapper.insert(apiLiteFlowChainPo);
        }
    }


    /**
     * 获取api执行链路
     * @param dataAssetApiId
     * @return
     */
    @Cacheable(value = DwOpenConstant.API_CHAIN_JSON_KEY + "#"+DwOpenConstant.API_CHAIN_JSON_EXPIRE_TIME
            ,key = "'getLiteFlowChain_'+#dataAssetApiId",unless = "#result == null")
    public String getLiteFlowChain(Long dataAssetApiId){
        ApiLiteFlowChainPo chainPo = new ApiLiteFlowChainPo();
        chainPo.setDataAssetApiId(dataAssetApiId);
        ApiLiteFlowChainPo liteFlowChainPo = apiLiteFlowChainMapper.selectOne(chainPo);
        if(liteFlowChainPo != null){
            return liteFlowChainPo.getChain();
        }
        return null;
    }


}
