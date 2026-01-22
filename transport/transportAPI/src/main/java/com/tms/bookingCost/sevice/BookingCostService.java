package com.tms.bookingCost.sevice;

import java.util.List;

import com.tms.bookingCost.bean.BookingCostBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface BookingCostService {
  public BookingCostBean createBookingCost(BookingCostBean bookingCostBean);
  
  public List<BookingCostBean> listBookingCost();
  
  public BookingCostBean getByBookingCostId(String bookingCostId);
  
  public BookingCostBean updateBookingCost(BookingCostBean bookingCostBean);
  
  public String deleteBookingCost(String bookingCostId);

public List<BookingCostBean> listOfBookingCostByFilter(List<FilterCriteriaBean> filters, int limit);
  
}
