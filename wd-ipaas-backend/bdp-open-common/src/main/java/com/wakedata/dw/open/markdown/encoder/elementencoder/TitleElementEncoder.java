package com.wakedata.dw.open.markdown.encoder.elementencoder;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import com.wakedata.dw.open.markdown.constant.TitleLevelEnum;
import com.wakedata.dw.open.markdown.element.TitleElement;

/**
 * 标题块解析器
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class TitleElementEncoder implements ElementEncoder {

    @Override
    public String encode(Element element) {
        TitleElement titleElement = (TitleElement) element;

        String content = titleElement.getContent();
        TitleLevelEnum titleLevelEnum = titleElement.getLevel();
        String result = "";

        Integer level = titleLevelEnum.getLevel();
        while (level > 0) {
            result = result.concat(FlagConstants.TITLE_ELEMENT_FLAG);
            level--;
        }

        return result.concat(FlagConstants.SPACE + content);
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.TITLE;
    }
}
