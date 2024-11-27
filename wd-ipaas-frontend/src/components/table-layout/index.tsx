import { FatTableLayout } from '@wakeadmin/components';
import './index.less';

const DefaultLayout: FatTableLayout = props => {
  return (
    <div class="bd-page page-layout">
      <div class="bd-header">
        <div class="title">{props.renderTitle?.()}</div>
        <div class="header-right">{props?.renderNavBar?.()}</div>
      </div>
      <div class="bd-container">
        <div class="bd-search page-search">{props?.renderQuery?.()}</div>
        <div class="bd-table theme-table">{props?.renderTable?.()}</div>
        <div class="page-pagination">{props.renderPagination?.()}</div>
      </div>
    </div>
  );
};

export default DefaultLayout;
