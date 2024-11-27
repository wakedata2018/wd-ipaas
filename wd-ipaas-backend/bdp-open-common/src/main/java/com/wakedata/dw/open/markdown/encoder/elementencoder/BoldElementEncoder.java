package com.wakedata.dw.open.markdown.encoder.elementencoder;


import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import com.wakedata.dw.open.markdown.element.BoldElement;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class BoldElementEncoder implements ElementEncoder {

    @Override
    public String encode(Element element) {
        BoldElement boldElement = (BoldElement) element;
        return FlagConstants.BOLD_ELEMENT_FAG + boldElement.getContent() + FlagConstants.BOLD_ELEMENT_FAG;
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.BOLD;
    }
}
