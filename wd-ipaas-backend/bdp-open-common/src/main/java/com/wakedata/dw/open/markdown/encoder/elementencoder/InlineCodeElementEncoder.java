package com.wakedata.dw.open.markdown.encoder.elementencoder;


import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import com.wakedata.dw.open.markdown.element.InlineCodeElement;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class InlineCodeElementEncoder implements ElementEncoder {

    @Override
    public String encode(Element element) {
        InlineCodeElement italicElement = (InlineCodeElement) element;
        return FlagConstants.INLINE_CODE_FLAG + italicElement.getContent() + FlagConstants.INLINE_CODE_FLAG;
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.INLINE_CODE;
    }
}
