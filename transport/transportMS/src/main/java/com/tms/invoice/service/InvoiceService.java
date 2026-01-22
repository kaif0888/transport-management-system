package com.tms.invoice.service;

import com.tms.invoice.bean.InvoiceBean;

public interface InvoiceService {

	InvoiceBean generateInvoice(InvoiceBean invoiceBean);

	InvoiceBean getInvoiceById(Long invoiceId);

	String deleteInvoice(Long invoiceId);
	

}
