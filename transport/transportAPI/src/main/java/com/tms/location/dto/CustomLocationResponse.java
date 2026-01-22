package com.tms.location.dto;

import java.util.List;

public class CustomLocationResponse {
    private String status;
    private int count;
    private List<PostOfficeResponse> data;

    public CustomLocationResponse(String status, int count, List<PostOfficeResponse> data) {
        this.status = status;
        this.count = count;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    public List<PostOfficeResponse> getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setData(List<PostOfficeResponse> data) {
        this.data = data;
    }
}
