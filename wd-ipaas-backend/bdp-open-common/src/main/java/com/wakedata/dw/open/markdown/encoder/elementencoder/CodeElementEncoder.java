package com.wakedata.dw.open.markdown.encoder.elementencoder;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import com.wakedata.dw.open.markdown.element.CodeElement;
import org.apache.commons.lang3.StringUtils;

/**
 * 代码块编译
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class CodeElementEncoder implements ElementEncoder {

    @Override
    public String encode(Element element) {
        CodeElement codeElement = (CodeElement) element;
        String language = codeElement.getLanguage();

        String result = "";
        result = result.concat(StringUtils.isNotEmpty(language) ? FlagConstants.CODE_ELEMENT_FLAG + language : FlagConstants.CODE_ELEMENT_FLAG)
                .concat(FlagConstants.LINE_BREAK)
                .concat(codeElement.getContent())
                .concat(FlagConstants.LINE_BREAK)
                .concat(FlagConstants.CODE_ELEMENT_FLAG)
                .concat(FlagConstants.LINE_BREAK);

        return result;
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.CODE;
    }
}
