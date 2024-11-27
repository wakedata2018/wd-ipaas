package com.wakedata.dw.lowcode.service.impl;

import cn.hutool.core.date.DateUtil;
import com.wakedata.dw.lowcode.common.LowCodeBatchMapper;
import com.wakedata.dw.lowcode.common.UniqueId;
import com.wakedata.dw.lowcode.model.AppPo;
import com.wakedata.dw.lowcode.service.LowCodeBatchService;
import com.wakedata.dw.lowcode.service.dto.BatchFetchLowCodeInfoDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.BasePo;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author wanghu@wakedata.com
 * @title 低代码批量服务实现类
 * @date 2021/12/1
 * @since v1.0.0
 */
public abstract class AbstractLowCodeBatchService<T extends BasePo, M extends Mapper<T>>
    extends BaseServiceImpl<T, M> implements LowCodeBatchService {

    @Override
    public <R extends AppPo & UniqueId> List<R> listByBatch(List<BatchFetchLowCodeInfoDTO> dtoList, Integer appId) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Collections.emptyList();
        }

        Set<String> names = dtoList.stream()
            .filter(dto -> StringUtils.isNotBlank(dto.getName()))
            .map(BatchFetchLowCodeInfoDTO::getName)
            .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(names)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }

        List<R> pos = getBatchMapper().listByBatch(names, appId);

        //name->dto
        Map<String, BatchFetchLowCodeInfoDTO> nameToParamMap = dtoList.stream()
            .collect(Collectors.toMap(BatchFetchLowCodeInfoDTO::getName, Function.identity(), (pre, next) -> pre));
        pos.removeIf(po -> {
            BatchFetchLowCodeInfoDTO dto = nameToParamMap.get(po.getName());
            //移除相同的更新时间的页面
            return Objects.nonNull(dto.getUpdateTime())
                && DateUtil.isSameTime(po.getUpdateTime(), dto.getUpdateTime());
        });
        return pos;
    }

    /**
     * 获取查询mapper
     */
    public abstract LowCodeBatchMapper getBatchMapper();

}
