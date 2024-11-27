package com.wakedata.dw.open.utils;


/** json err
* @ClassName: JsonException 
* @Description: 
* @author zhangyang
* @date 2016-3-3 上午10:41:02 
*  
*/
public class JsonException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JsonException(String message) {
		super(message);
	}

	public JsonException(Throwable cause) {
		super(cause);
	}

	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}
}
