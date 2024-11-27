package com.wakedata.dw.open.service.impl.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataAssetPublishStatusEnum;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.mapper.api.AppAccessRuleMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.AppAccessRulePo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.domain.AppAccessRuleDo;
import com.wakedata.dw.open.service.BatchCurdService;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.AppAccessRuleService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.vo.AppAccessRuleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author yiyufeng
 * @title DataAccessRuleServiceImpl
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
@Slf4j
public class AppAccessRuleServiceImpl implements AppAccessRuleService {

    @Autowired
    private AppAccessRuleMapper appAccessRuleMapper;
    @Autowired
    private ApiConditionMapper apiConditionMapper;
    @Autowired
    private DataAssetApiService dataAssetApiService;
    @Autowired
    private CurdService<AppAccessRulePo> curdService;
    @Autowired
    private BatchCurdService<AppAccessRulePo> batchCurdService;


    @Override
    public AppAccessRuleDo getAppAccessRule(Integer dataAssetApiId, Integer dataAccessAppId) {
        AppAccessRulePo accessRuleQuery = new AppAccessRulePo();
        accessRuleQuery.setDataAssetApiId(dataAssetApiId);
        accessRuleQuery.setDataAccessAppId(dataAccessAppId);
        List<AppAccessRulePo> accessRules = curdService.select(accessRuleQuery, this.appAccessRuleMapper);
        return AppAccessRulePo.toDataAccessRuleDo(accessRules);
    }

    @Override
    public Page<AppAccessRuleVo> pageAppAccessRuleMultiDatasource(Integer dataAccessAppId, Integer pageNo, Integer pageSize, String keyword, Integer dataSourceId) {
        // 查出所有数据资产
        Page<DataAssetApiPo> dataAssetApiList = dataAssetApiService.like(DataAssetApiPo.class,
                Arrays.asList("dataAssetName", "dataAssetDescription", "apiName", "apiDescription", "dataAssetApiMethod"), keyword, pageNo, pageSize);
        if (CollectionUtils.isEmpty(dataAssetApiList)) {
            return new Page<>();
        }
        Page<AppAccessRuleVo> resultList = new Page<>();
        for (DataAssetApiPo dataAssetApi : dataAssetApiList) {
            AppAccessRuleVo appAccessRuleVo = appAccessRule(dataAccessAppId, dataAssetApi.getDataAssetApiId());
            resultList.add(appAccessRuleVo);
        }
        resultList.setTotal(dataAssetApiList.getTotal());
        return resultList;
    }

    @Override
    public AppAccessRuleVo appAccessRule(Integer appAccessId, Integer dataAssetId) {
        DataAssetApiPo dataAssetApi = dataAssetApiService.detail(dataAssetId);
        AppAccessRuleVo appAccessRule = new AppAccessRuleVo();
        appAccessRule.setApiId(dataAssetId);
        // 获取这个数据资产的列
        if (DataAssetPublishStatusEnum.UN_PUBLISH.equals(dataAssetApi.getDataAssetPublishStatus())) {
            return null;
        }
        ApiConditionPo apiCondition = new ApiConditionPo();
        apiCondition.setDataAssetId(dataAssetId);
        apiCondition.setType(DataAssetEnums.FiledTypeEnums.RESULT);
        List<ApiConditionPo> apiConditions = apiConditionMapper.select(apiCondition);

        // 组装数据资产权限持有结果
        List<AppAccessRuleVo.AppAccessRuleDetailVo> accessRuleDetails
                = getAppAccessRuleDetails(appAccessId, dataAssetApi, apiConditions);
        appAccessRule.setDataAccessRuleDetailList(accessRuleDetails);

        if (dataAssetApi.getApiType() != DataAssetEnums.DataApiType.LITE_FLOW) {
            return appAccessRule;
        }

        //数据资产权限未使用，先注释
//        ApiFlowAttr apiFlowAttr = (ApiFlowAttr) dataAssetApi.getApiAttr();
//        DAGTaskEngine.OperatorContainer operatorContainer = DAGTaskEngine.justParse(apiFlowAttr);
//        List<ApiOperator> apiOperators = operatorContainer.filterApiOperator();
//        for (ApiOperator apiOperator : apiOperators) {
//            AppAccessRuleVo operatorAccessRule = new AppAccessRuleVo();
//            ApiComponent component = apiOperator.getComponent();
//            accessRuleDetails = getAppAccessRuleDetails(appAccessId, component.getDataAssetApi(), component.getResults());
//            if (CollectionUtils.isEmpty(accessRuleDetails)) {
//                continue;
//            }
//
//            operatorAccessRule.setApiId(component.getDataAssetApi().getDataAssetApiId());
//            operatorAccessRule.setDataAccessRuleDetailList(accessRuleDetails);
//            appAccessRule.getOperatorAppAccessRules().put(apiOperator.getName(), operatorAccessRule);
//        }
        return appAccessRule;
    }

