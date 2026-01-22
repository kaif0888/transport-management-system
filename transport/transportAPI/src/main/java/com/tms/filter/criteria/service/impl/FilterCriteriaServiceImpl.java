package com.tms.filter.criteria.service.impl;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FilterCriteriaServiceImpl<T> implements FilterCriteriaService<T> {

    @Autowired
    private EntityManager em;

    private static final List<String> DATE_FORMATS = List.of(
        "yyyy-MM-dd",
        "yyyy-MM-dd HH:mm:ss"
    );

    private Object parseValue(String value, Class<?> type) throws ParseException {
        if (type == String.class) {
            return value;
        } else if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(value);
        } else if (type == Float.class || type == float.class) {
            return Float.parseFloat(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == Date.class) {
            return parseDate(value);
        }
        // Default fallback
        return value;
    }

    private Date parseDate(String value) throws ParseException {
        for (String format : DATE_FORMATS) {
            try {
                return new SimpleDateFormat(format).parse(value);
            } catch (ParseException ignored) {
            }
        }
        throw new ParseException("Unparseable date: " + value, 0);
    }

    private Path<?> getPath(Path<?> root, String attribute) {
        Path<?> path = root;
        for (String part : attribute.split("\\.")) {
            path = path.get(part);
        }
        return path;
    }

    private void applyPredicate(List<Predicate> predicates, CriteriaBuilder cb, Path<?> path, FilterCriteriaBean filter, Object value) {
        String valueStr = filter.getValue();
        FilterOperation op = filter.getOperation();

        switch (op) {
            case EQUALS -> predicates.add(cb.equal(path, value));
            case NOT_EQUALS -> predicates.add(cb.notEqual(path, value));
            case GREATER_THAN -> {
                if (value instanceof Comparable) {
                    predicates.add(cb.greaterThan((Path<Comparable>) path, (Comparable) value));
                }
            }
            case LESS_THAN -> {
                if (value instanceof Comparable) {
                    predicates.add(cb.lessThan((Path<Comparable>) path, (Comparable) value));
                }
            }
            case GREATER_THAN_OR_EQUAL -> {
                if (value instanceof Comparable) {
                    predicates.add(cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value));
                }
            }
            case LESS_THAN_OR_EQUAL -> {
                if (value instanceof Comparable) {
                    predicates.add(cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value));
                }
            }
            case STARTS_WITH -> predicates.add(cb.like(cb.lower((Path<String>) path), valueStr.toLowerCase() + "%"));
            case CONTAINS -> predicates.add(cb.like(cb.lower((Path<String>) path), "%" + valueStr.toLowerCase() + "%"));
            case MATCHES -> predicates.add(cb.like((Path<String>) path, valueStr));
            case NOT_MATCHES -> predicates.add(cb.notLike((Path<String>) path, valueStr));
            case AMONG -> {
                List<Object> inValues = new ArrayList<>();
                for (String val : valueStr.split(",")) {
                    try {
                        inValues.add(parseValue(val.trim(), filter.getValueType() != null ? filter.getValueType() : String.class));
                    } catch (ParseException e) {
                        throw new RuntimeException("Error parsing value in AMONG operation", e);
                    }
                }
                predicates.add(path.in(inValues));
            }
            case NOT_AMONG -> {
                List<Object> notInValues = new ArrayList<>();
                for (String val : valueStr.split(",")) {
                    try {
                        notInValues.add(parseValue(val.trim(), filter.getValueType() != null ? filter.getValueType() : String.class));
                    } catch (ParseException e) {
                        throw new RuntimeException("Error parsing value in NOT_AMONG operation", e);
                    }
                }
                predicates.add(cb.not(path.in(notInValues)));
            }
            default -> throw new UnsupportedOperationException("Unknown operation: " + op);
        }
    }

    @Override
    public List<?> getListOfFilteredData(Class<T> clazz, List<FilterCriteriaBean> criteriaList, Integer limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);

        // Order by mDate descending if present
        try {
            root.get("mDate");
            cq.orderBy(cb.desc(root.get("mDate")));
        } catch (IllegalArgumentException e) {
            // Ignore if mDate not present
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (FilterCriteriaBean filter : criteriaList) {
                String attr = filter.getAttribute();
                String valueStr = filter.getValue();

                // Skip invalid or empty filters
                if (valueStr == null || "string".equals(valueStr) || valueStr.isEmpty()) {
                    continue;
                }

                Class<?> valueType = filter.getValueType() != null ? filter.getValueType() : String.class;

                try {
                    Path<?> path = getPath(root, attr);
                    Object value = parseValue(valueStr, valueType);
                    applyPredicate(predicates, cb, path, filter, value);
                } catch (ParseException e) {
                    throw new RuntimeException("Failed to parse value for attribute: " + attr + ", value: " + valueStr, e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid attribute path: " + attr, e);
                }
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<T> query = em.createQuery(cq);

        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public List<T> getFilteredData(Class<T> clazz, List<FilterCriteriaBean> criteriaList, Integer limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);

        // Optional ordering by mDate if it exists
        try {
            root.get("mDate");
            cq.orderBy(cb.desc(root.get("mDate")));
        } catch (IllegalArgumentException ignored) {
            // Ignore if "mDate" doesn't exist
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (FilterCriteriaBean filter : criteriaList) {
                String attr = filter.getAttribute();
                String valueStr = filter.getValue();

                if (valueStr == null || valueStr.isEmpty() || "string".equalsIgnoreCase(valueStr)) {
                    continue;
                }

                Class<?> valueType = filter.getValueType() != null ? filter.getValueType() : String.class;

                try {
                    Path<?> path = getPath(root, attr);
                    Object value = parseValue(valueStr, valueType);
                    applyPredicate(predicates, cb, path, filter, value);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid filter on attribute: " + attr + " — " + e.getMessage(), e);
                }
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<T> query = em.createQuery(cq);
        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

}
