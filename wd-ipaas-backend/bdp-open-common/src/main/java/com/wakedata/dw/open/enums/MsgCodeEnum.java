package com.wakedata.dw.open.enums;

/**
 * 管理后台错误码定义
 * @author wujiang
 * date 2018/5/27
 */
public enum MsgCodeEnum {

    /**
     * s_开头为系统提示
     **/
    s_success(200, "操作成功"),

    s_upgrade(100, "升级"),

    s_upgrade_set(101, "更新设置"),

    s_redirect(300, "重定向"),

    s_not_login(401, "未登录"),

    s_route(700, "路由"),

    s_error(1001, "系统错误"),

    s_service_pause(1002, "服务暂停"),

    s_service_busy(1003, "服务器忙"),

    s_service_not_implement(1004, "服务未实现"),

    s_interface_error(1004, "接口不存在"),

    s_interface_timeout(1005, "接口超时"),


    /**
     * w_开头为web层
     */
    w_illegal_argument(110000, "参数无效"),

    w_wrong_argument(110001, "参数错误"),

    w_empty_argument(110002, "参数不能为空"),

    w_wrong_v_code(110003, "验证码错误"),

    w_wrong_wx_openId(110004, "openId无效"),

    w_illegal_phone(110005, "手机号无效"),

    w_empty_phone(110006, "手机号不能为空"),

    w_wrong_password(110007, "密码错误"),

    w_empty_password(110008, "密码不能为空"),

    w_empty_name(110009, "名字不能为空"),

    w_empty_enterprises(110010, "公司不能为空"),

    w_empty_email(110011, "邮箱不能为空"),

    w_empty_requirement_desc(110012, "需求描述不能为空"),

    w_illegal_email(1100013, "邮箱格式不对"),

    w_empty_epId(1100014, "epId不能为空"),

    w_empty_depId(1100015, "depId不能为空"),

    w_data_api_not_publish(510003, "数据API未发布"),

    w_operation_illegal_state(120001, "操作状态不正确"),

    w_operation_illegal_http_method(120002, "http请求方法错误"),

    w_upload_failure(120003, "上传文件失败"),



    w_user_not_found(130000, "用户不存在"),

    w_user_not_bind(130001, "用户未绑定"),

    w_user_not_tenantId(130002 ,"当前用户未设置租户，请设置租户后重试"),

    w_user_account_exist(130005, "用户账号已存在"),

    w_user_not_id(130006,"id不能为空"),

    w_user_not_password(130007,"密码不能为空"),
    w_user_password_not_consistent(130008,"密码输入不一致"),

    w_user_account_error(130009, "账号或密码输入错误"),

    w_no_data_response(140000, "无数据返回"),

    w_user_not_delete(140001,"平台管理员不能被删除"),

    w_user_account_admin(140002,"平台管理员已存在，不能新建"),

    w_length_storeName(150000, "门店名称过长"),

    w_length_storeAddress(150001, "门店地址过长"),

    w_length_storeTel(150002, "门店联系方式过长"),

    w_app_name_exists(200001,"应用名称已存在！"),
    w_charge_man_exists(200002,"该负责人已有相同授权类型接入端"),
    w_app_lessee_not_exists(200003,"当前账号下不存在该应用！"),
    w_api_group_path_exists(210000,"接口分类公共路径已存在"),
    w_api_group_name_exists(210001,"接口分类名称已存在"),
    w_api_group_code_exists(210002,"接口分类编码已存在"),
    w_has_published_api(210003,"该接口分类下有已发布的API，不允许删除"),
    w_cannot_edit_published_api(210004,"该接口分类下有已发布的API，不允许编辑"),
    w_cannot_delete_datasource_cause_of_has_api(210005,"数据源已有API，请先删除相关API"),
    w_api_group_not_exists(210006,"该接口分类不存在,请刷新后重试"),
    w_api_group_reference(210007,"该接口分类存在子节点,请删除掉子节点后重试"),
    w_api_group_have_api(210008,"该接口分类下存在API,不能允许删除"),
    w_api_group_have_swagger(210009,"该接口分类下存在swagger,不能允许删除"),
    w_api_group_path_have_swagger(2100010,"该接口分类下存在swagger,不能允许更新接口分类公共路径"),

