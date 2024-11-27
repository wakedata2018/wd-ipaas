package com.wakedata.dw.open.markdown.encoder.elementencoder;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import com.wakedata.dw.open.markdown.element.ReferenceElement;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class ReferenceElementEncoder implements ElementEncoder {
    @Override
    public String encode(Element element) {
        ReferenceElement referenceElement = (ReferenceElement) element;
        String content = referenceElement.getContent();

        return FlagConstants.REFERENCE_ELEMENT_FLAG + FlagConstants.SPACE + content;
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.REFERENCE;
    }
}
