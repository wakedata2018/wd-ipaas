// env.d.ts
// 你可能把旧的 declare module '*.vue' 移除
declare module '*.vue' {
  import type { DefineComponent } from 'vue';
  const component: DefineComponent<{}, {}, any>;
  export default component;
}
