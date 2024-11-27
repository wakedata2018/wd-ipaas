package com.wakedata.dw.open.service.auth;

import com.wakedata.dw.open.model.auth.AuthAuthorizationPo;
import com.wakedata.dw.open.service.BaseDbService;

import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/11/16 14:27
 */
public interface AuthInfoApiService extends BaseDbService<AuthAuthorizationPo> {

    /**
     * 批量授权
     *
     * @param authAuthorizationPoList 传入第三方应用和apiId列表
     * @return List<AuthApiRelevancyPo> 返回一个关联表对象
     */
    Boolean batchAuthorization(List<AuthAuthorizationPo> authAuthorizationPoList);
}
