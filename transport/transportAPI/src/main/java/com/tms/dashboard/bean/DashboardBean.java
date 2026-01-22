package com.tms.dashboard.bean;

public class DashboardBean {
	
    private String label;
    private String count;
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
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

}
