package com.tms.filter.criteria.service;

import java.util.List;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface FilterCriteriaService<T> {
    List<?> getListOfFilteredData(Class<T> clazz, List<FilterCriteriaBean> criteriaList, Integer limit);
}
