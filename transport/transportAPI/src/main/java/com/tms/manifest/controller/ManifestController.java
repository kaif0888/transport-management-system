package com.tms.manifest.controller;

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

import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.manifest.bean.ManifestBean;
import com.tms.manifest.service.ManifestService;

@RestController
@RequestMapping("/manifest")
public class ManifestController {
	
	@Autowired
	private ManifestService manifestService;
    
	@PostMapping("/createManifest")
	public ManifestBean createManifest(@RequestBody ManifestBean manifestBean)
	{
		return manifestService.createManifest(manifestBean);
	}
	
	@GetMapping("/listManifest")
	public List<ManifestBean> listManifestBean()
	{
		return manifestService.listManifestBean();
	}
	
	@PutMapping("/updateManifest")
	public ManifestBean updateManifestBean(@RequestBody ManifestBean manifestBean)
	{
		return manifestService.updateManifestBean(manifestBean);
	}
	
	@PutMapping("/updateManifestById/{manifestId}")
	public ManifestBean updateManifestBeanById(@PathVariable String manifestId,@RequestBody ManifestBean manifestBean)
	{
		return manifestService.updateManifestById(manifestId, manifestBean);
	}
	
	@GetMapping("/getManifestById/{manifestId}")
	public ManifestBean getManifestById(@PathVariable String manifestId)
	{
		return manifestService.getManifestById(manifestId);
	}
	
	@DeleteMapping("deleteById/{manifestId}")
	public String deleteManifest(@PathVariable String manifestId)
	{
		return manifestService.deleteManifestById(manifestId);
	}
	
	@PostMapping("/filteredManifests")
	public ResponseEntity<List<ManifestBean>> filterManifests(@RequestBody FilterRequest request) {
	    try {
	        int limit = request.getLimit() != null ? request.getLimit() : 100; // default to 100
	        List<ManifestBean> filteredLocations = manifestService.filterManifests(request.getFilters(), limit);
	        return ResponseEntity.ok(filteredLocations);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}
}
