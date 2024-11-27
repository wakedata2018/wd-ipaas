package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.WhiteListPo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;
import java.util.Set;

/**
 * @author tanzhi
 * @title WhiteListMapper
 * @projectName bdp-open
 * @date 2019/9/17 19:42
 * @description
 */

public interface WhiteListMapper extends Mapper<WhiteListPo>, InsertListMapper<WhiteListPo> {

    /**
     * 根据appKey和ip查询是否有数据
     *
     * @param appKey
     * @return
     */
    Set<String> accessWhiteList(@Param("appKey") String appKey);

    /**
     * 查询一个接入端的Ip白名单
     *
     * @param accessAppId
     * @return
     */
    List<WhiteListPo> getWhiteList(@Param("accessAppId") Integer accessAppId);

}
