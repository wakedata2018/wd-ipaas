package com.wakedata.dw.open.mapper;

import com.wakedata.dw.open.model.api.AppApprovalPo;
import com.wakedata.dw.open.model.api.AppIdAndAuthApiCountVo;
import com.wakedata.dw.open.model.api.AuthApiVO;
import com.wakedata.dw.open.model.domain.DataAccessApprovalDo;
import com.wakedata.dw.open.model.query.AuthApiListQuery;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

import java.util.List;
import java.util.Map;

/**
 * @author yiyufeng
 * @title ApprovalMapper
 * @projectName bdp-open
 * @date
 * @description
 */
public interface AppApprovalMapper extends Mapper<AppApprovalPo>, InsertListMapper<AppApprovalPo> {
    /**
     * 通过api id查接入信息
     * @param dataAssetId
     * @return
     */
    List<DataAccessApprovalDo> selectApprovalInfo(Integer dataAssetId);

    /**
     * 批量查询api是否被用户收藏
     * @param collect
     * @param userIdentification
     * @return
     */
    List<Map> queryCollect(@Param("list") List<Integer> collect, @Param("userIdentification") String userIdentification);

    /**
     * 通过keyword和api id查审批信息
     * @param appApprovalPo
     * @param keyword
     * @param lesseeName 租户名称
     * @return
     */
    List<AppApprovalPo> selectPageApproveInfo(@Param("appApprovalPo") AppApprovalPo appApprovalPo, @Param("keyword") String keyword, @Param("lesseeName") String lesseeName);

    /**
    * @Description  查询被拒绝或者审批失败的数据
    * @Param [appApprovalPo, keyword]
    * @Return java.util.List<com.wakedata.dw.open.model.api.AppApprovalPo>
    * @Author wujiahao
    * @Date 2021/7/30
    **/
    List<String> selectApproveInfoId(@Param("appId") Integer appId, @Param("apiId") Integer apiId, @Param("lesseeId") Long lesseeId);

    /**
     * 根据id查询审批记录
     *
     * @param approvalId 审批记录id
     * @return 审批记录
     */
    AppApprovalPo findById(@Param("approvalId") Integer approvalId);

    /**
     * 获取授权API列表
     * @param authApiListQuery 授权API列表查询条件
     * @return List<AuthApiVO> 获取授权API列表
     */
    List<AuthApiVO> selectAuthApiVOList(@Param("query") AuthApiListQuery authApiListQuery);

    /**
     * 获取授权API列表数量
     * @param authApiListQuery 查询条件
     * @return
     */
    Integer selectAuthCount(@Param("query") AuthApiListQuery authApiListQuery);

    /**
     * 获取应用id和对应的授权api数量
     * @param authApiListQuery 查询条件
     * @return key：appid value: 授权的api数量
     */
    List<AppIdAndAuthApiCountVo> selectAuthCountGroupByAppId(@Param("query") AuthApiListQuery authApiListQuery);

    /**
     * 禁用审批或授权记录
     * @param lesseeId
     * @param appId
     * @param apiId
     * @return
     */
    Integer disableApprovalRecord(@Param("lesseeId") Long lesseeId,@Param("appId") Integer appId,@Param("apiId") Integer apiId);

    /**
     * 查询租户最后一次指定API审批结果
     *
     * @param appApprovalPo AppApprovalPo
     * @return AppApprovalPo
     */
    AppApprovalPo findLastApiApproveByLesseeId(@Param("appApprovalPo") AppApprovalPo appApprovalPo);

    /**
     * 更新活跃状态
     *
     * @param appApprovalPo appApprovalPo
     * @return true/false
     */
    Boolean updateActiveStatusById(@Param("appApprovalPo") AppApprovalPo appApprovalPo);

    /**
     * 批量更新活跃状态
     *
     * @param dataAssetApiIds apiId列表
     * @param activeStatus 有效状态value值
     * @return true/false
     */
    Boolean batchUpdateActiveStatusById(@Param("dataAssetApiIds") List<Integer> dataAssetApiIds, @Param("activeStatus") Integer activeStatus);

    /**
     * 查询租户自建api及授权数量
     * @param authApiListQuery
     * @return
     */
    Long selectLesseeApiCount(@Param("query") AuthApiListQuery authApiListQuery);
}
