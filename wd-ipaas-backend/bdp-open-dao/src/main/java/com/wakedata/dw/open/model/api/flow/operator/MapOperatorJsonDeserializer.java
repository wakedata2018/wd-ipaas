package com.wakedata.dw.open.model.api.flow.operator;

import cn.hutool.core.util.ClassLoaderUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 定义算子反序列实现
 * @author luomeng
 * @date 2022/12/14 17:18
 */
@Slf4j
public class MapOperatorJsonDeserializer extends JsonDeserializer<Map<String,Operator>> {
    @Override
    public Map<String,Operator> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Map<String,Operator> operators = new HashMap<>(16);
        ObjectCodec codec = p.getCodec();
        JsonNode treeNode = codec.readTree(p);
        Iterator<JsonNode> iterator = treeNode.iterator();
        while (iterator.hasNext()) {
            JsonNode subNode = iterator.next();
            String clazzName = subNode.get(Operator.DESERIALIZER_FIELD_CLAZZ_NAME).asText();
            String operatorId = subNode.get(Operator.DESERIALIZER_FIELD_OPERATOR_ID).asText();
            operators.put(operatorId,(Operator) codec.treeToValue(subNode, ClassLoaderUtil.loadClass(clazzName)));
        }
        return operators;
    }
}
