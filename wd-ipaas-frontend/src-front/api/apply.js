import { services } from 'dss-common'
const controller = '/dw/open/business';

export default {
    getApplyDetail(params){
        return services.get(`${controller}/data_access_rule/app_asset_access_rule`,{
            action:'获取要申请的数据列表',
            params
        })
    },
    applyApi(params){
        return services.json(`${controller}/approval/data_access_rule`,params,{
            action:'API申请'
        })
    }
}