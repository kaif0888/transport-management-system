// Invoice Service Implementation - Continued
package com.tms.invoice.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.customer.entity.CustomerEntity;
import com.tms.customer.repository.CustomerRepository;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.generic.entity.GenericEntity;
import com.tms.invoice.bean.InvoiceBean;

import com.tms.invoice.entity.InvoiceEntity;
import com.tms.invoice.generator.InvoicePdfGenerator;
import com.tms.invoice.repository.InvoiceRepository;
import com.tms.invoice.service.InvoiceService;
import com.tms.invoiceItem.bean.InvoiceItemBean;
import com.tms.invoiceItem.entity.InvoiceItemEntity;
import com.tms.invoiceItem.entity.InvoiceStatus;
import com.tms.invoiceItem.entity.PaymentStatus;
import com.tms.invoiceItem.repository.InvoiceItemRepository;
import com.tms.mail.service.MailService;
import com.tms.order.entity.OrderEntity;
import com.tms.order.repository.OrderRepository;
import com.tms.orderproduct.entity.OrderProductEntity;
import com.tms.orderproduct.repository.OrderProductRepository;
import com.tms.payment.entity.PaymentEntity;
import com.tms.payment.repository.PaymentRepository;
import com.tms.product.repository.ProductRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;



@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private static final String INVOICE_ID_PREFIX = "INV";
    private static final String INVOICE_NUMBER_PREFIX = "INV-";
    private static final BigDecimal DEFAULT_TAX_PERCENTAGE = new BigDecimal("18.00"); // 18% GST
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private InvoiceItemRepository invoiceItemRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private InvoicePdfGenerator pdfGenerator;
    
    @Autowired
    private OrderProductRepository orderProductRepository;
    
    @Autowired
    private ProductRepository productRepository; 
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

 
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MailService mailService;
    
    @Autowired
    private FilterCriteriaService<InvoiceEntity> filterCriteriaService;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);

