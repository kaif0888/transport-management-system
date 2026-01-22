package com.tms.location.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tms.location.entity.LocationEntity;

public interface LocationRepository extends JpaRepository<LocationEntity, String> {
	List<LocationEntity> findByLocationIdStartingWith(String prefix);

	boolean existsByLocationName(String locationName);

	LocationEntity findByLocationName(String string);

	LocationEntity getByLocationId(String string);
	
    List<LocationEntity> findByPincodeAndStatus(String pincode, String status);
    
    List<LocationEntity> findByPincodeAndLocationAreaAndDistrictAndState(
            String pincode, 
            String locationArea, 
            String district, 
            String state
        );


}
