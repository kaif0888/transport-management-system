package com.tms.feedback.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tms.feedback.bean.FeedbackBean;
import com.tms.feedback.entity.FeedbackEntity;
import com.tms.feedback.repository.FeedbackRepository;
import com.tms.feedback.service.FeedbackService;
import com.tms.filter.criteria.bean.FilterRequest;
import com.tms.filter.criteria.service.FilterCriteriaService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired
	private FeedbackRepository feedbackRepo;

	@Autowired
	private FilterCriteriaService<FeedbackEntity> filterCriteriaService;
	
	@Override
	public void addCustomerFeedback(FeedbackBean feedbackBean,String username) {
		if (feedbackBean == null) {
			throw new IllegalArgumentException("Please Enter Feedback :");
		}
		FeedbackEntity feedbackenity = new FeedbackEntity();
		BeanUtils.copyProperties(feedbackBean, feedbackenity);
		feedbackenity.setUsername(username);
		feedbackRepo.save(feedbackenity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FeedbackBean> getListOfFilterFeedback( FilterRequest request) {
		Integer limit=request.getLimit();
		List<FeedbackEntity>listoffilterbean=(List<FeedbackEntity>) filterCriteriaService.
				getListOfFilteredData(FeedbackEntity.class, request.getFilters(), limit);
		if (!listoffilterbean.isEmpty()) {
			List<FeedbackBean> listofBean = new ArrayList<>();
			for (FeedbackEntity entity : listoffilterbean) {
				FeedbackBean bean = new FeedbackBean();
				bean.setfeedBackId(entity.getfeedBackId());
				bean.setFeedBackMessage(entity.getFeedBackMessage());
				bean.setFeedbackModule(entity.getFeedbackModule());
				bean.setFeedbackRating(entity.getFeedbackRating());
				bean.setFeedbackType(entity.getFeedbackType());
				bean.setUsername(entity.getUsername());
				listofBean.add(bean);
			}
			return listofBean;
		}
		throw new IllegalArgumentException("No FeedBack Found :");
	}

	@Override
	public void removeFeedbackById(Integer feedbackId) {
		if (!feedbackRepo.existsById(feedbackId)) {
			throw new IllegalArgumentException("Id Not Found :");
		}
		feedbackRepo.deleteById(feedbackId);
	}

}
