package com.wakedata.dw.open.enums;

/**
 * 开放平台api错误码定义
 * @author luomeng
 * @date 2022/10/19 14:39
 */
public enum OpenApiMsgCodeEnum{

    /**
     * 错误码
     */
    s_success(200, "操作成功"),

    s_error(1001, "系统错误"),

    s_sign_check_fail(1006, "验证签名失败"),

    s_timestamp_check_fail(1007, "时间戳校验失败，超出了请求可重放时长"),

    s_access_token_invalid(1008, "accessToken无效或已过期"),

    s_refresh_token_invalid(1009, "refreshToken无效或已过期"),

    s_api_failed_to_execute(1010,"api执行失败：[错误码:%s，错误描述:%s]"),

    s_connector_auth_param_invalid(1012, "鉴权参数无效或已过期"),

    s_connector_auth_param_rule_invalid(1013, "鉴权失败，请检查鉴权参数或连接器鉴权规则配置"),

    w_operation_not_permitted(120000, "IP限制，无权限操作"),
    w_day_limit_failed(120004,"api超过了当天的调用次数限制"),
    w_month_limit_failed(120005,"api超过了当月的调用次数限制"),
    w_qps_limit_failed(120006,"api超过了最大QPS次数限制"),
    w_total_limit_failed(120007,"api超过了总的调用次数限制"),
    w_data_api_not_found(120008, "api不存在，请刷新"),
    w_data_api_is_private_not_authorization_app(120009, "加密状态的api不允许授权应用"),
    w_data_api_is_private_not_request_access(1200010, "加密状态的api不允许申请访问"),
    w_data_api_is_private_not_view_document(1200011, "加密状态的api不允许申请访问"),

    w_no_apply_column_or_already_have_permit(220001,"没有该API的资产访问权限"),
    w_wrong_api_path(220006,"不正确的API PATH"),
    w_data_app_id_not_found(430001, "appId对应的应用不存在"),
    w_data_api_not_publish(510003, "数据API未发布"),
    w_api_jwt_not_binding_error(510004,"appKey对应应用未授权此Api，请先授权！"),



    w_api_graph_execute_http_operator_error(510101,"调用注册HTTP API错误"),
    w_api_graph_execute_http_operator_timeout_error(510102,"调用注册HTTP API错误，请检查后端超时设置是否合理"),
    w_api_graph_execute_table_operator_error(510110,"调用表单API错误"),
    w_api_graph_add_table_operator_error(510111,"增加表单API错误"),
    w_api_graph_update_table_operator_error(510111,"修改表单API错误"),
    w_api_graph_execute_custom_operator_error(510120,"调用自定义SQL API错误，原因：%s"),
    w_api_graph_execute_select_column_operator_error(510130,"调用选择列错误"),
    w_api_graph_execute_select_row_operator_error(510140,"调用选择行错误"),
    w_api_graph_execute_groovy_script_operator_error(510150,"调用Groovy Script错误"),
    w_api_graph_execute_union_all_operator_error(510160,"调用UnionAll错误"),
    w_api_graph_execute_crontab_operator_error(510161,"调用定时任务错误"),

    w_api_graph_execute_judge_operator_error(510162,"调用判断算子错误"),
    w_api_graph_execute_start_operator_error(510163,"调用开始算子错误"),
    w_api_graph_execute_event_send_operator_error(510164, "调用事件发送算子错误"),
    w_api_graph_execute_event_send_operator_config_name_error(510165, "创建或修改事件发送算子失败，配置名重复"),
    w_api_graph_execute_event_operator_name_error(510166, "创建或修改事件算子失败的，步骤名不能为空"),
    w_api_graph_execute_event_send_operator_not_find_error(510167, "该事件发送算子不存在，请刷新页面重新进入编排"),
    w_api_graph_execute_event_receive_operator_not_find_error(510168, "调用事件接收算子错误"),
    w_api_graph_execute_branch_operator_error(510169, "调用分支算子错误"),
    w_api_graph_execute_connector_operator_error(510170, "调用连接器API错误"),

    w_api_graph_execute_for_operator_error(510171,"调用循环算子错误"),
    w_api_graph_execute_for_break_operator_error(510172,"调用循环算子退出错误"),
    w_api_graph_execute_for_sql_operator_error(510173,"调用sql算子错误"),
    w_api_redis_lock_config_not_find_error(510174, "无法找到Redis锁配置"),
    w_api_graph_execute_try_catch_operator_error(510175, "调用异常算子错误"),
    w_api_graph_execute_try_catch_operator_nesting_is_not_allowed_error(510176, "异常算子不允许嵌套在其他算子中"),

