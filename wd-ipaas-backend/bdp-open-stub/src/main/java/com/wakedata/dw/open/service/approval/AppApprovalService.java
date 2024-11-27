package com.wakedata.dw.open.service.approval;

import com.github.pagehelper.Page;
import com.google.common.collect.ImmutableMap;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.enums.ApprovalBusinessTypeEnum;
import com.wakedata.dw.open.enums.ApprovalStatusEnum;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.AppApprovalPo;
import com.wakedata.dw.open.model.api.AppAuthApiCountVo;
import com.wakedata.dw.open.model.api.AppIdAndAuthApiCountVo;
import com.wakedata.dw.open.model.api.AuthApiVO;
import com.wakedata.dw.open.model.domain.DataAccessApprovalDo;
import com.wakedata.dw.open.model.query.AuthApiListQuery;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.approval.vo.ApprovalApiVO;
import com.wakedata.dw.open.service.approval.vo.ApprovalDetailVo;
import com.wakedata.dw.open.service.approval.vo.ApprovalHistoryVo;
import com.wakedata.dw.open.service.approval.vo.ApprovalRecordVO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yiyufeng
 * @title ApprovalService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface AppApprovalService {

    /**
     * 创建审批
     *
     * @param userIdentification
     * @param approvalBusinessType
     * @param approvalBody
     * @param approvalPerson
     * @return
     */
    @Deprecated
    List<AppApprovalPo> createApproval(String userIdentification, ApprovalBusinessTypeEnum approvalBusinessType, Object approvalBody, String approvalPerson);

    /**
     * 创建审批API记录
     *
     * @param dataAssetApiId 申请的api id
     * @param userInfo       用户信息
     * @return 审批记录
     */
    AppApprovalPo createApprovalApi(Integer dataAssetApiId, IpaasUserInfo userInfo);

    /**
     * 分页查询API审批列表
     *
     * @param appApprovalPo 查询条件
     * @param pageNo        分页参数：页码
     * @param pageSize      分页参数：每页查询数量
     * @param keyword       API名称
     * @param userInfo      用户信息
     * @param lesseeName    租户名称
     * @return API审批列表
     */
    PageResultDTO<List<ApprovalRecordVO>> approvalApiRecord(AppApprovalPo appApprovalPo, Integer pageNo, Integer pageSize, String keyword, IpaasUserInfo userInfo, String lesseeName);

    /**
     * 审批API
     *
     * @param approvalApiVO 审核API请求参数
     * @return Boolean
     */
    Boolean approvalApi(ApprovalApiVO approvalApiVO);

    /**
     * 创建审批
     *
     * @param userIdentification
     * @param approvalBusinessType
     * @param dataAssetId
     * @return
     */

    AppApprovalPo createApproval(String userIdentification, ApprovalBusinessTypeEnum approvalBusinessType, Integer dataAssetId);

    /**
     * 审批历史，带有分页,模糊查询
     *
     * @param appApprovalPo
     * @param pageNo
     * @param pageSize
     * @param keyword
     * @return
     */
    Page<ApprovalHistoryVo> approvalHistory(AppApprovalPo appApprovalPo, Integer pageNo, Integer pageSize, String keyword);

    /**
     * 审批详情
     *
     * @param userIdentification
     * @param approvalId
     * @return
     */
    ApprovalDetailVo approvalDetail(String userIdentification, Integer approvalId);

    /**
     * 处理审批
     *
     * @param userIdentification
     * @param approvalId
     * @param message
     * @param statusEnum
     * @param formUrl
     * @return
     */
    boolean approval(String userIdentification, Integer approvalId, String message, ApprovalStatusEnum statusEnum, String formUrl);

    /**
     * k2审批回调
     *
     * @param approvalId
     * @param status
     * @param approvalDate
     * @param fromUrl
     * @return
     */
    boolean approvalCallback(Integer approvalId, Integer status, Date approvalDate, String fromUrl);

    /**
     * 删除审批信息
     *
     * @param approvalId
     * @return
     */
    boolean deleteApproval(Integer approvalId);

    /**
     * 通过dataAssetId 查接入的信息
     *
     * @param dataAssetId api id
     * @return
     */
    Page<DataAccessApprovalDo> getApprovalPoByAssetId(Integer dataAssetId, PageQuery pageQuery);

    /**
     * 删除app时，若改app有审批信息，则删除
     *
     * @param appId
     */
    void deleteApproveByAppId(Integer appId);

    /**
     * 批量查询api是否被收藏
     *
     * @param collect
     * @param userIdentification
     * @return
     */
    List<Map> queryCollectInfo(List<Integer> collect, String userIdentification);

    /**
     * 分页查询授权API列表
     *
     * @param authApiListQuery 查询条件
     * @param pageQuery        分页查询
     * @return
     */
    Page<AuthApiVO> selectAuthApiListByPage(AuthApiListQuery authApiListQuery, PageQuery pageQuery);


    /**
     * 查询授权api列表
     *
     * @param appId     应用id
     * @param lesseeId ipaas租户id
     * @param pageQuery 分页参数
     * @return Page<AuthApiVO>
     */
    Page<AuthApiVO> selectAuthApiListWithAppAuthStatus(Long lesseeId, Integer appId, PageQuery pageQuery);


    /**
     * 通过应用ID 查询已授权的api数量
     *
     * @param appId 应用id
     * @return
     */
    AppAuthApiCountVo selectAuthApiCount(Long lesseeId, Integer appId);

    /**
     * 通过API ID 查询api已授权的应用数量
     *
     * @param apiId
     * @param apiId API ID
     * @return
     */
    Integer selectAuthAppCount(Long lesseeId, Integer apiId);

    /**
     * 查询当前应用是否有该api的权限
     *
     * @param lesseeId
     * @param appId
     * @param apiId
     * @return
     */
    Integer selectAuthAppCountByAppId(Long lesseeId, Integer appId, Integer apiId);

    /**
     * 获取租户下，应用id和授权的api数量Map
     *
     * @return
     */
    ImmutableMap<Integer, AppIdAndAuthApiCountVo> selectAppIdAndAuthApiCountMap(Long lesseeId);

    /**
     * 应用授权api
     * @param lesseeId 租户id
     * @param appId 应用id
     * @param apiId API ID
     * @return
     */
    Boolean appAuthApi(Long lesseeId, Integer appId, Integer apiId);

    /**
     * 应用解除授权api
     *
     * @param lesseeId 租户id
     * @param appId 应用id
     * @param apiId API ID
     * @return
     */
    Boolean appDeauthApi(Long lesseeId, Integer appId, Integer apiId);

    /**
     * 更改api的审核状态(api没有租户隔离)
     *
     * @param dataAssetApiId dataAssetApiId
     * @return true/false
     */
    Boolean updateActiveStatusById(Integer dataAssetApiId);

    /**
     * 批量更改api的审核状态(api没有租户隔离)
     *
     * @param dataAssetApiIds API的Id列表
     * @param activeStatus api的审核状态
     * @return true/false
     */
    Boolean updateActiveStatusByIds(List<Integer> dataAssetApiIds, DataAssetEnums.ActiveStatus activeStatus);
}
