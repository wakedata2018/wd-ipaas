package com.wakedata.dw.open.markdown;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Markdown文档
 *
 * @author wangchensheng
 * date 2022/1/17
 */
@AllArgsConstructor
@Data
public class Markdown {
    /**
     * 文件名称
     */
    private String name;

    /**
     * 元素列表
     */
    private List<Element> elements;

    /**
     * 添加元素
     *
     * @param element 元素
     */
    public void add(Element element) {
        elements.add(element);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Element element : elements) {
            result.append(element.buildMdText());
        }
        return result.toString();
    }
}
