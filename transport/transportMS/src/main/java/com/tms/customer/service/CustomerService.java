package com.tms.customer.service;

import java.util.List;

import com.tms.customer.bean.CustomerBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface CustomerService {
	public CustomerBean createCustomer(CustomerBean customer);
    public CustomerBean getCustomerById(Long customerId);
	public CustomerBean updateCustomer(CustomerBean customer);
    public void deleteCustomer(Long customerId);
	public List<CustomerBean> filterCustomers(List<FilterCriteriaBean> filters, int limit);

}
