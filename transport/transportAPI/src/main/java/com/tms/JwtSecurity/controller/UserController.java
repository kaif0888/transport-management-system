package com.tms.JwtSecurity.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tms.JwtSecurity.bean.UserBean;
import com.tms.JwtSecurity.service.UserService;
import com.tms.filter.criteria.bean.FilterRequest;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;
	

	
    @PostMapping("/filterUsers")
    public ResponseEntity<List<UserBean>> filterUsers(@RequestBody FilterRequest request) {
        try {
            int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
            List<UserBean> filterUsers = userService.filterUsers(request.getFilters(), limit);
            return ResponseEntity.ok(filterUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @PostMapping("/update/{userId}")
    public ResponseEntity<UserBean> updateUser(@PathVariable String userId, @RequestBody UserBean bean) {
        try {
            bean.setUserId(userId); 
            UserBean updated = userService.updateUser(bean);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<UserBean> getUserById(@PathVariable String userId) {
        try {
            UserBean user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }


}
