package com.tms.expenseType.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.expense.entity.ExpenseEntity;
import com.tms.expense.repository.ExpenseRepository;
import com.tms.expenseType.bean.ExpenseTypeBean;
import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.expenseType.repository.ExpenseTypeRepository;
import com.tms.expenseType.service.ExpenseTypeService;

@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {
    @Autowired
	private ExpenseTypeRepository expenseTypeRepo;
    
    @Autowired
    private ExpenseRepository expenseRepo;
	
	@Override
	public ExpenseTypeBean createExpenseType(ExpenseTypeBean expenseTypeBean) {
		
		Long expenseId = expenseTypeBean.getExpenseTypeId();
		
		if(expenseId==null) {
			throw new IllegalArgumentException("Expense ID must not be null");
		}
		
		ExpenseEntity expenseEntity = expenseRepo.findById(expenseId)
		.orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));
		
		ExpenseTypeEntity expenseTypeEntity = new ExpenseTypeEntity();
	    BeanUtils.copyProperties(expenseTypeBean, expenseTypeEntity);
	    expenseTypeEntity.getExpenseTypeId();
	    
	    ExpenseTypeEntity saveEntity = expenseTypeRepo.save(expenseTypeEntity);
	    
	    ExpenseTypeBean saveBean = new ExpenseTypeBean();
	    BeanUtils.copyProperties(saveEntity, saveBean);
	     
	    saveBean.setExpenseTypeId(expenseId);
		
		
		return saveBean;
	}

//	@Override
//	public List<ExpenseTypeBean> listAllExpenseType() {
//		List<ExpenseTypeEntity> list = expenseTypeRepo.findAll();
//		List<ExpenseTypeBean> bean =null;
//		for(ExpenseTypeEntity expenseTypeEntity:list)
//		{
//			bean = new ArrayList<>();
//		    ExpenseTypeBean	expenseTypeBean = new ExpenseTypeBean();
//			BeanUtils.copyProperties(expenseTypeEntity, expenseTypeBean);
//			bean.add(expenseTypeBean);
//		 
//		}
//		return bean;
//	}
	
	@Override
	public List<ExpenseTypeBean> listAllExpenseType() {
	    List<ExpenseTypeEntity> entityList = expenseTypeRepo.findAll();
	    List<ExpenseTypeBean> beanList = new ArrayList<>();

	    for (ExpenseTypeEntity entity : entityList) {
	        ExpenseTypeBean bean = new ExpenseTypeBean();
	        BeanUtils.copyProperties(entity, bean);
	        if (entity.getExpenseTypeId() != null) {
	            bean.setExpenseTypeId(entity.getExpenseTypeId());
	        }
	        beanList.add(bean);
	    }
	    return beanList;
	}

	@Override
	public String deleteExpenseType(Long expenseTypeId) {
		if(expenseTypeId != null) {
		expenseTypeRepo.deleteById(expenseTypeId);
		return "The ExpenseType has Been deleted";
		}
		return "The expenseTypeId is null";
	}

}
