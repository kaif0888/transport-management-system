package com.tms.feedback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

@Table(name = "feedback")
@Entity
public class FeedbackEntity 
{

	@Id
	@TableGenerator(name = "HIBERNATE_SEQUENCE",allocationSize = 10)
	@GeneratedValue(generator = "HIBERNATE_SEQUENCE",strategy = GenerationType.TABLE)
	private Integer feedbackId;
	
	
	@Column(name = "FEEDBACK_MESSAGE")
	private String feedBackMessage;
	
	@Column(name="FEEDBACK_MODULE")
	private String feedbackModule;
	
	@Column(name="FEEDBACK_RATING")
	private Double feedbackRating;
	
	@Column(name = "FEEDBACK_TYPE")
	private String feedbackType;
	
	@Column(name="USERNAME")
	private String username;
	
	
	public FeedbackEntity() {
		
	}

	public FeedbackEntity(String feedBackMessage,String feedbackModule,Double feedbackRating, String feedbackType, 
			String username) {
		this.feedBackMessage = feedBackMessage;
		this.feedbackModule = feedbackModule;
		this.feedbackRating = feedbackRating;
		this.feedbackType = feedbackType;
		
		
		
	}
	
	public Integer getfeedBackId() {
		return feedbackId;
	}
	
	public void setfeedBackId(Integer feedbackId) {
		this.feedbackId=feedbackId;
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
	
	
	
	

