package com.wakedata.dw.open.service.xxljob;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.XxlJobQuery;
import com.wakedata.dw.open.service.api.dto.XxlJobDTO;

import java.util.List;

/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobService
 * @date 2022/10/25 14:35
 */
public interface XxlJobService {

    /**
     * 条件查询XxlJob列表
     *
     * @param xxlJobQuery 查询条件
     * @return xxlJob列表
     */
    PageResultDTO<List<XxlJobDTO>> pageList(XxlJobQuery xxlJobQuery);

    /**
     * 新增XxlJob
     *
     * @param xxlJobDTO 新增条件
     * @return true/false
     */
    ResultDTO<Boolean> addXxlJob(XxlJobDTO xxlJobDTO);

    /**
     * 更新XxlJob
     *
     * @param xxlJobDTO 更新条件
     * @return true/false
     */
    ResultDTO<Boolean> updateXxlJob(XxlJobDTO xxlJobDTO);

    /**
     * 删除XxlJob
     *
     * @param id id
     * @return true/false
     */
    ResultDTO<Boolean> deleteXxlJob(Integer id);

}
