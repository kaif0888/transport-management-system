package com.tms.driver.service;

import java.util.List;
import java.util.Optional;

import com.tms.driver.been.AvalaibleDriverBean;
import com.tms.driver.been.DriverBean;

public interface DriverService {

	public DriverBean addDriver(DriverBean driverBean);

	public DriverBean updateDriver(Long id, DriverBean driverBean);

	public String deleteDriver(Long id);

	public List<DriverBean> getDrivers();

	public List<AvalaibleDriverBean> getAvailableDrivers();

	public DriverBean getDriverById(Long driverId);

}
