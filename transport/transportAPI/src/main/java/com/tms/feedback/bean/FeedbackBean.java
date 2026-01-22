package com.tms.feedback.bean;

public class FeedbackBean 
{

	private Integer feedBackId;
	private String feedBackMessage;
	private String feedbackModule;
	private Double feedbackRating;
	private String feedbackType;
	private String username;
	
	
	
	public FeedbackBean() {
		
	}
	

	public FeedbackBean(String feedBackMessage,String feedbackModule,Double feedbackRating,String feedbackType ,String username) {
		super();
		this.feedbackType = feedbackType;
		this.feedbackModule = feedbackModule;
		this.feedBackMessage = feedBackMessage;
		this.feedbackRating = feedbackRating;
	}

	public Integer getfeedBackId() {
		return feedBackId;
	}
	
	public void setfeedBackId(Integer feedbackId) {
		this.feedBackId=feedbackId;
	}
	public String getFeedBackMessage() {
		return feedBackMessage;
	}

	public void setFeedBackMessage(String feedBackMessage) {
		this.feedBackMessage = feedBackMessage;
	}
	
	public String getFeedbackModule() {
		return feedbackModule;
	}

	public void setFeedbackModule(String feedbackModule) {
		this.feedbackModule = feedbackModule;
	}
	
	
	public Double getFeedbackRating() {
		return feedbackRating;
	}

	public void setFeedbackRating(Double feedbackRating) {
		this.feedbackRating = feedbackRating;
	}
	
	
	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	




	
	
}
