package com.tms.menu.bean;

import java.util.ArrayList;
import java.util.List;

public class MenuBean {

	private Long idMenu;
	private String menuName;
	private String menuAction;
	private String isActive;
	private MenuBean parent;
	private List<MenuBean> listOfChildMenu = new ArrayList<MenuBean>();

	public MenuBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getIdMenu() {
		return idMenu;
	}

	public void setIdMenu(Long idMenu) {
		this.idMenu = idMenu;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuAction() {
		return menuAction;
	}

	public void setMenuAction(String menuAction) {
		this.menuAction = menuAction;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public MenuBean getParent() {
		return parent;
	}

	public void setParent(MenuBean parent) {
		this.parent = parent;
	}

	public List<MenuBean> getListOfChildMenu() {
		return listOfChildMenu;
	}

	public void setListOfChildMenu(List<MenuBean> listOfChildMenu) {
		this.listOfChildMenu = listOfChildMenu;
	}

}
