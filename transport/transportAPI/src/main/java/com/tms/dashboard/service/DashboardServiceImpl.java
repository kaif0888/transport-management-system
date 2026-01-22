package com.tms.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.dashboard.bean.DashboardBean;
import com.tms.dispatch.entity.DispatchEntity;
import com.tms.dispatch.repository.DispatchRepository;
import com.tms.dispatchTracking.bean.DispatchTrackingBean;
import com.tms.dispatchTracking.service.DispatchTrackingService;
import com.tms.driver.entity.DriverEntity;
import com.tms.driver.repository.DriverRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.manifest.repository.ManifestRepository;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private DispatchTrackingService dispatchTrackingService;
	
	@Autowired
	private ManifestRepository manifestRepository;
	
	@Autowired
	private DispatchRepository dispatchRepository;



	// Available colors for dashboard items
	private static final String[] COLORS = { "#ad46ff", "#fb2c36", "#2b7fff", "#00c951" };

	@Override
	public List<DashboardBean> getDashboard() {
		List<DashboardBean> dashboardItems = new ArrayList<>();
		int colorIndex = 0;

		// Total vehicle count
		long totalVehicles = vehicleRepository.count();
		DashboardBean vehiclesBean = new DashboardBean();
		vehiclesBean.setLabel("All vehicles");
		vehiclesBean.setCount(String.valueOf(totalVehicles));
		vehiclesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		vehiclesBean.setIcon("FaTruck");
		vehiclesBean.setRoute("/vehicle");
		dashboardItems.add(vehiclesBean);

		// Total driver count
		long totalDrivers = driverRepository.count();
		DashboardBean driversBean = new DashboardBean();
		driversBean.setLabel("All drivers");
		driversBean.setCount(String.valueOf(totalDrivers));
		driversBean.setIcon("FaUser");
		driversBean.setRoute("/driver");
		driversBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		dashboardItems.add(driversBean);

		// Available vehicles (with status "Available")
		List<VehicleEntity> allVehicles = vehicleRepository.findAll();
		long availableVehicles = allVehicles.stream().filter(v -> "Available".equalsIgnoreCase(v.getStatus())).count();
		DashboardBean availableVehiclesBean = new DashboardBean();
		availableVehiclesBean.setLabel("Available vehicles");
		availableVehiclesBean.setCount(String.valueOf(availableVehicles));
		availableVehiclesBean.setIcon("FaTruck");
		availableVehiclesBean.setRoute("/vehicle");
		availableVehiclesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		dashboardItems.add(availableVehiclesBean);

		// Rented vehicles
		long rentedVehicles = allVehicles.stream().filter(v -> Boolean.TRUE.equals(v.getIsRented())).count();
		DashboardBean rentedVehiclesBean = new DashboardBean();
		rentedVehiclesBean.setLabel("Rented vehicles");
		rentedVehiclesBean.setCount(String.valueOf(rentedVehicles));
		rentedVehiclesBean.setIcon("FaTruck");
		rentedVehiclesBean.setRoute("/vehicle");
		rentedVehiclesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		dashboardItems.add(rentedVehiclesBean);

		// Drivers with expired licenses
		Date today = new Date();
		List<DriverEntity> allDrivers = driverRepository.findAll();
		long expiredLicenses = allDrivers.stream().filter(d -> d.getLicenseExpiry() != null).count();
		DashboardBean expiredLicensesBean = new DashboardBean();
		expiredLicensesBean.setLabel("Drivers with expired licenses");
		expiredLicensesBean.setCount(String.valueOf(expiredLicenses));
		expiredLicensesBean.setIcon("FaUser");
		expiredLicensesBean.setRoute("/driver");
		expiredLicensesBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		dashboardItems.add(expiredLicensesBean);

		// Drivers without vehicles assigned
		long driversWithoutVehicles = allDrivers.stream().filter(d -> d.getAssignedVehicle() == null).count();
		DashboardBean unassignedDriversBean = new DashboardBean();
		unassignedDriversBean.setLabel("Unassigned drivers");
		unassignedDriversBean.setCount(String.valueOf(driversWithoutVehicles));
		unassignedDriversBean.setIcon("FaUser");
		unassignedDriversBean.setRoute("/driver");
		unassignedDriversBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		dashboardItems.add(unassignedDriversBean);

//        List<FilterCriteriaBean> filters = new ArrayList<>();
//        List<DispatchTrackingBean> dispatchList = dispatchTrackingService.listOfDispatchTrackingByFilter(filters, 1000); // You can adjust the limit
//
//        long totalDelivered = dispatchList.stream()
//            .mapToLong(DispatchTrackingBean::getDeliveredOrders)
//            .sum();
//
//        long totalUndelivered = dispatchList.stream()
//            .mapToLong(DispatchTrackingBean::getPendingOrders)
//            .sum();
//        
//        // Delivered Consignments
//        DashboardBean deliveredBean = new DashboardBean();
//        deliveredBean.setLabel("Delivered consignments");
//        deliveredBean.setCount(String.valueOf(totalDelivered));
//        deliveredBean.setIcon("FaCheck");
//        deliveredBean.setRoute("/dispatch");
//        deliveredBean.setColor(COLORS[colorIndex++ % COLORS.length]);
//        dashboardItems.add(deliveredBean);
//
//        // Undelivered Consignments
//        DashboardBean undeliveredBean = new DashboardBean();
//        undeliveredBean.setLabel("Undelivered consignments");
//        undeliveredBean.setCount(String.valueOf(totalUndelivered));
//        undeliveredBean.setIcon("FaTimes");
//        undeliveredBean.setRoute("/dispatch");
//        undeliveredBean.setColor(COLORS[colorIndex++ % COLORS.length]);
//        dashboardItems.add(undeliveredBean);

		List<FilterCriteriaBean> filters = new ArrayList<>();

		// You don't need to add branch filters manually because your service method
		// does it internally
		List<DispatchTrackingBean> dispatchList = dispatchTrackingService.listOfDispatchTrackingByFilter(filters, 1000);

		long totalDelivered = dispatchList.stream().mapToLong(DispatchTrackingBean::getDeliveredOrders).sum();

		long totalUndelivered = dispatchList.stream().mapToLong(DispatchTrackingBean::getPendingOrders).sum();

		DashboardBean deliveredBean = new DashboardBean();
		deliveredBean.setLabel("Consignments Delivered");
		deliveredBean.setCount(String.valueOf(totalDelivered));
		deliveredBean.setIcon("FaCheck");
		deliveredBean.setRoute("/dispatch");
		deliveredBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		dashboardItems.add(deliveredBean);

		DashboardBean undeliveredBean = new DashboardBean();
		undeliveredBean.setLabel("Consignments Undelivered");
		undeliveredBean.setCount(String.valueOf(totalUndelivered));
		undeliveredBean.setIcon("FaTimes");
		undeliveredBean.setRoute("/dispatch");
		undeliveredBean.setColor(COLORS[colorIndex++ % COLORS.length]);
		dashboardItems.add(undeliveredBean);
		
		//for manifest cards
		List<DispatchEntity> allDispatches = dispatchRepository.findAll();

		Map<String, Long> dispatchStatusCount = allDispatches.stream()
		    .filter(d -> d.getStatus() != null)
		    .collect(Collectors.groupingBy(
		        d -> d.getStatus().toUpperCase(), // Group by status like "CREATED", "DISPATCHED", etc.
		        Collectors.counting()
		    ));

		for (Map.Entry<String, Long> entry : dispatchStatusCount.entrySet()) {
		    String status = entry.getKey();     // e.g., "CREATED", "DISPATCHED"
		    Long count = entry.getValue();      // Count of dispatches with that status

		    DashboardBean statusBean = new DashboardBean();
		    statusBean.setLabel("Shipment: " + status);  // e.g., "Dispatch: CREATED"
		    statusBean.setCount(String.valueOf(count));
		    statusBean.setIcon("FaShippingFast"); // Choose any icon
		    statusBean.setRoute("/dispatch");
		    statusBean.setColor(COLORS[colorIndex++ % COLORS.length]);

		    dashboardItems.add(statusBean);
		}


		return dashboardItems;
	}
}
