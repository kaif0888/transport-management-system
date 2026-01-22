package com.tms.invoiceItem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tms.invoiceItem.entity.InvoiceItemEntity;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItemEntity, String> {

    List<InvoiceItemEntity> findByInvoiceInvoiceId(String invoiceId);
    
    List<InvoiceItemEntity> findByProductProductId(String productId);
    
    void deleteByInvoiceInvoiceId(String invoiceId);
}
