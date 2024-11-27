package com.wakedata.dw.open.model.api;

import java.util.List;
import lombok.Data;

/**
 * @author caoshuang
 * @title AppAccessVo
 * @projectName bdp-open
 * @description appAccessListVo
 */
@Data
public class AppAccessListVo{
    List<AppAccessPo> appAccessVoList;
    List<Integer> defaultAppIdList;
}