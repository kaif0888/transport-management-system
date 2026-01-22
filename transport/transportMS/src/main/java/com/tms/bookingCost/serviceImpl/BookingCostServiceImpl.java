package com.tms.bookingCost.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.bookingCost.bean.BookingCostBean;
import com.tms.bookingCost.entity.BookingCostEntity;
import com.tms.bookingCost.repository.BookingCostRepository;
import com.tms.bookingCost.sevice.BookingCostService;
import com.tms.expenseType.entity.ExpenseTypeEntity;
import com.tms.expenseType.repository.ExpenseTypeRepository;

@Service
public class BookingCostServiceImpl implements BookingCostService {

    @Autowired
    private BookingCostRepository bookingCostRepo;

    @Autowired
    private ExpenseTypeRepository expenseTypeRepo;

    @Override
    public BookingCostBean createBookingCost(BookingCostBean bookingCostBean) {
        Long expenseTypeId = bookingCostBean.getExpenseTypeId();

        if (expenseTypeId == null) {
            throw new IllegalArgumentException("The ExpenseType ID cannot be null");
        }

        ExpenseTypeEntity expenseTypeEntity = expenseTypeRepo.findById(expenseTypeId)
                .orElseThrow(() -> new RuntimeException("ExpenseType not found with ID: " + expenseTypeId));

        BookingCostEntity bookingCostEntity = new BookingCostEntity();
        BeanUtils.copyProperties(bookingCostBean, bookingCostEntity);
        bookingCostEntity.setExpenseType(expenseTypeEntity);

        BookingCostEntity savedEntity = bookingCostRepo.save(bookingCostEntity);

        BookingCostBean savedBean = new BookingCostBean();
        BeanUtils.copyProperties(savedEntity, savedBean);
        savedBean.setExpenseTypeId(expenseTypeId);

        return savedBean;
    }

    @Override
    public List<BookingCostBean> listBookingCost() {
        List<BookingCostEntity> entities = bookingCostRepo.findAll();
        List<BookingCostBean> beans = new ArrayList<>();

        for (BookingCostEntity entity : entities) {
            BookingCostBean bookingCostBean = new BookingCostBean();
            BeanUtils.copyProperties(entity, bookingCostBean);
            if (entity.getExpenseType() != null) {
                bookingCostBean.setExpenseTypeId(entity.getExpenseType().getExpenseTypeId());
            }
            beans.add(bookingCostBean);
        }

        return beans;
    }

    @Override
    public BookingCostBean getByBookingCostId(Long bookingCostId) {
        Optional<BookingCostEntity> opt = bookingCostRepo.findById(bookingCostId);
        if (opt.isPresent()) {
            BookingCostEntity entity = opt.get();
            BookingCostBean bean = new BookingCostBean();
            BeanUtils.copyProperties(entity, bean);
            if (entity.getExpenseType() != null) {
                bean.setExpenseTypeId(entity.getExpenseType().getExpenseTypeId());
            }
            return bean;
        }
        return null;
    }

    @Override
    public BookingCostBean updateBookingCost(BookingCostBean bookingCostBean) {
        Optional<BookingCostEntity> opt = bookingCostRepo.findById(bookingCostBean.getBookingCostId());
        if (opt.isPresent()) {
            BookingCostEntity entity = opt.get();

            entity.setAmount(bookingCostBean.getAmount());
            entity.setDescription(bookingCostBean.getDescription());
            entity.setDate(bookingCostBean.getDate());

            // Optionally update expenseType if changed
            if (bookingCostBean.getExpenseTypeId() != null &&
                (entity.getExpenseType() == null || 
                 !bookingCostBean.getExpenseTypeId().equals(entity.getExpenseType().getExpenseTypeId()))) {
                ExpenseTypeEntity expenseType = expenseTypeRepo.findById(bookingCostBean.getExpenseTypeId())
                        .orElseThrow(() -> new RuntimeException("ExpenseType not found with ID: " + bookingCostBean.getExpenseTypeId()));
                entity.setExpenseType(expenseType);
            }

            BookingCostEntity savedEntity = bookingCostRepo.save(entity);

            BookingCostBean bean = new BookingCostBean();
            BeanUtils.copyProperties(savedEntity, bean);
            if (savedEntity.getExpenseType() != null) {
                bean.setExpenseTypeId(savedEntity.getExpenseType().getExpenseTypeId());
            }
            return bean;
        }
        return null;
    }

    @Override
    public String deleteBookingCost(Long bookingCostId) {
        if (bookingCostId != null && bookingCostRepo.existsById(bookingCostId)) {
            bookingCostRepo.deleteById(bookingCostId);
            return "The BookingCost has been deleted.";
        }
        return "The BookingCost was not found or could not be deleted.";
    }
}
