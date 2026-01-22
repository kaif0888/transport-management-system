package com.tms.branch.service.impl;

import com.tms.branch.bean.BranchBean;
import com.tms.branch.bean.BranchLocationResponse;
import com.tms.branch.entity.BranchEntity;
import com.tms.branch.repository.BranchRepository;
import com.tms.branch.service.BranchService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.location.bean.LocationBean;
import com.tms.location.entity.LocationEntity;
import com.tms.location.repository.LocationRepository;
import com.tms.location.service.LocationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private FilterCriteriaService<BranchEntity> filterCriteriaService;
    

    @Autowired
    private LocationService locationService;


    @Override
    public BranchBean createBranch(BranchBean bean) {
        LocationEntity location = locationRepository.findById(bean.getLocationId())
            .orElseThrow(() -> new RuntimeException("Location not found"));  

        BranchEntity branchEntity = new BranchEntity();
        branchEntity.setBranchName(bean.getBranchName());
        branchEntity.setContactInfo(bean.getContactInfo());
        branchEntity.setLocation(location);

        branchEntity = branchRepository.save(branchEntity);
        bean.setBranchId(branchEntity.getBranchId());
        return bean;
    }

    @Override
    public BranchBean getBranchById(Long branchId) {
        BranchEntity branchEntity = branchRepository.findById(branchId)
            .orElseThrow(() -> new RuntimeException("Branch not found"));
        return convertToBean(branchEntity);
    }

    @Override
    public List<BranchBean> getBranchesByLocationId(Long locationId) {
        return branchRepository.findByLocation_LocationId(locationId)
            .stream().map(this::convertToBean).collect(Collectors.toList());
    }

    @Override
    public List<BranchBean> getAllBranches() {
        List<BranchEntity> branchEntity = branchRepository.findAll();
        return branchEntity.stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    @Override
    public BranchBean updateBranch(BranchBean branchBean) {
        BranchEntity branchEntity = branchRepository.findById(branchBean.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        branchEntity.setBranchName(branchBean.getBranchName());
        branchEntity.setContactInfo(branchBean.getContactInfo());

        if (branchBean.getLocationId() != null &&
            !branchBean.getLocationId().equals(branchEntity.getLocation().getLocationId())) {

            LocationEntity location = locationRepository.findById(branchBean.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            branchEntity.setLocation(location);
        }

        BranchEntity updatedEntity = branchRepository.save(branchEntity);
        return convertToBean(updatedEntity);
    }

    @Override
    public void deleteBranch(Long branchId) {
        branchRepository.deleteById(branchId);
    }

    private BranchBean convertToBean(BranchEntity branchEntity) {
        BranchBean branchBean = new BranchBean();
        branchBean.setBranchId(branchEntity.getBranchId());
        branchBean.setBranchName(branchEntity.getBranchName());
        branchBean.setContactInfo(branchEntity.getContactInfo());

        if (branchEntity.getLocation() != null) {
        	branchBean.setLocationId(branchEntity.getLocation().getLocationId());
        }

        return branchBean;
    }

	@Override
	public List<BranchBean> filterBranchs(List<FilterCriteriaBean> filterCriteriaList, int maxResults) {
		// TODO Auto-generated method stub
	    try {
	        List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(BranchEntity.class, filterCriteriaList, maxResults);
	        return filteredEntities.stream()
	                .map(entity -> convertToBean((BranchEntity) entity))
	                .collect(Collectors.toList());
	    } catch (Exception e) {
	        throw new RuntimeException("Error filtering branches: " + e.getMessage(), e);
	    }
	}
	
	public BranchLocationResponse getAllBranchesAndLocations() {
	    BranchLocationResponse response = new BranchLocationResponse();
	    
	    List<BranchBean> branchBeans = getAllBranches(); // already implemented
	    List<LocationBean> locationBeans = locationRepository.findAll().stream().map(loc -> {
	        LocationBean bean = new LocationBean();
	        bean.setLocationId(loc.getLocationId());
	        bean.setLocationName(loc.getLocationName());
	        bean.setLocationAddress(loc.getLocationAddress());
	       
	        return bean;
	    }).collect(Collectors.toList());

	    response.setBranches(branchBeans);
	    response.setLocations(locationBeans);

	    return response;
	}
}
