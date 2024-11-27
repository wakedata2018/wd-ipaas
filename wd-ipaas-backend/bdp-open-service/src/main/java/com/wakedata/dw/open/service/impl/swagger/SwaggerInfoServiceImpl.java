package com.wakedata.dw.open.service.impl.swagger;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.SwaggerExecuteStatusEnum;
import com.wakedata.dw.open.enums.SwaggerImportTypeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.swagger.SwaggerInfoMapper;
import com.wakedata.dw.open.model.api.AbstractApiAttr;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.swagger.SwaggerInfoPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.impl.swagger.helper.SwaggerInfoHelper;
import com.wakedata.dw.open.service.impl.swagger.helper.TemporaryApiParseUtil;
import com.wakedata.dw.open.service.listener.SwaggerRespParamParsedEvent;
import com.wakedata.dw.open.service.swagger.SwaggerApiService;
import com.wakedata.dw.open.service.swagger.SwaggerInfoService;
import com.wakedata.dw.open.service.swagger.dto.SwaggerInfoQueryDTO;
import com.wakedata.dw.open.service.vo.ApiDetailVo;
import com.wakedata.dw.open.swagger.SwaggerApiDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Swagger信息服务实现
 *
 * @author chenshaopeng
 * date 2021/11/1
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SwaggerInfoServiceImpl extends BaseServiceImpl<SwaggerInfoPo, SwaggerInfoMapper> implements SwaggerInfoService {

    @Autowired
    @Override
    protected void init(CurdService<SwaggerInfoPo> curdService, SwaggerInfoMapper mapper) {
        super.set(curdService, mapper);
    }

    @Resource
    private ApiGroupService apiGroupService;

    @Resource
    private DataAssetApiService dataAssetApiService;

    @Resource
    private SwaggerInfoHelper swaggerInfoHelper;

    @Resource
    private SwaggerApiService swaggerApiService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void addSwaggerInfo(SwaggerInfoPo swaggerInfo) {
        this.checkAddSwaggerInfo(swaggerInfo, Boolean.FALSE);
        swaggerInfo.setApiAmount(SwaggerInfoPo.DEFAULT_API_AMOUNT);
        swaggerInfo.setExecuteStatus(SwaggerExecuteStatusEnum.UN_EXECUTE.getValue());
        swaggerInfo.setImportType(SwaggerImportTypeEnum.URL);
        super.add(swaggerInfo);
    }

    @Override
    public SwaggerInfoPo addSwaggerInfoFromFile(SwaggerInfoPo swaggerInfo) {
        readSwaggerJsonFromFile(swaggerInfo);
        this.checkAddSwaggerInfo(swaggerInfo, Boolean.TRUE);
        swaggerInfo.setApiAmount(SwaggerInfoPo.DEFAULT_API_AMOUNT);
        swaggerInfo.setExecuteStatus(SwaggerExecuteStatusEnum.UN_EXECUTE.getValue());
        swaggerInfo.setImportType(SwaggerImportTypeEnum.FILE);
        super.add(swaggerInfo);
        // 返回的SwaggerInfoPo把swaggerJson置空，不传输大数据
        Integer id = swaggerInfo.getId();
        SwaggerInfoPo swaggerInfoPo = detail(id);
        swaggerInfoPo.setSwaggerJson(null);
        swaggerInfoHelper.getSwaggerApiCount(id, swaggerInfoPo);
        FileUtil.del(swaggerInfo.getTempFilePath());
        return swaggerInfoPo;
    }

    @Override
    public void updateSwaggerInfo(SwaggerInfoPo swaggerInfo) {
        SwaggerInfoPo check = checkUpdateSwaggerInfo(swaggerInfo, Boolean.FALSE);
        swaggerInfo.setApiGroupId(check.getApiGroupId());
        swaggerInfo.setCreateUser(check.getCreateUser());
        swaggerInfo.setCreateTime(check.getCreateTime());
        swaggerInfo.setImportType(SwaggerImportTypeEnum.URL);
        super.update(swaggerInfo);
    }

    @Override
    public SwaggerInfoPo updateSwaggerInfoFromFile(SwaggerInfoPo swaggerInfo) {
        SwaggerInfoPo check = checkUpdateSwaggerInfo(swaggerInfo, Boolean.TRUE);
        readSwaggerJsonFromFile(swaggerInfo);

        swaggerInfo.setApiGroupId(check.getApiGroupId());
        swaggerInfo.setCreateUser(check.getCreateUser());
        swaggerInfo.setCreateTime(check.getCreateTime());
        swaggerInfo.setImportType(SwaggerImportTypeEnum.FILE);
        super.update(swaggerInfo);
        // 返回的SwaggerInfoPo把swaggerJson置空，不传输大数据
        Integer id = swaggerInfo.getId();
        SwaggerInfoPo swaggerInfoPo = detail(id);
        swaggerInfoPo.setSwaggerJson(null);
        swaggerInfoHelper.getSwaggerApiCount(id, swaggerInfoPo);
        FileUtil.del(swaggerInfo.getTempFilePath());
        return swaggerInfoPo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSwaggerInfo(Integer swaggerInfoId) {
        SwaggerInfoPo swaggerInfoPo = swaggerInfoHelper.checkForExist(swaggerInfoId);
        swaggerApiService.deleteBySwaggerId(swaggerInfoPo.getId());
        super.delete(swaggerInfoId);
    }

    @Override
    public SwaggerInfoPo detail(Integer swaggerInfoId) {
        SwaggerInfoPo show = super.show(swaggerInfoId);
        swaggerInfoHelper.getSwaggerApiCount(swaggerInfoId, show);
        return show;
    }

    @Override
    public PageResultDTO<List<SwaggerInfoPo>> queryPage(SwaggerInfoQueryDTO queryDTO) {
        try (Page<Object> page = PageHelper.startPage(queryDTO.getPageNo(), queryDTO.getPageSize())) {
            List<SwaggerInfoPo> swaggerInfoPos = getMapper().selectByExample(buildQueryPageExample(queryDTO));
            // 查询分组名称
            List<Integer> apiGroupIds = swaggerInfoPos.stream().map(SwaggerInfoPo::getApiGroupId).collect(Collectors.toList());
            Map<Integer, String> apiGroupMap;
            if (CollectionUtil.isNotEmpty(apiGroupIds)) {
                apiGroupMap = apiGroupService.queryApiGroupListByIds(apiGroupIds).stream().collect(Collectors.toMap(ApiGroupPo::getId, ApiGroupPo::getGroupName));
            } else {
                apiGroupMap = new HashMap<>(16);
            }
            for (SwaggerInfoPo swaggerInfoPo : swaggerInfoPos) {
                // 查询api数量
                swaggerInfoHelper.getSwaggerApiCount(swaggerInfoPo.getId(), swaggerInfoPo);
                swaggerInfoPo.setApiGroupName(apiGroupMap.getOrDefault(swaggerInfoPo.getApiGroupId(), null));
            }
            PageResultDTO<List<SwaggerInfoPo>> resultDTO = new PageResultDTO<>();
            resultDTO.setData(swaggerInfoPos);
            resultDTO.setPageNo(page.getPageNum());
            resultDTO.setPageSize(page.getPageSize());
            resultDTO.setTotalCount(Long.valueOf(page.getTotal()).intValue());
            return resultDTO;
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void updateSwaggerApi(Integer swaggerId, String createUser) throws Exception {
        SwaggerInfoPo swaggerInfo = swaggerInfoHelper.checkForExist(swaggerId);
        ApiGroupPo apiGroupPo = apiGroupService.checkForExist(swaggerInfo.getApiGroupId());
        Integer apiGroupId = apiGroupPo.getId();

        // 实例化并转换swagger api数据
        SwaggerImportTypeEnum swaggerImportType = swaggerInfo.getImportType() == null ? SwaggerImportTypeEnum.URL : swaggerInfo.getImportType();
        SwaggerApiDetail swaggerApiDetail;
        if (SwaggerImportTypeEnum.URL == swaggerInfo.getImportType()) {
            swaggerApiDetail = new SwaggerApiDetail(swaggerInfo.getSwaggerUrl(), apiGroupPo.getGroupPath(), null, swaggerImportType);
        } else {
            swaggerApiDetail = new SwaggerApiDetail(null, apiGroupPo.getGroupPath(), swaggerInfo.getSwaggerJson(), swaggerImportType);
        }

        swaggerApiDetail.setApiDomainName(swaggerInfo.getApiDomainName());
        swaggerApiDetail.transition();

        List<ApiDetailVo> apiDetailVoList = swaggerApiDetail.getApiDetailList();
        // 汇总API Path，从API表中查询API数据
        Map<String, DataAssetApiPo> dataAssetApiMap;
        List<String> dataAssetApiMethodList = apiDetailVoList.stream()
                .map(apiDetailVo -> apiDetailVo.getDataAssetApi().getDataAssetApiMethod()).collect(Collectors.toList());
        List<DataAssetApiPo> dataAssetApiPoList = dataAssetApiService.findByDataAssetApiMethodList(dataAssetApiMethodList);
        if (CollectionUtil.isEmpty(dataAssetApiPoList)) {
            dataAssetApiMap = new HashMap<>(16);
        } else {
            dataAssetApiMap = dataAssetApiPoList.stream().collect(Collectors.toMap(DataAssetApiPo::getDataAssetApiMethod, apiPo -> apiPo, (oldVal, newVal) -> newVal));
        }

        //保存所有api的AbstractApiAttr，以便可以通过事件发布
        List<AbstractApiAttr> httpApiAttrs = new ArrayList<>(apiDetailVoList.size());
        for (ApiDetailVo apiDetailVo : apiDetailVoList) {
            // 判断API Path是否存在数据库中，如果是则不更新数据，防止参数出现变更
            if (dataAssetApiMap.containsKey(apiDetailVo.getDataAssetApi().getDataAssetApiMethod())) {
                continue;
            }
            // 保存API信息
            apiDetailVo.getDataAssetApi().setApiGroupId(apiGroupId);
            apiDetailVo.getDataAssetApi().setInCharge(createUser);
            dataAssetApiService.addApi(apiDetailVo);

            httpApiAttrs.add(apiDetailVo.getDataAssetApi().getApiAttr());
        }

        //发布swagger解析完成事件
        if (CollectionUtil.isNotEmpty(httpApiAttrs)) {
            applicationEventPublisher.publishEvent(new SwaggerRespParamParsedEvent(this, httpApiAttrs));
        }
        // 更新swagger信息
        swaggerInfo.setApiGroupId(apiGroupId);
        swaggerInfo.setApiAmount(apiDetailVoList.size());
        swaggerInfo.setUpdateTime(new Date());
        super.update(swaggerInfo);
    }

    /**
     * 检查Api外部域名
     */
    private void checkApiDomainName(SwaggerInfoPo swaggerInfo) {
        if (Objects.nonNull(swaggerInfo.getApiDomainName())) {
            URI uri = URLUtil.toURI(swaggerInfo.getApiDomainName());
            if (Objects.isNull(uri.getHost()) || Objects.isNull(uri.getScheme())) {
                throw new OpenException(MsgCodeEnum.w_domain_name_error);
            }
            if (StringUtils.isNoneBlank(uri.getPath())) {
                throw new OpenException(MsgCodeEnum.w_domain_name_path_is_not_allowed_error);
            }
            String port = uri.getPort() != -1 ? ":" + uri.getPort() : "";
            String finalDomainName = uri.getScheme() + "://" + uri.getHost() + port;
            swaggerInfo.setApiDomainName(finalDomainName);
        }
    }

    /**
     * 检查Swagger名称
     */
    private void checkSwaggerName(SwaggerInfoPo swaggerInfo) {
        SwaggerInfoPo po = SwaggerInfoPo.builder()
                .swaggerName(swaggerInfo.getSwaggerName())
                .lesseeId(swaggerInfo.getLesseeId())
                .build();
        if (super.find(po).size() > 0) {
            throw new OpenException(MsgCodeEnum.w_swagger_name_exists);
        }
    }

    /**
     * 检查Swagger Url
     */
    private void checkSwaggerUrl(SwaggerInfoPo swaggerInfo) {
        String swaggerUrl = swaggerInfo.getSwaggerUrl();
        // SwaggerInfo路径去除前后空格
        swaggerUrl = StringUtils.trim(swaggerUrl);
        SwaggerInfoPo po = SwaggerInfoPo.builder()
                .swaggerUrl(swaggerUrl)
                .lesseeId(swaggerInfo.getLesseeId())
                .build();
        if (super.find(po).size() > 0) {
            throw new OpenException(MsgCodeEnum.w_swagger_url_exists);
        }
    }

    /**
     * 检查接口分类
     *
     * @param apiGroupId 接口分类id
     */
    private void checkApiGroup(Integer apiGroupId) {
        ApiGroupPo apiGroupPo = apiGroupService.show(apiGroupId);
        if (apiGroupPo == null) {
            throw new OpenException((MsgCodeEnum.w_api_group_not_exists));
        }
    }

    /**
     * 构建分页查询swagger信息Example
     *
     * @param queryDTO Swagger分页查询条件DTO
     * @return Example
     */
    private Example buildQueryPageExample(SwaggerInfoQueryDTO queryDTO) {
        WeekendSqls<SwaggerInfoPo> sqls = WeekendSqls.custom();
        sqls.andEqualTo(SwaggerInfoPo::getLesseeId, queryDTO.getLesseeId());
        if (StringUtils.isNotBlank(queryDTO.getSwaggerName())) {
            sqls.andLike(SwaggerInfoPo::getSwaggerName, "%" + queryDTO.getSwaggerName() + "%");
        }
        if (queryDTO.getApiGroupId() != null) {
            sqls.andEqualTo(SwaggerInfoPo::getApiGroupId, queryDTO.getApiGroupId());
        }
        if (queryDTO.getUpdateTimeStart() != null && queryDTO.getUpdateTimeEnd() != null) {
            sqls.andBetween(SwaggerInfoPo::getUpdateTime, queryDTO.getUpdateTimeStart(), queryDTO.getUpdateTimeEnd());
        }
        return Example.builder(SwaggerInfoPo.class)
                .select("id", "swaggerName", "description", "swaggerUrl", "apiGroupId",
                        "apiDomainName", "createUser", "lesseeId", "executeStatus", "apiAmount",
                        "importType", "respMappingRule", "parseTime", "createTime", "updateTime")
                .where(sqls)
                .build();
    }

    /**
     * 新增Swagger信息前验证参数
     *
     * @param swaggerInfo Swagger信息
     * @param isFromFile  是否通过上传文件添加，true:是、false：否
     */
    private void checkAddSwaggerInfo(SwaggerInfoPo swaggerInfo, Boolean isFromFile) {
        this.checkApiDomainName(swaggerInfo);
        this.checkSwaggerName(swaggerInfo);
        this.checkApiGroup(swaggerInfo.getApiGroupId());
        // 如果是通过Swagger Url的方式导入，才校验url参数
        if (!isFromFile) {
            this.checkSwaggerUrl(swaggerInfo);
        }
    }

    /**
     * 修改Swagger信息前验证参数
     *
     * @param swaggerInfo Swagger信息
     * @param isFromFile  是否通过上传文件添加，true:是、false：否
     */
    private SwaggerInfoPo checkUpdateSwaggerInfo(SwaggerInfoPo swaggerInfo, Boolean isFromFile) {
        this.checkApiGroup(swaggerInfo.getApiGroupId());
        SwaggerInfoPo check = swaggerInfoHelper.checkForExist(swaggerInfo.getId());

        if (Objects.nonNull(swaggerInfo.getApiDomainName())
                && !swaggerInfo.getApiDomainName().equals(check.getApiDomainName())) {
            this.checkApiDomainName(swaggerInfo);
        }
        if (!check.getSwaggerName().equals(swaggerInfo.getSwaggerName())) {
            this.checkSwaggerName(swaggerInfo);
        }
        if (!isFromFile && !check.getSwaggerUrl().equals(swaggerInfo.getSwaggerUrl())) {
            this.checkSwaggerUrl(swaggerInfo);
            swaggerInfo.setApiAmount(SwaggerInfoPo.DEFAULT_API_AMOUNT);
        } else {
            swaggerInfo.setApiAmount(check.getApiAmount());
        }
        return check;
    }

    /**
     * 读取上传的文件，获取Swagger Json文本
     *
     * @param swaggerInfo Swagger信息对象
     */
    private void readSwaggerJsonFromFile(SwaggerInfoPo swaggerInfo) {
        String tempFilePath = swaggerInfo.getTempFilePath();
        swaggerInfo.setSwaggerJson(TemporaryApiParseUtil.parseJsonByTempFilepath(tempFilePath));
    }

}