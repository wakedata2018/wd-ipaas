package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.model.api.WhiteListPo;
import com.wakedata.dw.open.service.BaseDbService;

import java.util.List;
import java.util.Set;

/**
 * @author tanzhi
 * @title WhiteListService
 * @projectName bdp-open
 * @date 2019/9/17 19:43
 * @description
 */
public interface WhiteListService extends BaseDbService<WhiteListPo> {

    /**
     * 是否是在白名单中
     *
     * @param appKey
     * @return
     */
    Set<String> accessWhiteList(String appKey);

    /**
     * 添加到访问白名单中
     *
     * @param appId
     * @param ips
     * @return
     */
    Boolean addToAccessList(Integer appId, String ips);

    /**
     * 查询列表
     *
     * @param appId
     * @return
     */
    List<WhiteListPo> showList(Integer appId);

}
