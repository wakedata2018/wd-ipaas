package com.wakedata.dw.open.markdown.constant;

/**
 * 元素类型
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public enum ElementEnum {
    /**
     * 代码块
     */
    CODE,

    /**
     * 引用块  > content
     */
    REFERENCE,

    /**
     * 表格
     */
    TABLE,

    /**
     * 标题 以级别划分
     */
    TITLE,

    /**
     * 普通文本
     */
    STRING,

    /**
     * 链接
     */
    URL,

    /**
     * 图像
     */
    IMAGE,

    /**
     * 文本加粗
     */
    BOLD,

    /**
     * 文本斜体
     */
    ITALIC,

    /**
     *
     */
    INLINE_CODE
}