//    @Override
//    public InvoiceBean createInvoice(InvoiceBean invoiceBean) {
//        validateInvoiceBean(invoiceBean);
//        
//        InvoiceEntity invoice = convertBeanToEntity(invoiceBean);
//        invoice.setInvoiceId(generateUniqueInvoiceId());
//        invoice.setInvoiceNumber(generateUniqueInvoiceNumber());
//        invoice.setInvoiceDate(LocalDateTime.now());
//        
//        setUserInfoFromAuthentication(invoice);
//        calculateInvoiceTotals(invoice);
//        
//        invoice = invoiceRepository.save(invoice);
//        
//        // Save invoice items
//        if (invoiceBean.getInvoiceItems() != null && !invoiceBean.getInvoiceItems().isEmpty()) {
//            saveInvoiceItems(invoice, invoiceBean.getInvoiceItems());
//        }
//        
//        return convertEntityToBean(invoice);
//    }



    @Value("${invoice.storage.path}")
    private String invoiceStoragePath;

    @Override
    public InvoiceBean createInvoice(InvoiceBean invoiceBean) {
        validateInvoiceBean(invoiceBean);

        InvoiceEntity invoice = convertBeanToEntity(invoiceBean);
        invoice.setInvoiceId(generateUniqueInvoiceId());
        invoice.setInvoiceNumber(generateUniqueInvoiceNumber());
        invoice.setInvoiceDate(LocalDateTime.now());
        
        // Set default values if not provided
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(LocalDateTime.now().plusDays(30)); // Default 30 days
        }
        
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.DRAFT);
        }
        
        if (invoice.getPaymentStatus() == null) {
            invoice.setPaymentStatus(PaymentStatus.UNPAID);
        }
        
        if (invoice.getTaxPercentage() == null) {
            invoice.setTaxPercentage(DEFAULT_TAX_PERCENTAGE);
        }

        setCustomerAndOrderReferences(invoice, invoiceBean);
        setUserInfoFromAuthentication(invoice);
        
        // Calculate subtotal from invoice items first
        if (invoiceBean.getInvoiceItems() != null && !invoiceBean.getInvoiceItems().isEmpty()) {
            BigDecimal subtotal = invoiceBean.getInvoiceItems().stream()
                .map(item -> item.getQuantity().multiply(item.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            invoice.setSubtotal(subtotal);
        }
        
        calculateInvoiceTotals(invoice);

        invoice = invoiceRepository.save(invoice);

        // Save invoice items
        if (invoiceBean.getInvoiceItems() != null && !invoiceBean.getInvoiceItems().isEmpty()) {
            saveInvoiceItems(invoice, invoiceBean.getInvoiceItems());
        }

        // Generate and store PDF
        generateAndSavePdfFile(invoice);

        return convertEntityToBean(invoice);
    }

    private void generateAndSavePdfFile(InvoiceEntity invoice) {
        try {
            InvoiceBean bean = convertEntityToBean(invoice);
            String dateFolder = LocalDate.now().toString();
            String dirPath = invoiceStoragePath + File.separator + dateFolder;

            logger.info("PDF directory: " + dirPath);
            File dir = new File(dirPath);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Cannot create directory: " + dirPath);
            }

            String filename = "Invoice_" + invoice.getInvoiceNumber() + "_" + System.currentTimeMillis() + ".pdf";
            String fullPath = dirPath + File.separator + filename;
            logger.info("Writing PDF to: " + fullPath);

            try (OutputStream out = new FileOutputStream(fullPath)) {
                pdfGenerator.generate(bean, out);
            }
            invoice.setPdfPath(fullPath);
            invoiceRepository.save(invoice);
            logger.info("PDF saved successfully and path stored.");

        } catch (Exception e) {
            logger.error("Error saving PDF file", e);
            throw new RuntimeException("Failed to save PDF to disk", e);
        }
    }
    @Override
    public InvoiceBean createInvoiceFromPayment(String paymentId) {
        // Fetch payment entity with all required relationships
        PaymentEntity payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));
        
        // Create invoice bean from payment data
        InvoiceBean invoiceBean = createInvoiceBeanFromPayment(payment);
        
        // Validate the constructed invoice bean
        validateInvoiceBean(invoiceBean);
        
        // Convert to entity and set basic invoice info
        InvoiceEntity invoice = convertBeanToEntity(invoiceBean);
        invoice.setInvoiceId(generateUniqueInvoiceId());
        invoice.setInvoiceNumber(generateUniqueInvoiceNumber());
        invoice.setInvoiceDate(LocalDateTime.now());
        
        // Set default values
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(LocalDateTime.now().plusDays(30));
        }
        
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.PAID); // Since it's from payment, mark as paid
        }
        
        if (invoice.getPaymentStatus() == null) {
            invoice.setPaymentStatus(PaymentStatus.PAID);
        }
        
        if (invoice.getTaxPercentage() == null) {
            invoice.setTaxPercentage(DEFAULT_TAX_PERCENTAGE);
        }
        
        // Set customer and order references
        if (payment.getCustomerEntity() != null) {
            invoice.setCustomer(payment.getCustomerEntity());
        }
        
        if (payment.getOrder() != null) {
            invoice.setOrder(payment.getOrder());
        }
        
        // Set user info from authentication
        setUserInfoFromAuthentication(invoice);
        
        // Calculate invoice totals
        calculateInvoiceTotals(invoice);
        
        // Save the invoice
        invoice = invoiceRepository.save(invoice);
        
        // Update payment entity with invoice reference
        paymentRepository.save(payment);
        
        // Save invoice items if any
        if (invoiceBean.getInvoiceItems() != null && !invoiceBean.getInvoiceItems().isEmpty()) {
            saveInvoiceItems(invoice, invoiceBean.getInvoiceItems());
        }
        
        // Generate and store PDF
        generateAndSavePdfFile(invoice);
        
        return convertEntityToBean(invoice);
    }

