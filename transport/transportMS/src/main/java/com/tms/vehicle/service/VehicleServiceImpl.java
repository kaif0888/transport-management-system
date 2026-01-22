package com.tms.vehicle.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.driver.repository.DriverRepository;
import com.tms.vehicle.been.VehicleAvalaibleDropdown;
import com.tms.vehicle.been.VehicleBean;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public VehicleBean createVehicle(VehicleBean bean) {
        if (bean.getRegistrationNumber() == null || bean.getRegistrationNumber().isEmpty()) {
            throw new RuntimeException("Registration number cannot be empty.");
        }

    

        Optional<VehicleEntity> existingVehicle = vehicleRepository
                .findByRegistrationNumber(bean.getRegistrationNumber());
        if (existingVehicle.isPresent()) {
            throw new RuntimeException(
                    "Vehicle with registration number " + bean.getRegistrationNumber() + " already exists.");
        }

        VehicleEntity entity = new VehicleEntity();
        BeanUtils.copyProperties(bean, entity);
        vehicleRepository.save(entity);
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    public List<VehicleBean> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(entity -> {
            VehicleBean bean = new VehicleBean();
            BeanUtils.copyProperties(entity, bean);
            return bean;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VehicleBean> getVehiclesByRegistrationNumber(String registrationNumber) {
        List<VehicleEntity> vehicles = vehicleRepository.findByRegistrationNumberStartingWith(registrationNumber);
        return vehicles.stream().map(entity -> {
            VehicleBean bean = new VehicleBean();
            BeanUtils.copyProperties(entity, bean);
            return bean;
        }).collect(Collectors.toList());
    }

    @Override
    public VehicleBean updateVehicle(Long vehicleId, VehicleBean bean) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID must not be null for update.");
        }

        VehicleEntity entity = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

        if (bean.getRegistrationNumber() != null) {
            if (bean.getRegistrationNumber().isEmpty()) {
                throw new RuntimeException("Registration number cannot be empty");
            }

            Optional<VehicleEntity> existingVehicle = vehicleRepository
                    .findByRegistrationNumber(bean.getRegistrationNumber());
            if (existingVehicle.isPresent() && !existingVehicle.get().getVehicleId().equals(vehicleId)) {
                throw new RuntimeException(
                        "Vehicle with registration number " + bean.getRegistrationNumber() + " already exists.");
            }
        }



        if (bean.getRegistrationNumber() != null) {
            entity.setRegistrationNumber(bean.getRegistrationNumber());
        }
        if (bean.getModel() != null) {
            entity.setModel(bean.getModel());
        }
        if (bean.getCapacity() != null) {
            entity.setCapacity(bean.getCapacity());
        }
        if (bean.getStatus() != null) {
            entity.setStatus(bean.getStatus());
        }

        vehicleRepository.save(entity);

        VehicleBean updatedBean = new VehicleBean();
        BeanUtils.copyProperties(entity, updatedBean);
        return updatedBean;
    }

    @Override
    public Map<String, List<VehicleBean>> getVehiclesGroupedByModel() {
        List<VehicleEntity> allVehicles = vehicleRepository.findAll();
        return allVehicles.stream().collect(Collectors.groupingBy(entity -> {
            String model = entity.getModel();
            return (model != null) ? model : "Unknown Model";
        }, Collectors.mapping(entity -> {
            VehicleBean bean = new VehicleBean();
            BeanUtils.copyProperties(entity, bean);
            return bean;
        }, Collectors.toList())));
    }

    @Override
    public VehicleBean getVehicleById(Long vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID cannot be null.");
        }

        VehicleEntity entity = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));

        VehicleBean bean = new VehicleBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    @Override
    public List<VehicleAvalaibleDropdown> getAvailableVehicles() {
        List<VehicleEntity> unassignedVehicles = vehicleRepository.findVehiclesNotAssignedToAnyDriver();

        return unassignedVehicles.stream().map(entity -> {
            VehicleAvalaibleDropdown bean = new VehicleAvalaibleDropdown();
            bean.setVehicleId(entity.getVehicleId());
            bean.setRegistrationNumber(entity.getRegistrationNumber());
            bean.setModel(entity.getModel());
            return bean;
        }).collect(Collectors.toList());
    }

    @Override
    public List<VehicleAvalaibleDropdown> getUnrentedVehicles() {
        List<VehicleEntity> unrentedVehicles = vehicleRepository.findVehiclesNotRented();

        return unrentedVehicles.stream().map(vehicle -> {
            VehicleAvalaibleDropdown bean = new VehicleAvalaibleDropdown();
            bean.setVehicleId(vehicle.getVehicleId());
            bean.setRegistrationNumber(vehicle.getRegistrationNumber());
            bean.setModel(vehicle.getModel());
            return bean;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctVehicleModels() {
        List<VehicleEntity> allVehicles = vehicleRepository.findAll();

        Set<String> distinctModels = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        allVehicles.stream().map(VehicleEntity::getModel)
                .filter(model -> model != null && !model.isEmpty())
                .forEach(distinctModels::add);

        return distinctModels.stream().collect(Collectors.toList());
    }

//    @Override
//    public List<VehicleBean> getFilteredVehicles(String registrationNumber, String model, String company) {
//        List<VehicleEntity> vehicles = vehicleRepository.findWithFilters(registrationNumber, model, company);
//
//        return vehicles.stream().map(entity -> {
//            VehicleBean bean = new VehicleBean();
//            BeanUtils.copyProperties(entity, bean);
//            return bean;
//        }).collect(Collectors.toList());
//    }
}
