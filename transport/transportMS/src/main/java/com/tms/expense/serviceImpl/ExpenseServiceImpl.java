package com.tms.expense.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.drivertms.been.DriverBeen;
import com.tms.drivertms.entity.DriverEntity;
import com.tms.expense.bean.ExpenseBean;
import com.tms.expense.entity.ExpenseEntity;
import com.tms.expense.repository.ExpenseRepository;
import com.tms.expense.service.ExpenseService;
import com.tms.vehicle.entity.VehicleEntity;
import com.tms.vehicle.repository.VehicleRepository;

@Service
public class ExpenseServiceImpl implements ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Override
	public ExpenseBean addExpense(ExpenseBean dto) {
		ExpenseEntity entity = new ExpenseEntity();
		BeanUtils.copyProperties(dto, entity);

		VehicleEntity vehicle = vehicleRepository.findById(dto.getVehicleId())
				.orElseThrow(() -> new RuntimeException("Vehicle not found"));

		entity.setVehicle(vehicle);

		ExpenseEntity savedEntity = expenseRepository.save(entity);

		ExpenseBean response = new ExpenseBean();
		BeanUtils.copyProperties(savedEntity, response);
		response.setVehicleId(vehicle.getVehicleId());

		return response;
	}

	@Override
	public List<ExpenseBean> listAllExpenses() {
		return expenseRepository.findAll().stream().map(entity -> {
			ExpenseBean dto = new ExpenseBean();
			BeanUtils.copyProperties(entity, dto);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public ExpenseBean updateExpense(Long expenseId, ExpenseBean dto) {
		ExpenseEntity entity = expenseRepository.findById(expenseId)
				.orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));

		BeanUtils.copyProperties(dto, entity);
		ExpenseEntity updatedEntity = expenseRepository.save(entity);

		ExpenseBean response = new ExpenseBean();
		BeanUtils.copyProperties(updatedEntity, response);
		return response;
	}

	@Override
	public void deleteExpense(Long expenseId) {
		if (!expenseRepository.existsById(expenseId)) {
			throw new RuntimeException("Expense not found with ID: " + expenseId);
		}
		expenseRepository.deleteById(expenseId);
	}
}
