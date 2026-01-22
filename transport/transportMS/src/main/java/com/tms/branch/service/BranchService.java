package com.tms.branch.service;

import com.tms.branch.bean.BranchBean;
import com.tms.branch.bean.BranchLocationResponse;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

import java.util.List;

public interface BranchService {
    BranchBean createBranch(BranchBean branchBean);
    BranchBean getBranchById(Long branchId);
    List<BranchBean> getBranchesByLocationId(Long locationId);
    void deleteBranch(Long branchId);
	List<BranchBean> getAllBranches();
	BranchBean updateBranch(BranchBean branchBean);
	List<BranchBean> filterBranchs(List<FilterCriteriaBean> filterCriteriaList, int limit);
	BranchLocationResponse getAllBranchesAndLocations();

}
