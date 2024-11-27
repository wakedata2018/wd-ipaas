import { IG6GraphEvent } from '@antv/g6';
import eventBus, { EventName } from '../event-bus';

const isMac = /macintosh|mac os x/i.test(navigator.userAgent);

// 需要 ctrl 或 command 的快捷键
const needCtrls: string[] = [];
// 快捷键对应事件名
const keyCodeEvent: Record<string, string> = {
  backspace: EventName.deleteItem,
  delete: EventName.deleteItem,
};

export default {
  getDefaultCfg() {
    return {};
  },
  getEvents() {
    return {
      keydown: 'onKeyDown',
    };
  },
  onKeyDown(e: IG6GraphEvent) {
    const code = (e.key ?? '').toLowerCase();
    if (!code) {
      return;
    }
    const target = e.target as any;
    if ((target.tagName ?? '').toLowerCase() === 'body') {
      const eventKey = keyCodeEvent[code];
      if (eventKey) {
        e.preventDefault();
        if (needCtrls.includes(code)) {
          if (isMac ? e.metaKey : e.ctrlKey) {
            eventBus.$emit(eventKey);
          }
        } else {
          eventBus.$emit(eventKey);
        }
      }
    }
  },
};
