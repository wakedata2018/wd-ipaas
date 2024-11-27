import { FatFormTabsLayout } from '@wakeadmin/components';
import './index.less';

const DrawLayout: FatFormTabsLayout = props => {
  return (
    <div class="draw-layout">
      {props.renderTabs?.()}
      <div class="footer bd-dialog-footer">{props.renderSubmitter?.()}</div>
    </div>
  );
};

export default DrawLayout;
