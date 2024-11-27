/**
 * 页面Tittle
 * menuData 路由
 * identifier 权限标识符
 * name 返回值
 */
function findPageName(menuData, identifier) {
  let name = '';
  for (const item of menuData) {
    if (item.identifier && item.identifier === identifier) {
      name = item.name;
      break;
    } else if (item.childMenu.length && !name) {
      name = findPageName(item.childMenu, identifier);
    }
  }
  return name;
}

export default {
  findPageName,
};
