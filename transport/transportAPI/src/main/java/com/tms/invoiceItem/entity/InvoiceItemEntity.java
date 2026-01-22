package com.tms.invoiceItem.entity;

import java.math.BigDecimal;

import com.tms.generic.entity.GenericEntity;
import com.tms.invoice.entity.InvoiceEntity;
import com.tms.product.entity.ProductEntity;

import jakarta.persistence.*;

@Entity 
@Table(name = "invoice_item")
public class InvoiceItemEntity extends GenericEntity {

 @Id
 @Column(name = "invoiceItemId")
 private String invoiceItemId;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "invoiceId")
 private InvoiceEntity invoice;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "productId")
 private ProductEntity product;

 @Column(name = "productName")
 private String productName;

 @Column(name = "description", columnDefinition = "TEXT")
 private String description;

 @Column(name = "quantity", precision = 10, scale = 2)
 private BigDecimal quantity;

 @Column(name = "unitPrice", precision = 15, scale = 2)
 private BigDecimal unitPrice;

 @Column(name = "weight", precision = 10, scale = 2)
 private BigDecimal weight;

 @Column(name = "totalPrice", precision = 15, scale = 2)
 private BigDecimal totalPrice;

 @Column(name = "taxAmount", precision = 15, scale = 2)
 private BigDecimal taxAmount;

 @Column(name = "discountAmount", precision = 15, scale = 2)
 private BigDecimal discountAmount;

 // Constructors
 public InvoiceItemEntity() {}

 public InvoiceItemEntity(InvoiceEntity invoice, ProductEntity product, String productName, 
                        BigDecimal quantity, BigDecimal unitPrice, BigDecimal weight) {
     this.invoice = invoice;
     this.product = product;
     this.productName = productName;
     this.quantity = quantity;
     this.unitPrice = unitPrice;
     this.weight = weight;
     this.totalPrice = quantity.multiply(unitPrice);
 }

 // Getters and Setters
 public String getInvoiceItemId() {
     return invoiceItemId;
 }

 public void setInvoiceItemId(String invoiceItemId) {
     this.invoiceItemId = invoiceItemId;
 }

 public InvoiceEntity getInvoice() {
     return invoice;
 }

 public void setInvoice(InvoiceEntity invoice) {
     this.invoice = invoice;
 }

 public ProductEntity getProduct() {
     return product;
 }

 public void setProduct(ProductEntity product) {
     this.product = product;
 }

 public String getProductName() {
     return productName;
 }

 public void setProductName(String productName) {
     this.productName = productName;
 }

 public String getDescription() {
     return description;
 }

 public void setDescription(String description) {
     this.description = description;
 }

 public BigDecimal getQuantity() {
     return quantity;
 }

 public void setQuantity(BigDecimal quantity) {
     this.quantity = quantity;
 }

 public BigDecimal getUnitPrice() {
     return unitPrice;
 }

 public void setUnitPrice(BigDecimal unitPrice) {
     this.unitPrice = unitPrice;
 }

 public BigDecimal getWeight() {
     return weight;
 }

 public void setWeight(BigDecimal weight) {
     this.weight = weight;
 }

 public BigDecimal getTotalPrice() {
     return totalPrice;
 }

 public void setTotalPrice(BigDecimal totalPrice) {
     this.totalPrice = totalPrice;
 }

 public BigDecimal getTaxAmount() {
     return taxAmount;
 }

 public void setTaxAmount(BigDecimal taxAmount) {
     this.taxAmount = taxAmount;
 }

 public BigDecimal getDiscountAmount() {
     return discountAmount;
 }

 public void setDiscountAmount(BigDecimal discountAmount) {
     this.discountAmount = discountAmount;
 }
}
