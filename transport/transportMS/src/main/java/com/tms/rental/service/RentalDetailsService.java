package com.tms.rental.service;

import java.util.List;

import com.tms.rental.bean.RentalDetailsBean;

import java.util.List;

public interface RentalDetailsService {
	
    RentalDetailsBean addRental (RentalDetailsBean bean);
    List<RentalDetailsBean> listRentals();
    RentalDetailsBean getRentalById(Long rentalDetailsId);
    RentalDetailsBean updateRental(Long id, RentalDetailsBean bean);
    String trackRentalStatus(Long id);
    String deleteRental(Long id);
    

    
}
