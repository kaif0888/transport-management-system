package com.tms.filter.criteria.bean;

import com.tms.filter.criteria.constant.FilterOperation;

public class FilterCriteriaBean {
    private String attribute;
    private FilterOperation operation;
    private String value;
    private Class<?> valueType = String.class; // default to String

    public FilterCriteriaBean() {}

    public FilterCriteriaBean(String attribute, FilterOperation operation, String value) {
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
    }

    public FilterCriteriaBean(String attribute, FilterOperation operation, String value, Class<?> valueType) {
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
        this.valueType = valueType;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public FilterOperation getOperation() {
        return operation;
    }

    public void setOperation(FilterOperation operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public void setValueType(Class<?> valueType) {
        this.valueType = valueType;
    }
}
