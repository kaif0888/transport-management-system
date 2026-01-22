package com.tms.bookingCost.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.bookingCost.bean.BookingCostBean;
import com.tms.bookingCost.serviceImpl.BookingCostServiceImpl;
import com.tms.bookingCost.sevice.BookingCostService;
import com.tms.filter.criteria.bean.FilterRequest;





@RestController
@RequestMapping(value="/bookingCost")
public class BookingCostController {

	@Autowired
	private BookingCostServiceImpl bookingCostServiceImpl;
	
	@Autowired
	BookingCostService bookingCostService;
	
	@PostMapping("/createBookingCost")
	public BookingCostBean createBookingCost(@RequestBody BookingCostBean bookingCostBean)
	{
		return bookingCostServiceImpl.createBookingCost(bookingCostBean);
	}
	
	@GetMapping("/listBookingCost")
	public List<BookingCostBean> listBookingCost()
	{
		return bookingCostServiceImpl.listBookingCost();
	}
	
	@GetMapping("/getByIdBookingCost/{bookingCostId}")
	public BookingCostBean getByIdBookingCost(@PathVariable String bookingCostId)
	{
		return bookingCostServiceImpl.getByBookingCostId(bookingCostId);
	}
	
	@PutMapping("/updateBookingCost")
	public BookingCostBean updateBookingCost(@RequestBody BookingCostBean bookingCostBean)
	{
		return bookingCostServiceImpl.updateBookingCost(bookingCostBean);
	}
	
	@DeleteMapping("/deleteBookingCost/{bookingCostId}")
	public String deleteBookingCost(@PathVariable String bookingCostId)
	{
		return bookingCostServiceImpl.deleteBookingCost(bookingCostId);
	}
	
	 @PostMapping("/listOfBookingCostByFilter")
	   	public ResponseEntity<List<BookingCostBean>> listOfBookingCostByFilter(@RequestBody FilterRequest request) {
	   		try {
	   			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
	   			List<BookingCostBean> listOfBookingCostByFilter = bookingCostService.listOfBookingCostByFilter(request.getFilters(),
	   					limit);
	   			return ResponseEntity.ok(listOfBookingCostByFilter);
	   		} catch (Exception e) {
	   			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	   		}

	   	}
	
	
	
}
