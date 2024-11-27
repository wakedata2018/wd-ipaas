package com.wakedata.dw.open.liteflow.globalparams;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author Wangchensheng@wakedata.com
 * date 2023年02月22日 15:18:31
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalParamDescription {
    String value();
}
