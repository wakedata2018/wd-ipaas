package com.wakedata.dw.open.service.impl.api.captcha.helper;

import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.impl.api.captcha.constants.CaptchaConstants;
import com.wakedata.dw.open.service.impl.api.captcha.utils.ImageVerificationUtil;
import com.wakedata.wd.permission.captcha.dto.ImageReadDTO;
import com.wakedata.wd.permission.captcha.dto.ImageVerificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

/***
 * @author： wangchensheng
 * @date： 2022-05-12 12:13
 * @version 1.0
 */
@Slf4j
@Component
public class CaptchaHelper {

    public ImageVerificationDTO selectSlideCode(String type, Integer width, Integer height) {
        ImageVerificationDTO imageVerificationDTO = new ImageVerificationDTO();
        imageVerificationDTO.setType(type);

        // 读取原图
        ImageReadDTO originImage = readTargetImage();
        // 读取模板图(碎片图模板)
        ImageReadDTO templateImage = readTemplateImage();
        // 读取边框图(碎片图模板边框)
        ImageReadDTO borderImage = readBorderImageFile();

        // 通过原图,模板图,边框图 ===> 原图,碎片图和遮罩图
        ImageVerificationUtil.generateCoordinates(imageVerificationDTO,templateImage.getImage(),width,height);
        ImageVerificationUtil.setRedis(imageVerificationDTO);
        ImageVerificationUtil.pictureTemplateCutout(imageVerificationDTO,
                originImage.getImage(),originImage.getInputStream(),originImage.getFileExtension(),
                templateImage.getImage(),templateImage.getFileExtension());
        ImageVerificationUtil.cutoutImageEdge(imageVerificationDTO,borderImage.getImage(),borderImage.getFileExtension());
        return imageVerificationDTO;
    }

    /**
     * 读取目标图
     *
     * @return ImageRead 图片包装对象
     */
    private ImageReadDTO readTargetImage() {
        ImageReadDTO imageRead = new ImageReadDTO();

        try {
            Random random = new Random(System.currentTimeMillis());
            String path = CaptchaConstants.PRE_ORIGIN_IMAGE_PATH.concat("/*");

            // 读取源文件列表
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(path);
            if (ObjectUtils.isEmpty(resources)) {
                throw new RuntimeException("not found target image");
            }

            // 读取源文件
            int i = random.nextInt(resources.length);
            String filename = resources[i].getFilename();
            if (StringUtils.isEmpty(filename)){
                throw new OpenException("获取不到验证码图片");
            }

            // 通过源文件信息组装ImageRead对象
            imageRead.setInputStream(resources[i].getInputStream());
            imageRead.setImage(ImageIO.read(resources[i].getInputStream()));
            imageRead.setFileExtension(filename.substring(filename.lastIndexOf(".") + 1));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(CaptchaConstants.IO_EXCEPTION);
        }
        return imageRead;
    }

    /**
     * 读取模板图(碎片图模板)
     *
     * @return ImageRead 图片包装对象
     */
    private ImageReadDTO readTemplateImage() {
        ImageReadDTO templateImageFile = new ImageReadDTO();
        try {
            String path = CaptchaConstants.PRE_TEMPLATE_IMAGE_PATH.concat(CaptchaConstants.TEMPLATE_PATH);

            // 获取模板图(缺图)
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource(path);
            if (ObjectUtils.isEmpty(resource)) {
                throw new RuntimeException("not found template image");
            }

            // 获取模板图(缺图)文件名
            String filename = resource.getFilename();
            if (StringUtils.isEmpty(filename)){
                throw new OpenException("获取不到模板图");
            }

            // 通过模板图(缺图)信息组装ImageRead对象
            templateImageFile.setImage(ImageIO.read(resource.getInputStream()));
            templateImageFile.setInputStream(resource.getInputStream());
            templateImageFile.setFileExtension(filename.substring(filename.lastIndexOf(".") + 1));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(CaptchaConstants.IO_EXCEPTION);
        }
        return templateImageFile;
    }

    /**
     * 读取边框图(碎片图模板边框)
     *
     * @return ImageRead 图片包装对象
     */
    private ImageReadDTO readBorderImageFile(){
        ImageReadDTO borderImageFile = new ImageReadDTO();
        try {
            String path = CaptchaConstants.PRE_TEMPLATE_IMAGE_PATH.concat(CaptchaConstants.BORDER_PATH);

            // 获取边框图
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource(path);
            if (ObjectUtils.isEmpty(resource)) {
                throw new RuntimeException("not found template image");
            }

            // 获取边框图文件名
            String filename = resource.getFilename();
            if (StringUtils.isEmpty(filename)){
                throw new OpenException("获取不到边框图");
            }

            // 通过边框图信息组装ImageRead
            borderImageFile.setImage(ImageIO.read(resource.getInputStream()));
            borderImageFile.setInputStream(resource.getInputStream());
            borderImageFile.setFileExtension(resource.getFilename().substring(resource.getFilename().lastIndexOf(".") + 1));

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(CaptchaConstants.IO_EXCEPTION);
        }
        return borderImageFile;
    }
}
