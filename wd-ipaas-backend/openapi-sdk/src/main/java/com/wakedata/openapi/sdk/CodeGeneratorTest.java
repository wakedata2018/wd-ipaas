package com.wakedata.openapi.sdk;

import cn.hutool.core.util.IdUtil;
import com.wakedata.openapi.sdk.generator.CodeGenerator;
import com.wakedata.openapi.sdk.generator.param.Api;
import com.wakedata.openapi.sdk.generator.param.ApiField;
import com.wakedata.openapi.sdk.generator.param.ApiGroup;
import com.wakedata.openapi.sdk.generator.param.TemplateParam;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器测试
 * @author luomeng
 * @date 2022/10/26 20:49
 */
@Slf4j
public class CodeGeneratorTest {

    public static void main(String[] args) {
        //生成目录
        String generatorDir = "D:\\sdk-generator\\";
        //模板参数
        TemplateParam param = new TemplateParam();
        param.setProjectName("ipaas");
        param.setProjectVersion("R2.1.0-S23-RC01");
        param.setAppKey(IdUtil.fastSimpleUUID());
        param.setAppSecret(IdUtil.fastSimpleUUID());
        param.setApiRequestHost("https://ipaas.wakedt.cn");

//        List<ApiGroup> apiGroupList = getApiGroupList();
        param.setApiGroupList(new ArrayList<>());
        //生成sdk
        File file = CodeGenerator.startGenerator(generatorDir,param);
        log.info("生成sdk地址：{}",file.getPath());
    }

    private static List<ApiGroup> getApiGroupList() {
        List<ApiGroup> apiGroupList = new ArrayList<>();

        ApiGroup apiGroup1 = new ApiGroup();
        apiGroup1.setName("Organization");
        apiGroup1.setDesc("组织架构接口分组");
        apiGroup1.setGroupPackage("organization");

        List<Api> apiList1 = new ArrayList<>();
        Api api1 = new Api();
        api1.setClassName("OrganizationAttr");
        api1.setPathName("ORGANIZATION_ATTR");
        api1.setDesc("组织属性查询");
        api1.setUrl("organization/query.attr");
        api1.setMethod("GET");
        api1.setGroupPackage("organization");
        api1.setGroupName("Organization");
        api1.setApiPackage("attr");
        api1.setContainBody(false);
        api1.setContainHead(false);
        api1.setContainParam(true);
        List<ApiField> apiFieldList = new ArrayList<>();

        apiFieldList.add(new ApiField("String","name","名称"));
        apiFieldList.add(new ApiField("Long","id","唯一id"));

        ApiField apiField = new ApiField();
        apiField.setName("itemList");
        apiField.setType("List<Object>");
        apiField.setDesc("子项");
        apiField.setItemClassName("ItemList");
        List<ApiField> itemApiFieldList = new ArrayList<>();
        itemApiFieldList.add(new ApiField("String","name1","名称"));
        itemApiFieldList.add(new ApiField("Long","id","唯一id"));

        ApiField apiField2 = new ApiField();
        apiField2.setName("itemList2");
        apiField2.setType("Object");
        apiField2.setDesc("子项");
        apiField2.setItemClassName("ItemList2");
        List<ApiField> itemApiFieldList2 = new ArrayList<>();
        itemApiFieldList2.add(new ApiField("Long","id2","唯一id"));
        apiField2.setItemFieldList(itemApiFieldList2);

        itemApiFieldList.add(apiField2);
        apiField.setItemFieldList(itemApiFieldList);
        apiFieldList.add(apiField);

        api1.setReqParamList(apiFieldList);
        api1.setContainRespBody(true);
        api1.setRespBodyList(apiFieldList);
        api1.setContainRespHead(true);
        api1.setRespHeadList(apiFieldList);

        apiList1.add(api1);

        apiGroup1.setApiList(apiList1);
        apiGroupList.add(apiGroup1);

        ApiGroup apiGroup = new ApiGroup();
        apiGroup.setName("Member");
        apiGroup.setDesc("会员接口分组");
        apiGroup.setGroupPackage("member");

        List<Api> apiList = new ArrayList<>();
        Api api = new Api();
        api.setClassName("MemberPoint");
        api.setPathName("MEMBER_POINT");
        api.setDesc("会员积分查询");
        api.setUrl("member/query.point");
        api.setMethod("POST");
        api.setGroupPackage("member");
        api.setGroupName("Member");
        api.setApiPackage("point");
        api.setContainBody(true);
        api.setReqBodyList(apiFieldList);
        api.setContainHead(true);
        api.setReqHeadList(apiFieldList);
        api.setContainParam(true);
        api.setReqParamList(apiFieldList);
        api.setContainRespBody(true);
        api.setRespBodyList(apiFieldList);
        api.setContainRespHead(true);
        api.setRespHeadList(apiFieldList);

        apiList.add(api);
        apiGroup.setApiList(apiList);
        apiGroupList.add(apiGroup);
        return apiGroupList;
    }
}