    private List<AppAccessRuleVo.AppAccessRuleDetailVo> getAppAccessRuleDetails(
            Integer appAccessId,
            DataAssetApiPo dataAssetApi,
            List<ApiConditionPo> apiConditions) {
        List<DatasourceTableColumnDo> tableColumns = transform(apiConditions, dataAssetApi);
        // 获取拥有的数据资产权限
        Set<String> dataApiRuleSet = this.searchDataAppRuleOfApi(appAccessId, dataAssetApi.getDataAssetApiId());
        if (null == dataApiRuleSet) {
            dataApiRuleSet = new HashSet<>(0);
        }

        // 组装数据资产权限持有结果
        List<AppAccessRuleVo.AppAccessRuleDetailVo> accessRuleDetails = new ArrayList<>(tableColumns.size());
        for (DatasourceTableColumnDo tableColumn : tableColumns) {
            AppAccessRuleVo.AppAccessRuleDetailVo dataAccessRuleDetail = new AppAccessRuleVo.AppAccessRuleDetailVo();
            BeanUtils.copyProperties(tableColumn, dataAccessRuleDetail);
            dataAccessRuleDetail.setAuthorize(dataApiRuleSet.contains(tableColumn.getDatasourceTableColumnName()));
            accessRuleDetails.add(dataAccessRuleDetail);
        }

        return accessRuleDetails;
    }


