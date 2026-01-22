package com.tms.invoice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tms.invoice.entity.InvoiceEntity;
import com.tms.invoiceItem.entity.InvoiceStatus;
import com.tms.invoiceItem.entity.PaymentStatus;




@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {

    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);
    
    List<InvoiceEntity> findByCustomerCustomerId(String customerId);
    
    List<InvoiceEntity> findByOrderOrderId(String orderId);
    
    List<InvoiceEntity> findByPaymentPaymentId(String paymentId);
    
    List<InvoiceEntity> findByStatus(InvoiceStatus status);
    
    List<InvoiceEntity> findByPaymentStatus(PaymentStatus paymentStatus);
    
    List<InvoiceEntity> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<InvoiceEntity> findByDueDateBeforeAndPaymentStatusNot(LocalDateTime dueDate, PaymentStatus paymentStatus);
    
    @Query("SELECT i FROM InvoiceEntity i WHERE i.invoiceNumber LIKE %:searchTerm% " +
           "OR i.customer.customerName LIKE %:searchTerm% " +
           "OR i.order.orderId LIKE %:searchTerm%")
    List<InvoiceEntity> searchInvoices(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(i) FROM InvoiceEntity i WHERE i.status = :status")
    Long countByStatus(@Param("status") InvoiceStatus status);
    
    @Query("SELECT SUM(i.totalAmount) FROM InvoiceEntity i WHERE i.paymentStatus = :paymentStatus")
    Double getTotalAmountByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);
    
    List<InvoiceEntity> findByInvoiceNumberStartingWith(String prefix);
}