    w_func_expression_is_invalid(520020, "函数表达式无效，表达式: %s"),
    w_function_not_found(520021, "未找到函数: %s, 表达式: %s"),
    w_missing_argument(520022, "缺少参数，函数表达式：%s"),
    w_invalid_argument(520023, "无效参数：%s，函数表达式：%s"),
    w_execute_function_error(520024, "执行函数错误: %s"),
    w_missing_comma_after_argument(520025, "参数后缺少逗号：%s，函数表达式：%s"),
    w_missing_close_parenthesis_after_argument(520026, "参数后缺少右括号：%s，函数表达式：%s"),
    w_function_is_not_empty(520027, "函数表达式不能为空"),
    w_param_mapper_type_error(520029,"字段【%s】由%s转成%s失败,转换内容为【%s】"),
    w_param_mapper_format_error(520030,"字段【%s】Json格式错误"),

    w_app_api_asset_fail(520011, "未找到可用的应用访问地址"),
    w_app_api_asset_not_authorized(520015, "请求使用的tenantId和appBuId与应用授权的数据不匹配，调用失败"),
    w_app_api_asset_test_not_authorized(520010, "请求使用的tenantId和appBuId与应用授权的数据不匹配，调用失败，api测试请使用tenantId:%s,appBuId:%s"),
    w_app_is_not_authorized(520005, "应用未绑定授权信息，调用失败"),
    w_data_gateway_app_id_no_found(500001, "appKey不存在或未发布"),

    w_api_graph_parse_error(510011,"Api编排解析发生错误" ),
    w_api_graph_cycle_parse_error(510012,"api编排数据中存在闭环，解析出错" ),
    w_api_graph_break_parse_error(510013,"api编排数据中退出循环算子只能在循环算子中子流程的最后节点使用，且只有一个" ),
    w_api_graph_for_item_parse_error(510014,"api编排数据中循环算子没有添加子流程" ),

    w_app_api_params_required(520016, "请求api失败，api中%s参数%s为必填"),
    w_app_api_params_type(520017, "请求api失败，api参数%s数据类型不正确"),

    w_cloud_function_execute_fail(530010, "自定义函数【%s】执行失败，错误信息：【%s】"),

    w_func_exception_msg(530011,"函数执行异常"),

    w_condition_exception_msg(530012,"条件判断执行异常"),

    w_liteflow_exception_msg(530013,"服务编排执行异常"),

    w_notfound_connector_secret_key(530014, "无法找到连接器【%s】配置的密钥信息"),

    w_connector_group_name_duplicate(530015, "连接器分类名称重复"),

    w_connector_group_have_children_group(530016, "当前选择删除的连接器分类存在子分类，不能删除"),

    w_connector_group_already_connector_use(530017, "当前选择删除的连接器分类已被连接器平台关联使用，不能删除"),

    w_connector_name_already_exist(530018, "当前连接器平台名称已经存在，请更改名称"),

    w_connector_environment_address_name_already_repeat(530019, "当前填写的信息中存在重复的环境名称"),

    w_connector_params_name_already_repeat(530020, "当前填写的鉴权参数中存在重复的参数名称"),

    w_connector_identification_already_repeat(530021, "连接器平台的唯一标识已存在"),
    w_missing_authentication_parameters(530022,"缺失鉴权参数【%s】，请检查平台【%s】的鉴权参数设置"),
    w_authentication_fail(530023, "鉴权失败，异常信息：%s，url：%s"),
    w_authentication_fail_no_msg(530024, "鉴权失败"),
    w_request_connector_api_fail(530025, "请求连接器API失败"),
    w_notfound_connector_environment_address(530026, "无法找到平台环境信息"),
    w_connector_connectorEnvironmentAddress_name_already_exist(530027, "当前填写的环境名称中，【%s】已存在"),

    w_connector_id_cannot_be_null(530028, "连接器平台id不能为空"),

    w_connector_does_not_exist(530029, "id为【%s】的连接器平台不存在"),
    w_secret_name_already_exist(530030, "当前密钥名称已经存在，请更改名称"),
    w_secret_id_cannot_be_null(530031, "平台密钥id不能为空"),
    w_secret_key_does_not_exist(530032, "平台密钥不存在"),

    w_api_graph_execute_create_variable_operator_error(530033, "执行创建变量算子出错"),
    w_api_graph_execute_variable_update_operator_error(530034, "执行更新变量算子出错"),

    w_connector_auth_config_not_found(530040, "连接器鉴权配置不存在"),
    w_secret_key_delete_fail(530041, "平台密钥删除失败"),
    w_param_set_cookie_not_found(530042, "无法找到Set-Cookie参数"),

    w_resp_param_mapping_rule_status_error(530043, "映射规则的状态不正确"),
    w_resp_param_mapping_rule_name_must_not_be_repeated(530044, "映射规则名称不能重复"),
    w_resp_param_mapping_rule_is_default_rule_can_not_to_update_or_delete(530045, "默认的参数映射规则不允许修改或者删除"),
    w_resp_param_mapping_rule_already_used_by_the_api(530045, "该规则已经被api%s使用，不能修改或者删除"),
    w_resp_param_mapping_rule_not_find_by_api(530046, "当前api配置的映射规则不存在，请重新选择映射规则"),
    ;


    /**
     * 错误码
     */
    private int code;

    /**
     * 错误描述
     */
    private String desc;


    OpenApiMsgCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
