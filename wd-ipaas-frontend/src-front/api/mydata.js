import { services } from 'dss-common'
const controller = '/dw/open/business';

export default {
    getApiList(params) {
        return services.get(`${controller}/data_access_app/my`, {
            action: '获取列表',
            params
        })
    },
    getApplyList(params) {
        return services.get(`${controller}/approval/history`, {
            action: '获取我的申请列表',
            params
        })
    },
    getCollectList(params) {
        return services.get(`${controller}/collect/my_collect`, {
            action: '获取我的收藏列表',
            params
        })
    },
    deleteCollect(params) {
        return services.post(`${controller}/collect/delete`, params, {
            action: '取消收藏'
        })
    },
    getStatisList(params) {
        return services.get(`${controller}/api/my_api`, {
            action: '获取我的api统计列表',
            params
        })
    },
    getItemList() {
        return services.get(`${controller}/api/list`, {
            action: '获取过滤信息'
        })
    },
    getJoinInfo(params) {
        return services.get(`${controller}/data_access_app/my`, {
            params,
            action: '获取我的接入信息'
        })
    },
    addJoin(params) {
        return services.post(`${controller}/data_access_app/create`, params, {
            action: '新增接入'
        })
    },
    editJoin(params) {
        return services.post(`${controller}/data_access_app/edit`, params, {
            action: '编辑接入'
        })
    },
    resetSecret(params) {
        return services.post(`${controller}/data_access_app/reset_secret`, params, {
            action: '重置密钥'
        })
    },
    deleteJoin(params) {
        return services.post(`${controller}/data_access_app/delete`, params, {
            action: '删除接入'
        })
    },
    deleteMyApply(params) {
        return services.post(`${controller}/api/delete_my_api`, params, {
            action: '删除我的接入'
        })
    }
}