    w_cancel_collect_fail(220000,"取消收藏失败"),
    w_no_apply_column_or_already_have_permit(220001,"没有该API的资产访问权限"),
    w_not_exists_api(220002,"不存在的API"),
    w_api_is_unpublished_cannot_delete(220003,"API未下线，不能删除"),
    w_datasource_is_not_exists(220004,"无该数据源"),
    w_datasource_name_already_exists(220005,"数据源连接名已存在"),

    w_wrong_api_id(220007,"不正确的APP ID"),
    w_api_name_exists(220008,"API名称已存在"),
    w_wrong_api_path_rule(220009,"API PATH只能包含数字字母下划线，长度127"),
    w_api_path_exists(220010,"API PATH已存在"),
    w_cannot_delete_published_api(220011,"已发布的API不允许修改"),
    w_cannot_apply_not_exists_api(220012,"不存在的API无法申请"),
    w_cannot_apply_not_published_api(220013,"申请的API还未发布"),
    w_user_has_not_access_app(220014,"该用户还没有创建接入端"),
    w_wrong_apply_user_info(220015,"发起人员人息错误"),
    w_beyond_the_max_api_name_length(220016,"API名称最长为256"),
    w_beyond_the_max_api_path_length(220017,"API PATH最长为256"),
    w_beyond_the_max_api_description_length(220018,"API 描述最长为256"),
    w_approval_message_neither_empty_nor_beyond_max_length(220019,"审批意见不能为空，长度不超过255"),
    w_api_custom_sql_max_length(220020,"api的自定义sql超长"),

    w_swagger_name_exists(220021,"swagger名称已存在"),
    w_swagger_url_exists(220022,"swagger地址已存在"),
    w_swagger_info_not_exists(220023,"swagger信息不存在"),
    w_swagger_upload_error(220024,"上传失败的数据"),
    w_swagger_have_not_api(220025,"该swagger下不存在api"),
    w_swagger_internal_same_api_name(220027,"swagger自身ApiName重复"),
    w_redis_lock_type_not_null(220028, "Redis锁类型不能为空"),
    w_redis_lock_key_params_not_empty(220029, "Redis锁检测字段至少有一项"),
    w_redis_lock_wait_time_is_invalid(220030, "Redis锁等待时间要在0-60秒之间"),
    w_redis_lock_lease_time_is_invalid(220031, "Redis锁释放时间要在0-60秒之间"),
    w_swagger_delete_fail(220032,"删除当前Swagger中的临时api失败"),
    w_swagger_api_is_not_empty(220032,"SwaggerApi不存在"),
    w_swagger_api_parse_is_fail(220033, "存在解析失败的临时API,请移除该API"),
    w_swagger_api_import_is_fail(220033, "存在导入成功/导入失败的临时API, 请移除该API"),

    w_swagger_api_convert_import_add_success(220034, "新增接口成功"),
    w_swagger_api_convert_import_add_error_api_path(220035, "接口映射地址未找到，执行新增（接口路径重复，新增失败）"),
    w_swagger_api_convert_import_add_error_api_name(220041, "接口映射地址未找到，执行新增（接口名称重复，新增失败）"),
    w_swagger_api_convert_import_update_success(220036, "更新接口数据成功"),
    w_swagger_api_convert_import_update_error(220037, "已上线API不能被覆盖"),
    w_swagger_api_import_add_success(220038, "新增接口成功"),
    w_swagger_api_import_add_fail_api_name(220039, "接口名称重复"),
    w_swagger_api_import_add_fail_api_path(220040, "接口路径重复"),

