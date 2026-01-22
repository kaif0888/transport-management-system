package com.tms.drivertms.service;

import java.util.List;
import java.util.Optional;

import com.tms.drivertms.been.AvalaibleDriverBean;
import com.tms.drivertms.been.DriverBeen;
import com.tms.drivertms.entity.DriverEntity;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface DriverService {

	public DriverBeen addDriver(DriverBeen driverBean);

	public DriverBeen updateDriver(Integer id, DriverBeen driverBean);

	public String deleteDriver(Integer id);

//	public List<DriverBeen> getDrivers();

	public List<AvalaibleDriverBean> getAvailableDrivers();

	public DriverBeen getDriverById(Integer driverId);

	public List<DriverBeen> getDriverbyfilterCriteria(List<FilterCriteriaBean> filters, int limit);

}
