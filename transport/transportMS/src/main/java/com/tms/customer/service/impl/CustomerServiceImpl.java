package com.tms.customer.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.customer.bean.CustomerBean;
import com.tms.customer.entity.CustomerEntity;
import com.tms.customer.repository.CustomerRepository;
import com.tms.customer.service.CustomerService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FilterCriteriaService<CustomerEntity> filterCriteriaService;

    @Override
    public CustomerBean createCustomer(CustomerBean customerBean) {
        CustomerEntity customerEntity = new CustomerEntity();
        BeanUtils.copyProperties(customerBean, customerEntity);
        customerRepository.save(customerEntity);
        BeanUtils.copyProperties(customerEntity, customerBean);
        return customerBean;
    }

    @Override
    public CustomerBean getCustomerById(Long customerId) {
        Optional<CustomerEntity> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            return convertToBean(optionalCustomer.get());
        } else {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
    }

    @Override
    public CustomerBean updateCustomer(CustomerBean customerBean) {
        if (customerBean.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null for update");
        }

        Optional<CustomerEntity> existingCustomer = customerRepository.findById(customerBean.getCustomerId());
        if (existingCustomer.isPresent()) {
            CustomerEntity customerEntity = convertToEntity(customerBean);
            CustomerEntity updatedEntity = customerRepository.save(customerEntity);
            return convertToBean(updatedEntity);
        } else {
            throw new RuntimeException("Customer not found with ID: " + customerBean.getCustomerId());
        }
    }

    @Override
    public void deleteCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<CustomerBean> filterCustomers(List<FilterCriteriaBean> filters, int limit) {
        try {
            @SuppressWarnings("unchecked")
            List<CustomerEntity> filteredEntities = (List<CustomerEntity>) filterCriteriaService
                .getListOfFilteredData(CustomerEntity.class, filters, limit);

            return filteredEntities.stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error filtering customers: " + e.getMessage(), e);
        }
    }

    private CustomerEntity convertToEntity(CustomerBean customerBean) {
        CustomerEntity entity = new CustomerEntity();
        BeanUtils.copyProperties(customerBean, entity);
        return entity;
    }

    private CustomerBean convertToBean(CustomerEntity customerEntity) {
        CustomerBean bean = new CustomerBean();
        BeanUtils.copyProperties(customerEntity, bean);
        return bean;
    }
}
