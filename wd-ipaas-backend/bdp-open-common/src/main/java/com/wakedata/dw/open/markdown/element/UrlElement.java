package com.wakedata.dw.open.markdown.element;

import com.wakedata.dw.open.markdown.constant.ElementEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 链接元素
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class UrlElement extends AbstractElement {

    private String tips;

    private String url;

    @Override
    public ElementEnum getBlockType() {
        return ElementEnum.URL;
    }
}
