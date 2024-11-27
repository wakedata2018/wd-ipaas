export interface ContextMenuItem {
  cmd: string;
  name: string;
  divided?: boolean;
}
export interface ContextMenuDefinition {
  getMenus(item: any, com: any): ContextMenuItem[];

  // 事件处理器
  [eventHandler: `on_${string}`]: (command: string, node: any) => void;
}
