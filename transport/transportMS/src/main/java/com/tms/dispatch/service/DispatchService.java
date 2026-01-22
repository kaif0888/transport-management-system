
package com.tms.dispatch.service;

import java.util.List;
import com.tms.dispatch.bean.DispatchBean;
import com.tms.dispatch.entity.DispatchEntity;
import com.tms.filter.criteria.bean.FilterCriteriaBean;






public interface DispatchService {
	
	
	
	    DispatchBean createDispatch(DispatchBean dispatchBean);

	    List<DispatchBean> getAllDispatches();

	    
		DispatchBean updateDispatchById(Long id, DispatchBean dispatchBean);
		
	  }
