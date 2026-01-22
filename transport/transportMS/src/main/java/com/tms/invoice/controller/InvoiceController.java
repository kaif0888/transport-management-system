package com.tms.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.invoice.bean.InvoiceBean;
import com.tms.invoice.service.impl.InvoiceServiceImpl;

;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value="/invoice")
public class InvoiceController {
	
	@Autowired
    private InvoiceServiceImpl invoiceServiceImpl;
	
//	@PostMapping(value="/generateInvoice")
//	public InvoiceBean generateInvoice(@RequestBody InvoiceBean invoiceBean)
//	{
//	    System.out.println(">>> JSON successfully mapped?");
//	    System.out.println("Amount: " + invoiceBean.getAmount());
//	    System.out.println("IssueDate: " + invoiceBean.getIssueDate());
//	    System.out.println("Status: " + invoiceBean.getStatus());
//		return invoiceServiceImpl.generateInvoice(invoiceBean);
//	}
//
	@PostMapping("/generateInvoice")
	public ResponseEntity<InvoiceBean> generateInvoice(@RequestBody InvoiceBean invoiceBean) {
	    InvoiceBean savedBean = invoiceServiceImpl.generateInvoice(invoiceBean);
	    return ResponseEntity.ok(savedBean);
	}
	
	
	@GetMapping(value="/getInvoiceById/{invoiceId}")
	public InvoiceBean getInvoiceById(@PathVariable Long invoiceId)
	{
		return invoiceServiceImpl.getInvoiceById(invoiceId);
	}
	
	@DeleteMapping(value="/deleteInvoice/{invoiceId}")
	public String deleteInvoice(@PathVariable Long invoiceId)
	{
		 return invoiceServiceImpl.deleteInvoice(invoiceId);
		
	
	}
}
