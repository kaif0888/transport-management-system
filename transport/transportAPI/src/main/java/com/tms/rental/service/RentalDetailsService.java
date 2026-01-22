package com.tms.rental.service;

import java.util.List;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.rental.bean.RentalDetailsBean;

import java.util.List;

public interface RentalDetailsService {
	
    RentalDetailsBean addRental (RentalDetailsBean bean);
    List<RentalDetailsBean> listRentals();
    RentalDetailsBean getRentalById(String rentalDetailsId);
    RentalDetailsBean updateRental(String id, RentalDetailsBean bean);
    String trackRentalStatus(String id);
    String deleteRental(String id);
	List<RentalDetailsBean> listOfRentalDetailByFilter(List<FilterCriteriaBean> filters, int limit);
    

    
}