//    private InvoiceBean createInvoiceBeanFromPayment(PaymentEntity payment) {
//        InvoiceBean invoiceBean = new InvoiceBean();
//        
//        // Set customer information
//        if (payment.getCustomerEntity() != null) {
//            invoiceBean.setCustomerId(payment.getCustomerId());
//            invoiceBean.setCustomerName(payment.getCustomerEntity().getCustomerName());
//            invoiceBean.setCustomerEmail(payment.getCustomerEntity().getCustomerEmail());
//            invoiceBean.setCustomerPhone(payment.getCustomerEntity().getCustomerNumber());
////            invoiceBean.setBillingAddress(payment.getCustomerEntity().getBillingAddress());
////            invoiceBean.setShippingAddress(payment.getCustomerEntity().getShippingAddress());
//        }
//        
//        // Set order information
//        if (payment.getOrder() != null) {
//            invoiceBean.setOrderId(payment.getOrderId());
//            invoiceBean.setDeliveryDate(payment.getOrder().getDeliveryDate());
//            
//            // Create invoice items from order items
//            List<InvoiceItemBean> invoiceItems = createInvoiceItemsFromOrder(payment.getOrder());
//            invoiceBean.setInvoiceItems(invoiceItems);
//            
//            // Calculate subtotal from invoice items
//            BigDecimal subtotal = invoiceItems.stream()
//                .map(InvoiceItemBean::getTotalPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//            invoiceBean.setSubtotal(subtotal);
//        }
//        
//        // Set payment information
//        invoiceBean.setPaymentMethod(payment.getPaymentMethod());
//        invoiceBean.setTotalPayment(payment.getTotalPayment());
//        invoiceBean.setPaymentDate(payment.getPaymentDate());
//        invoiceBean.setPaidAmount(payment.getTotalPayment());
//        
//        // Set invoice date to current date
//        invoiceBean.setInvoiceDate(LocalDateTime.now());
//        
//        // Set due date
//        invoiceBean.setDueDate(LocalDateTime.now().plusDays(30));
//        
//        // Set status
//        invoiceBean.setStatus(InvoiceStatus.PAID);
//        invoiceBean.setPaymentStatus(PaymentStatus.PAID);
//        
//        // Set tax percentage
//        invoiceBean.setTaxPercentage(DEFAULT_TAX_PERCENTAGE);
//        
//        return invoiceBean;
//    }
    
    private InvoiceBean createInvoiceBeanFromPayment(PaymentEntity payment) {
        InvoiceBean invoiceBean = new InvoiceBean();

        if (payment.getCustomerEntity() != null) {
            invoiceBean.setCustomerId(payment.getCustomerId());

            invoiceBean.setCustomerName(
                payment.getCustomerEntity().getCustomerName() != null
                    ? payment.getCustomerEntity().getCustomerName()
                    : "N/A"
            );

            invoiceBean.setCustomerEmail(
                payment.getCustomerEntity().getCustomerEmail() != null
                    ? payment.getCustomerEntity().getCustomerEmail()
                    : "N/A"
            );

            invoiceBean.setCustomerPhone(
                payment.getCustomerEntity().getCustomerNumber() != null
                    ? payment.getCustomerEntity().getCustomerNumber()
                    : "N/A"
            );
        }

        if (payment.getOrder() != null) {
            invoiceBean.setOrderId(payment.getOrderId());
            invoiceBean.setDeliveryDate(payment.getOrder().getDeliveryDate());

            List<InvoiceItemBean> invoiceItems = createInvoiceItemsFromOrder(payment.getOrder());
            invoiceBean.setInvoiceItems(invoiceItems);

            BigDecimal subtotal = invoiceItems.stream()
                .map(i -> i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            invoiceBean.setSubtotal(subtotal);
        }

        invoiceBean.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "N/A");
        invoiceBean.setTotalPayment(payment.getTotalPayment() != null ? payment.getTotalPayment() : 0.0);
        invoiceBean.setPaymentDate(payment.getPaymentDate());
        invoiceBean.setPaidAmount(payment.getTotalPayment() != null ? payment.getTotalPayment() : 0.0);

        invoiceBean.setInvoiceDate(LocalDateTime.now());
        invoiceBean.setDueDate(LocalDateTime.now().plusDays(30));

        invoiceBean.setStatus(InvoiceStatus.PAID);
        invoiceBean.setPaymentStatus(PaymentStatus.PAID);
        invoiceBean.setTaxPercentage(DEFAULT_TAX_PERCENTAGE);

        return invoiceBean;
    }


 
