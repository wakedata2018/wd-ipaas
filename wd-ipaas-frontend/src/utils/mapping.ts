type JsonSchemaList = Record<string, JsonSchemaProperties>;

type JsonSchemaType = 'array' | 'object' | 'string' | 'long' | 'integer' | 'boolean' | 'double';
interface JsonSchemaProperties {
  name: string;
  type: JsonSchemaType;
  value: string;
  disabled?: boolean;
  properties?: JsonSchemaList;
  items?: JsonSchemaProperties;
}

// 禁用所有子节点
// const disableAllChild = (list: JsonSchemaList) => {
//   Object.values(list).forEach((field: JsonSchemaProperties) => {
//     const type = field.type;
//     // 对象
//     if (type === 'object' && field.properties) {
//       disableAllChild(field.properties);
//     } else if (type === 'array' && field.items?.type === 'object' && field.items?.properties) {
//       // 对象数组
//       disableAllChild(field?.items?.properties);
//     }
//     field.disabled = true;
//   });
// };

// // 查找是否有映射值得子节点
// const hasValue = (list: JsonSchemaList) => {
//   return Object.values(list).findIndex(field => {
//     return field.value;
//   });
// };

// // 循环work
// // rules 收集树的所有映射值
// const loopTree = (tree: JsonSchemaList = {}, rules: string[] = []) => {
//   Object.values(tree).forEach(item => {
//     const type = item.type;
//     const value = item.value;

//     // 对象
//     if (type === 'object' && item.properties) {
//       // 有映射值
//       if (value) {
//         // 禁用所有下级节点
//         disableAllChild(item.properties);
//       } else {
//         // 没有映射值
//         // 查找是否有映射值的孩子
//         const hasValueChild = hasValue(item.properties);
//         // 禁用规则：存在有映射值的孩子
//         // 开启规则：所有孩子都没有映射值
//         if (hasValueChild !== -1) {
//           item.disabled = true;
//         } else {
//           item.disabled = false;
//         }
//         loopTree(item.properties, rules);
//       }
//     } else if (type === 'array' && item.items?.type === 'object' && item.items.properties) {
//       item.disabled = false;
//       // 对象数组 禁用下级所有节点
//       disableAllChild(item.items.properties);
//     } else {
//       // 其他 打开
//       item.disabled = false;
//     }

//     if (value && !rules.includes(value)) {
//       rules.push(value);
//     }
//   });
//   return rules;
// };

const getRules = (tree: JsonSchemaList = {}, rules: string[] = []) => {
  Object.values(tree).forEach(item => {
    const type = item.type;
    const value = item.value;

    // 对象
    if (type === 'object' && item.properties) {
      getRules(item.properties, rules);
    }

    if (value && !rules.includes(value)) {
      rules.push(value);
    }
  });
  return rules;
};

export { getRules };
