function authorize(permitList, code) {
  let hash = location.hash;
  hash = hash.split('?')[0] ?? hash;
  hash = hash.substring(1);
  const currentMenu = handleData(permitList, hash);
  if (Array.isArray(currentMenu) && currentMenu.length > 0) {
    const current = currentMenu.find(item => item.identifier === code);
    return !!current;
  }
  return false;
}

function handleData(list, hash) {
  for (const index in list) {
    const item = list[index];
    if (item.url === hash) {
      return item.childMenu;
    } else {
      const menu = handleData(item.childMenu || [], hash);
      if (Array.isArray(menu)) {
        return menu;
      } else {
        continue;
      }
    }
  }
}

export { authorize };
