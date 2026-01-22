package com.tms.invoice.generator;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.tms.invoice.bean.InvoiceBean;
import com.tms.invoiceItem.bean.InvoiceItemBean;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class InvoicePdfGenerator {

    // ✅ Updated Default Company Details
    @Value("${company.name:Simpana Technologies Pvt. Ltd.}")
    private String companyName;

    @Value("${company.address:Pune Maharashtra}")
    private String companyAddress;

    @Value("${company.phone:+91 0000000000}")
    private String companyPhone;

    @Value("${company.email:simpanatech@gmail.com}")
    private String companyEmail;

    @Value("${company.website:SimpanaTechnologies.com}")
    private String companyWebsite;

    @Value("${company.tax.number:TAX123456789}")
    private String companyTaxNumber;

    @Value("${company.logo.path:}")
    private String companyLogoPath;

    // ✅ Rupees symbol
    private static final String CURRENCY = "\u20B9";

    public void generate(InvoiceBean invoice, OutputStream out) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // Define fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);

        // Add company header
        addCompanyHeader(document, companyFont, normalFont);

        // Add invoice title and basic info
        addInvoiceHeader(document, invoice, titleFont, headerFont, normalFont);

        // Add customer information
        addCustomerInfo(document, invoice, headerFont, normalFont);

        // Add shipping information if available
        addShippingInfo(document, invoice, headerFont, normalFont);

        // Add invoice items table
        addInvoiceItemsTable(document, invoice, headerFont, normalFont);

        // Add totals section
        addTotalsSection(document, invoice, headerFont, normalFont, boldFont);

        // Add payment information
        addPaymentInfo(document, invoice, headerFont, normalFont);

        // Add notes and terms
        addNotesAndTerms(document, invoice, headerFont, normalFont);

        // Add footer
        addFooter(document, smallFont);

        document.close();
    }

    private void addCompanyHeader(Document document, Font companyFont, Font normalFont) throws DocumentException {
        // Company header table
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20f);

        // Left side - Company info
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        companyCell.setPadding(0);

        Paragraph companyPara = new Paragraph();
        companyPara.add(new Chunk(companyName, companyFont));
        companyPara.add(Chunk.NEWLINE);
        companyPara.add(new Chunk(companyAddress, normalFont));
        companyPara.add(Chunk.NEWLINE);
        companyPara.add(new Chunk("Phone: " + companyPhone, normalFont));
        companyPara.add(Chunk.NEWLINE);
        companyPara.add(new Chunk("Email: " + companyEmail, normalFont));
        companyPara.add(Chunk.NEWLINE);
        companyPara.add(new Chunk("Website: " + companyWebsite, normalFont));
        companyPara.add(Chunk.NEWLINE);
        companyPara.add(new Chunk("Tax ID: " + companyTaxNumber, normalFont));

        companyCell.addElement(companyPara);

        // Right side - Logo placeholder or company branding
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        logoCell.setPadding(0);

        // If logo path is provided, you can add image here
        if (companyLogoPath != null && !companyLogoPath.isEmpty()) {
            try {
                Image logo = Image.getInstance(companyLogoPath);
                logo.scaleToFit(100, 50);
                logoCell.addElement(logo);
            } catch (Exception e) {
                // If logo fails to load, add company name as fallback
                Paragraph logoFallback = new Paragraph(companyName, companyFont);
                logoFallback.setAlignment(Element.ALIGN_RIGHT);
                logoCell.addElement(logoFallback);
            }
        } else {
            Paragraph logoFallback = new Paragraph(companyName, companyFont);
            logoFallback.setAlignment(Element.ALIGN_RIGHT);
            logoCell.addElement(logoFallback);
        }

        headerTable.addCell(companyCell);
        headerTable.addCell(logoCell);

        document.add(headerTable);

        // Add separator line
        addSeparatorLine(document);
    }

    private void addInvoiceHeader(Document document, InvoiceBean invoice, Font titleFont, Font headerFont, Font normalFont) throws DocumentException {
        // Invoice header table
        PdfPTable invoiceHeaderTable = new PdfPTable(2);
        invoiceHeaderTable.setWidthPercentage(100);
        invoiceHeaderTable.setSpacingAfter(20f);

        // Left side - Invoice title
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(0);

        Paragraph titlePara = new Paragraph("INVOICE", titleFont);
        titlePara.setSpacingAfter(10f);
        titleCell.addElement(titlePara);

        // Right side - Invoice details
        PdfPCell detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        detailsCell.setPadding(0);

        Paragraph detailsPara = new Paragraph();
        detailsPara.add(new Chunk("Invoice #: ", headerFont));
        detailsPara.add(new Chunk(invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "N/A", normalFont));
        detailsPara.add(Chunk.NEWLINE);
        detailsPara.add(new Chunk("Invoice Date: ", headerFont));
        detailsPara.add(new Chunk(invoice.getInvoiceDate() != null
                ? invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                : "N/A", normalFont));
        detailsPara.add(Chunk.NEWLINE);
        detailsPara.add(new Chunk("Due Date: ", headerFont));
        detailsPara.add(new Chunk(invoice.getDueDate() != null
                ? invoice.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                : "Upon Receipt", normalFont));
        detailsPara.add(Chunk.NEWLINE);
        detailsPara.add(new Chunk("Status: ", headerFont));
        detailsPara.add(new Chunk(invoice.getStatus() != null ? invoice.getStatus().toString() : "Pending", normalFont));

        // Add delivery date if available
        if (invoice.getDeliveryDate() != null) {
            detailsPara.add(Chunk.NEWLINE);
            detailsPara.add(new Chunk("Delivery Date: ", headerFont));
            detailsPara.add(new Chunk(invoice.getDeliveryDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), normalFont));
        }

        detailsPara.setAlignment(Element.ALIGN_RIGHT);
        detailsCell.addElement(detailsPara);

        invoiceHeaderTable.addCell(titleCell);
        invoiceHeaderTable.addCell(detailsCell);

        document.add(invoiceHeaderTable);
    }

    private void addCustomerInfo(Document document, InvoiceBean invoice, Font headerFont, Font normalFont) throws DocumentException {
        // Customer info table
        PdfPTable customerTable = new PdfPTable(2);
        customerTable.setWidthPercentage(100);
        customerTable.setSpacingAfter(20f);

        // Bill To section
        PdfPCell billToCell = new PdfPCell();
        billToCell.setBorder(Rectangle.NO_BORDER);
        billToCell.setPadding(10);
        billToCell.setBackgroundColor(new BaseColor(245, 245, 245));

        Paragraph billToPara = new Paragraph();
        billToPara.add(new Chunk("BILL TO:", headerFont));
        billToPara.add(Chunk.NEWLINE);
        billToPara.add(new Chunk(invoice.getCustomerName() != null ? invoice.getCustomerName() : "N/A", normalFont));
        billToPara.add(Chunk.NEWLINE);

        if (invoice.getCustomerEmail() != null && !invoice.getCustomerEmail().isEmpty()) {
            billToPara.add(new Chunk("Email: " + invoice.getCustomerEmail(), normalFont));
            billToPara.add(Chunk.NEWLINE);
        }
        if (invoice.getCustomerPhone() != null && !invoice.getCustomerPhone().isEmpty()) {
            billToPara.add(new Chunk("Phone: " + invoice.getCustomerPhone(), normalFont));
            billToPara.add(Chunk.NEWLINE);
        }
        if (invoice.getBillingAddress() != null && !invoice.getBillingAddress().isEmpty()) {
            billToPara.add(new Chunk("Address: " + invoice.getBillingAddress(), normalFont));
        }

        billToCell.addElement(billToPara);

        // Order info section
        PdfPCell orderInfoCell = new PdfPCell();
        orderInfoCell.setBorder(Rectangle.NO_BORDER);
        orderInfoCell.setPadding(10);
        orderInfoCell.setBackgroundColor(new BaseColor(245, 245, 245));

        Paragraph orderInfoPara = new Paragraph();
        orderInfoPara.add(new Chunk("ORDER INFO:", headerFont));
        orderInfoPara.add(Chunk.NEWLINE);

        if (invoice.getOrderId() != null && !invoice.getOrderId().isEmpty()) {
            orderInfoPara.add(new Chunk("Order ID: " + invoice.getOrderId(), normalFont));
            orderInfoPara.add(Chunk.NEWLINE);
        }

        // Add receiver information if available
        if (invoice.getReceiverName() != null && !invoice.getReceiverName().isEmpty()) {
            orderInfoPara.add(new Chunk("Receiver: " + invoice.getReceiverName(), normalFont));
            orderInfoPara.add(Chunk.NEWLINE);
        }
        if (invoice.getReceiverEmail() != null && !invoice.getReceiverEmail().isEmpty()) {
            orderInfoPara.add(new Chunk("Receiver Email: " + invoice.getReceiverEmail(), normalFont));
            orderInfoPara.add(Chunk.NEWLINE);
        }

        orderInfoPara.add(new Chunk("Payment Status: " + (invoice.getPaymentStatus() != null ? invoice.getPaymentStatus().toString() : "Pending"), normalFont));

        orderInfoCell.addElement(orderInfoPara);

        customerTable.addCell(billToCell);
        customerTable.addCell(orderInfoCell);

        document.add(customerTable);
    }

    private void addShippingInfo(Document document, InvoiceBean invoice, Font headerFont, Font normalFont) throws DocumentException {
        if ((invoice.getOriginLocation() != null && !invoice.getOriginLocation().isEmpty()) ||
                (invoice.getDestinationLocation() != null && !invoice.getDestinationLocation().isEmpty()) ||
                (invoice.getShippingAddress() != null && !invoice.getShippingAddress().isEmpty())) {

            PdfPTable shippingTable = new PdfPTable(2);
            shippingTable.setWidthPercentage(100);
            shippingTable.setSpacingAfter(20f);

            PdfPCell originCell = new PdfPCell();
            originCell.setBorder(Rectangle.NO_BORDER);
            originCell.setPadding(10);
            originCell.setBackgroundColor(new BaseColor(250, 250, 250));

            Paragraph originPara = new Paragraph();
            originPara.add(new Chunk("ORIGIN:", headerFont));
            originPara.add(Chunk.NEWLINE);
            originPara.add(new Chunk(invoice.getOriginLocation() != null ? invoice.getOriginLocation() : "N/A", normalFont));

            originCell.addElement(originPara);

            PdfPCell destinationCell = new PdfPCell();
            destinationCell.setBorder(Rectangle.NO_BORDER);
            destinationCell.setPadding(10);
            destinationCell.setBackgroundColor(new BaseColor(250, 250, 250));

            Paragraph destinationPara = new Paragraph();
            destinationPara.add(new Chunk("DESTINATION:", headerFont));
            destinationPara.add(Chunk.NEWLINE);
            destinationPara.add(new Chunk(invoice.getDestinationLocation() != null ? invoice.getDestinationLocation() : "N/A", normalFont));

            if (invoice.getShippingAddress() != null && !invoice.getShippingAddress().isEmpty()) {
                destinationPara.add(Chunk.NEWLINE);
                destinationPara.add(new Chunk("Shipping Address: " + invoice.getShippingAddress(), normalFont));
            }

            destinationCell.addElement(destinationPara);

            shippingTable.addCell(originCell);
            shippingTable.addCell(destinationCell);

            document.add(shippingTable);
        }
    }

    private void addInvoiceItemsTable(Document document, InvoiceBean invoice, Font headerFont, Font normalFont) throws DocumentException {
        List<InvoiceItemBean> items = invoice.getInvoiceItems();
        if (items == null || items.isEmpty()) {
            return;
        }

        boolean hasWeight = items.stream().anyMatch(item -> item.getWeight() != null && item.getWeight().compareTo(BigDecimal.ZERO) > 0);
        boolean hasDiscount = items.stream().anyMatch(item -> item.getDiscountAmount() != null && item.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0);

        float[] columnWidths;
        if (hasWeight && hasDiscount) {
            columnWidths = new float[]{0.8f, 3.5f, 1f, 1f, 1.5f, 1.5f, 1.5f, 1.5f};
        } else if (hasWeight) {
            columnWidths = new float[]{0.8f, 3.8f, 1f, 1f, 1.5f, 1.5f, 1.5f};
        } else if (hasDiscount) {
            columnWidths = new float[]{0.8f, 3.8f, 1f, 1.5f, 1.5f, 1.5f, 1.5f};
        } else {
            columnWidths = new float[]{1f, 4f, 1f, 2f, 2f, 2f};
        }

        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        addStyledTableHeader(table, "#", headerFont);
        addStyledTableHeader(table, "Description", headerFont);
        addStyledTableHeader(table, "Qty", headerFont);
        if (hasWeight) {
            addStyledTableHeader(table, "Weight", headerFont);
        }
        addStyledTableHeader(table, "Unit Price", headerFont);
        if (hasDiscount) {
            addStyledTableHeader(table, "Discount", headerFont);
        }
        addStyledTableHeader(table, "Tax", headerFont);
        addStyledTableHeader(table, "Total", headerFont);

        int itemNumber = 1;
        for (InvoiceItemBean item : items) {
            addStyledTableCell(table, String.valueOf(itemNumber++), normalFont, Element.ALIGN_CENTER);

            String description = item.getDescription() != null && !item.getDescription().isEmpty()
                    ? item.getDescription()
                    : (item.getProductName() != null ? item.getProductName() : "N/A");
            addStyledTableCell(table, description, normalFont, Element.ALIGN_LEFT);

            addStyledTableCell(table, formatBigDecimal(item.getQuantity()), normalFont, Element.ALIGN_CENTER);

            if (hasWeight) {
                addStyledTableCell(table, formatBigDecimal(item.getWeight()) + " kg", normalFont, Element.ALIGN_CENTER);
            }

            addStyledTableCell(table, CURRENCY + formatBigDecimal(item.getUnitPrice()), normalFont, Element.ALIGN_RIGHT);

            if (hasDiscount) {
                addStyledTableCell(table,
                        item.getDiscountAmount() != null ? CURRENCY + formatBigDecimal(item.getDiscountAmount()) : CURRENCY + "0.00",
                        normalFont, Element.ALIGN_RIGHT);
            }

            addStyledTableCell(table,
                    item.getTaxAmount() != null ? CURRENCY + formatBigDecimal(item.getTaxAmount()) : CURRENCY + "0.00",
                    normalFont, Element.ALIGN_RIGHT);

            addStyledTableCell(table, CURRENCY + formatBigDecimal(item.getTotalPrice()), normalFont, Element.ALIGN_RIGHT);
        }

        document.add(table);
    }

    private void addTotalsSection(Document document, InvoiceBean invoice, Font headerFont, Font normalFont, Font boldFont) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(50);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setSpacingAfter(20f);

        addTotalRow(totalsTable, "Subtotal:", CURRENCY + formatBigDecimal(invoice.getSubtotal()), normalFont);

        if (invoice.getTaxAmount() != null && invoice.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
            String taxLabel = "Tax";
            if (invoice.getTaxPercentage() != null && invoice.getTaxPercentage().compareTo(BigDecimal.ZERO) > 0) {
                taxLabel += " (" + formatBigDecimal(invoice.getTaxPercentage()) + "%)";
            }
            addTotalRow(totalsTable, taxLabel + ":", CURRENCY + formatBigDecimal(invoice.getTaxAmount()), normalFont);
        }

        if (invoice.getDiscountAmount() != null && invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            String discountLabel = "Discount";
            if (invoice.getDiscountPercentage() != null && invoice.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
                discountLabel += " (" + formatBigDecimal(invoice.getDiscountPercentage()) + "%)";
            }
            addTotalRow(totalsTable, discountLabel + ":", "-" + CURRENCY + formatBigDecimal(invoice.getDiscountAmount()), normalFont);
        }

        addTotalRow(totalsTable, "Total Amount:", CURRENCY + formatBigDecimal(invoice.getTotalAmount()), boldFont);

        document.add(totalsTable);
    }

    private void addPaymentInfo(Document document, InvoiceBean invoice, Font headerFont, Font normalFont) throws DocumentException {
        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.setSpacingAfter(20f);

        PdfPCell paymentCell = new PdfPCell();
        paymentCell.setBorder(Rectangle.NO_BORDER);
        paymentCell.setPadding(0);

        Paragraph paymentPara = new Paragraph();
        paymentPara.add(new Chunk("PAYMENT INFORMATION:", headerFont));
        paymentPara.add(Chunk.NEWLINE);

        if (invoice.getPaymentMethod() != null && !invoice.getPaymentMethod().isEmpty()) {
            paymentPara.add(new Chunk("Payment Method: " + invoice.getPaymentMethod(), normalFont));
            paymentPara.add(Chunk.NEWLINE);
        }
        if (invoice.getPaymentId() != null && !invoice.getPaymentId().isEmpty()) {
            paymentPara.add(new Chunk("Payment ID: " + invoice.getPaymentId(), normalFont));
            paymentPara.add(Chunk.NEWLINE);
        }
        if (invoice.getPaidAmount() != null && BigDecimal.valueOf(invoice.getPaidAmount()).compareTo(BigDecimal.ZERO) > 0) {
            paymentPara.add(new Chunk("Amount Paid: " + CURRENCY + formatBigDecimal(BigDecimal.valueOf(invoice.getPaidAmount())), normalFont));
            paymentPara.add(Chunk.NEWLINE);
        }

        if (invoice.getTotalPayment() != null && invoice.getTotalPayment() > 0) {
            paymentPara.add(new Chunk("Total Payment: " + CURRENCY + String.format("%.2f", invoice.getTotalPayment()), normalFont));
            paymentPara.add(Chunk.NEWLINE);
        }
        if (invoice.getBalanceAmount() != null && invoice.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0) {
            paymentPara.add(new Chunk("Balance Due: " + CURRENCY + formatBigDecimal(invoice.getBalanceAmount()), normalFont));
            paymentPara.add(Chunk.NEWLINE);
        }
        if (invoice.getPaymentDate() != null) {
            paymentPara.add(new Chunk("Payment Date: " + invoice.getPaymentDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), normalFont));
        }

        paymentCell.addElement(paymentPara);

        PdfPCell emptyCell = new PdfPCell();
        emptyCell.setBorder(Rectangle.NO_BORDER);

        paymentTable.addCell(paymentCell);
        paymentTable.addCell(emptyCell);

        document.add(paymentTable);
    }

    private void addNotesAndTerms(Document document, InvoiceBean invoice, Font headerFont, Font normalFont) throws DocumentException {
        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            document.add(new Paragraph("Notes:", headerFont));
            document.add(new Paragraph(invoice.getNotes(), normalFont));
            document.add(new Paragraph(" "));
        }

        if (invoice.getTermsAndConditions() != null && !invoice.getTermsAndConditions().isEmpty()) {
            document.add(new Paragraph("Terms and Conditions:", headerFont));
            document.add(new Paragraph(invoice.getTermsAndConditions(), normalFont));
        } else {
            document.add(new Paragraph("Terms and Conditions:", headerFont));
            document.add(new Paragraph("Payment is due within 30 days of invoice date. Late payments may incur additional charges.", normalFont));
        }
    }

    private void addFooter(Document document, Font smallFont) throws DocumentException {
        document.add(new Paragraph(" "));
        addSeparatorLine(document);

        Paragraph footer = new Paragraph();
        footer.add(new Chunk("Thank you for your business!", smallFont));
        footer.add(Chunk.NEWLINE);
        footer.add(new Chunk("For questions about this invoice, please contact us at " + companyEmail + " or " + companyPhone, smallFont));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10f);

        document.add(footer);
    }

    private void addSeparatorLine(Document document) throws DocumentException {
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        separator.setLineWidth(1f);
        document.add(new Chunk(separator));
        document.add(new Paragraph(" "));
    }

    private void addStyledTableHeader(PdfPTable table, String headerTitle, Font font) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(new BaseColor(52, 73, 94));
        header.setBorderWidth(1);
        header.setBorderColor(BaseColor.WHITE);
        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPadding(8);
        table.addCell(header);
    }

    private void addStyledTableCell(PdfPTable table, String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String amount, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(3);

        PdfPCell amountCell = new PdfPCell(new Phrase(amount, font));
        amountCell.setBorder(Rectangle.NO_BORDER);
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        amountCell.setPadding(3);

        table.addCell(labelCell);
        table.addCell(amountCell);
    }

    private String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return String.format("%.2f", value);
    }
}
