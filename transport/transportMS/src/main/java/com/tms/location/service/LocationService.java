package com.tms.location.service;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.location.bean.LocationBean;
import java.util.List;

public interface LocationService {
    LocationBean createLocation(LocationBean locationBean);
    LocationBean getLocationById(Long locationId);
    List<LocationBean> getAllLocations();
    void deleteLocation(Long locationId);
	LocationBean updateLocation(LocationBean locationBean);
	List<LocationBean> filterLocations(List<FilterCriteriaBean> filterCriteriaList, int maxResults);}
