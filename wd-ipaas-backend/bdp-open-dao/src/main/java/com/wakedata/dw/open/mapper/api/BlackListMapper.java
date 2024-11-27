package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.BlackListPo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;
import java.util.Set;

/**
 * @author liuzheng
 * @title BlackListMapper
 * @date 2021/4/6 17:58
 * @projectName bdp-open
 * @description
 */
public interface BlackListMapper extends Mapper<BlackListPo>, InsertListMapper<BlackListPo> {

    /**
     * 校验是否在黑名单中.
     * @param appKey 接入应用appKey.
     * @return 返回对应黑名单列表.
     */
    Set<String> checkBlockList(@Param("appKey") String appKey);

    /**
     * 查询列表.
     * @param accessAppId 接入应用id.
     * @return 对应接入应用黑名单列表.
     */
    List<BlackListPo> getBlackList(@Param("accessAppId") Integer accessAppId);

}
