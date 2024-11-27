/**
 *
 * @param {Vue.Component} Component
 * @param {{defaultProps?: {[key: string]: any}, extendsProps?: {[key: string]: Vue.PropOptions}}} [options]
 */
export function overideProps(Component, options = {}) {
  const props = getProps(Component);
  const { defaultProps, extendsProps } = options;

  // 覆盖默认值
  if (defaultProps) {
    for (const key in defaultProps) {
      if (key in props) {
        // copy on write
        props[key] = { ...(props[key] || {}), default: defaultProps[key] };
      }
    }
  }

  // 自定义 props
  if (extendsProps) {
    for (const key in extendsProps) {
      // copy on write
      props[key] = { ...(props[key] || {}), ...extendsProps[key] };
    }
  }

  return props;
}

/**
 * 规范化 props 格式
 * @param {*} props
 */
function normalizeProps(props) {
  if (props == null) {
    return {};
  }

  if (Array.isArray(props)) {
    return props.reduce((prev, cur) => {
      prev[cur] = {};
      return prev;
    }, {});
  }

  const newProps = {};

  for (const key in props) {
    const value = props[key];
    newProps[key] = value;

    // type
    if (Array.isArray(value) || typeof value === 'function') {
      newProps[key] = {
        type: value,
      };
    }
  }

  return newProps;
}

/**
 * 获取一个组件的 props 定义
 */
export function getProps(componentCtor) {
  const props = {};

  // props 可能定义在 mixins 中
  if (componentCtor.mixins) {
    componentCtor.mixins.forEach(m => {
      if (m.props) {
        Object.assign(props, normalizeProps(m.props));
      }
    });
  }

  if (componentCtor.props) {
    Object.assign(props, normalizeProps(componentCtor.props));
  }

  return props;
}

/**
 * 判断是否存在 相关 slots
 * @param {string} name slot 名称
 * @param {Vue} [vm] Vue 实例, 作为 Vue methods 使用时可选
 */
export function hasSlots(name, vm) {
  // 作为 methods
  if (this && this.$options) {
    return !!(this.$slots[name] || this.$scopedSlots[name]);
  }

  if (vm == null) {
    throw TypeError('请传入 vm 参数');
  }

  return !!(vm.$slots[name] || vm.$scopedSlots[name]);
}