//    private List<InvoiceItemBean> createInvoiceItemsFromOrder(OrderEntity order) {
//        List<InvoiceItemBean> invoiceItems = new ArrayList<>();
//
//        if (order != null && order.getOrderProducts() != null) {
//            for (OrderProductEntity orderProduct : order.getOrderProducts()) {
//                InvoiceItemBean invoiceItem = new InvoiceItemBean();
//
//                invoiceItem.setProductId(orderProduct.getProduct().getProductId()); // Assuming ProductEntity has getProductId()
//                invoiceItem.setProductName(orderProduct.getProductName());
//
//                // Convert quantity to numeric before calculation
//                BigDecimal quantity = new BigDecimal(orderProduct.getQuantity());
//                invoiceItem.setQuantity(quantity);
//
//                BigDecimal unitPrice = orderProduct.getPricePerUnit();
//                invoiceItem.setUnitPrice(unitPrice);
//
//                // Calculate total price
//                invoiceItem.setTotalPrice(quantity.multiply(unitPrice));
//
//                // Optional description if you add it to the entity
//                invoiceItem.setDescription("Product from order " + order.getOrderId());
//
//                invoiceItems.add(invoiceItem);
//            }
//        }
//
//        return invoiceItems;
//    }
    
    private List<InvoiceItemBean> createInvoiceItemsFromOrder(OrderEntity order) {
        List<InvoiceItemBean> invoiceItems = new ArrayList<>();

        if (order != null && order.getOrderProducts() != null) {
            for (OrderProductEntity orderProduct : order.getOrderProducts()) {

                InvoiceItemBean invoiceItem = new InvoiceItemBean();

                // Product ID safe
                if (orderProduct.getProduct() != null) {
                    invoiceItem.setProductId(orderProduct.getProduct().getProductId());
                } else {
                    invoiceItem.setProductId("N/A");
                }

                // Product Name safe
                invoiceItem.setProductName(
                    orderProduct.getProductName() != null ? orderProduct.getProductName() : "N/A"
                );

                // Quantity safe
                BigDecimal quantity;
                try {
                    quantity = new BigDecimal(
                        orderProduct.getQuantity() != null ? orderProduct.getQuantity() : "0"
                    );
                } catch (Exception e) {
                    quantity = BigDecimal.ZERO;
                }
                invoiceItem.setQuantity(quantity);

                // Unit price safe
                BigDecimal unitPrice = orderProduct.getPricePerUnit() != null
                    ? orderProduct.getPricePerUnit()
                    : BigDecimal.ZERO;

                invoiceItem.setUnitPrice(unitPrice);

                // Total price safe
                invoiceItem.setTotalPrice(quantity.multiply(unitPrice));

                invoiceItem.setDescription("Product from order " + order.getOrderId());

                invoiceItems.add(invoiceItem);
            }
        }

        return invoiceItems;
    }





    @Override
    public InvoiceBean createInvoiceFromOrder(String orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found for ID: " + orderId));
        
        List<OrderProductEntity> orderProducts = orderProductRepository.findByOrderOrderId(orderId);
        if (orderProducts.isEmpty()) {
            throw new RuntimeException("No products found for order: " + orderId);
        }
        
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(generateUniqueInvoiceId());
        invoice.setInvoiceNumber(generateUniqueInvoiceNumber());
        invoice.setCustomer(order.getCustomer());
        invoice.setOrder(order);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(30)); // 30 days payment terms
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setPaymentStatus(PaymentStatus.UNPAID);
        invoice.setTaxPercentage(DEFAULT_TAX_PERCENTAGE);
        
        setUserInfoFromAuthentication(invoice);
        
        // Create invoice items from order products
        List<InvoiceItemEntity> invoiceItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (OrderProductEntity orderProduct : orderProducts) {
            InvoiceItemEntity item = new InvoiceItemEntity();
            item.setInvoiceItemId(generateUniqueInvoiceItemId());
            item.setInvoice(invoice);
            item.setProduct(orderProduct.getProduct());
            item.setProductName(orderProduct.getProductName());
            item.setQuantity(new BigDecimal(orderProduct.getQuantity()));
            item.setUnitPrice(orderProduct.getPricePerUnit());
            item.setWeight(orderProduct.getTotalWeight());
            item.setTotalPrice(item.getQuantity().multiply(item.getUnitPrice()));
            
            setUserInfoFromAuthentication(item);
            
            invoiceItems.add(item);
            subtotal = subtotal.add(item.getTotalPrice());
        }
        
        invoice.setSubtotal(subtotal);
        invoice.setInvoiceItems(invoiceItems);
        calculateInvoiceTotals(invoice);
        
        invoice = invoiceRepository.save(invoice);
        invoiceItemRepository.saveAll(invoiceItems);
        
        return convertEntityToBean(invoice);
    }

    @Override
    public InvoiceBean updateInvoice(String invoiceId, InvoiceBean invoiceBean) {
        InvoiceEntity existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
        
        validateInvoiceBean(invoiceBean);
        
        // Update invoice details
        updateEntityFromBean(existingInvoice, invoiceBean);
        setUserInfoFromAuthentication(existingInvoice);
        calculateInvoiceTotals(existingInvoice);
        
        existingInvoice = invoiceRepository.save(existingInvoice);
        
        // Update invoice items
        if (invoiceBean.getInvoiceItems() != null) {
            // Delete existing items
            invoiceItemRepository.deleteByInvoiceInvoiceId(invoiceId);
            // Save new items
            saveInvoiceItems(existingInvoice, invoiceBean.getInvoiceItems());
        }
        
        return convertEntityToBean(existingInvoice);
    }

    @Override
    public InvoiceBean getInvoiceById(String invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
        return convertEntityToBean(invoice);
    }

    @Override
    public InvoiceBean getInvoiceByNumber(String invoiceNumber) {
        InvoiceEntity invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found for number: " + invoiceNumber));
        return convertEntityToBean(invoice);
    }

    @Override
    public List<InvoiceBean> getAllInvoices() {
        List<InvoiceEntity> invoices = invoiceRepository.findAll();
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> getInvoicesByCustomerId(String customerId) {
        List<InvoiceEntity> invoices = invoiceRepository.findByCustomerCustomerId(customerId);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> getInvoicesByOrderId(String orderId) {
        List<InvoiceEntity> invoices = invoiceRepository.findByOrderOrderId(orderId);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> getInvoicesByStatus(InvoiceStatus status) {
        List<InvoiceEntity> invoices = invoiceRepository.findByStatus(status);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> getInvoicesByPaymentStatus(PaymentStatus paymentStatus) {
        List<InvoiceEntity> invoices = invoiceRepository.findByPaymentStatus(paymentStatus);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<InvoiceEntity> invoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> getOverdueInvoices() {
        LocalDateTime currentDate = LocalDateTime.now();
        List<InvoiceEntity> invoices = invoiceRepository.findByDueDateBeforeAndPaymentStatusNot(
                currentDate, PaymentStatus.PAID);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> searchInvoices(String searchTerm) {
        List<InvoiceEntity> invoices = invoiceRepository.searchInvoices(searchTerm);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public List<InvoiceBean> getInvoicesByFilterCriteria(List<FilterCriteriaBean> filters, int limit) {
        List<InvoiceEntity> invoices = filterCriteriaService.getFilteredData(InvoiceEntity.class, filters, limit);
        return invoices.stream().map(this::convertEntityToBean).collect(Collectors.toList());
    }

    @Override
    public void deleteInvoice(String invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
        
        // Delete invoice items first
        invoiceItemRepository.deleteByInvoiceInvoiceId(invoiceId);
        
        // Delete invoice
        invoiceRepository.delete(invoice);
    }

    @Override
    public InvoiceBean updateInvoiceStatus(String invoiceId, InvoiceStatus status) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
        
        invoice.setStatus(status);
        setUserInfoFromAuthentication(invoice);
        
        // Auto-update payment status based on invoice status
        if (status == InvoiceStatus.PAID && invoice.getPaymentStatus() != PaymentStatus.PAID) {
            invoice.setPaymentStatus(PaymentStatus.PAID);
            invoice.setTotalAmount(invoice.getTotalAmount());
            invoice.setBalanceAmount(BigDecimal.ZERO);
        } else if (status == InvoiceStatus.OVERDUE) {
            invoice.setPaymentStatus(PaymentStatus.OVERDUE);
        }
        
        invoice = invoiceRepository.save(invoice);
        return convertEntityToBean(invoice);
    }

    @Override
    public InvoiceBean updatePaymentStatus(String invoiceId, PaymentStatus paymentStatus) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
        
        invoice.setPaymentStatus(paymentStatus);
        setUserInfoFromAuthentication(invoice);
        
        // Auto-update invoice status based on payment status
        if (paymentStatus == PaymentStatus.PAID) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setTotalAmount(invoice.getTotalAmount());
            invoice.setBalanceAmount(BigDecimal.ZERO);
        } else if (paymentStatus == PaymentStatus.OVERDUE) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        }
        
        invoice = invoiceRepository.save(invoice);
        return convertEntityToBean(invoice);
    }

    @Override
    public InvoiceBean sendInvoice(String invoiceId) {
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
        
        // Update status to SENT
        invoice.setStatus(InvoiceStatus.SENT);
        setUserInfoFromAuthentication(invoice);
        invoice = invoiceRepository.save(invoice);
        
        // Send email to customer
        try {
            String customerEmail = invoice.getCustomer().getCustomerEmail();
            String subject = "Invoice " + invoice.getInvoiceNumber() + " - " + invoice.getCustomer().getCustomerName();
            String body = buildInvoiceEmailBody(invoice);
            
            // Generate PDF and attach
            byte[] pdfBytes = generateInvoicePdf(invoiceId);
            mailService.sendEmailWithAttachment(customerEmail, subject, body, pdfBytes, 
                    invoice.getInvoiceNumber() + ".pdf");
                    
        } catch (Exception e) {
            throw new RuntimeException("Failed to send invoice email: " + e.getMessage());
        }
        
        return convertEntityToBean(invoice);
    }

//    @Override
//    public byte[] generateInvoicePdf(String invoiceId) {
//        InvoiceEntity invoiceEntity = invoiceRepository.findById(invoiceId)
//                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));
//
//        InvoiceBean invoiceBean = convertEntityToBean(invoiceEntity);
//
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            pdfGenerator.generate(invoiceBean, out);
//            return out.toByteArray();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
//        }
//    }
    
    @Override
    public byte[] generateInvoicePdf(String invoiceId) {
        InvoiceEntity invoiceEntity = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for ID: " + invoiceId));

        String pdfPath = invoiceEntity.getPdfPath();
        if (pdfPath != null && new File(pdfPath).exists()) {
            try {
                return java.nio.file.Files.readAllBytes(new File(pdfPath).toPath());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read stored PDF: " + e.getMessage(), e);
            }
        }

        // Fallback to generating PDF dynamically if file doesn't exist
        InvoiceBean invoiceBean = convertEntityToBean(invoiceEntity);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            pdfGenerator.generate(invoiceBean, out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }



    @Override
    public Long getInvoiceCountByStatus(InvoiceStatus status) {
        return invoiceRepository.countByStatus(status);
    }

    @Override
    public Double getTotalAmountByPaymentStatus(PaymentStatus paymentStatus) {
        return invoiceRepository.getTotalAmountByPaymentStatus(paymentStatus);
    }

    // Private helper methods
    
    private void validateInvoiceBean(InvoiceBean invoiceBean) {
        if (invoiceBean == null) {
            throw new IllegalArgumentException("Invoice bean cannot be null");
        }
        
        if (invoiceBean.getCustomerId() == null || invoiceBean.getCustomerId().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        
        if (invoiceBean.getInvoiceItems() == null || invoiceBean.getInvoiceItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least one item");
        }
    }

    private InvoiceEntity convertBeanToEntity(InvoiceBean bean) {
        InvoiceEntity entity = new InvoiceEntity();
        
        entity.setInvoiceId(bean.getInvoiceId());
        entity.setInvoiceNumber(bean.getInvoiceNumber());
        entity.setInvoiceDate(bean.getInvoiceDate());
        entity.setDueDate(bean.getDueDate());
        entity.setSubtotal(bean.getSubtotal());
        entity.setTaxAmount(bean.getTaxAmount());
        entity.setTaxPercentage(bean.getTaxPercentage());
        entity.setDiscountAmount(bean.getDiscountAmount());
        entity.setDiscountPercentage(bean.getDiscountPercentage());
        entity.setTotalAmount(bean.getTotalAmount());
        entity.setPaidAmount(bean.getPaidAmount());
        entity.setBalanceAmount(bean.getBalanceAmount());
        entity.setStatus(bean.getStatus());
        entity.setPaymentStatus(bean.getPaymentStatus());
        entity.setNotes(bean.getNotes());
        entity.setTermsAndConditions(bean.getTermsAndConditions());
        
        return entity;
    }

    private InvoiceBean convertEntityToBean(InvoiceEntity entity) {
        InvoiceBean bean = new InvoiceBean();
        
        bean.setInvoiceId(entity.getInvoiceId());
        bean.setInvoiceNumber(entity.getInvoiceNumber());
        bean.setInvoiceDate(entity.getInvoiceDate());
        bean.setDueDate(entity.getDueDate());
        bean.setSubtotal(entity.getSubtotal() != null ? entity.getSubtotal() : BigDecimal.ZERO);
        bean.setTaxAmount(entity.getTaxAmount() != null ? entity.getTaxAmount() : BigDecimal.ZERO);
        bean.setTaxPercentage(entity.getTaxPercentage() != null ? entity.getTaxPercentage() : DEFAULT_TAX_PERCENTAGE);
        bean.setDiscountAmount(entity.getDiscountAmount() != null ? entity.getDiscountAmount() : BigDecimal.ZERO);
        bean.setDiscountPercentage(entity.getDiscountPercentage() != null ? entity.getDiscountPercentage() : BigDecimal.ZERO);
        bean.setTotalAmount(entity.getTotalAmount() != null ? entity.getTotalAmount() : BigDecimal.ZERO);
        bean.setPaidAmount(entity.getPaidAmount() != null ? entity.getPaidAmount() : 0.0);
        bean.setBalanceAmount(entity.getBalanceAmount() != null ? entity.getBalanceAmount() : BigDecimal.ZERO);
        bean.setStatus(entity.getStatus() != null ? entity.getStatus() : InvoiceStatus.DRAFT);
        bean.setPaymentStatus(entity.getPaymentStatus() != null ? entity.getPaymentStatus() : PaymentStatus.UNPAID);
        bean.setNotes(entity.getNotes());
        bean.setTermsAndConditions(entity.getTermsAndConditions());
        
        // Set customer info
        if (entity.getCustomer() != null) {
            bean.setCustomerId(entity.getCustomer().getCustomerId());
            bean.setCustomerName(entity.getCustomer().getCustomerName());
            bean.setCustomerEmail(entity.getCustomer().getCustomerEmail());
            bean.setCustomerPhone(entity.getCustomer().getCustomerNumber());
//            bean.setBillingAddress(entity.getCustomer().getBillingAddress());
//            bean.setShippingAddress(entity.getCustomer().getShippingAddress());
        }
        
        // Set order info
        if (entity.getOrder() != null) {
            bean.setOrderId(entity.getOrder().getOrderId());
            bean.setDeliveryDate(entity.getOrder().getDeliveryDate());
            // Add origin/destination if available in order
          
        }
        
        // Convert invoice items
        if (entity.getInvoiceItems() != null && !entity.getInvoiceItems().isEmpty()) {
            List<InvoiceItemBean> itemBeans = entity.getInvoiceItems().stream()
                    .map(this::convertItemEntityToBean)
                    .collect(Collectors.toList());
            bean.setInvoiceItems(itemBeans);
        }
        
        return bean;
    }

    private InvoiceItemBean convertItemEntityToBean(InvoiceItemEntity entity) {
        InvoiceItemBean bean = new InvoiceItemBean();
        
        bean.setInvoiceItemId(entity.getInvoiceItemId());
        bean.setInvoiceId(entity.getInvoice().getInvoiceId());
        bean.setProductName(entity.getProductName());
        bean.setDescription(entity.getDescription());
        bean.setQuantity(entity.getQuantity() != null ? entity.getQuantity() : BigDecimal.ZERO);
        bean.setUnitPrice(entity.getUnitPrice() != null ? entity.getUnitPrice() : BigDecimal.ZERO);
        bean.setWeight(entity.getWeight() != null ? entity.getWeight() : BigDecimal.ZERO);
        bean.setTotalPrice(entity.getTotalPrice() != null ? entity.getTotalPrice() : BigDecimal.ZERO);
        bean.setTotalPrice(entity.getTotalPrice() != null ? entity.getTotalPrice() : BigDecimal.ZERO);
        bean.setTaxAmount(entity.getTaxAmount() != null ? entity.getTaxAmount() : BigDecimal.ZERO);
        bean.setDiscountAmount(entity.getDiscountAmount() != null ? entity.getDiscountAmount() : BigDecimal.ZERO);
        
        if (entity.getProduct() != null) {
            bean.setProductId(entity.getProduct().getProductId());
        }
        
        return bean;
    }

    private void updateEntityFromBean(InvoiceEntity entity, InvoiceBean bean) {
        entity.setInvoiceDate(bean.getInvoiceDate());
        entity.setDueDate(bean.getDueDate());
        entity.setSubtotal(bean.getSubtotal());
        entity.setTaxAmount(bean.getTaxAmount());
        entity.setTaxPercentage(bean.getTaxPercentage());
        entity.setDiscountAmount(bean.getDiscountAmount());
        entity.setDiscountPercentage(bean.getDiscountPercentage());
        entity.setTotalAmount(bean.getTotalAmount());
        entity.setPaidAmount(bean.getPaidAmount());
        entity.setBalanceAmount(bean.getBalanceAmount());
        entity.setStatus(bean.getStatus());
        entity.setPaymentStatus(bean.getPaymentStatus());
        entity.setNotes(bean.getNotes());
        entity.setTermsAndConditions(bean.getTermsAndConditions());
    }
    private void saveInvoiceItems(InvoiceEntity invoice, List<InvoiceItemBean> itemBeans) {
        List<InvoiceItemEntity> items = new ArrayList<>();
        
        for (InvoiceItemBean itemBean : itemBeans) {
            InvoiceItemEntity item = new InvoiceItemEntity();
            item.setInvoiceItemId(generateUniqueInvoiceItemId());
            item.setInvoice(invoice);
            item.setProductName(itemBean.getProductName());
            item.setDescription(itemBean.getDescription());
            item.setQuantity(itemBean.getQuantity());
            item.setUnitPrice(itemBean.getUnitPrice());
            item.setWeight(itemBean.getWeight());
            item.setTotalPrice(itemBean.getQuantity().multiply(itemBean.getUnitPrice()));
            item.setTaxAmount(itemBean.getTaxAmount());
            item.setDiscountAmount(itemBean.getDiscountAmount());
            
            setUserInfoFromAuthentication(item);
            items.add(item);
        }
        
        invoiceItemRepository.saveAll(items);
    }

    private void calculateInvoiceTotals(InvoiceEntity invoice) {
        BigDecimal subtotal = invoice.getSubtotal();
        if (subtotal == null) {
            subtotal = BigDecimal.ZERO;
            invoice.setSubtotal(subtotal);
        }
        
        // Calculate tax amount
        BigDecimal taxPercentage = invoice.getTaxPercentage();
        if (taxPercentage == null) {
            taxPercentage = DEFAULT_TAX_PERCENTAGE;
            invoice.setTaxPercentage(taxPercentage);
        }
        
        BigDecimal taxAmount = subtotal.multiply(taxPercentage).divide(new BigDecimal("100"));
        invoice.setTaxAmount(taxAmount);
        
        // Calculate discount amount
        BigDecimal discountAmount = invoice.getDiscountAmount();
        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
            invoice.setDiscountAmount(discountAmount);
        }
        
        BigDecimal discountPercentage = invoice.getDiscountPercentage();
        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = subtotal.multiply(discountPercentage).divide(new BigDecimal("100"));
            invoice.setDiscountAmount(discountAmount);
        }
        
        // Calculate total amount
        BigDecimal totalAmount = subtotal.add(taxAmount).subtract(discountAmount);
        invoice.setTotalAmount(totalAmount);
        
        // Calculate balance amount
        Double paidAmount = invoice.getPaidAmount();
        if (paidAmount == null) {
           
            invoice.setPaidAmount(paidAmount);
        }
        
        if (invoice.getPaidAmount() != null && invoice.getTotalAmount() != null) {
            BigDecimal paid = BigDecimal.valueOf(invoice.getPaidAmount());
            BigDecimal balance = invoice.getTotalAmount().subtract(paid);
            invoice.setBalanceAmount(balance);
        }

    }


    private String generateUniqueInvoiceId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return INVOICE_ID_PREFIX + timestamp;
    }

    private String generateUniqueInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = invoiceRepository.count() + 1;
        return INVOICE_NUMBER_PREFIX + timestamp + "-" + String.format("%04d", count);
    }

    private String generateUniqueInvoiceItemId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "INVITEM" + timestamp;
    }

