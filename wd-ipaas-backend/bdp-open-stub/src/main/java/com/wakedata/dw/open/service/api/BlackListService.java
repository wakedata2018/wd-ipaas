package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.model.api.BlackListPo;
import com.wakedata.dw.open.service.BaseDbService;

import java.util.List;
import java.util.Set;

/**
 * @author liuzheng
 * @title BlackListService
 * @date 2021/4/6 15:30
 * @projectName bdp-open
 * @description
 */
public interface BlackListService extends BaseDbService<BlackListPo> {

    /**
     * 查询列表.
     * @param appId 接入应用id.
     * @return 对应接入应用黑名单列表.
     */
    List<BlackListPo> showBlackList(Integer appId);

    /**
     * 添加、修改访问黑名单.
     * @param appId 接入应用id.
     * @param ips 黑名单字符串.
     * @return 是否成功.
     */
    Boolean addToBlackList(Integer appId, String ips);

    /**
     * 校验是否在黑名单中.
     * @param appKey 接入应用appKey.
     * @return 返回对应黑名单列表.
     */
    Set<String> checkBlockList(String appKey);

}
