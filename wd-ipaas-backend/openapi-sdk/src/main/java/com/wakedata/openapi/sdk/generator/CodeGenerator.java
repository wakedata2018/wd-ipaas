package com.wakedata.openapi.sdk.generator;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.openapi.sdk.generator.param.Api;
import com.wakedata.openapi.sdk.generator.param.ApiGroup;
import com.wakedata.openapi.sdk.generator.param.TemplateParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 代码生成
 *
 * @author luomeng
 * @date 2022/10/25 18:54
 */
@Slf4j
public class CodeGenerator {

    /**
     * 开始生成代码
     *
     * @param generatorDir  生成目录
     * @param templateParam 模板参数
     * @return
     */
    public static File startGenerator(String generatorDir, TemplateParam templateParam) {
        VelocityContext context = createVelocityContext(templateParam);
        //生成项目sdk-公共文件
        codeGenerator(context, CodeTemplate.COMMON_TEMPLATE, generatorDir, CodeTemplate.TEMPLATE_PROJECT_CODE_DIR, templateParam.getProjectName());
        //生成项目sdk-pom文件
        codeGenerator(context, CodeTemplate.XML_TEMPLATE, generatorDir, null, templateParam.getProjectName());
        //生成项目sdk-api文件
        codeGenerator(context, CodeTemplate.API_TEMPLATE, generatorDir, CodeTemplate.TEMPLATE_PROJECT_CODE_DIR + CodeTemplate.TEMPLATE_PROJECT_CODE_API_DIR, templateParam.getProjectName(), templateParam.getApiGroupList());
        //打包成zip压缩包
        String sdkPath = generatorDir + CodeTemplate.TEMPLATE_PROJECT_DIR.replace(CodeTemplate.TEMPLATE_PROJECT_NAME,templateParam.getProjectName())
                .replace(File.separator, "");
        File file = ZipUtil.zip(sdkPath,sdkPath + ".zip", StandardCharsets.UTF_8,true);
        //删除多余的文件
        FileUtil.clean(sdkPath);
        return file;
    }

    /**
     * 创建velocity模板上下文
     *
     * @param templateParam 模板参数
     * @return
     */
    private static VelocityContext createVelocityContext(TemplateParam templateParam) {
        initVelocityProperties();
        templateParam.setDateTime(DateUtil.formatDateTime(new Date()));
        return new VelocityContext(JSONUtil.toBean(JSONUtil.toJsonStr(templateParam), Map.class));
    }

    /**
     * 初始化velocity模板属性
     */
    private static void initVelocityProperties() {
        Properties p = new Properties();
        //设置velocity资源加载方式为class
        p.setProperty("resource.loader", "class");
        //设置velocity资源加载方式为file时的处理类
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.put("input.encoding", StandardCharsets.UTF_8.name());
        p.put("output.encoding", StandardCharsets.UTF_8.name());
        Velocity.init(p);
    }


