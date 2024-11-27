package com.wakedata.dw.open.service.impl.xxljob;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.AppAccessMapper;
import com.wakedata.dw.open.mapper.xxljob.XxlJobMapper;
import com.wakedata.dw.open.model.query.XxlJobQuery;
import com.wakedata.dw.open.model.xxljob.XxlJobDO;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.dto.XxlJobDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.impl.xxljob.helper.XxlJobTaskHelper;
import com.wakedata.dw.open.service.impl.xxljob.helper.XxlJobTaskParamHelper;
import com.wakedata.dw.open.service.impl.xxljob.helper.XxlJobTaskScheduleFiledHelper;
import com.wakedata.dw.open.service.xxljob.XxlJobService;
import com.wakedata.dw.open.service.xxljob.XxlJobTaskScheduleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author WangChenSheng
 * @descriptor 定时任务
 * @title XxlJobServiceImpl
 * @date 2022/10/25 14:55
 */
@Service
public class XxlJobServiceImpl extends BaseServiceImpl<XxlJobDO, XxlJobMapper> implements XxlJobService {

    @Resource
    private XxlJobTaskHelper xxlJobTaskHelper;

    @Resource
    private XxlJobTaskParamHelper xxlJobTaskParamHelper;

    @Resource
    private XxlJobTaskScheduleService xxlJobTaskScheduleService;

    @Resource
    private XxlJobTaskScheduleFiledHelper xxlJobTaskScheduleFiledHelper;

    @Resource
    private AppAccessMapper appAccessMapper;

    @Resource
    private XxlJobMapper xxlJobMapper;

    @Autowired
    @Override
    protected void init(CurdService<XxlJobDO> curdService, XxlJobMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public PageResultDTO<List<XxlJobDTO>> pageList(XxlJobQuery query) {
        PageResultDTO<List<XxlJobDTO>> resultDTO = new PageResultDTO<>();
        PageHelper.startPage(query.getPageNo(), query.getPageSize());

        List<XxlJobDO> xxlJobDOList = this.getMapper().selectPageList(query);
        if (xxlJobDOList.isEmpty()){
            return resultDTO;
        }

        // 解析请求参数
        Map<Integer, String> map = xxlJobTaskHelper.getAppName(query);
        for (XxlJobDO xxlJobDO : xxlJobDOList) {
            xxlJobTaskParamHelper.convertParamsToObject(xxlJobDO);
            xxlJobDO.setDataAssetAppName(map.get(xxlJobDO.getDataAssetAppId()));
        }

        PageInfo<XxlJobDO> pageInfo = new PageInfo<>(xxlJobDOList);
        List<XxlJobDO> list = pageInfo.getList();
        List<XxlJobDTO> dataList = BeanUtil.copyList(list, XxlJobDTO.class);

        resultDTO.setData(dataList);
        resultDTO.setPageNo(query.getPageNo());
        resultDTO.setPageSize(query.getPageSize());
        resultDTO.setTotalCount(new Long(pageInfo.getTotal()).intValue());
        return resultDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> addXxlJob(XxlJobDTO xxlJobDTO){

        xxlJobTaskHelper.checkXxlJobCron(xxlJobDTO);
        xxlJobTaskHelper.checkApiInfo(xxlJobDTO);
        if (StringUtils.isEmpty(xxlJobDTO.getXxlTimeStamp())){
            throw new OpenException(MsgCodeEnum.w_xxl_job_time_stamp_is_null);
        }

        // 新增任务信息
        XxlJobDO xxlJobDO = BeanUtil.copy(xxlJobDTO, XxlJobDO.class);
        xxlJobTaskParamHelper.convertParamsToJson(xxlJobDTO,xxlJobDO);
        xxlJobMapper.addXxlJob(xxlJobDO);
        // 新增缓存
        RedisUtil.getInstance().set(
                xxlJobTaskHelper.getTaskCode(xxlJobDO.getXxlTimeStamp(),xxlJobDO.getId()),
                String.valueOf(xxlJobDTO.getTaskExecuteAmount()));
        // 新增xxlJob调度中心任务信息
        xxlJobDTO.setId(xxlJobDO.getId());
        Map<String, Object> addParams = xxlJobTaskScheduleFiledHelper.initAddParams(xxlJobDTO);
        xxlJobTaskScheduleService.addXxlJobTaskScheduleInfo(addParams);

        return ResultDTO.success(Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> updateXxlJob(XxlJobDTO xxlJobDTO){

        xxlJobTaskHelper.checkXxlJobCron(xxlJobDTO);
        xxlJobTaskHelper.checkApiInfo(xxlJobDTO);
        if (Objects.isNull(xxlJobDTO.getId())){
            throw new OpenException(MsgCodeEnum.w_xxl_job_id_is_null);
        }
        XxlJobDO xxlJobInfo = xxlJobTaskHelper.queryXxlJobInfo(xxlJobDTO.getId());

        // 更新任务信息
        XxlJobDO xxlJobDO = BeanUtil.copy(xxlJobDTO, XxlJobDO.class);
        xxlJobTaskParamHelper.convertParamsToJson(xxlJobDTO,xxlJobDO);
        super.update(xxlJobDO);
        // 更新调度中心任务信息
        Map<String, Object> updateParams = xxlJobTaskScheduleFiledHelper.initUpdateParams(xxlJobDTO,xxlJobInfo.getXxlTimeStamp());
        xxlJobTaskScheduleService.updateXxlJobTaskScheduleInfo(updateParams);

        return ResultDTO.success(Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> deleteXxlJob(Integer id){
        if (Objects.isNull(id)){
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        XxlJobDO xxlJobInfo = xxlJobTaskHelper.queryXxlJobInfo(id);

        // 删除任务信息
        this.getMapper().deleteByPrimaryKey(id);
        // 删除缓存
        RedisUtil.getInstance().del(xxlJobTaskHelper.getTaskCode(xxlJobInfo.getXxlTimeStamp(),xxlJobInfo.getId()));
        // 删除指定调度中心任务信息
        Map<String, Object> deleteParams = xxlJobTaskScheduleFiledHelper.initDeleteParams(id, xxlJobInfo.getXxlTimeStamp());
        xxlJobTaskScheduleService.deleteXxlJobTaskScheduleInfo(deleteParams);

        return ResultDTO.success(Boolean.TRUE);
    }

}
