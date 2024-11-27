package com.wakedata.dw.open.markdown.element;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.encoder.ElementEncoderFactory;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public abstract class AbstractElement implements Element {

    @Override
    public String buildMdText() {
        ElementEncoder encoder = ElementEncoderFactory.getEncoder(this.getBlockType());
        return encoder.encode(this);
    }

}
