package com.tms.manifest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
