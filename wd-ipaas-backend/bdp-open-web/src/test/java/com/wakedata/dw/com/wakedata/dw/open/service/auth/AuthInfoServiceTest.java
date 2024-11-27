package com.wakedata.dw.com.wakedata.dw.open.service.auth;

import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.open.model.auth.AuthInfoPo;
import com.wakedata.dw.open.service.auth.AuthInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * AuthInfoService单元测试
 *
 * @author wujunqiang
 * @date 2021/12/10 16:23
 */
@RunWith(SpringRunner.class)
public class AuthInfoServiceTest extends DwOpenWebApplicationTest {

    @Autowired
    private AuthInfoService authInfoService;

    @Test
    public void detailTest() {
        System.out.println(authInfoService.detail(20));
    }

    @Test
    public void selectByIdsTest() {
        List<Integer> idList = Arrays.asList(20, 24, 29);
        List<AuthInfoPo> authInfoPos = authInfoService.selectByIds(idList);
        authInfoPos.forEach(System.out::println);
    }

}