    w_dao_execute_error(300000, "操作数据库数据发生错误"),
    w_dao_search_error(300001, "获取数据库数据发生错误"),
    w_dao_insert_error(300002, "数据库插入数据发生错误"),
    w_dao_update_error(300003, "数据库更新数据未知发生错误"),
    w_dao_delete_primary_error(300004, "数据库删除主键数据未知发生错误"),
    w_dao_delete_condition_error(300005, "数据库删除条件数据发生错误"),
    w_connection_error(300006, "无法连接"),
    w_sql_clause_error(300007,"调用API数据库语法错误"),
    w_swagger_versions_not_support(300008,"swagger版本不支持"),
    w_domain_name_error(300009,"访问地址输入错误"),
    w_nacos_service_list_error(3000010, "获取服务列表异常"),
    w_nacos_metadata_not_found(3000011, "获取元数据信息异常"),
    w_file_name_not_blank(3000012, "文件名不能为空"),
    w_file_read_error(3000013, "文件读取失败，请尝试重新上传文件后再提交"),
    w_file_name_suffix_not_supported(3000014, "当前文件后缀名不支持上传"),
    w_import_api_text_data_structure_wrong(3000015, "导入api的文件内容格式不正确"),
    w_import_connector_data_structure_wrong(3000016, "导入连接器的文件内容格式不正确"),
    w_file_upload_error(3000017, "文件上传失败，请重试"),
    w_import_text_is_not_json(3000018, "导入的内容不是json格式，请修改后重新上传"),
    w_parse_import_data_fail(3000019, "无法从文件中解析到数据，请检查导入文件的内容"),
    w_open_api_versions_not_support(3000020, "openapi版本不支持"),
    w_resp_param_mapping_rule_not_found(3000021, "无法找到响应体参数映射规则"),


    w_order_delete_node_fail(310001, "排序节点删除失败"),
    w_order_update_node_fail(310002, "排序节点更新失败"),
    w_order_insert_node_fail(310003, "排序节点插入失败"),
    w_order_node_illegal(310004, "排序节点排序数据非法,请联系管理员修复"),
    w_order_node_not_found(310005, "排序节点排序数据不存在"),
    w_order_target_node_not_found(310006, "移动的目标节点不存在"),
    w_order_node_lock_fail(310007, "排序节点锁定失败"),

    w_fr_dashboard_create_fail(410001, "创建自助分析平台仪表盘失败"),
    w_fr_dashboard_update_fail(410002, "自助分析平台仪表盘变更失败"),
    w_fr_dashboard_delete_fail(410003, "删除自助分析平台仪表盘失败"),
    w_fr_dashboard_query_fail(410004, "获取自助分析平台仪表盘失败"),
    w_fr_dashboard_name_exist(410005, "仪表盘名称已存在"),
    w_fr_dashboard_name_not_exist(410006, "仪表盘不存在"),
    w_fr_dashboard_save_as_fail(410007, "仪表盘另存为失败"),
    w_fr_dashboard_remote_request_fail(410008, "请求自助分析平台仪表盘接口发生未知错误"),
    w_fr_dashboard_remote_name_exist(410009, "自助分析平台系统中已存在该名称的仪表盘或目录,请前往自助分析平台核对"),
    w_fr_dashboard_homepage_can_not_delete(410010, "首页自助分析平台仪表盘不允许删除"),
    w_fr_dashboard_homepage_change_fail(410011, "更改自助分析平台仪表盘首页失败"),
    w_fr_dashboard_order_change_fail(410012, "自助分析平台仪表盘排序更改失败"),
    w_fr_data_asset_list_fail(411001, "获取自助分析平台数据资产失败"),
    w_fr_data_asset_detail_fail(411002, "获取自助分析平台数据资产详情失败"),
    w_fr_data_asset_not_exist(411003, "自助分析平台数据资产不存在"),
    w_fr_data_asset_preview_fail(411004, "自助分析平台数据资产预览失败"),
    w_fr_data_asset_list_empty(411005, "获取自助分析平台数据资产列表为空"),
    w_fr_data_asset_detail_empty(411006, "获取自助分析平台数据资产详情为空"),
    w_fr_data_asset_package_empty(411007, "获取自助分析平台数据资产分包为空"),
    w_fr_data_asset_package_fail(411008, "获取自助分析平台数据资产分包失败"),

    w_fr_crystal_select_status_change_fail(412001, "修改水晶球选择状态失败"),
    w_fr_crystal_user_crystal_empty(412002, "用户水晶球列表为空"),
    w_fr_crystal_empty(412003, "水晶球列表为空"),
    w_fr_crystal_user_crystal_not_exist(412004, "用户水晶球不存在"),
    w_fr_crystal_order_change_fail(412005, "水晶球排序更改失败"),

    w_data_access_rule_unavailable(420001, "数据访问规则不可用"),
    w_data_access_req_illegal(420002, "数据请求参数非法"),
    w_data_access_resp_illegal(420003, "数据响应参数非法"),

