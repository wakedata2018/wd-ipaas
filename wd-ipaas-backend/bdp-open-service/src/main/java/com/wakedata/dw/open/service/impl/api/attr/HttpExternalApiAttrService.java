package com.wakedata.dw.open.service.impl.api.attr;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.attr.HttpExternalApiMapper;
import com.wakedata.dw.open.model.api.external.http.HttpCode;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.service.api.attr.ApiAttrService;
import com.wakedata.dw.open.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author ZhangXueJun
 * @title ExternalApiServiceImpl
 * @date 2021/3/1 16:11
 * @projectName dw-open
 * @description
 */
@Service("httpExternalApiAttrService")
@Slf4j
public class HttpExternalApiAttrService implements ApiAttrService<HttpExternalApiAttr> {

    @Autowired
    private HttpExternalApiMapper httpExternalApiMapper;

    @Override
    public HttpExternalApiAttr getApiAttr(Integer dataAssetApiId, DataAssetEnums.DataApiType apiKind) {
        HttpExternalApiAttr httpExternalApiAttr = new HttpExternalApiAttr();
        httpExternalApiAttr.setApiId(dataAssetApiId);
        httpExternalApiAttr = httpExternalApiMapper.selectOne(httpExternalApiAttr);

        if (httpExternalApiAttr == null) {
            return httpExternalApiAttr;
        }

        if (StringUtils.isNotEmpty(httpExternalApiAttr.getErrorDefinitionJson())) {
            httpExternalApiAttr.setHttpCodes(JsonUtil.toListObject(httpExternalApiAttr.getErrorDefinitionJson(), HttpCode.class));
        }
        return httpExternalApiAttr;
    }

    @Override
    public HttpExternalApiAttr saveOrUpdateApiAttr(Integer dataAssetApiId, HttpExternalApiAttr externalApi) {
        if (externalApi == null) {
            return externalApi;
        }
        if (CollectionUtils.isNotEmpty(externalApi.getHttpCodes())) {
            externalApi.setErrorDefinitionJson(JsonUtil.toJson(externalApi.getHttpCodes()));
        }

        if (externalApi.getApiId() == null) {
            externalApi.setApiId(dataAssetApiId);

            HttpExternalApiAttr apiAttr = httpExternalApiMapper.getExternalApi(externalApi.getApiId());
            if(Objects.isNull(apiAttr)){
                httpExternalApiMapper.insert(externalApi);
                return externalApi;
            }
            externalApi.setId(apiAttr.getId());
        }
        httpExternalApiMapper.updateByPrimaryKey(externalApi);
        return externalApi;
    }

    @Override
    public int deleteApiAttrByApiId(Integer dataAssetApiId) {
        if (ObjectUtil.isEmpty(dataAssetApiId)) {
            throw new OpenException(MsgCodeEnum.w_api_id_is_not_empty);
        }
        HttpExternalApiAttr apiAttr = new HttpExternalApiAttr();
        apiAttr.setApiId(dataAssetApiId);
        return httpExternalApiMapper.delete(apiAttr);
    }

    public List<HttpExternalApiAttr> queryAttrByPath(String path){
        if (StringUtils.isEmpty(path)) {
            return new ArrayList<>();
        }

        Example example = new Example(HttpExternalApiAttr.class);
        example.createCriteria().andEqualTo("path", path);
        return httpExternalApiMapper.selectByExample(example);
    }
}