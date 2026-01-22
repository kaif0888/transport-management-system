package com.tms.location.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.bean.LocationBean;
import com.tms.location.dto.PostOfficeResponse;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.location.service.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private FilterCriteriaService<LocationEntity> filterCriteriaService;
    
	@Autowired
	UserRepository  userRepository;

    private String generateLocationUniqueId() {
        String prefix = "LOC";
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fullPrefix = prefix + dateStr + "-";

        // Fetch all Location entities whose IDs start with today's prefix
        List<LocationEntity> todayLocations = locationRepository.findByLocationIdStartingWith(fullPrefix);

        // Extract sequence number from existing IDs
        int maxSeq = todayLocations.stream()
            .map(loc -> loc.getLocationId().substring(fullPrefix.length()))
            .mapToInt(seq -> {
                try {
                    return Integer.parseInt(seq);
                } catch (NumberFormatException e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);

        int nextSeq = maxSeq + 1;
        String formattedSeq = String.format("%03d", nextSeq);

        return fullPrefix + formattedSeq;
    }

    @Override
    public LocationBean createLocation(LocationBean bean) {
        try {
            LocationEntity locationEntity = new LocationEntity();   

            // Generate and set the unique location ID
            String uniqueId = generateLocationUniqueId();
            locationEntity.setLocationId(uniqueId);

            locationEntity.setLocationAddress(bean.getLocationAddress());
            locationEntity.setLocationArea(bean.getLocationArea());
            locationEntity.setCreatedBy(bean.getCreatedBy());
            locationEntity.setCreatedDate(LocalDateTime.now());
            locationEntity.setLastModifiedBy(bean.getLastModifiedBy());
            locationEntity.setLastModifiedDate(LocalDateTime.now());
            locationEntity.setPincode(bean.getPincode());
            locationEntity.setState(bean.getState());
            locationEntity.setBlock(bean.getBlock());
            locationEntity.setCircle(bean.getCircle());
            locationEntity.setStatus("Active");
            locationEntity.setCountry(bean.getCountry());
            locationEntity.setDistrict(bean.getDistrict());
            String generatedLocationName = bean.getLocationArea()+","+bean.getBlock() + "," +bean.getCircle()+","+
            		bean.getDistrict() + "( " +
            		bean.getState() + ")-" +
            		bean.getPincode();

            locationEntity.setLocationName(generatedLocationName);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            locationEntity.setCreatedBy(authentication.getName()); 
            
    	    User currentUser = userRepository.findByEmail(authentication.getName())
    	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
    	    locationEntity.setBranchIds(currentUser.getBranchIds());

            locationEntity = locationRepository.save(locationEntity);

            // Set the generated ID back into the bean
            bean.setLocationId(locationEntity.getLocationId());

            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating location: " + e.getMessage());
        }
    }


    @Override
    public LocationBean getLocationById(String locationId) {
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
    public void deleteLocation(String locationId) {
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
            locationEntity.setLocationAddress(locationBean.getLocationAddress());
            locationEntity.setLocationArea(locationBean.getLocationArea());
            locationEntity.setPincode(locationBean.getPincode());
            locationEntity.setState(locationBean.getState());
            locationEntity.setStatus(locationBean.getStatus());
            locationEntity.setCircle(locationBean.getCircle());
            locationEntity.setDistrict(locationBean.getDistrict());
            locationEntity.setBlock(locationBean.getBlock());
            locationEntity.setCountry(locationBean.getCountry());
            locationEntity.setStatus("Active");
            
            String generatedLocationName = locationBean.getLocationArea()+","+locationBean.getBlock() + "," +locationBean.getCircle()+","+
            		locationBean.getDistrict() + "( " +
            		locationBean.getState() + ")-" +
            		locationBean.getPincode();
      

            locationEntity.setLocationName(generatedLocationName);

            
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            locationEntity.setCreatedBy(authentication.getName()); 
            
        	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        	locationEntity.setLastModifiedBy(authentication.getName());

            locationEntity = locationRepository.save(locationEntity);
            return convertToBean(locationEntity);

        } catch (Exception e) {
            throw new RuntimeException("Error updating location: " + e.getMessage());
        }
    }

    private LocationBean convertToBean(LocationEntity locationEntity) {
        LocationBean locationBean = new LocationBean();

        locationBean.setLocationId(safe(locationEntity.getLocationId()));
        locationBean.setLocationName(safe(locationEntity.getLocationName()));
        locationBean.setLocationArea(safe(locationEntity.getLocationArea()));
        locationBean.setLocationAddress(safe(locationEntity.getLocationAddress()));
        locationBean.setPincode(safe(locationEntity.getPincode()));
        locationBean.setState(safe(locationEntity.getState()));
        locationBean.setStatus(safe(locationEntity.getStatus()));
        locationBean.setCircle(safe(locationEntity.getCircle()));
        locationBean.setDistrict(safe(locationEntity.getDistrict()));
        locationBean.setBlock(safe(locationEntity.getBlock()));
        locationBean.setCountry(safe(locationEntity.getCountry()));
        locationBean.setLastModifiedBy(locationEntity.getLastModifiedBy());
        locationBean.setLastModifiedDate(locationEntity.getLastModifiedDate());

        return locationBean;
    }

    // Utility method
    private String safe(String value) {
        return value != null ? value : "";
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

    @Override
    public List<PostOfficeResponse> getPostOfficesByPincode(String pincode) {
        List<PostOfficeResponse> resultList = new ArrayList<>();
        try {
           // String url = "https://api.postalpincode.in/pincode/" + pincode;
            String url = "https://dev.apiman.in/pincode/" + pincode;
            RestTemplate restTemplate = new RestTemplate();
            String json = restTemplate.getForObject(url, String.class);
            System.out.println(json);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode postOffices = root.get("Postoffice");

            if (postOffices != null && postOffices.isArray()) {
                for (JsonNode office : postOffices) {
                    PostOfficeResponse res = new PostOfficeResponse();
                    res.setLocationArea(office.get("officename").asText());
                    res.setLocationAddress(office.get("description").isNull() ? null : office.get("description").asText());
                    res.setStatus(office.get("Deliverystatus").asText());
                    res.setCircle(office.get("circlename").asText());
                    res.setDistrict(office.get("Districtname").asText());
                    res.setBlock(office.get("Taluk").asText());
                    res.setState(office.get("statename").asText());
                    res.setCountry(office.get("country").asText());
                    res.setPincode(office.get("pincode").asText());

                    resultList.add(res);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
    
    @Override
    public List<PostOfficeResponse> getLocationsAddressByPincode(String pincode) {

        List<LocationEntity> locationList =
                locationRepository.findByPincodeAndStatus(pincode, "Active");

        if (locationList.isEmpty()) {
            throw new RuntimeException("Service not available for this pincode " + pincode);
        }

        return locationList.stream().map(location -> {
            PostOfficeResponse response = new PostOfficeResponse();
            response.setLocationArea(location.getLocationArea());
            response.setLocationAddress(location.getLocationAddress());
            response.setPincode(location.getPincode());
            response.setState(location.getState());
            response.setStatus(location.getStatus());
            response.setCircle(location.getCircle());
            response.setDistrict(location.getDistrict());
            response.setBlock(location.getBlock());
            response.setCountry(location.getCountry());
            return response;
        }).collect(Collectors.toList());
    }




   
}
