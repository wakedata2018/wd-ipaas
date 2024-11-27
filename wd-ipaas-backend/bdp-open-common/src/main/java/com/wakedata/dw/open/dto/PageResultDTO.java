package com.wakedata.dw.open.dto;

import com.github.pagehelper.Page;
import java.io.Serializable;

/**
 * @author pengxy
 * @title PageResultDTO
 * @projectName bdp-open
 * @date 2016/10/31
 * @description
 */
public class PageResultDTO<T> extends ResultDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageSize;

    private Integer pageNo;

    private Integer totalCount;

    public Integer getPageSize() {
        return pageSize;
    }


    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public Integer getPageNo() {
        return pageNo;
    }


    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }


    public Integer getTotalCount() {
        return totalCount;
    }


    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public static <T extends Page<?>> PageResultDTO<T> success(T data) {
        PageResultDTO<T> resultDTO = new PageResultDTO<>();
        resultDTO.setData(data);
        resultDTO.setTotalCount((int) data.getTotal());
        return resultDTO;
    }

    public PageResultDTO() {
        super();
    }

    public PageResultDTO(Integer pageSize, Integer pageNo) {
        super();
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    @Override
    public String toString() {
        return "PageResultDTO{" + "pageSize=" + pageSize + ", pageNo=" + pageNo + ", totalCount="
                + totalCount + '}';
    }
}
