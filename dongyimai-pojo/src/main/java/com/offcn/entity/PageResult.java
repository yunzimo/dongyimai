package com.offcn.entity;

import com.offcn.pojo.TbBrand;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    private Long total;  //总记录数
    private List rows;//分页后的品牌集合

    public PageResult() {
    }

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
