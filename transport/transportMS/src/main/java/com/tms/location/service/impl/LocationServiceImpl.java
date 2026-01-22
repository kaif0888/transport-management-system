package com.tms.location.service.impl;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.bean.LocationBean;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.location.service.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private FilterCriteriaService<LocationEntity> filterCriteriaService;

    @Override
    public LocationBean createLocation(LocationBean bean) {
        try {
            LocationEntity locationEntity = new LocationEntity();   
            locationEntity.setLocationName(bean.getLocationName());
            locationEntity.setLocationAddress(bean.getLocationAddress());

            locationEntity = locationRepository.save(locationEntity);
            bean.setLocationId(locationEntity.getLocationId());
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating location: " + e.getMessage());
        }
    }

    @Override
    public LocationBean getLocationById(Long locationId) {
        try {
            LocationEntity locationEntity = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found with ID: " + locationId));
            return convertToBean(locationEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving location: " + e.getMessage());
        }
    }

    @Override
    public List<LocationBean> getAllLocations() {
        try {
            return locationRepository.findAll()
                    .stream()
                    .map(this::convertToBean)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all locations: " + e.getMessage());
        }
    }

    @Override
    public void deleteLocation(Long locationId) {
        try {
            locationRepository.deleteById(locationId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting location: " + e.getMessage());
        }
    }

    @Override
    public LocationBean updateLocation(LocationBean locationBean) {
        try {
            Optional<LocationEntity> optionalEntity = locationRepository.findById(locationBean.getLocationId());

            if (!optionalEntity.isPresent()) {
                throw new RuntimeException("Location not found with ID: " + locationBean.getLocationId());
            }

            LocationEntity locationEntity = optionalEntity.get();
            locationEntity.setLocationName(locationBean.getLocationName());
            locationEntity.setLocationAddress(locationBean.getLocationAddress());

            locationEntity = locationRepository.save(locationEntity);
            return convertToBean(locationEntity);

        } catch (Exception e) {
            throw new RuntimeException("Error updating location: " + e.getMessage());
        }
    }

    private LocationBean convertToBean(LocationEntity locationEntity) {
        LocationBean locationBean = new LocationBean();   
        locationBean.setLocationId(locationEntity.getLocationId());
        locationBean.setLocationName(locationEntity.getLocationName());
        locationBean.setLocationAddress(locationEntity.getLocationAddress());
        return locationBean;
    }

	@Override
	public List<LocationBean> filterLocations(List<FilterCriteriaBean> filterCriteriaList, int maxResults) {
	    try {
	        List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(LocationEntity.class, filterCriteriaList, maxResults);
	        return (List<LocationBean>) filteredEntities.stream()
	                .map(entity -> convertToBean((LocationEntity) entity))
	                .collect(Collectors.toList());
	    } catch (Exception e) {
	        throw new RuntimeException("Error filtering locations: " + e.getMessage());
	    }
}
}
