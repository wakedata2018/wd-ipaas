package com.wakedata.dw.open.markdown;

import com.wakedata.dw.open.markdown.constant.ElementEnum;

/**
 * 编码器，将java代码编译成markdown 语法
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public interface ElementEncoder {
    /**
     * 编译
     *
     * @param element java代码
     * @return markdown语法字符串
     */
    String encode(Element element);

    /**
     * 返回块类型
     *
     * @return 块类型
     */
    ElementEnum getType();
}
