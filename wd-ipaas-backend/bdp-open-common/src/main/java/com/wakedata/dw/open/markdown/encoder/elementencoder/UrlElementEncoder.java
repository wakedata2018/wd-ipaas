package com.wakedata.dw.open.markdown.encoder.elementencoder;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.element.UrlElement;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class UrlElementEncoder implements ElementEncoder {
    @Override
    public String encode(Element element) {
        UrlElement urlElement = (UrlElement) element;

        String url = urlElement.getUrl();
        String tips = urlElement.getTips();

        String result = "[";
        if (tips != null) {
            result += tips;
        }

        result += "](" + url + ")";
        return result;
    }

    @Override
    public ElementEnum getType() {
        return null;
    }
}
