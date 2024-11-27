package com.wakedata.dw.open.accesstoken;

import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luomeng
 * @date 2022/8/6 16:05
 */
@Slf4j
public class TimestampUtil {

    /**
     * 校验时间戳
     * @param timestamp 时间戳
     * @param replayTime 防重放时间
     * @return
     */
    public static Boolean checkTimestampAndThrowExc(String timestamp, Long replayTime){

        Long tmp = Long.parseLong(timestamp);
        Long currTime = System.currentTimeMillis();
        Long result = Math.abs(currTime - tmp)/1000L;
        if(result > replayTime){
            log.error("时间戳校验失败，超出了可重放时长,timestamp = {},currTime = {},result = {},replayTime = {}",timestamp,currTime,result,replayTime);
            throw new OpenException(OpenApiMsgCodeEnum.s_timestamp_check_fail);
        }
        return true;
    }
}