    /**
     * 生成项目sdk-基础文件
     *
     * @param context          模板上下文
     * @param templatePathList 模板路径
     * @param generatorDir     生成路径
     * @param templatePath     模板生成地址
     * @param projectName      生成项目名
     */
    private static void codeGenerator(VelocityContext context, List<String> templatePathList, String generatorDir, String templatePath, String projectName) {

        for (String template : templatePathList) {
            //拼接模板路径
            template = CodeTemplate.TEMPLATE_PROJECT_DIR + ObjectUtil.defaultIfNull(templatePath, "") + template;
            //生成文件路径
            String filePath = generatorDir + template.replace(CodeTemplate.TEMPLATE_PROJECT_NAME, projectName)
                    .replace(CodeTemplate.TEMPLATE_FILE_SUFFIX, "");
            //生成文件目录
            String fileDir = filePath.replace(filePath.substring(filePath.lastIndexOf(File.separator) + 1), "");
            File directory = new File(fileDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            try {
                //使用模板生成文件内容
                FileWriter writer = new FileWriter(filePath);
                Template tpl = Velocity.getTemplate(CodeTemplate.TEMPLATE_DIR + template, StandardCharsets.UTF_8.name());
                tpl.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                log.error("生成项目基础代码失败,", e);
            }
        }

    }


    /**
     * 生成项目sdk - api文件
     *
     * @param context          模板上下文
     * @param templatePathList 模板路径
     * @param generatorDir     生成路径
     * @param templatePath     模板生成地址
     * @param projectName      生成项目名
     * @param apiGroupList     api数据
     */
    private static void codeGenerator(VelocityContext context, List<String> templatePathList, String generatorDir, String templatePath, String projectName, List<ApiGroup> apiGroupList) {

        for(ApiGroup apiGroup : apiGroupList){
            for(Api api : apiGroup.getApiList()){
                //设置分组信息
                api.setGroupName(apiGroup.getName());
                api.setGroupPackage(apiGroup.getGroupPackage());
                //设置上下文参数
                context.put(TemplateParam.TEMPLATE_PARAM_API,api);
                context.internalPut(TemplateParam.TEMPLATE_PARAM_API,api);
                //生成api代码
                codeGenerator(context, templatePathList, generatorDir, templatePath, projectName, api);

            }

        }

    }

    /**
     * 生成api代码
     * @param context
     * @param templatePathList
     * @param generatorDir
     * @param templatePath
     * @param projectName
     * @param api
     */
    private static void codeGenerator(VelocityContext context, List<String> templatePathList, String generatorDir, String templatePath, String projectName, Api api) {
        for (String template : templatePathList) {
            //拼接模板路径
            template = CodeTemplate.TEMPLATE_PROJECT_DIR + templatePath + template;
            //生成文件路径
            String filePath = generatorDir + template.replace(CodeTemplate.TEMPLATE_PROJECT_NAME, projectName)
                    .replace(CodeTemplate.TEMPLATE_API_GROUP_PACKAGE, api.getGroupPackage() )
                    .replace(CodeTemplate.TEMPLATE_API_PACKAGE, api.getApiPackage())
                    .replace(CodeTemplate.TEMPLATE_API_CLASS_NAME, api.getClassName())
                    .replace(CodeTemplate.TEMPLATE_FILE_SUFFIX, "");
            //生成文件目录
            String fileDir = filePath.replace(filePath.substring(filePath.lastIndexOf(File.separator) + 1), "");
            File directory = new File(fileDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            //无对应位置参数不生成文件,响应体参数除外
            if (!isContainPositionParam(api, template)) {
                continue;
            }
            try {
                //使用模板生成文件内容
                FileWriter writer = new FileWriter(filePath);
                Template tpl = Velocity.getTemplate(CodeTemplate.TEMPLATE_DIR + template, StandardCharsets.UTF_8.name());
                tpl.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (Exception e) {
                log.error("生成项目api代码失败,", e);
            }
        }
    }

    /**
     * 是否包含指定位置参数
     * @param api
     * @param template
     * @return
     */
    private static boolean isContainPositionParam(Api api, String template) {
        if(template.contains(CodeTemplate.ApiTemplateEnum.REQUEST_BODY_TEMPLATE.getTemplate()) && !api.getContainBody()){
            return false;
        }
        if(template.contains(CodeTemplate.ApiTemplateEnum.REQUEST_HEAD_TEMPLATE.getTemplate()) && !api.getContainHead()){
            return false;
        }
        if(template.contains(CodeTemplate.ApiTemplateEnum.REQUEST_PARAM_TEMPLATE.getTemplate()) && !api.getContainParam()){
            return false;
        }
        if(template.contains(CodeTemplate.ApiTemplateEnum.RESPONSE_HEAD_TEMPLATE.getTemplate()) && !api.getContainRespHead()){
            return false;
        }
        return true;
    }
}
