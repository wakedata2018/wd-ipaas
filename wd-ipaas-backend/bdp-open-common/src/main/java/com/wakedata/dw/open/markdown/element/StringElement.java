package com.wakedata.dw.open.markdown.element;

import com.wakedata.dw.open.markdown.constant.ElementEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class StringElement extends AbstractElement {

    private String content;

    @Override
    public String buildMdText() {
        return content;
    }

    @Override
    public ElementEnum getBlockType() {
        return ElementEnum.STRING;
    }
}