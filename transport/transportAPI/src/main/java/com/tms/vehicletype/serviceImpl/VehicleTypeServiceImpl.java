package com.tms.vehicletype.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.vehicletype.Bean.VehicleTypeBean;
import com.tms.vehicletype.entity.VehicleTypeEntity;
import com.tms.vehicletype.repository.VehicleTypeRepository;
import com.tms.vehicletype.service.VehicleTypeService;

@Service
public class VehicleTypeServiceImpl implements VehicleTypeService  {
	
	@Autowired
	private VehicleTypeRepository vehicleTypeRepo;
	
	@Autowired
	UserRepository  userRepository;

	private String generateUniqueVehicleTypeId() {
	    String prefix = "VT-";
	    String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
	    String fullPrefix = prefix + dateStr + "-";

	    List<VehicleTypeEntity> todayVehicleTypes = vehicleTypeRepo.findByVehicleTypeIdStartingWith(fullPrefix);

	    int maxSeq = todayVehicleTypes.stream()
	        .map(v -> v.getVehicleTypeId().substring(fullPrefix.length()))
	        .mapToInt(seq -> {
	            try {
	                return Integer.parseInt(seq);
	            } catch (NumberFormatException e) {
	                return 0;
	            }
	        })
	        .max()
	        .orElse(0);

	    return fullPrefix + String.format("%03d", maxSeq + 1);
	}

//	@Override
//	public VehicleTypeBean createVehicleType(VehicleTypeBean vehicleType) {
//	    VehicleTypeEntity entity = new VehicleTypeEntity();
//	    BeanUtils.copyProperties(vehicleType, entity);
//
//	    // Set the generated unique vehicleTypeId
//	    entity.setVehicleTypeId(generateUniqueVehicleTypeId());
//
//	    VehicleTypeEntity saveEntity = vehicleTypeRepo.save(entity);
//	    VehicleTypeBean saveBean = new VehicleTypeBean();
//	    BeanUtils.copyProperties(saveBean, saveEntity,"createdDate", "lastModifiedDate");
//	    saveEntity.setLastModifiedDate(LocalDate.now());
//	    saveEntity.setCreatedDate(LocalDate.now());
//	    return saveBean;
//	}
	@Override
	public VehicleTypeBean createVehicleType(VehicleTypeBean vehicleType) {
	    VehicleTypeEntity entity = new VehicleTypeEntity();
	    BeanUtils.copyProperties(vehicleType, entity);

	    // Set generated fields
	    entity.setVehicleTypeId(generateUniqueVehicleTypeId());
	    entity.setCreatedDate(LocalDateTime.now());
//	    entity.setLastModifiedDate(LocalDateTime.now());
	    
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    entity.setCreatedBy(authentication.getName());
	    
	    User currentUser = userRepository.findByEmail(authentication.getName())
	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
	    entity.setBranchIds(currentUser.getBranchIds());

	    // Save entity
	    VehicleTypeEntity saveEntity = vehicleTypeRepo.save(entity);

	    // Copy back to bean
	    VehicleTypeBean saveBean = new VehicleTypeBean();
	    BeanUtils.copyProperties(saveEntity, saveBean);
	    
	    return saveBean;
	}



	@Override
	public List<VehicleTypeBean> listVehicleType() {
		List<VehicleTypeEntity> entities = vehicleTypeRepo.findAll();
		List<VehicleTypeBean> bean = new ArrayList<>();
		for (VehicleTypeEntity entity : entities) {
			 VehicleTypeBean vehicleTypeBean = new VehicleTypeBean();
			BeanUtils.copyProperties(entity, vehicleTypeBean );
			bean.add(vehicleTypeBean);
		}
		return bean;
	}

	@Override
	public VehicleTypeBean updateVehicleType(VehicleTypeBean vehicleType) {
		Optional<VehicleTypeEntity> opt = vehicleTypeRepo.findById(vehicleType.getVehicleTypeId());
		VehicleTypeBean saveBean = null;
		if(opt.isPresent())
		{
			VehicleTypeEntity entity = opt.get();
			entity.setVehicleTypeName(vehicleType.getVehicleTypeName());
			entity.setDescription(vehicleType.getDescription());
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    entity.setLastModifiedBy(authentication.getName());
		    
			VehicleTypeEntity saveEntity = vehicleTypeRepo.save(entity);
			saveBean= new VehicleTypeBean();
			BeanUtils.copyProperties(saveEntity, saveBean);
		}
		return saveBean;
	}
 
	@Override
	public String deleteVehicleType(String vehicleTypeId) {
		if(vehicleTypeId != null)
		{
			vehicleTypeRepo.deleteById(vehicleTypeId);
			return "The Vehicle Entity has been deleted";
		}
		return "VehicleType Id cannot be null";
	}

}
