import services from '@/common/services';
const controller = '/dw/open/business/xxl';

export default {

  /**
   * 查询XxlJob任务列表
   * @param {Object} params 
   * @returns 
   */
  getTaskList(params) {
    return services.json(`${controller}/page.list`, params, {
      action: '查询XxlJob任务列表',
    });
  },

  /**
   * 更新定时任务
   * @param {Object} params 
   * @returns 
   */
   updateTask(params) {
    return services.json(`${controller}/update`, params, {
      action: '更新定时任务',
    });
  },

   /**
   * 新增定时任务
   * @param {Object} params 
   * @returns 
   */
    addTask(params) {
      return services.json(`${controller}/add`, params, {
        action: '新增定时任务',
      });
    },

  /**
   * 根据id删除定时任务
   * @param {Object} params 
   * @returns 
   */
  deleteTask(params) {
    return services.get(`${controller}/delete`, {
      action: '删除定时任务',
      params,
    });
  },
}