//    private void setUserInfoFromAuthentication(Object entity) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated()) {
//            String username = auth.getName();
//            User user = userRepository.findByUsername(username).orElse(null);
//            
//            // Set user info in generic entity if it extends GenericEntity
//            // This would be handled by the GenericEntity class
//        }
//    }

    private void setUserInfoFromAuthentication(Object entity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isPresent() && entity instanceof GenericEntity) {
                User user = optionalUser.get();
                GenericEntity genericEntity = (GenericEntity) entity;

                genericEntity.setCreatedBy(user.getUserId());
                genericEntity.setLastModifiedBy(user.getUserId());
            }
        }
    }

    
    private String buildInvoiceEmailBody(InvoiceEntity invoice) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(invoice.getCustomer().getCustomerName()).append(",\n\n");
        body.append("Please find attached invoice ").append(invoice.getInvoiceNumber());
        body.append(" dated ").append(invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        body.append(" for amount ₹").append(invoice.getTotalAmount()).append(".\n\n");
        body.append("Payment due date: ").append(invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        body.append("\n\nThank you for your business!\n\n");
        body.append("Best regards,\nTMS Team");
        
        return body.toString();
    }
    
    
    private void setCustomerAndOrderReferences(InvoiceEntity invoice, InvoiceBean bean) {
        if (bean.getCustomerId() != null) {
            CustomerEntity customer = customerRepository.findById(bean.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + bean.getCustomerId()));
            invoice.setCustomer(customer);
        }

        if (bean.getOrderId() != null) {
            OrderEntity order = orderRepository.findById(bean.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + bean.getOrderId()));
            invoice.setOrder(order);
        }
    }

    
    

}