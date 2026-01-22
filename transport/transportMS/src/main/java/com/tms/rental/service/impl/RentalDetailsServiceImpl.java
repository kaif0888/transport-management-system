package com.tms.rental.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.rental.bean.RentalDetailsBean;
import com.tms.rental.entity.RentalDetailsEntity;
import com.tms.rental.repository.RentalDetailsRepository;
import com.tms.rental.service.RentalDetailsService;
import com.tms.vehicle.repository.VehicleRepository;

@Service
public class RentalDetailsServiceImpl implements RentalDetailsService {

    @Autowired
    private RentalDetailsRepository rentalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    // Add new rental to system
    @Override
    public RentalDetailsBean addRental(RentalDetailsBean bean) {
        RentalDetailsEntity entity = new RentalDetailsEntity();
        BeanUtils.copyProperties(bean, entity);

        if (bean.getVehicleId() != null) {
            vehicleRepository.findById(bean.getVehicleId());
                   
        }

        rentalRepository.save(entity);
        BeanUtils.copyProperties(entity, bean);
        if (entity.getVehicle() != null) {
            bean.setVehicleId(entity.getVehicle().getVehicleId());
        }
        return bean;
    }

    // Get list of all rentals
    @Override
    public List<RentalDetailsBean> listRentals() {
        return rentalRepository.findAll().stream().map(entity -> {
            RentalDetailsBean bean = new RentalDetailsBean();
            BeanUtils.copyProperties(entity, bean);
            if (entity.getVehicle() != null) {
                bean.setVehicleId(entity.getVehicle().getVehicleId());
            }
            return bean;
        }).collect(Collectors.toList());
    }

    // Update rental record details
    @Override
    public RentalDetailsBean updateRental(Long id, RentalDetailsBean bean) {
        RentalDetailsEntity entity = rentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental not found with ID: " + id));

        entity.setProviderName(bean.getProviderName());
        entity.setRentalStartDate(bean.getRentalStartDate());
        entity.setRentalEndDate(bean.getRentalEndDate());
        entity.setRentalCost(bean.getRentalCost());

        rentalRepository.save(entity);
        BeanUtils.copyProperties(entity, bean);
        if (entity.getVehicle() != null) {
            bean.setVehicleId(entity.getVehicle().getVehicleId());
        }
        return bean;
    }

    // Track the status of a rental
    @Override
    public String trackRentalStatus(Long id) {
        return rentalRepository.existsById(id)
                ? "Rental active or record found"
                : "Rental record not found";
    }

    // Delete rental by ID
    @Override
    public String deleteRental(Long id) {
        if (rentalRepository.existsById(id)) {
            rentalRepository.deleteById(id);
            return "Rental deleted successfully";
        } else {
            return "Rental not found";
        }
    }

    // Get rental by ID
    @Override
    public RentalDetailsBean getRentalById(Long rentalDetailsId) {
        RentalDetailsEntity entity = rentalRepository.findById(rentalDetailsId)
                .orElseThrow(() -> new RuntimeException("Rental not found with ID: " + rentalDetailsId));

        RentalDetailsBean bean = new RentalDetailsBean();
        BeanUtils.copyProperties(entity, bean);

        if (entity.getVehicle() != null) {
            bean.setVehicleId(entity.getVehicle().getVehicleId());
        }

        return bean;
    }
}
