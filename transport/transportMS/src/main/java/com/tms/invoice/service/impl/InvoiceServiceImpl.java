package com.tms.invoice.service.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.invoice.bean.InvoiceBean;
import com.tms.invoice.entity.InvoiceEntity;
import com.tms.invoice.repository.InvoiceRepository;
import com.tms.invoice.service.InvoiceService;
@Service
public class InvoiceServiceImpl implements InvoiceService {
	
	@Autowired 
	private InvoiceRepository invoiceRepository;

//	@Override
//	public InvoiceBean generateInvoice(InvoiceBean invoiceBean) {
//		// TODO Auto-generated method stub
//		
//		InvoiceEntity invoiceEntity = new InvoiceEntity();
//		BeanUtils.copyProperties(invoiceBean, invoiceEntity);
//		InvoiceEntity savedEntity = invoiceRepository.save(invoiceEntity);
//		InvoiceBean savedBean = new InvoiceBean();
//		BeanUtils.copyProperties(savedEntity, savedBean);
//		return savedBean;
//	}
//	
	
	
	public InvoiceBean generateInvoice(InvoiceBean invoiceBean) {

	    InvoiceEntity invoiceEntity = new InvoiceEntity();
	    BeanUtils.copyProperties(invoiceBean, invoiceEntity);

	    System.out.println("Before save: " + invoiceEntity.getAmount() + ", " + invoiceEntity.getIssueDate());

	    InvoiceEntity savedEntity = invoiceRepository.save(invoiceEntity);

	    System.out.println("After save: " + savedEntity.getAmount() + ", " + savedEntity.getIssueDate());

	    InvoiceBean savedBean = new InvoiceBean();
	    BeanUtils.copyProperties(savedEntity, savedBean);

	    return savedBean;
	}
	

	@Override
	public InvoiceBean getInvoiceById(Long invoiceId) {
		// TODO Auto-generated method stub
		
		Optional<InvoiceEntity> optional = invoiceRepository.findById(invoiceId);
		InvoiceBean invoiceBean = null;
		if(optional.isPresent())
		{
			InvoiceEntity invoiceEntity = optional.get();
			invoiceBean = new InvoiceBean();
			BeanUtils.copyProperties(invoiceEntity, invoiceBean);
		}
		return invoiceBean;
	}

	@Override
	public String deleteInvoice(Long invoiceId) {
		// TODO Auto-generated method stub
		invoiceRepository.deleteById(invoiceId);
		return "It is deleted";
	}

}
