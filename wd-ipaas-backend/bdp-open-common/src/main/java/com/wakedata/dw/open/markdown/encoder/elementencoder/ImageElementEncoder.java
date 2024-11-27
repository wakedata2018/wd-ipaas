package com.wakedata.dw.open.markdown.encoder.elementencoder;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.element.ImageElement;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class ImageElementEncoder implements ElementEncoder {

    @Override
    public String encode(Element element) {
        ImageElement imageElement = (ImageElement) element;

        String imageUrl = imageElement.getImageUrl();
        return "![](" + imageUrl + ")";
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.IMAGE;
    }
}
