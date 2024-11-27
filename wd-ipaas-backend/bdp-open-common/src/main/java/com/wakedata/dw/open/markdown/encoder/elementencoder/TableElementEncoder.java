package com.wakedata.dw.open.markdown.encoder.elementencoder;

import com.wakedata.dw.open.markdown.Element;
import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.constant.FlagConstants;
import com.wakedata.dw.open.markdown.element.TableElement;

import java.util.List;

/**
 * 表格编码器
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class TableElementEncoder implements ElementEncoder {
    private static final String SEP = "|";
    private static final String SPE2 = "-------------";

    @Override
    public String encode(Element element) {
        TableElement tableElement = (TableElement) element;
        List<String> titles = tableElement.getTitles();
        StringBuilder result = new StringBuilder(SEP);

        // 拼接表头
        for (String title : titles) {
            result.append(FlagConstants.SPACE).append(title).append(FlagConstants.SPACE).append(SEP);
        }

        result.append(FlagConstants.LINE_BREAK);
        result.append(SEP);
        // 拼接 表头与表内容分割
        for (int i = 0; i < titles.size(); i++) {
            result.append(FlagConstants.SPACE).append(SPE2).append(SEP);
        }
        result.append(FlagConstants.LINE_BREAK);

        // 拼接表格内容
        List<TableElement.TableRow> rows = tableElement.getRows();
        for (TableElement.TableRow tableRow : rows) {
            List<String> list = tableRow.getRows();
            result.append(SEP);
            for (String item : list) {
                result.append(FlagConstants.SPACE);
                result.append(item == null ? FlagConstants.SPACE : item);
                result.append(SEP);
            }
            result.append(FlagConstants.LINE_BREAK);
        }

        return result.toString();
    }

    @Override
    public ElementEnum getType() {
        return ElementEnum.TABLE;
    }
}
