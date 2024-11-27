import Vue from 'vue';
/**
 * 整体的事件总线
 */
const overallEventName = {
  scrollTo: 'scroll-to',
  showOrHideSidemenu: 'show-or-hide-sidemenu',
  windowResize: 'window-resize',
  changeNarrowSidebar: 'change-narrow-sidebar'
};
export { overallEventName };
export default new Vue();
