package com.wakedata.dw.open.markdown.encoder;

import com.wakedata.dw.open.markdown.ElementEncoder;
import com.wakedata.dw.open.markdown.constant.ElementEnum;
import com.wakedata.dw.open.markdown.encoder.elementencoder.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:46:41
 */
public class ElementEncoderFactory {

    private static final Map<String, ElementEncoder> ENCODER_MAP = new ConcurrentHashMap<>();

    static {
        ENCODER_MAP.put(ElementEnum.CODE.name(), new CodeElementEncoder());
        ENCODER_MAP.put(ElementEnum.TABLE.name(), new TableElementEncoder());
        ENCODER_MAP.put(ElementEnum.TITLE.name(), new TitleElementEncoder());
        ENCODER_MAP.put(ElementEnum.REFERENCE.name(), new ReferenceElementEncoder());
        ENCODER_MAP.put(ElementEnum.BOLD.name(), new BoldElementEncoder());
        ENCODER_MAP.put(ElementEnum.INLINE_CODE.name(), new InlineCodeElementEncoder());
        ENCODER_MAP.put(ElementEnum.ITALIC.name(), new ItalicElementEncoder());
        ENCODER_MAP.put(ElementEnum.IMAGE.name(), new ImageElementEncoder());
        ENCODER_MAP.put(ElementEnum.URL.name(), new UrlElementEncoder());
    }

    public static ElementEncoder getEncoder(ElementEnum elementEnum) {
        ElementEncoder elementEncoder = ENCODER_MAP.get(elementEnum.name());
        if (elementEncoder == null) {
            throw new IllegalStateException("Miss elementEncoder : " + elementEnum.name());
        }

        return elementEncoder;
    }
}
