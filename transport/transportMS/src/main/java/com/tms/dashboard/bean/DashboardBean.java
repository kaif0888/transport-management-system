package com.tms.dashboard.bean;

public class DashboardBean {
	
    private String label;
    private long count;
    private String color;
    private String icon;
    private String route;
    
    
    

	public DashboardBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
    public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}


	public String getRoute() {
		return route;
	}


	public void setRoute(String route) {
		this.route = route;
	}
    
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

}
