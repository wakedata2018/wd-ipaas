export function getPathData(data, path) {
  const paths = path.split('.');
  return paths.reduce((val, next) => {
    return val ? val[next] : undefined;
  }, data);
}
export function getTreeCurrentObj(
  tree,
  value,
  {
    isLeafProp = 'isLeaf',
    childrenProp = 'children',
    valueProp = 'id',
    pathProp = 'path'
  } = {
    isLeafProp: 'isLeaf',
    childrenProp: 'children',
    valueProp: 'id',
    pathProp: 'path'
  },
  findedPath = ''
) {
  let finded = null;
  if (value === null || value === undefined) return null;
  if (tree) {
    for (let i = 0; i < tree.length; i++) {
      const item = tree[i];
      let finalPath = findedPath;
      if (item[pathProp]) {
        finalPath += item[pathProp] + '/';
      }

      if (!value || getPathData(item, valueProp) + '' == value + '') {
        finded = {
          ...item,
          path: finalPath
        };
        break;
      }
      if (
        item[childrenProp] &&
        item[childrenProp].length > 0 &&
        !item[isLeafProp]
      ) {
        {
          const treeConfig = {
            isLeafProp,
            childrenProp,
            valueProp,
            pathProp
          };
          finded = getTreeCurrentObj(
            item[childrenProp],
            value,
            treeConfig,
            finalPath
          );
          if (finded) {
            break;
          }
        }
      }
    }
  }
  return finded;
}
