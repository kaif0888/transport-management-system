package com.tms.vehicletype.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.vehicletype.Bean.VehicleTypeBean;
import com.tms.vehicletype.entity.VehicleTypeEntity;
import com.tms.vehicletype.repository.VehicleTypeRepository;
import com.tms.vehicletype.service.VehicleTypeService;

@Service
public class VehicleTypeServiceImpl implements VehicleTypeService  {
	
	@Autowired
	private VehicleTypeRepository vehicleTypeRepo;

	@Override
	public VehicleTypeBean createVehicleType(VehicleTypeBean vehicleType) {
		VehicleTypeEntity entity = new VehicleTypeEntity();
		BeanUtils.copyProperties(vehicleType, entity);
		VehicleTypeEntity saveEntity = vehicleTypeRepo.save(entity);
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
			VehicleTypeEntity saveEntity = vehicleTypeRepo.save(entity);
			saveBean= new VehicleTypeBean();
			BeanUtils.copyProperties(saveEntity, saveBean);
		}
		return saveBean;
	}
 
	@Override
	public String deleteVehicleType(Long vehicleTypeId) {
		if(vehicleTypeId != null)
		{
			vehicleTypeRepo.deleteById(vehicleTypeId);
			return "The Vehicle Entity has been deleted";
		}
		return "VehicleType Id cannot be null";
	}

}
