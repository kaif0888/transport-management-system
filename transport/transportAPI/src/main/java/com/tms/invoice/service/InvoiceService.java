package com.tms.invoice.service;

import java.time.LocalDateTime;
import java.util.List;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.invoice.bean.InvoiceBean;
import com.tms.invoiceItem.entity.InvoiceStatus;
import com.tms.invoiceItem.entity.PaymentStatus;



public interface InvoiceService {

    InvoiceBean createInvoice(InvoiceBean invoiceBean);
    
    InvoiceBean createInvoiceFromOrder(String orderId);
    
    public InvoiceBean createInvoiceFromPayment(String paymentId);
    
    InvoiceBean updateInvoice(String invoiceId, InvoiceBean invoiceBean);
    
    InvoiceBean getInvoiceById(String invoiceId);
    
    InvoiceBean getInvoiceByNumber(String invoiceNumber);
    
    List<InvoiceBean> getAllInvoices();
    
    List<InvoiceBean> getInvoicesByCustomerId(String customerId);
    
    List<InvoiceBean> getInvoicesByOrderId(String orderId);
    
    List<InvoiceBean> getInvoicesByStatus(InvoiceStatus status);
    
    List<InvoiceBean> getInvoicesByPaymentStatus(PaymentStatus paymentStatus);
    
    List<InvoiceBean> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<InvoiceBean> getOverdueInvoices();
    
    List<InvoiceBean> searchInvoices(String searchTerm);
    
    List<InvoiceBean> getInvoicesByFilterCriteria(List<FilterCriteriaBean> filters, int limit);
    
    void deleteInvoice(String invoiceId);
    
    InvoiceBean updateInvoiceStatus(String invoiceId, InvoiceStatus status);
    
    InvoiceBean updatePaymentStatus(String invoiceId, PaymentStatus paymentStatus);
    
    InvoiceBean sendInvoice(String invoiceId);
    
    byte[] generateInvoicePdf(String invoiceId);
    
    Long getInvoiceCountByStatus(InvoiceStatus status);
    
    Double getTotalAmountByPaymentStatus(PaymentStatus paymentStatus);
}