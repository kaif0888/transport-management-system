package com.tms.location.service;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.location.bean.LocationBean;
import com.tms.location.dto.PostOfficeResponse;

import java.util.List;

public interface LocationService {
    LocationBean createLocation(LocationBean locationBean);
    LocationBean getLocationById(String locationId);
    List<LocationBean> getAllLocations();
    void deleteLocation(String locationId);
	LocationBean updateLocation(LocationBean locationBean);
	List<LocationBean> filterLocations(List<FilterCriteriaBean> filterCriteriaList, int maxResults);
	List<PostOfficeResponse> getPostOfficesByPincode(String pincode);
	List<PostOfficeResponse> getLocationsAddressByPincode(String pincode);
}
