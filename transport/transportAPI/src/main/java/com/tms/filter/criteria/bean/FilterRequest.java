package com.tms.filter.criteria.bean;

import java.util.List;

public class FilterRequest {
    private Integer limit;
    private List<FilterCriteriaBean> filters;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<FilterCriteriaBean> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterCriteriaBean> filters) {
        this.filters = filters;
    }
}
