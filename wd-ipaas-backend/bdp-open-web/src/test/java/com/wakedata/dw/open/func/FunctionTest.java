package com.wakedata.dw.open.func;

import cn.hutool.json.JSONUtil;
import com.wakedata.dw.DwOpenWebApplication;
import com.wakedata.dw.open.function.FuncExecutor;
import com.wakedata.dw.open.function.FunctionEnumUtil;
import com.wakedata.dw.open.function.FunctionVo;
import com.wakedata.dw.open.service.lessee.OpenAccountService;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author luomeng
 * @date 2022/8/3 16:09
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DwOpenWebApplication.class)
public class FunctionTest {


    @Test
    public void testCustomFunction(){

        String expression = "fn.custom.addNum(10,20)";
        Object result = FuncExecutor.getInstance().exec(null, expression);
        assertEquals(30,result);
    }

    @Test
    public void testCustomStringFunction(){

        String expression = "fn.custom.addNum(fn.math.negateExact(10),20)";
        Object result = FuncExecutor.getInstance().exec(null, expression);
        assertEquals(10,result);

        String expression2 = "fn.custom.addNum(10,fn.math.addExact(21,-1))";
        Object result2 = FuncExecutor.getInstance().exec(null, expression2);
        assertEquals(30,result2);

        String funcExpression = "fn.string.toString(fn.custom.addNum(10,fn.math.addExact(21,-1)))";
        String result3 = (String) FuncExecutor.getInstance().exec(null, funcExpression);
        assertEquals("30", result3);
    }


    @Test
    public void testSupportFunctionList(){
        List<FunctionVo> supportFunctionList = FunctionEnumUtil.getSupportFunctionList();
        log.info("list:{}", JSONUtil.toJsonPrettyStr(supportFunctionList));
    }
}
