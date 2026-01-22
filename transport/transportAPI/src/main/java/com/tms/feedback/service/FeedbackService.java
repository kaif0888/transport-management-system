package com.tms.feedback.service;

import java.util.List;
import com.tms.feedback.bean.FeedbackBean;
import com.tms.filter.criteria.bean.FilterRequest;

public interface FeedbackService {
	
	public void addCustomerFeedback(FeedbackBean feedbackBean,String username);
	
	public List<FeedbackBean> getListOfFilterFeedback( FilterRequest request);
	
	public void removeFeedbackById(Integer feedbackId);

}
