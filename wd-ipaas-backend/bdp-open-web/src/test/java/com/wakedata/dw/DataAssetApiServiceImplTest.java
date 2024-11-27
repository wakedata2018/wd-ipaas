package com.wakedata.dw;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.enums.DataAssetEnums.FiledTypeEnums;
import com.wakedata.dw.open.enums.DataAssetEnums.FiledTypeAttrEnums;
import com.google.common.collect.Lists;

import com.wakedata.dw.open.enums.DataAssetEnums.DataApiType;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.DataAssetApiServiceImpl;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DwOpenWebApplication.class)
public class DataAssetApiServiceImplTest extends TestCase {

    @Resource
    DataAssetApiServiceImpl dataAssetApiService;

    /**
     * 测试：存储API响应参数
     */
    @Test
    @Rollback
    @Transactional()
    public void testSaveResponses() {
        // 由于cglib类是通过继承代理，无法代理私有方法，因此无法通过原始对象执行方法
        if (AopUtils.isCglibProxy(dataAssetApiService)) {
            // 如果是cglib代理对象，则转为原始对象
            dataAssetApiService = (DataAssetApiServiceImpl) AopProxyUtils.getSingletonTarget(dataAssetApiService);
        }
        // 获取class
        Class<? extends DataAssetApiServiceImpl> clazz = dataAssetApiService.getClass();
        try {
            Method method = clazz.getDeclaredMethod("saveResponses", ApiDetailVo.class);
            method.setAccessible(true);
            for (ApiDetailVo api : getApis()) {
                method.invoke(dataAssetApiService, api);
            }
            assertTrue(true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private List<ApiDetailVo> getApis() {
        List<ApiDetailVo> apiDetailVos = new ArrayList<>();

        // HTTP
        ApiDetailVo apiDetailHTTP = new ApiDetailVo();
        // api资产基本信息
        DataAssetApiPo assetApiHTTP = new DataAssetApiPo();
        assetApiHTTP.setDataAssetApiId(888);
        assetApiHTTP.setApiType(DataApiType.EXTERNAL_HTTP);
        apiDetailHTTP.setDataAssetApi(assetApiHTTP);
        // api响应参数列表
        apiDetailHTTP.setResponseParams(this.buildRespParams());
        apiDetailVos.add(apiDetailHTTP);

        // 数据库表单
        ApiDetailVo apiDetailTable = new ApiDetailVo();
        // api资产基本信息
        DataAssetApiPo assetApiTable = new DataAssetApiPo();
        assetApiTable.setDataAssetApiId(888);
        assetApiTable.setApiType(DataApiType.NORMAL_TABLE);
        apiDetailTable.setDataAssetApi(assetApiTable);
        // api响应参数列表
        apiDetailTable.setResults(this.buildResults());
        apiDetailVos.add(apiDetailTable);

        return apiDetailVos;
    }

    private List<ApiRespParamDTO> buildRespParams() {
        List<ApiRespParamDTO> responseParams = new ArrayList<>();

        ApiRespParamDTO child = new ApiRespParamDTO();
        child.setId(0);
        child.setAssetColumns("");
        child.setAssetDataType("");
        child.setDescription("");
        child.setType(HttpParamKind.BODY);
        child.setChildApiRespParams(Lists.newArrayList());
        child.setAssetColumnLength(0);

        ApiRespParamDTO paramPo = new ApiRespParamDTO();
        paramPo.setId(0);
        paramPo.setAssetColumns("");
        paramPo.setAssetDataType("");
        paramPo.setDescription("");
        paramPo.setType(HttpParamKind.BODY);
        paramPo.setChildApiRespParams(Collections.singletonList(child));
        paramPo.setAssetColumnLength(0);

        responseParams.add(paramPo);
        return responseParams;
    }

    private List<ApiConditionPo> buildResults() {
        List<ApiConditionPo> results =  new ArrayList<>();
        ApiConditionPo result = new ApiConditionPo();
        result.setId(0);
        result.setDataAssetId(0);
        result.setAssetColumns("");
        result.setAssetDatatype("");
        result.setAssetColumnsLength(0);
        result.setDescriptions("");
        result.setType(FiledTypeEnums.PARAMETERS);
        result.setTypeAttr(FiledTypeAttrEnums.OPERATOR);
        result.setHttpParamKind(HttpParamKind.QUERY);
        result.setSample("");
        result.setRequired(false);
        result.setMultiValue(false);
        result.setAutoPare(false);
        results.add(result);
        return results;
    }
}