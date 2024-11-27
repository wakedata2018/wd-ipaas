package com.wakedata.dw.open.service.impl.api.captcha.utils;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.wakedata.common.redis.util.RedisUtil;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.impl.api.captcha.constants.CaptchaConstants;
import com.wakedata.wd.permission.captcha.dto.ImageVerificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;

/***
 * @author： wangchensheng
 * @date： 2022-05-12 13:53
 * @version 1.0
 */
@Slf4j
public class ImageVerificationUtil {

    /**
     * 根据原图生成感兴趣区域坐标
     * @param templateImage 模板图
     * @param width 验证码图片宽度
     * @param height 验证码图片高度
     */
    public static void generateCoordinates(ImageVerificationDTO imageVerificationDTO, BufferedImage templateImage,
                                           Integer width, Integer height) {

        int x, y;

        //  抠图模板宽度
        int templateImageWidth = templateImage.getWidth();
        //  抠图模板高度
        int templateImageHeight = templateImage.getHeight();

        Random random = new Random(System.currentTimeMillis());

        //  取范围内坐标数据，坐标抠图一定要落在原图中，否则会导致程序错误
        x = random.nextInt(width - templateImageWidth - templateImageWidth + 1) + templateImageWidth;
        y = random.nextInt(height - templateImageHeight - templateImageHeight + 1) + templateImageHeight;
        if (templateImageHeight - height >= 0) {
            y = random.nextInt(10);
        }

        imageVerificationDTO.setX(x);
        imageVerificationDTO.setY(y);

    }

    /**
     * 将生成的x轴和y轴存入redis中
     *
     * @param imageVerificationDTO 含有x轴和y轴的imageVerificationDTO对象
     */
    public static void setRedis(ImageVerificationDTO imageVerificationDTO){
        String redisKey = CaptchaConstants.CAPTCHA_REDIS_KEY_LOGIN + UUID.fastUUID();
        RedisUtil.getInstance().set(redisKey, JSON.toJSONString(imageVerificationDTO), CaptchaConstants.CAPTCHA_REDIS_KEY_LOGIN_EXPIRE_TIME);
        imageVerificationDTO.setRedisKey(redisKey);
    }

    /**
     * 根据原图和模板图,获取碎片图和遮罩图,
     * 然后将三张图进行加密处理组装到ImageVerificationDTO对象当中
     *
     * @param originImage 源图文件
     * @param inputStream 原图图片流
     * @param originImageFileType 源图文件扩展名
     * @param templateImage 模板图文件
     * @param templateImageFileType 模板图文件扩展名
     */
    public static void pictureTemplateCutout(ImageVerificationDTO imageVerificationDTO,
                                             BufferedImage originImage, InputStream inputStream, String originImageFileType,
                                             BufferedImage templateImage, String templateImageFileType) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        // 生成的随机坐标
        int x = imageVerificationDTO.getX();
        int y = imageVerificationDTO.getY();
        // 模板的高度和宽度
        int templateImageWidth = templateImage.getWidth();
        int templateImageHeight = templateImage.getHeight();

