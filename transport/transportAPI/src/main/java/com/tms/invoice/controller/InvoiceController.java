package com.tms.invoice.controller;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.invoice.bean.InvoiceBean;

import com.tms.invoice.service.InvoiceService;
import com.tms.invoiceItem.entity.InvoiceStatus;
import com.tms.invoiceItem.entity.PaymentStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value="/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceBean> createInvoice(@RequestBody InvoiceBean invoiceBean) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoiceBean));
    }

    @PostMapping("/from-order/{orderId}")
    public ResponseEntity<InvoiceBean> createFromOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(invoiceService.createInvoiceFromOrder(orderId));
    }
    
    @PostMapping("/generate/{paymentId}")
    public ResponseEntity<InvoiceBean> generateInvoiceFromPayment(@PathVariable String paymentId) {
        try {
            InvoiceBean invoice = invoiceService.createInvoiceFromPayment(paymentId);
            return ResponseEntity.ok(invoice);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(null); // Or use custom error response
        }
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceBean> updateInvoice(@PathVariable String invoiceId, @RequestBody InvoiceBean invoiceBean) {
        return ResponseEntity.ok(invoiceService.updateInvoice(invoiceId, invoiceBean));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceBean> getInvoiceById(@PathVariable String invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceBean> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceBean>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceBean>> getByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomerId(customerId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<InvoiceBean>> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByOrderId(orderId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceBean>> getByStatus(@PathVariable InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<InvoiceBean>> getByPaymentStatus(@PathVariable PaymentStatus paymentStatus) {
        return ResponseEntity.ok(invoiceService.getInvoicesByPaymentStatus(paymentStatus));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<InvoiceBean>> getByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(invoiceService.getInvoicesByDateRange(startDate, endDate));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceBean>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @GetMapping("/search")
    public ResponseEntity<List<InvoiceBean>> searchInvoices(@RequestParam String query) {
        return ResponseEntity.ok(invoiceService.searchInvoices(query));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<InvoiceBean>> getByFilterCriteria(
            @RequestBody List<FilterCriteriaBean> filters,
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(invoiceService.getInvoicesByFilterCriteria(filters, limit));
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{invoiceId}/status")
    public ResponseEntity<InvoiceBean> updateInvoiceStatus(
            @PathVariable String invoiceId,
            @RequestParam InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(invoiceId, status));
    }

    @PutMapping("/{invoiceId}/payment-status")
    public ResponseEntity<InvoiceBean> updatePaymentStatus(
            @PathVariable String invoiceId,
            @RequestParam PaymentStatus paymentStatus) {
        return ResponseEntity.ok(invoiceService.updatePaymentStatus(invoiceId, paymentStatus));
    }

    @PostMapping("/{invoiceId}/send")
    public ResponseEntity<InvoiceBean> sendInvoice(@PathVariable String invoiceId) {
        return ResponseEntity.ok(invoiceService.sendInvoice(invoiceId));
    }

    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable String invoiceId) {
        byte[] pdf = invoiceService.generateInvoicePdf(invoiceId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + invoiceId + ".pdf")
                .body(pdf);
    }

    @GetMapping("/count/status")
    public ResponseEntity<Long> getInvoiceCountByStatus(@RequestParam InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoiceCountByStatus(status));
    }

    @GetMapping("/total/payment-status")
    public ResponseEntity<Double> getTotalAmountByPaymentStatus(@RequestParam PaymentStatus paymentStatus) {
        return ResponseEntity.ok(invoiceService.getTotalAmountByPaymentStatus(paymentStatus));
    }
}
