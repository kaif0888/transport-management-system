package com.tms.manifest.service;

import java.util.List;

import com.tms.filter.criteria.bean.FilterCriteriaBean;
import com.tms.manifest.bean.ManifestBean;

public interface ManifestService {
  public ManifestBean createManifest(ManifestBean manifestBean);
  public List<ManifestBean> listManifestBean();
  public  ManifestBean updateManifestBean(ManifestBean manifestBean);
  public ManifestBean getManifestById(String manifestId);
  public ManifestBean updateManifestById(String manifestId,ManifestBean manifestBean);
  public String deleteManifestById(String manifestId);
public List<ManifestBean> filterManifests(List<FilterCriteriaBean> filters, int limit);
}
