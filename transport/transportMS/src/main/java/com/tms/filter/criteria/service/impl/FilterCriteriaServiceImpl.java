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

    private Object parseValue(String value, Class<?> type) throws ParseException {
        if (type == String.class) {
            return value;
        } else if (type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == Long.class) {
            return Long.parseLong(value);
        } else if (type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == Date.class) {
            // Customize date format as needed
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(value);
        }
        // Default fallback
        return value;
    }
    
    @Override
    public List<?> getListOfFilteredData(Class<T> clazz, List<FilterCriteriaBean> criteriaList, Integer limit) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);

        // Optional: default order by mDate descending if exists
        try {
            root.get("mDate");
            cq.orderBy(cb.desc(root.get("mDate")));
        } catch (IllegalArgumentException e) {
            // mDate not present, skip ordering
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteriaList != null && !criteriaList.isEmpty()) {
            for (FilterCriteriaBean filter : criteriaList) {
                String attr = filter.getAttribute();
                String valueStr = filter.getValue();
                FilterOperation op = filter.getOperation();
                
                if (valueStr == null || "string".equals(valueStr) || valueStr.isEmpty()) {
                    continue;
                }

                Class<?> valueType = filter.getValueType() != null ? filter.getValueType() : String.class;

                try {
                    Path<?> path = root.get(attr);
                    Object value = parseValue(valueStr, valueType);

                    switch (op) {
                        case EQUALS:
                            predicates.add(cb.equal(path, value));
                            break;
                        case NOT_EQUALS:
                            predicates.add(cb.notEqual(path, value));
                            break;
                        case GREATER_THAN:
                            if (value instanceof Comparable) {
                                predicates.add(cb.greaterThan((Path<Comparable>) path, (Comparable) value));
                            }
                            break;
                        case LESS_THAN:
                            if (value instanceof Comparable) {
                                predicates.add(cb.lessThan((Path<Comparable>) path, (Comparable) value));
                            }
                            break;
                        case GREATER_THAN_OR_EQUAL:
                            if (value instanceof Comparable) {
                                predicates.add(cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value));
                            }
                            break;
                        case LESS_THAN_OR_EQUAL:
                            if (value instanceof Comparable) {
                                predicates.add(cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value));
                            }
                            break;
                        case STARTS_WITH:
                            predicates.add(cb.like(cb.lower((Path<String>) path), valueStr.toLowerCase() + "%"));
                            break;
                        case CONTAINS:
                            predicates.add(cb.like(cb.lower((Path<String>) path), "%" + valueStr.toLowerCase() + "%"));
                            break;
                        case MATCHES:
                            predicates.add(cb.like((Path<String>) path, valueStr));
                            break;
                        case NOT_MATCHES:
                            predicates.add(cb.notLike((Path<String>) path, valueStr));
                            break;
                        case AMONG:
                            List<Object> inValues = new ArrayList<>();
                            for (String val : valueStr.split(",")) {
                                inValues.add(parseValue(val.trim(), valueType));
                            }
                            predicates.add(path.in(inValues));
                            break;
                        case NOT_AMONG:
                            List<Object> notInValues = new ArrayList<>();
                            for (String val : valueStr.split(",")) {
                                notInValues.add(parseValue(val.trim(), valueType));
                            }
                            predicates.add(cb.not(path.in(notInValues)));
                            break;
                        default:
                            throw new UnsupportedOperationException("Unknown operation: " + op);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException("Failed to parse value for attribute: " + attr + ", value: " + valueStr, e);
                }
            }
        }

        // ✅ Only apply filters if any valid predicates exist
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<T> query = em.createQuery(cq);

        // ✅ Always apply the limit
        if (limit != null && limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }


//    @Override
//    public List<?> getListOfFilteredData(Class<T> clazz, List<FilterCriteriaBean> criteriaList, Integer limit) {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<T> cq = cb.createQuery(clazz);
//        Root<T> root = cq.from(clazz);
//
//        // Optional: default order by mDate descending if exists
//        try {
//            root.get("mDate");
//            cq.orderBy(cb.desc(root.get("mDate")));
//        } catch (IllegalArgumentException e) {
//            // Attribute mDate does not exist, skip ordering
//        }
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        if (criteriaList != null) {
//            for (FilterCriteriaBean filter : criteriaList) {
//                String attr = filter.getAttribute();
//                String valueStr = filter.getValue();
//                FilterOperation op = filter.getOperation();
//                Class<?> valueType = filter.getValueType() != null ? filter.getValueType() : String.class;
//
//                try {
//                    Path<?> path = root.get(attr);
//                    Object value = parseValue(valueStr, valueType);
//
//                    switch (op) {
//                        case EQUALS:
//                            predicates.add(cb.equal(path, value));
//                            break;
//                        case NOT_EQUALS:
//                            predicates.add(cb.notEqual(path, value));
//                            break;
//                        case GREATER_THAN:
//                            if (value instanceof Comparable) {
//                                predicates.add(cb.greaterThan((Path<Comparable>) path, (Comparable) value));
//                            }
//                            break;
//                        case LESS_THAN:
//                            if (value instanceof Comparable) {
//                                predicates.add(cb.lessThan((Path<Comparable>) path, (Comparable) value));
//                            }
//                            break;
//                        case GREATER_THAN_OR_EQUAL:
//                            if (value instanceof Comparable) {
//                                predicates.add(cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value));
//                            }
//                            break;
//                        case LESS_THAN_OR_EQUAL:
//                            if (value instanceof Comparable) {
//                                predicates.add(cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value));
//                            }
//                            break;
//                        case STARTS_WITH:
//                            predicates.add(cb.like(cb.lower((Path<String>) path), valueStr.toLowerCase() + "%"));
//                            break;
//                        case CONTAINS:
//                            predicates.add(cb.like(cb.lower((Path<String>) path), "%" + valueStr.toLowerCase() + "%"));
//                            break;
//                        case MATCHES:
//                            predicates.add(cb.like((Path<String>) path, valueStr));
//                            break;
//                        case NOT_MATCHES:
//                            predicates.add(cb.notLike((Path<String>) path, valueStr));
//                            break;
//                        case AMONG:
//                            // Split comma separated values, parse each to the right type
//                            List<Object> inValues = new ArrayList<>();
//                            for (String val : valueStr.split(",")) {
//                                inValues.add(parseValue(val.trim(), valueType));
//                            }
//                            predicates.add(path.in(inValues));
//                            break;
//                        case NOT_AMONG:
//                            List<Object> notInValues = new ArrayList<>();
//                            for (String val : valueStr.split(",")) {
//                                notInValues.add(parseValue(val.trim(), valueType));
//                            }
//                            predicates.add(cb.not(path.in(notInValues)));
//                            break;
//                        default:
//                            throw new UnsupportedOperationException("Unknown operation: " + op);
//                    }
//                } catch (ParseException e) {
//                    throw new RuntimeException("Failed to parse value for attribute: " + attr + ", value: " + valueStr, e);
//                } catch (IllegalArgumentException e) {
//                    throw new RuntimeException("Attribute not found: " + attr, e);
//                }
//            }
//        }
//
//        cq.where(predicates.toArray(new Predicate[0]));
//        TypedQuery<T> query = em.createQuery(cq);
//
//        if (limit != null && limit > 0) {
//            query.setMaxResults(limit);
//        }
//
//        return query.getResultList();
//    }
}
