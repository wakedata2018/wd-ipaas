let eventMap: Record<string, ((...args: any[]) => any)[]> = {};
const event = {
  on(eventName: string, fn: (...args: any[]) => any) {
    if (!eventMap[eventName]) {
      eventMap[eventName] = [];
    }
    eventMap[eventName].push(fn);
  },
  off(eventName: string, fn: (...args: any[]) => any) {
    const events = eventMap[eventName];
    if (events?.length) {
      eventMap[eventName] = events.filter(eventFn => eventFn !== fn);
    }
  },
};

export default event;
