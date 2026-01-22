package com.tms.dispatchTracking.service;

import java.util.List;

import com.tms.dispatchTracking.bean.DispatchTrackingBean;
import com.tms.dispatchTracking.dto.OrderTrackingResponse;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface DispatchTrackingService {
  public List<DispatchTrackingBean> createTracking(DispatchTrackingBean bean);
    public DispatchTrackingBean updateTracking(DispatchTrackingBean bean);
    public String deleteTracking(String trackingId);
	List<DispatchTrackingBean> listOfDispatchTrackingByFilter(List<FilterCriteriaBean> filters, int limit);
	public DispatchTrackingBean updateStatus(String trackingId);
	public DispatchTrackingBean getDispatchTrackingById(DispatchTrackingBean dispatchTrackingBean);
	List<DispatchTrackingBean> getOrderTrackingByDispatchId(String dispatchId);
//	List<DispatchTrackingBean> createTracking(List<DispatchTrackingBean> beans);
	public List<OrderTrackingResponse> getTrackingByOrderId(String orderId);

    
}
