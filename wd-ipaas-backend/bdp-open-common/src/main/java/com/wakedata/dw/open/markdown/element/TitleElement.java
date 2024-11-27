package com.wakedata.dw.open.markdown.element;

import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.TitleLevelEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标题块
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class TitleElement extends AbstractElement {

    private TitleLevelEnum level;

    private String content;

    @Override
    public ElementEnum getBlockType() {
        return ElementEnum.TITLE;
    }

}
