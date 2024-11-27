package com.wakedata.dw.open.markdown.encoder.elementencoder;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import com.wakedata.dw.open.markdown.element.ItalicElement;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class ItalicElementEncoder implements ElementEncoder {

    @Override
    public String encode(Element element) {
        ItalicElement italicElement = (ItalicElement) element;
        return FlagConstants.ITALIC_ELEMENT_FAG + italicElement.getContent() + FlagConstants.ITALIC_ELEMENT_FAG;
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.ITALIC;
    }
}
