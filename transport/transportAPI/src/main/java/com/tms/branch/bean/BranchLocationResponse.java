package com.tms.branch.bean;

import java.util.List;

import com.tms.location.bean.LocationBean;

public class BranchLocationResponse {
    private List<BranchBean> branches;
    private List<LocationBean> locations;

    // Getters and Setters
    public List<BranchBean> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchBean> branches) {
        this.branches = branches;
    }

    public List<LocationBean> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationBean> locations) {
        this.locations = locations;
    }
}
