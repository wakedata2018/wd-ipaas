package com.wakedata.dw.open.mapper.lessee;

import com.wakedata.dw.open.model.lessee.OpenAccountPo;
import com.wakedata.dw.open.model.query.AccountQuery;
import com.wakedata.dw.open.model.vo.OpenAccountVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * ipaas账号管理
 * @author luomeng
 * @date 2022/8/2 17:10
 */
public interface OpenAccountMapper extends Mapper<OpenAccountPo> {

    /**
     * 根据账号查询信息
     * @param userIdentification
     * @param id
     * @return
     */
    int selectAccountNum(@Param("userIdentification") String userIdentification,@Param("id") Long id);

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    OpenAccountVo getAccountInfo(@Param("id") Long id);

    /**
     * 查询账号列表
     * @param openAccountQuery
     * @return
     */
    List<OpenAccountVo> selectList(AccountQuery openAccountQuery);

    /**
     * 查询账号总数
     * @param query
     * @return
     */
    Integer selectListCount(AccountQuery query);

    /**
     * 查询账号数据
     * @param username
     * @return
     */
    OpenAccountVo getAccountByUsername(@Param("acount") String username);
}
