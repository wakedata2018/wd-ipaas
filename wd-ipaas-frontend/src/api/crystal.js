import services from '@/common/services';
const controller = '/dw/open/business/crystal';

export default {
  create(params) {
    return services.post(`${controller}/add`, params, {
      action: '创建报表',
    });
  },
  delete(id) {
    return services.post(
      `${controller}/delete`,
      {
        id,
      },
      {
        action: '删除报表',
      }
    );
  },
  get(id) {
    return services.get(`${controller}/show`, {
      action: '获取报表',
      params: { id },
    });
  },
  list(params) {
    return services.get(`${controller}/show_all`, {
      action: '获取报表列表',
      params,
    });
  },
  update(params) {
    return services.post(`${controller}/update`, params, {
      action: '更新报表',
    });
  },
  groupList(params) {
    return services.get(`${controller}/group/show_all`, {
      action: '获取报表分组列表',
      params,
    });
  },
  pageGroupList(params) {
    return services.get(`${controller}/group/show_all/paged`, {
      action: '获取报表分组列表',
      params,
    });
  },
  addGroup(params) {
    return services.post(`${controller}/group/add`, params, {
      action: '创建报表分组',
    });
  },
  updateGroup(params) {
    return services.post(`${controller}/group/update`, params, {
      action: '更新报表分组',
    });
  },
  deleteGroup(id) {
    return services.post(
      `${controller}/group/delete`,
      {
        id,
      },
      {
        action: '删除报表分组',
      }
    );
  },
  showUsers(params) {
    // GET /dw/open/business/crystal/show_users_by_id
    return services.get(`${controller}/show_users_by_id`, {
      params,
      action: '获取报表授权用户',
    });
  },
  userJoin(crystalDetailId, userBips) {
    return services.post(
      `${controller}/join`,
      {
        crystalDetailId,
        userBips,
      },
      {
        action: '报表授权用户',
      }
    );
  },
  userUnjoin(crystalDetailId, userBips) {
    return services.post(
      `${controller}/un_join`,
      {
        crystalDetailId,
        userBips,
      },
      {
        action: '报表授权用户',
      }
    );
  },
};