    w_data_app_id_not_found(430001, "appId对应的应用不存在"),
    w_data_app_id_edit_fail(430002, "数据接入APP编辑失败"),
    w_data_app_id_delete_fail(430003, "数据接入APP删除失败"),
    w_data_app_id_reset_secret_fail(430004, "数据接入APP重置密码失败"),
    w_data_app_id_reference_by_api(430005,"数据接入已被api引用授权,请先解除与api之间的绑定"),

    w_data_approval_pass_fail(440001, "审批通过回调失败"),
    w_data_approval_create_empty(440002, "请求单参数为空"),
    w_data_approval_create_fail(440003, "创建审批单失败"),
    w_data_approval_param_illegal(440004, "审批单创建参数非法"),
    w_data_approval_app_null(440005, "申请权限的app为空"),
    w_data_approval_api_null(440006, "申请的API为空"),
    w_data_approval_app_not_exist(440007, "申请权限的app不存在"),
    w_data_approval_api_not_exist(440008, "申请的API不存在"),
    w_data_approval_api_column_not_exist(440009, "申请的API数据列不存在"),
    w_fr_approval_field_not_exist(440010, "申请的自助分析平台数据资产列不存在"),
    w_fr_approval_write_error(440011, "自助分析平台数据资产权限写入发生错误"),
    w_approval_not_exist(440012, "审批单不存在"),
    w_approval_already_exist(440013, "审批已存在"),

    w_auth_permission_denied(450001, "操作权限不允许"),

    w_data_gateway_app_id_no_found(500001, "appKey不存在或未发布"),
    w_data_gateway_sign_verify_fail(500002, "sign验证失败"),
    w_data_gateway_sign_expire(500003, "sign已过期"),
    w_data_gateway_busy(500004, "系统正忙，请稍候再试"),

    w_data_api_param_illegal(510001, "数据API请求参数非法"),
    w_data_api_not_exist(510002, "数据API不存在"),

    w_data_api_warn_exist(510004,"该api的告警已存在"),
    w_data_api_name_duplicate(510005,"api告警的名称重复"),
    w_data_type_un_support(510006,"不支持的告警类型"),
    w_data_api_rule_exist(510004,"该api的流量控制规则已存在"),
    w_data_api_rule_name_duplicate(510005,"流量控制规则的名称重复"),
    w_api_sql_param_error(510006,"sql中自定义的请求参数异常" ),
    w_api_sql_analysis_failed(510007,"自定义sql解析异常,请检查" ),
    w_api_sql_analysis_type_failed(510008,"只支持查询sql"),
    w_api_sql_analysis_alias_error(510009,"sql函数处理的字段需要追加别名"),
    w_api_sql_analysis_alias_must_not_null(510010,"特定的sql字段需要追加别名"),
    w_api_sql_analysis_column_alias_must_not_null(510011, "返回参数 %s 不能包含符号. ，需要追加别名"),

    w_api_graph_save_file_error(510012,"Api编排保存流程文件失败" ),
    w_api_graph_publish_error(510013,"Api编排需要全部发布状态" ),
    w_api_sql_request_param_type_illegal(510014, "请求参数类型 %s 不合法，请修改"),
    w_api_sql_response_param_must_not_empty(510015, "无法从sql中解析到查询字段，不允许使用select * 语句"),
    w_api_normal_table_update_where_must_not_empty(510016, "数据表修改必须要有where条件，不允许全表修改"),
    w_api_sql_where_must_have_one(510017, "不允许操作全表"),
    w_unsupported_sql_types(510018, "不支持的sql类型"),
    w_acquire_redis_lock_timeout(510019, "获取Redis锁失败，锁名称：%s，超时时长：%s秒"),
    w_release_redis_lock_timeout(510020, "发现Redis锁已被释放，锁名称：%s，锁定时长：%s秒"),
    w_api_graph_execute_error(510100,"Api编排调用失败"),




    w_api_jwt_apply_token_error(510171,"申请Token失败、非法请求！"),
    w_api_jwt_apply_token_issued_date_before_current_error(510172,"申请Token失败、授权截止时间必须大于当前时间！"),
    w_api_jwt_token_error(510173,"Token无效！"),

    w_app_auth_list_error(520001,"查询api列表失败"),
    w_api_is_authorized(520002, "API已经授权，无法删除"),
    w_app_auth_config_error(520003, "查询应用配置失败"),

