package com.tms.boxes.service;

import java.util.List;
import java.util.Map;

import com.tms.boxes.bean.BoxBean;
import com.tms.product.entity.ProductEntity;

public interface BoxService {

	BoxBean createBox(BoxBean boxBean);

	BoxBean getBoxById(String boxId);

	List<ProductEntity> getAllBoxes(Map<String, Object> filters);

	BoxBean addProductToBox(String boxId, String productId, Integer quantity);

	BoxBean removeProductFromBox(String boxId, String productId);

}
