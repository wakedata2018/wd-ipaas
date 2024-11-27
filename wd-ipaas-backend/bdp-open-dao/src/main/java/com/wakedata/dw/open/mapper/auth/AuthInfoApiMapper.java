package com.wakedata.dw.open.mapper.auth;

import com.wakedata.dw.open.model.auth.AuthAuthorizationPo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;


/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/11/16 14:39
 */
public interface AuthInfoApiMapper extends Mapper<AuthAuthorizationPo>, InsertListMapper<AuthAuthorizationPo> {

    /**
     * 根据apiId和authId对关联表进行查重
     *
     * @param apiId apiId
     * @param authId 第三方应用Id
     * @return int 返回一个查询出来的条数
     */
    int checkByApiIdAndAuthId(@Param("apiId") Integer apiId, @Param("authId") Integer authId);
    /**
     * 根据apiId和authId对关联表进行查重
     *
     * @param authAuthorizationPo 第三方应用Id
     * @return int 返回一个查询出来的条数
     */
    Boolean updateByApiIdAndAuthId(AuthAuthorizationPo authAuthorizationPo);
}
