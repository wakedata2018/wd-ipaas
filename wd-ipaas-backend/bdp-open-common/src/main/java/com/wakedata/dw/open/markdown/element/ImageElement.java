package com.wakedata.dw.open.markdown.element;

import com.wakedata.dw.open.markdown.constant.ElementEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片元素
 *
 * @author wangchensheng
 * date 2022/1/17
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class ImageElement extends AbstractElement {

    private String imageUrl;

    @Override
    public ElementEnum getBlockType() {
        return ElementEnum.IMAGE;
    }

}
