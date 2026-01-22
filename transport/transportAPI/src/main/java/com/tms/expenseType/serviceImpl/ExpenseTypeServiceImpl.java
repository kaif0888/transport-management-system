package com.tms.expenseType.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tms.JwtSecurity.entity.User;
import com.tms.JwtSecurity.repository.UserRepository;
import com.tms.expense.bean.ExpenseBean;
import com.tms.expense.entity.ExpenseEntity;
import com.tms.expense.repository.ExpenseRepository;
import com.tms.expenseType.bean.ExpenseTypeBean;
import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.expenseType.repository.ExpenseTypeRepository;
import com.tms.expenseType.service.ExpenseTypeService;
import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.filter.criteria.constant.FilterOperation;
import com.tms.filter.criteria.service.FilterCriteriaService;
import com.tms.productcategory.bean.ProductCategoryBean;
import com.tms.productcategory.entity.ProductCategoryEntity;

@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {
	@Autowired
	private ExpenseTypeRepository expenseTypeRepo;

	@Autowired
	private ExpenseRepository expenseRepo;

	@Autowired
	UserRepository userRepository;

	private ExpenseTypeBean convertToBean(ExpenseTypeEntity entity) {
		ExpenseTypeBean bean = new ExpenseTypeBean();
		bean.setExpenseTypeId(entity.getExpenseTypeId());
		bean.setExpenseTypeName(entity.getExpenseTypeName());
		bean.setDescription(entity.getDescription());
		return bean;
	}

	private String generateExpenseTypeId() {
		String prefix = "EXP-TYPE-";
		String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fullPrefix = prefix + dateStr + "-";

		List<ExpenseTypeEntity> todayTypes = expenseTypeRepo.findByExpenseTypeIdStartingWith(fullPrefix);

		int maxSeq = todayTypes.stream().map(e -> e.getExpenseTypeId().substring(fullPrefix.length())).mapToInt(seq -> {
			try {
				return Integer.parseInt(seq);
			} catch (NumberFormatException ex) {
				return 0;
			}
		}).max().orElse(0);

		return fullPrefix + String.format("%03d", maxSeq + 1);
	}

	@Override
	public ExpenseTypeBean createExpenseType(ExpenseTypeBean expenseTypeBean) {
		ExpenseTypeEntity expenseTypeEntity = new ExpenseTypeEntity();
		String uniqueId = generateExpenseTypeId();
		expenseTypeEntity.setExpenseTypeId(uniqueId);
		expenseTypeEntity.setExpenseTypeName(expenseTypeBean.getExpenseTypeName());
		expenseTypeEntity.setDescription(expenseTypeBean.getDescription());
//		expenseTypeEntity.setExpenseTypeId(expenseTypeBean.getExpenseTypeId());
//		BeanUtils.copyProperties(expenseTypeBean, expenseTypeEntity, "expenseTypeId");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		expenseTypeEntity.setCreatedBy(authentication.getName());

		expenseTypeEntity.setCreatedDate(LocalDateTime.now());
		expenseTypeEntity.setLastModifiedDate(LocalDateTime.now());
		// Get branchId from current authenticated user
		User currentUser = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("Logged-in user not found"));
		expenseTypeEntity.setBranchIds(currentUser.getBranchIds());

		ExpenseTypeEntity savedEntity = expenseTypeRepo.save(expenseTypeEntity);
		ExpenseTypeBean responseBean = new ExpenseTypeBean();
//		BeanUtils.copyProperties(savedEntity, responseBean);
		responseBean.setExpenseTypeName(savedEntity.getExpenseTypeName());
		responseBean.setExpenseTypeId(savedEntity.getExpenseTypeId());
		responseBean.setDescription(savedEntity.getDescription());
		return responseBean;
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
	public ExpenseTypeBean getExpenseTypeById(String id) {
		ExpenseTypeEntity entity = expenseTypeRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Expense Type not found"));
		return convertToBean(entity);
	}

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
	public String deleteExpenseType(String expenseTypeId) {
		if (expenseTypeId != null) {
			expenseTypeRepo.deleteById(expenseTypeId);
			return "The ExpenseType has Been deleted";
		}
		return "The expenseTypeId is null";
	}

	@Autowired
	private FilterCriteriaService<ExpenseTypeEntity> filterCriteriaService;


	@Override
	public List<ExpenseTypeBean> getExpenseTypebyfilterCriteria(List<FilterCriteriaBean> filters, int limit) {
	    try {
//	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	        String username = authentication.getName();
//
//	        User currentUser = userRepository.findByEmail(username)
//	            .orElseThrow(() -> new RuntimeException("Logged-in user not found"));
//
//	        // Add branch filtering for non-admin users
//	        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole().name())) {
//	            filters.removeIf(f -> f.getAttribute().equalsIgnoreCase("branchIds"));
//
//	            FilterCriteriaBean branchFilter = new FilterCriteriaBean();
//	            branchFilter.setAttribute("branchIds");
//	            branchFilter.setOperation(FilterOperation.AMONG);
//	            branchFilter.setValue(currentUser.getBranchIds());
//	            branchFilter.setValueType(String.class);
//
//	            filters.add(branchFilter);
//	        }

	        List<?> filteredEntities = filterCriteriaService.getListOfFilteredData(ExpenseTypeEntity.class, filters, limit);

	        return filteredEntities.stream()
	            .filter(entity -> entity instanceof ExpenseTypeEntity)
	            .map(entity -> convertToBean((ExpenseTypeEntity) entity))
	            .collect(Collectors.toList());

	    } catch (Exception e) {
	        throw new RuntimeException("Error filtering Expense Types: " + e.getMessage(), e);
	    }
	}

}
