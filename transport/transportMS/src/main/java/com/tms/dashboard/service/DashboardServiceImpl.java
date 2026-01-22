package com.tms.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.dashboard.bean.*;
import com.tms.driver.entity.DriverEntity;
import com.tms.driver.repository.DriverRepository;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    // Available colors for dashboard items
    private static final String[] COLORS = {
        "#ad46ff",
        "#fb2c36",
        "#2b7fff",
        "#00c951"
    };

    @Override
    public List<DashboardBean> getDashboard() {
        List<DashboardBean> dashboardItems = new ArrayList<>();
        int colorIndex = 0;

        // Total vehicle count
        long totalVehicles = vehicleRepository.count();
        DashboardBean vehiclesBean = new DashboardBean();
        vehiclesBean.setLabel("All vehicles");
        vehiclesBean.setCount(totalVehicles);
        vehiclesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
        vehiclesBean.setIcon("FaTruck");
        vehiclesBean.setRoute("/vehicle");
        dashboardItems.add(vehiclesBean);

        // Total driver count
        long totalDrivers = driverRepository.count();
        DashboardBean driversBean = new DashboardBean();
        driversBean.setLabel("All drivers");
        driversBean.setCount(totalDrivers);
        driversBean.setIcon("FaUser");
        driversBean.setRoute("/driver");
        driversBean.setColor(COLORS[colorIndex++ % COLORS.length]);
        dashboardItems.add(driversBean);

        // Available vehicles (with status "Available")
        List<VehicleEntity> allVehicles = vehicleRepository.findAll();
        long availableVehicles = allVehicles.stream()
            .filter(v -> "Available".equalsIgnoreCase(v.getStatus()))
            .count();
        DashboardBean availableVehiclesBean = new DashboardBean();
        availableVehiclesBean.setLabel("Available vehicles");
        availableVehiclesBean.setCount(availableVehicles);
        availableVehiclesBean.setIcon("FaTruck");
        availableVehiclesBean.setRoute("/vehicle");
        availableVehiclesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
        dashboardItems.add(availableVehiclesBean);

        // Rented vehicles
        long rentedVehicles = allVehicles.stream()
            .filter(v -> v.getIsRented() != null && v.getIsRented())
            .count();
        DashboardBean rentedVehiclesBean = new DashboardBean();
        rentedVehiclesBean.setLabel("Rented vehicles");
        rentedVehiclesBean.setCount(rentedVehicles);
        rentedVehiclesBean.setIcon("FaTruck");
        rentedVehiclesBean.setRoute("/vehicle");
        rentedVehiclesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
        dashboardItems.add(rentedVehiclesBean);

        // Drivers with expired licenses
        Date today = new Date();
        List<DriverEntity> allDrivers = driverRepository.findAll();
//        long expiredLicenses = allDrivers.stream()
//            .filter(d -> d.getLicenseExpiry() != null && d.getLicenseExpiry().isBefore(today))
//            .count();
        DashboardBean expiredLicensesBean = new DashboardBean();
        expiredLicensesBean.setLabel("Drivers with expired licenses");
//        expiredLicensesBean.setCount(expiredLicenses);
        expiredLicensesBean.setIcon("FaUser");
        expiredLicensesBean.setRoute("/driver");
        expiredLicensesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
        dashboardItems.add(expiredLicensesBean);

        // Drivers without vehicles assigned
        long driversWithoutVehicles = allDrivers.stream()
            .filter(d -> d.getAssignedVehicle() == null)
            .count();
        DashboardBean unassignedDriversBean = new DashboardBean();
        unassignedDriversBean.setLabel("Unassigned drivers");
        unassignedDriversBean.setIcon("FaUser");
        unassignedDriversBean.setRoute("/driver");
        unassignedDriversBean.setCount(driversWithoutVehicles);
        unassignedDriversBean.setColor(COLORS[colorIndex++ % COLORS.length]);
        dashboardItems.add(unassignedDriversBean);

        return dashboardItems;
    }
}