        try {

            // 1)获取碎片图(根据模板图)

            // 根据模板图尺寸创建透明图片
            BufferedImage cutoutImage = new BufferedImage(templateImageWidth, templateImageHeight, templateImage.getType());
            // 根据随机坐标和模板图在原图获取矩形区域图片
            BufferedImage interestArea = getInterestArea(x, y, templateImageWidth, templateImageHeight, inputStream, originImageFileType);
            // 结合模板图,模板透明图,矩形区域三者进行抠图 得到碎片图
            cutoutImageByTemplateImage(interestArea, templateImage, cutoutImage);

            //  图片绘图
            int bold = 5;
            Graphics2D graphics = cutoutImage.createGraphics();
            graphics.setBackground(Color.white);

            //  设置抗锯齿属性
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setStroke(new BasicStroke(bold, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            graphics.drawImage(cutoutImage, 0, 0, null);
            graphics.dispose();

            // 2)获取遮罩图(根据原图和模板图)
            BufferedImage shadeImage = generateShadeByTemplateImage(originImage, templateImage, x, y);

            // 3)对原图 碎片图 遮罩图进行图片加密处理
            byteArrayOutputStream = new ByteArrayOutputStream();
            //  图片转为二进制字符串
            ImageIO.write(originImage, originImageFileType, byteArrayOutputStream);
            byte[] originImageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.reset();
            //  图片加密成base64字符串
            String originImageString = Base64Utils.encodeToString(originImageBytes);
            imageVerificationDTO.setOriginImage(originImageString);

            ImageIO.write(shadeImage, templateImageFileType, byteArrayOutputStream);
            //  图片转为二进制字符串
            byte[] shadeImageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.reset();
            //  图片加密成base64字符串
            String shadeImageString = Base64Utils.encodeToString(shadeImageBytes);
            imageVerificationDTO.setShadeImage(shadeImageString);

            ImageIO.write(cutoutImage, templateImageFileType, byteArrayOutputStream);
            //  图片转为二进制字符串
            byte[] cutoutImageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
            //  图片加密成base64字符串
            String cutoutImageString = Base64Utils.encodeToString(cutoutImageBytes);
            imageVerificationDTO.setCutoutImage(cutoutImageString);


        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(CaptchaConstants.IO_EXCEPTION);
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 根据随机坐标获取区域(矩形,大小取决于模板的长和宽)
     * @param x 感兴趣区域X轴
     * @param y 感兴趣区域Y轴
     * @param templateImageWidth  模板图宽度
     * @param templateImageHeight 模板图高度
     * @param originImageType 源图扩展名
     * @return BufferedImage 矩形区域图片
     */
    private static BufferedImage getInterestArea(int x, int y, int templateImageWidth, int templateImageHeight, InputStream inputStream, String originImageType) {
        ImageInputStream imageInputStream = null;

        try {

            // 获取解码图片文件后缀的处理器
            Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersBySuffix(originImageType);
            ImageReader imageReader = imageReaderIterator.next();

            // 完善处理器参数
            imageInputStream = ImageIO.createImageInputStream(inputStream);
            imageReader.setInput(imageInputStream, true);

            //通过处理器获取相关参数 并完善
            ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
            Rectangle rectangle = new Rectangle(x, y, templateImageWidth, templateImageHeight);
            imageReadParam.setSourceRegion(rectangle);

            // 通过处理器处理相关参数,获取到解码后的图片文件
            return imageReader.read(0, imageReadParam);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(CaptchaConstants.IO_EXCEPTION);
        } finally {
            try {
                if (imageInputStream != null) {
                    imageInputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 根据模板图抠图
     * @param interestArea  感兴趣区域图
     * @param templateImage  模板图
     * @param cutoutImage 裁剪图
     */
    private static void cutoutImageByTemplateImage(BufferedImage interestArea, BufferedImage templateImage, BufferedImage cutoutImage) {
        //  获取模板图片矩阵
        int[][] templateImageMatrix = getMatrix(templateImage);

        //  将模板图非透明像素设置到剪切图中
        for (int i = 0; i < templateImageMatrix.length; i++) {
            for (int j = 0; j < templateImageMatrix[0].length; j++) {
                int rgb = templateImageMatrix[i][j];
                if (rgb < 0) {
                    cutoutImage.setRGB(i, j, interestArea.getRGB(i, j));
                }
            }
        }
    }

    /**
     * 图片生成图像矩阵
     * @param bufferedImage  图片源
     * @return 图片矩阵
     */
    private static int[][] getMatrix(BufferedImage bufferedImage) {
        int[][] matrix = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                matrix[i][j] = bufferedImage.getRGB(i, j);
            }
        }
        return matrix;
    }

    /**
     * 根据原图和模板图生成遮罩图
     * @param originImage 源图
     * @param templateImage 模板图
     * @param x 感兴趣区域X轴
     * @param y 感兴趣区域Y轴
     * @return 遮罩图
     * @throws IOException 数据转换异常
     */
    private static BufferedImage generateShadeByTemplateImage(BufferedImage originImage, BufferedImage templateImage, int x, int y) throws IOException {
        //  根据原图，创建支持alpha通道的rgb图片
        BufferedImage shadeImage = new BufferedImage(originImage.getWidth(), originImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        //  原图片矩阵
        int[][] originImageMatrix = getMatrix(originImage);
        //  模板图片矩阵
        int[][] templateImageMatrix = getMatrix(templateImage);

        //  将原图的像素拷贝到遮罩图
        for (int i = 0; i < originImageMatrix.length; i++) {
            for (int j = 0; j < originImageMatrix[0].length; j++) {
                int rgb = originImage.getRGB(i, j);
                //  获取rgb色度
                int r = (0xff & rgb);
                int g = (0xff & (rgb >> 8));
                int b = (0xff & (rgb >> 16));
                //  无透明处理
                rgb = r + (g << 8) + (b << 16) + (255 << 24);
                shadeImage.setRGB(i, j, rgb);
            }
        }

        //  对遮罩图根据模板像素进行处理
        for (int i = 0; i < templateImageMatrix.length; i++) {
            for (int j = 0; j < templateImageMatrix[0].length; j++) {
                int rgb = templateImage.getRGB(i, j);

                //对源文件备份图像(x+i,y+j)坐标点进行透明处理
                if (rgb < 0) {
                    int originRGB = shadeImage.getRGB(x + i, y + j);
                    int r = (0xff & originRGB);
                    int g = (0xff & (originRGB >> 8));
                    int b = (0xff & (originRGB >> 16));


                    originRGB = r + (g << 8) + (b << 16) + (140 << 24);

                    //  对遮罩透明处理
                    shadeImage.setRGB(x + i, y + j, originRGB);
                    //  设置遮罩颜色
//                    shadeImage.setRGB(x + i, y + j, originRGB);

                }

            }
        }

        return shadeImage;
    }

    /**
     * 对碎片图进行描边
     * @param imageVerificationDTO 图片容器
     * @param borderImage 描边图
     * @param borderImageFileType 描边图类型
     */
    public static void cutoutImageEdge(ImageVerificationDTO imageVerificationDTO, BufferedImage borderImage, String borderImageFileType){

        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            String cutoutImageString = imageVerificationDTO.getCutoutImage();
            //  图片解密成二进制字符创
            byte[] bytes = Base64Utils.decodeFromString(cutoutImageString);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            //  读取图片
            BufferedImage cutoutImage = ImageIO.read(byteArrayInputStream);
            //  获取模板边框矩阵， 并进行颜色处理
            int[][] borderImageMatrix = getMatrix(borderImage);
            for (int i = 0; i < borderImageMatrix.length; i++) {
                for (int j = 0; j < borderImageMatrix[0].length; j++) {
                    int rgb = borderImage.getRGB(i, j);
                    if (rgb < 0) {
                        cutoutImage.setRGB(i, j , -7237488);
                    }
                }
            }
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(cutoutImage, borderImageFileType, byteArrayOutputStream);
            //  新模板图描边处理后转成二进制字符串
            byte[] cutoutImageBytes = byteArrayOutputStream.toByteArray();
            //  二进制字符串加密成base64字符串
            String cutoutImageStr = Base64Utils.encodeToString(cutoutImageBytes);
            imageVerificationDTO.setCutoutImage(cutoutImageStr);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(CaptchaConstants.IO_EXCEPTION);
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }


}
