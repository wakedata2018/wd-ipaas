import services from '@/common/services';
import store from '../store';
const controller = '/dw/open/action';
let resource = 'open.dashboard';
let threadId = null;

function getUser() {
  const user = store.state.user;
  if (user && user.loginName) {
    return user.loginName;
  }
  return '';
}
function getUrl() {
  const requestUrl = window.location.href;
  // console.log(requestUrl);
  return requestUrl;
}
export default {
  heartbeat() {
    return services.post(
      `${controller}/report`,
      {
        pageResource: resource,
        pageEvent: 'heartbeat',
        actionUser: getUser(),
      },
      {
        action: '心跳包',
        hidden: true,
      }
    );
  },
  report(pageResource, pageEvent = 'click') {
    resource = pageResource;
    return services.post(
      `${controller}/report`,
      {
        pageResource,
        pageEvent,
        actionUser: getUser(),
        requestUrl: getUrl(),
      },
      {
        action: '上报事件',
        hidden: true,
      }
    );
  },
  runHeartbeat() {
    this.heartbeat().always(() => {
      threadId = setTimeout(() => {
        this.runHeartbeat();
      }, 6000);
    });
  },
  startHeartbeat() {
    this.stopHeartbeat();
    this.runHeartbeat();
  },
  stopHeartbeat() {
    if (threadId) {
      clearTimeout(threadId);
    }
  },
  queryReport(params) {
    return services.json(`${controller}/query/report`, params, {
      action: '查询操作日志',
    });
  },
  queryMenu(params) {
    return services.get(`${controller}/query/menu`, {
      action: '查询菜单',
      params,
    });
  },
  queryOperator(params) {
    return services.get(`${controller}/query/identification/list`, {
      action: '查询日志操作用户',
      params,
    });
  },
  queryLogDetail(params) {
    return services.get(`${controller}/query/audit/log/information`, {
      action: '查询日志详情',
      params,
    });
  },
};
