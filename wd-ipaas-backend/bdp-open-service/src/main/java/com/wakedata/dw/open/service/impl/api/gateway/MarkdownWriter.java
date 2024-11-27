package com.wakedata.dw.open.service.impl.api.gateway;


import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.markdown.Markdown;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
@Slf4j
public class MarkdownWriter {

    public static void write(Markdown markdown, HttpServletResponse response) {
        exportMarkDownFile(response, markdown);
    }

    private static void exportMarkDownFile(HttpServletResponse response, Markdown markdown) {
        try (OutputStream outputStream = response.getOutputStream()) {
            String[] arrList = markdown.toString().split(FlagConstants.LINE_BREAK);
            String sb = Arrays.stream(arrList).map(arr -> arr + "\r\n").collect(Collectors.joining());
            byte[] bytes = sb.getBytes(StandardCharsets.UTF_8);

            response.setContentType("application/markdown");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(String.format(markdown.getName() + ".md", System.currentTimeMillis()), "UTF-8"));
            response.setHeader("Accept-Ranges", "bytes");

            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new OpenException(MsgCodeEnum.w_export_api_fail);
        }
    }
}
