package com.wakedata.dw.open.markdown.element;

import com.wakedata.dw.open.markdown.constant.ElementEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 表格块
 *
 * @author wangchensheng
 * date 2022/1/17
 */
@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class TableElement extends AbstractElement {

    private List<String> titles;

    private List<TableRow> rows;

    @Override
    public ElementEnum getBlockType() {
        return ElementEnum.TABLE;
    }

    @Data
    public static class TableRow {
        private List<String> rows;
    }
}
