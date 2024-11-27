/**
 * mutation 和 action 常量
 * 加上命名空间，防止触发多个mutation
 */

const list = {
    common: [
        // 当前登录用户信息
        'CUR_USER_INFO'
    ],
    config: [
        'DATATYPE', // 数据库字段类型列表
        'EXPRTYPE', // 操作符类型列表
        'CHARTTYPE', // 报表布局类型查询
    ],
    auth: [
        // 查询权限
        'FETCH_PRIVS',
        // 设置权限
        'SET_PRIVS'
    ],
    dataset: [
        // 查询数据集列表
        'FETCH_DATASETS',
        // 设置数据集列表
        'SET_DATASETS'
    ],
    selfhelp: [
        'SAVE_METAS', // 字段保存
        'SAVE_PARAMS', // 数据集保存
    ],
    visual: [
        'VIEW_CONFIG' // 可视化视图核心配置(存库配置)
    ]
};

// 映射
const types = {};
let key, item;
for (key in list) {
    types[key] = {};
    for (item of list[key]) {
        types[key][item] = `${key}\\${item}`;
    }
}

export default types;