    private List<DatasourceTableColumnDo> transform(List<ApiConditionPo> apiConditions, DataAssetApiPo dataAssetApi) {
        List<DatasourceTableColumnDo> result = new ArrayList<>(apiConditions.size());
        for (ApiConditionPo apiCondition : apiConditions) {
            DatasourceTableColumnDo tableColumn = new DatasourceTableColumnDo();
            tableColumn.setDatasourceTableColumnDesc(apiCondition.getDescriptions());
            tableColumn.setDatasourceTableColumnType(apiCondition.getAssetDatatype());
            tableColumn.setDatasourceTableName(dataAssetApi.getDataAssetName());
            tableColumn.setDatasourceTableColumnName(apiCondition.getAssetColumns());
            tableColumn.setDatasourceTableColumnLength(String.valueOf(apiCondition.getAssetColumnsLength()));
            result.add(tableColumn);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppAccessRuleDo storeAppAccessRule(AppAccessRuleDo dataAccessRule) {
        List<AppAccessRulePo> dataAccessRuleList = new ArrayList<>(dataAccessRule.getDataAccessRuleFieldList().size());
        for (String dataAccessRuleFieldName : dataAccessRule.getDataAccessRuleFieldList()) {
            AppAccessRulePo tDataAccessRule = new AppAccessRulePo();
            BeanUtils.copyProperties(dataAccessRule, tDataAccessRule);
            tDataAccessRule.setDataAssetFieldName(dataAccessRuleFieldName);
            dataAccessRuleList.add(tDataAccessRule);
        }
        if (CollectionUtils.isNotEmpty(dataAccessRuleList)) {
            this.batchCurdService.insertList(dataAccessRuleList, this.appAccessRuleMapper);
        } else {
            throw new OpenException(MsgCodeEnum.w_no_apply_column_or_already_have_permit);
        }
        return AppAccessRulePo.toDataAccessRuleDo(dataAccessRuleList);
    }


    @Override
    public Set<String> searchDataAppRuleOfApi(Integer dataAccessAppId, Integer dataAccessApiId) {
        try {
            return this.appAccessRuleMapper.searchDataAppRuleSetOfApi(dataAccessAppId, dataAccessApiId);
        } catch (Exception e) {
            log.error("获取数据接入APP {} 对API {} 拥有权限的列发生未知错误");
            throw new OpenException(MsgCodeEnum.w_dao_search_error);
        }
    }

    @Override
    public Boolean validateAppAccessRule(Set<String> dataAccessRuleReq, Set<String> dataAccessRuleResp, AppAccessRuleDo dataAccessRule) {
        Set<String> allowDataAccessRuleFieldNameSet = new HashSet<>(dataAccessRule.getDataAccessRuleFieldList());
        // 校验数据访问请求参数
        if (!validateDataAccessField(dataAccessRuleReq, allowDataAccessRuleFieldNameSet)) {
            log.error("不符合数据访问规则 {} 约束的请求参数 {}", dataAccessRule, dataAccessRuleReq);
            throw new OpenException(MsgCodeEnum.w_data_access_req_illegal);
        }
        // 校验数据访问响应参数
//        if (!validateDataAccessField(dataAccessRuleResp, allowDataAccessRuleFieldNameSet)) {
//            log.error("不符合数据访问规则 {} 约束的响应参数 {}", dataAccessRule, dataAccessRuleResp);
//            throw new OpenException(MsgCodeEnum.w_data_access_resp_illegal);
//        }
        return true;
    }

    @Override
    public int deleteAppAccessRuleOfAppId(Integer dataAccessAppId) {
        AppAccessRulePo deleteDataAccessRuleQuery = new AppAccessRulePo();
        deleteDataAccessRuleQuery.setDataAccessAppId(dataAccessAppId);
        return this.curdService.delete(deleteDataAccessRuleQuery, this.appAccessRuleMapper);
    }

    @Override
    public int delete(Integer dataAssetApiId) {
        AppAccessRuleDo t = new AppAccessRuleDo();
        t.setDataAssetApiId(dataAssetApiId);
        int delete = this.curdService.delete(t, this.appAccessRuleMapper);
        return delete;
    }

    @Override
    public boolean revokeAppAccessRule(List<AppAccessRuleDo> dataAccessRuleList) {
        for (AppAccessRuleDo appAccessRuleDo : dataAccessRuleList) {
            Integer dataAccessAppId = appAccessRuleDo.getDataAccessAppId();
            Integer dataAssetApiId = appAccessRuleDo.getDataAssetApiId();
            List<String> dataAccessRuleFieldList = appAccessRuleDo.getDataAccessRuleFieldList();
            for (String field : dataAccessRuleFieldList) {
                AppAccessRulePo appAccessRulePo = new AppAccessRuleDo();
                appAccessRulePo.setDataAccessAppId(dataAccessAppId);
                appAccessRulePo.setDataAssetApiId(dataAssetApiId);
                appAccessRulePo.setDataAssetFieldName(field);
                this.curdService.delete(appAccessRulePo, appAccessRuleMapper);
            }
            //如果权限全部收回,则删除app和api的关联信息
            AppAccessRulePo appAccessRulePo = new AppAccessRuleDo();
            appAccessRulePo.setDataAccessAppId(dataAccessAppId);
            appAccessRulePo.setDataAssetApiId(dataAssetApiId);
            int i = this.appAccessRuleMapper.selectCount(appAccessRulePo);
            if (i == 0) {
                this.appAccessRuleMapper.deleteByApiAppId(dataAccessAppId,dataAssetApiId);
            }
        }
        return true;
    }

    @Override
    public int delete(Integer dataAssetApiId, Integer dataAccessAppId) {
        AppAccessRulePo appAccessRulePo = new AppAccessRulePo();
        appAccessRulePo.setDataAccessAppId(dataAccessAppId);
        appAccessRulePo.setDataAssetApiId(dataAssetApiId);
        int delete = this.curdService.delete(appAccessRulePo, this.appAccessRuleMapper);
        return delete;
    }

    @Override
    public void deleteAppAccessRuleOfApiId(Integer dataAssetApiId) {
        AppAccessRulePo appAccessRulePo = new AppAccessRulePo();
        appAccessRulePo.setDataAssetApiId(dataAssetApiId);
        this.curdService.delete(appAccessRulePo, this.appAccessRuleMapper);
    }

    @Override
    public Boolean batchDeleteAppAccessRuleOfApiId(List<Integer> dataAssetApiIds) {
        return appAccessRuleMapper.batchDeleteByApiIds(dataAssetApiIds);
    }

    private boolean validateDataAccessField(Set<String> dataAccessRuleFieldSet, Set<String> allowDataAccessRuleFieldNameSet) {
        for (String dataAccessRuleFieldName : dataAccessRuleFieldSet) {
            if (!allowDataAccessRuleFieldNameSet.contains(dataAccessRuleFieldName)) {
                log.info("数据资产列 {} 不存在于访问规则列表 {} 中", dataAccessRuleFieldName, allowDataAccessRuleFieldNameSet);
                return false;
            }
        }
        return true;
    }
}