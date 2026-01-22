package com.tms.dashboard.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.tms.dashboard.service.DashboardService;
import com.tms.dashboard.bean.DashboardBean;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardContoller {
	  @Autowired
	    private DashboardService dashboardService;
	    
	    @GetMapping
	    public ResponseEntity<List<DashboardBean>> getDashboardData() {
	        List<DashboardBean> dashboardData = dashboardService.getDashboard();
	        return ResponseEntity.ok(dashboardData);
	    }
}
