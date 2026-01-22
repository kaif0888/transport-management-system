package com.tms.boxes.service;

import java.util.List;

import com.tms.boxes.bean.BoxBean;

public interface OrderBoxService {

	void addBoxToOrder(String orderId, String boxId);

	List<BoxBean> getBoxesForOrder(String orderId);
	void removeBoxFromOrder(String orderId, String boxId);

}
