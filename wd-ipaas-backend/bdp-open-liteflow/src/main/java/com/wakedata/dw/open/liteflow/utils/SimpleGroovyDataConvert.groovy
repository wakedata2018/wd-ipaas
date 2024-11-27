package com.wakedata.dw.open.liteflow.utils


import com.alibaba.fastjson.JSONObject
import com.wakedata.dw.open.liteflow.component.api.SimpleNodeComponent
import com.wakedata.dw.open.model.api.flow.operator.script.DataConvert

/**
 * Groovy Script脚本实现
 */
class SimpleGroovyDataConvert implements DataConvert<JSONObject> {

    /**
     * 该方法为算子执行入口，不能修改方法信息【包含方法返回格式JSONObject，方法名称convert，方法入参JSONObject context】，只能修改方法内部逻辑和返回参数
     *
     * @param context 上下文参数对象，可获取到与当前算子入边连线上所有算子的参数及返回值
     * 参数获取示例： 注意：只能从开始算子里面取到请求参数，其他算子只能获取到响应数据
     * 【取开始算子参数：context.getJSONObject(算子步骤英文名).getJSONObject(参数位置[请求头HEAD/请求参数QUERY/请求体BODY]).get(参数名称)
     * 如：context.getJSONObject("start").getJSONObject("BODY").getJSONObject("tenantId")】
     * 【取其他算子参数：context.getJSONObject(算子步骤英文名).getJSONObject(参数位置[响应头HEAD/响应体BODY]).get(参数名称)
     * 如：context.getJSONObject("xxx").getJSONObject("BODY").getJSONArray("data")】
     * @return 自定义返回参数内容，但需为com.alibaba.fastjson.JSONObject格式
     */
    @Override
    JSONObject convert(JSONObject context) {
        JSONObject result = new JSONObject()

        result.put(SimpleNodeComponent.ID, 1)
        result.put(SimpleNodeComponent.SLOT_INDEX, 1)
        result.put(SimpleNodeComponent.DATA, "data")

        return result
    }
}
