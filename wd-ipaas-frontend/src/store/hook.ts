import store from '.';
import { Store } from 'vuex';

export interface StoreState {
  user: String | null;
  theme: String;
  apiDeployList: never[];
  permitList: never[];
}

export function useStore() {
  return store as Store<StoreState>;
}
