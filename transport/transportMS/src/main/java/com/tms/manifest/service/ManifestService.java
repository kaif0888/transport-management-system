package com.tms.manifest.service;

import java.util.List;

import com.tms.manifest.bean.ManifestBean;

public interface ManifestService {
  public ManifestBean createManifest(ManifestBean manifestBean);
  public List<ManifestBean> listManifestBean();
  public  ManifestBean updateManifestBean(ManifestBean manifestBean);
  
}
