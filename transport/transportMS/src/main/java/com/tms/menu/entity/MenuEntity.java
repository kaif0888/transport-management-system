package com.tms.menu.entity;

import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "WMS_MENU")
public class MenuEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID_MENU")
	private Long idMenu;

	@Column(name = "NAME_MENU")
	private String menuName;

	@Column(name = "ACTION_MENU")
	private String menuAction;

	@Column(name = "IS_ACTIVE")
	private String isActive;

	@Column(name = "DESCRIPTION_MENU")
	private String menuDescription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private MenuEntity parent;

	@JsonIgnore
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<MenuEntity> listOfChildMenu = new ArrayList<MenuEntity>();

	public MenuEntity() {
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

	public String getMenuDescription() {
		return menuDescription;
	}

	public void setMenuDescription(String menuDescription) {
		this.menuDescription = menuDescription;
	}

	public MenuEntity getParent() {
		return parent;
	}

	public void setParent(MenuEntity parent) {
		this.parent = parent;
	}

	public List<MenuEntity> getListOfChildMenu() {
		return listOfChildMenu;
	}

	public void setListOfChildMenu(List<MenuEntity> listOfChildMenu) {
		this.listOfChildMenu = listOfChildMenu;
	}

}
