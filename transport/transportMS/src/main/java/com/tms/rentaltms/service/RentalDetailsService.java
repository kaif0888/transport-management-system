package com.tms.rentaltms.service;

import java.util.List;

import java.util.List;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.rentaltms.bean.RentalDetailsBean;

public interface RentalDetailsService {
	
    RentalDetailsBean addRental (RentalDetailsBean bean);
    List<RentalDetailsBean> listRentals();
    RentalDetailsBean getRentalById(Integer rentalDetailsId);
    RentalDetailsBean updateRental(Integer id, RentalDetailsBean bean);
    String trackRentalStatus(Integer id);
    String deleteRental(Integer id);
	List<RentalDetailsBean> listOfRentalDetailByFilter(List<FilterCriteriaBean> filters, int limit);
    

    
}
