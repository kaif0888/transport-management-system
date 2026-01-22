package com.tms.driver.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tms.document.bean.DocumentBean;
import com.tms.driver.been.AvalaibleDriverBean;
import com.tms.driver.been.DriverBean;
import com.tms.driver.been.DriverDocumentRequest;
import com.tms.driver.been.DriverDocumentResponseBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface DriverService {

	public DriverBean addDriver(DriverBean driverBean);

	public DriverBean updateDriver(String id, DriverBean driverBean);

	public String deleteDriver(String id);

	public List<DriverBean> getDrivers();

	public List<AvalaibleDriverBean> getAvailableDrivers();

	public DriverDocumentResponseBean getDriverById(String driverId);

	public List<DriverBean> getDriverbyfilterCriteria(List<FilterCriteriaBean> filters, int limit);

	public DriverBean assignVechileDriver(String driverId,String vechicleId);

	public DriverBean unAssignVechileDriver(String driverId, String vechicleId);


	public Object addDriverWithDocument(DriverBean driver, DocumentBean document);
	
	List<Map<String, Object>> getDriverExpiryList();

}
