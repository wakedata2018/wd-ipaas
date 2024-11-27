package com.wakedata.dw.open.markdown;


import com.wakedata.dw.open.markdown.constant.ElementEnum;

/**
 * 元素的能力
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public interface Element {

    /**
     * 构建模块文本
     *
     * @return 模块文本
     */
    String buildMdText();

    /**
     * 获取模块类型
     *
     * @return 模块类型
     */
    ElementEnum getBlockType();
}
