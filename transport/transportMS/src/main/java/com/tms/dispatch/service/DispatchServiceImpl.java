package com.tms.dispatch.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.dispatch.bean.DispatchBean;
import com.tms.dispatch.entity.DispatchEntity;
import com.tms.dispatch.repository.DispatchRepository;
import com.tms.driver.entity.DriverEntity;
import com.tms.driver.repository.DriverRepository;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DispatchServiceImpl implements DispatchService {

	@Autowired
	private DispatchRepository dispatchRepository;
	@Autowired
	private DriverRepository driverRepository;
	@Autowired
	private VehicleRepository vehicleRepository;

	@Override
	public DispatchBean createDispatch(DispatchBean dispatchBean) {

		DriverEntity driver = driverRepository.findById(dispatchBean.getDriverId())
				.orElseThrow(() -> new RuntimeException("Driver not found"));
		VehicleEntity vehicle = vehicleRepository.findById(dispatchBean.getVehicleId())
				.orElseThrow(() -> new RuntimeException("Vehicle not found"));

		DispatchEntity dispatch = new DispatchEntity();
		BeanUtils.copyProperties(dispatchBean, dispatch);

		dispatch.setDriver(driver);
		dispatch.setVehicle(vehicle);
		DispatchEntity savedDispatch = dispatchRepository.save(dispatch);
		DispatchBean responseBean = new DispatchBean();
		BeanUtils.copyProperties(dispatch, responseBean);
		return responseBean;
	}

	@Override
	public List<DispatchBean> getAllDispatches() {
		List<DispatchEntity> dispatchList = dispatchRepository.findAll();
		return dispatchList.stream().map(dispatch -> {
			DispatchBean bean = new DispatchBean();
			BeanUtils.copyProperties(dispatch, bean);
			return bean;
		}).collect(Collectors.toList());
	}

	@Override
	public DispatchBean updateDispatchById(Long dispatchId, DispatchBean dispatchBean) {
		DispatchEntity dispatch = dispatchRepository.findById(dispatchId).orElse(null);

		DriverEntity driver = driverRepository.findById(dispatchBean.getDriverId())
				.orElseThrow(() -> new RuntimeException("Driver not found"));

		VehicleEntity vehicle = vehicleRepository.findById(dispatchBean.getVehicleId())
				.orElseThrow(() -> new RuntimeException("Vehicle not found"));

		if (dispatch != null) {
			BeanUtils.copyProperties(dispatchBean, dispatch, "dispatchId");
			DispatchEntity updatedDispatch = dispatchRepository.save(dispatch);
			DispatchBean responseBean = new DispatchBean();
			BeanUtils.copyProperties(updatedDispatch, responseBean);
			return responseBean;
		}
		return null;
	}

}
