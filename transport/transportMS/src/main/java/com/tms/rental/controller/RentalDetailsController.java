package com.tms.rental.controller;

//RentalDetailsController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tms.rental.bean.RentalDetailsBean;
import com.tms.rental.service.RentalDetailsService;

import java.util.List;
@RestController
@CrossOrigin(origins="*")
@RequestMapping("/rental")
public class RentalDetailsController {
@Autowired


private RentalDetailsService rentalService;

 @PostMapping("/AddRentalDetails")
 public RentalDetailsBean addRental(@RequestBody RentalDetailsBean bean) {
     return rentalService.addRental(bean);
 }

      @GetMapping("/RentalDetailList")
     public List<RentalDetailsBean> listRentals() {
     return rentalService.listRentals();
     }
      
      @PostMapping("/listOfRentalDetailByFilter")
  	public ResponseEntity<List<RentalDetailsBean>> listOfRentalDetailByFilter(@RequestBody FilterRequest request) {
  		try {
  			int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
  			List<RentalDetailsBean> listOfRentalDetailByFilter = rentalService.listOfRentalDetailByFilter(request.getFilters(),
  					limit);
  			return ResponseEntity.ok(listOfRentalDetailByFilter);
  		} catch (Exception e) {
  			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  		}

  	}

     @PutMapping("/updateRentalDetail/{id}")
     public RentalDetailsBean updateRental(@PathVariable Long id, @RequestBody RentalDetailsBean bean) {
     return rentalService.updateRental(id, bean);
     }

     @GetMapping("/track/{id}")
     public String trackRental(@PathVariable Long id) {
     return rentalService.trackRentalStatus(id);
      }
     
     @GetMapping("/getRentalById/{id}")
     public ResponseEntity<RentalDetailsBean> getRentalById(@PathVariable("id") Long rentalDetailsId) {
         try {
        	 RentalDetailsBean rental = rentalService.getRentalById(rentalDetailsId);
             return ResponseEntity.ok(rental);
         } catch (RuntimeException e) {
             // If vehicle not found or other runtime exception
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
         } catch (Exception e) {
             // For any other unexpected exception
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
         }
     }
 
 
     // ❌ Delete rental
     @DeleteMapping("/DeleteRentalDetails/{id}")
     public String deleteRental(@PathVariable Long id) {
     return rentalService.deleteRental(id);
     }
 
 
}
