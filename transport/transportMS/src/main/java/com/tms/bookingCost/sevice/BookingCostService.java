package com.tms.bookingCost.sevice;

import java.util.List;

import com.tms.bookingCost.bean.BookingCostBean;

public interface BookingCostService {
  public BookingCostBean createBookingCost(BookingCostBean bookingCostBean);
  
  public List<BookingCostBean> listBookingCost();
  
  public BookingCostBean getByBookingCostId(Long bookingCostId);
  
  public BookingCostBean updateBookingCost(BookingCostBean bookingCostBean);
  
  public String deleteBookingCost(Long bookingCostId);
  
}
