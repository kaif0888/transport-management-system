package com.tms.menu.service;

import java.util.List;

import com.tms.menu.bean.MenuBean;


public interface MenuService {

	public void addNewMenu(MenuBean menuBean);

	public List<MenuBean> getlistOfAllMenu();

	public MenuBean getMenuByMenuId(Long idMenu);

	public List<MenuBean> getlistOfMenuByMenuId(Iterable<Long> idMenu);

}
