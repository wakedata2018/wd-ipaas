package com.wakedata.dw.open.model.api.flow.operator;

import com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator;
import com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorOperator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.model.api.flow.operator.script.GroovyScriptTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.simple.SimpleOperator;
import com.wakedata.dw.open.model.api.flow.operator.sql.JoinTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.sql.UnionTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.transform.ColumnSelectTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.transform.RowSelectTransformOperator;

/**
 * 核心方法，接受Visitor的访问
 *
 * @author ZhangXueJun
 * @title Visitor
 * @date 2019/10/25 12:54
 * @projectName dw-stream
 * @description
 */
public interface OpVisitor extends Operator.Visitor{

    /**
     * 虚拟算子节点
     *
     * @param vertexOperator
     * @return
     * @throws Exception
     */
    void visit(VertexOperator vertexOperator) throws Exception;

    /**
     * 访问到api算子
     *
     * @param apiOperator
     * @return
     * @throws Exception
     */
    void visit(ApiOperator apiOperator) throws Exception;

    /**
     * union all
     *
     * @param unionTransformOperator
     * @throws Exception
     */
    void visit(UnionTransformOperator unionTransformOperator) throws Exception;

    /**
     * groovy 算子
     *
     * @param groovyScriptTransformOperator
     * @return
     * @throws Exception
     */
    void visit(GroovyScriptTransformOperator groovyScriptTransformOperator) throws Exception;

    /**
     * 访问join算子
     *
     * @param joinTransformOperator
     */
    void visit(JoinTransformOperator joinTransformOperator);

    /**
     * 访问到测试用例算子
     *
     * @param simpleOperator
     */
    void visit(SimpleOperator simpleOperator);

    void visit(ColumnSelectTransformOperator columnSelectTransformOperator);

    void visit(RowSelectTransformOperator rowSelectTransformOperator);

    /**
     * 访问到连接器算子
     *
     * @param connectorOperator 连接器算子
     */
    void visit(ConnectorOperator connectorOperator);

    /**
     * 循环算子
     * @param forOperator
     */
    void visit(ForOperator forOperator);

    /**
     * 退出循环算子
     * @param breakOperator
     */
    void visit(BreakOperator breakOperator);
}
