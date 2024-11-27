package com.wakedata.dw.open.model.query;

/**
 * @author pengxu
 * @title PageQuery
 * @projectName bdp-open
 * @date 2016/10/31
 * @description
 */
public class PageQuery {

    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 分页数，从1开始
     */
    protected int pageNo = DEFAULT_PAGE_NO;

    protected int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 偏移量，自动计算的值，分页用，无需设置
     */
    protected int offset;

    public int getPageNo() {
        return pageNo;
    }


    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }


    public int getPageSize() {
        return pageSize;
    }


    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public void setOffset(int offset) {
        this.offset = offset;
    }


    public int getOffset() {
        if (pageNo == 0) {
            return 0;
        }
        return (pageNo - 1) * pageSize;
    }

    public PageQuery() {
    }

    public PageQuery(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
