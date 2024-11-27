package com.wakedata.dw.open.common.function;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wakedata.dw.open.function.FunctionEnumSet;
import org.junit.Test;


import static com.wakedata.dw.open.function.FunctionEnumUtil.getSupportFunctionList;

/**
 * @author luomeng
 * @date 2022/8/18 17:41
 */
public class FunctionEnumSetTests {

    @Test
    public void getAllFunctions() {
        System.out.println("result = " + JSONUtil.toJsonPrettyStr(getSupportFunctionList()));
    }

}
