package com.wakedata.dw.open.mapper;

import com.wakedata.dw.open.model.UserPageActionPo;
import com.wakedata.dw.open.model.query.UserActionPageQuery;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author yiyufeng
 * @title UserPageActionMapper
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
public interface UserPageActionMapper extends Mapper<UserPageActionPo> {
    /**
     * 定时清理SQL
     *
     * @return
     */
    int cleanUp();

    /**
     * 查询操作日志
     * @param query 操作日志查询条件
     * @return
     */
    List<UserPageActionPo> queryReport(@Param("query") UserActionPageQuery query);

    /**
     * 查询操作日志详情
     * @param id 操作日志Id
     * @return UserPageActionPo
     */
    UserPageActionPo queryAuditLogInformation(@Param("id") Long id);

    /**
     * 获取下拉菜单列表
     * @return List<String>
     */
    List<String> queryMenu();

    /**
     * 查询账号唯一标识列表
     * @param userIdentification 账号唯一标识，用作模糊查询
     * @return List<String>
     */
    List<String> selectUserIdentificationList(@Param("userIdentification") String userIdentification);
}