    w_app_auth_list_delete_error(520006, "已上线应用不能删除，请先下线"),
    w_wrong_api_path_is_not_allowed_to_exceed_level_4(520011, "api path不允许超过4级"),
    w_api_create_approval_repeat(520012, "不允许对API重复申请"),
    w_domain_name_path_is_not_allowed_error(520013, "映射域名不允许带/路径"),
    w_api_domain_name_is_not_empty(520014, "后台服务Host不能为空"),

    w_document_api_id_decoding_exception(520018, "apiId解码失败，请使用正确的apiId"),
    w_json_schema_format_is_incorrect(520019, "json-schema格式不正确"),
    w_func_expression_is_invalid(520020, "函数表达式无效，表达式: %s"),
    w_function_not_found(520021, "未找到函数: %s, 表达式: %s"),
    w_missing_argument(520022, "缺少参数，函数表达式：%s"),
    w_invalid_argument(520023, "无效参数：%s，函数表达式：%s"),
    w_execute_function_error(520024, "执行函数错误: %s"),
    w_missing_comma_after_argument(520025, "参数后缺少逗号：%s，函数表达式：%s"),
    w_missing_close_parenthesis_after_argument(520026, "参数后缺少右括号：%s，函数表达式：%s"),
    w_function_is_not_empty(520027, "函数表达式不能为空"),

    w_swagger_data_query_fail(520028, "读取不到swagger数据"),
    w_license_verity_error(520030, "license证书已过期或校验异常"),

    w_xxl_job_cron_error(520031, "定时任务cron表达式不正确"),
    w_xxl_job_id_is_null(520032, "定时任务id不能为空"),
    w_xxl_job_time_stamp_is_null(520033, "定时任务唯一标识时间戳不能为空"),
    w_xxl_job_info__is_null(520034, "定时任务信息不存在"),
    w_xxl_job_schedule_is_null(520035, "调度中心任务信息不存在"),
    w_api_id_is_not_empty(520036, "api id不能为空"),
    w_event_receive_is_null(520037, "接收算子配置不存在，id：%s"),
    w_api_have_xxl_job(520038, "该API绑定了定时任务"),
    w_api_is_un_public(520039, "所选API为下线状态,不能绑定定时任务"),
    w_xxl_job_cron_is_un_valid(520040, "定时任务表达式cron不合法"),
    w_xxl_job_is_not_in_time(520041, "定时任务执行时间不在设定范围内"),
    w_xxl_job_cron_is_not_execute(520042, "定时任务cron表达式不会被执行"),
    w_connector_api_group_name_already_existed(520043, "连接器平台api分组的名称重复"),
    w_connector_api_group_id_cannot_be_null(520044, "连接器平台api分组的id不能为空"),
    w_connector_api_group_is_already_associated_with_api(520045, "分类下存在接口数据，不允许删除"),
    w_connector_api_group_not_exist(520046, "不存在此连接器api分组"),
    w_connector_is_not_exit(520047, "连接器平台不存在"),
    w_connector_group_is_not_exit(520048, "连接器平台api分组不存在"),
    w_connector_api_name_already_existed(520049, "连接器平台api的名称重复"),
    w_connector_api_method_already_existed(520050, "连接器平台api的路径重复"),
    w_connector_api_id_cannot_be_null(520052, "连接器平台api的id不能为空"),
    w_connector_api_not_exist(520053, "该连接器平台api不存在，请刷新页面"),
    w_connector_api_is_published(520054, "该连接器平台api已发布，请先下线该api之后再进行删除"),
    w_connector_auth_config_is_relate(520061, "该连接器鉴权配置已关联应用，无法删除"),
    w_connector_auth_identification_is_repeat(520062, "连接器鉴权配置标识不能重复"),
    w_export_api_fail(520063, "导出api文件失败"),
    w_no_data_open_api_to_export(520064, "选择的分类下未发现可导出的API，请重新选择")
    ;


    private int code;

    /**
     * 错误描述
     */
    private String desc;


    MsgCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public int getCode() {
        return code;
    }


    private void setCode(int code) {
        this.code = code;
    }


    public String getDesc() {
        return desc;
    }


    private void setDesc(String desc) {
        this.desc = desc;
    }


}
