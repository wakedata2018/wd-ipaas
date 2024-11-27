package com.wakedata.dw.open.service.impl.api.helper;

import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.dto.DataAssetApiDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WangChenSheng
 * @descriptor ApiGroupHelper
 * @title ApiGroupHelper
 * @date 2022/8/23 11:11
 */
@Component
public class ApiGroupHelper {

    @Resource
    private ApiGroupService apiGroupService;

    /**
     * 获取相应的接口分组信息
     *
     * @return Map集合(key=id value=name)
     * @param dataList api列表信息
     */
    public Map<Integer, String> getGroupList(List<DataAssetApiDTO> dataList) {

        List<Integer> apiGroupIdList = dataList.stream().map(DataAssetApiDTO::getApiGroupId).collect(Collectors.toList());
        return getGroupNameMap(apiGroupIdList);
    }

    public Map<Integer, String> getGroupNameMap(List<Integer> apiGroupIdList) {
        List<ApiGroupPo> apiGroupPoList = apiGroupService.queryApiGroupListByIds(apiGroupIdList);
        if (CollectionUtils.isEmpty(apiGroupPoList)){
            return new HashMap<>(1);
        }
        return apiGroupPoList.stream().collect(Collectors.toMap(ApiGroupPo::getId, ApiGroupPo::getGroupName, (apiGroupId, apiGroupName) -> apiGroupName));
    }
}
