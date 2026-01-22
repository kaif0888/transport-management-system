
package com.tms.dispatch.service;

import java.util.List;
import com.tms.dispatch.bean.DispatchBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface DispatchService {

	DispatchBean createDispatch(DispatchBean dispatchBean);

	List<DispatchBean> getAllDispatches();

	DispatchBean updateDispatchById(String id, DispatchBean dispatchBean);

	List<DispatchBean> listOfDispatchByFilter(List<FilterCriteriaBean> filters, int limit);

	DispatchBean dispatchUpdateByDriverPiksUp(String dispatchId, String activeLocation);

}
