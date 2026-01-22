package com.tms.dispatchTracking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import com.tms.dispatchTracking.entity.DispatchTrackingEntity;

public interface DispatchTrackingRepository extends JpaRepository<DispatchTrackingEntity, String> {
    
    List<DispatchTrackingEntity> findByTrackingIdStartingWith(String prefix);
    
//    @Modifying
//    @Transactional
//    @Query(value = "INSERT INTO tracking_sequence (date_str,next_val) VALUES (CURRENT_DATE(),1) ON DUPLICATE KEY UPDATE next_val = next_val + 1", nativeQuery = true)
//    void insertAndIncrement();
    
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tracking_sequence (date_str, next_val) VALUES (CURDATE(), 1) " +
                   "ON DUPLICATE KEY UPDATE next_val = LAST_INSERT_ID(next_val + 1)", nativeQuery = true)
    void updateAndGetNextVal();

    
    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    Long getLastInsertedId();
    
    List<DispatchTrackingEntity> findByDispatch_DispatchId(String dispatchId);
    
    // Additional method to check if tracking exists for a specific order
    boolean existsByDispatch_DispatchIdAndOrder_OrderId(String dispatchId, String orderId);
    
    // Method to find tracking by order ID
    List<DispatchTrackingEntity> findByOrder_OrderId(String orderId);
}
