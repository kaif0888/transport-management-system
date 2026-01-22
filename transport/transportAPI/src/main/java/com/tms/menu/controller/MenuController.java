package com.tms.menu.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tms.constant.Constants;
import com.tms.menu.bean.MenuBean;
import com.tms.menu.service.MenuService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;




@CrossOrigin(origins = Constants.CROSS_ORIGIN)
@RestController
@RequestMapping("/menu")
public class MenuController {

	@Autowired
	MenuService menuService;

	@RequestMapping(value = "addnewmenu", method = RequestMethod.POST)
	public void addNewMenu(HttpServletRequest req, HttpServletResponse res, @RequestBody MenuBean menuBean) {
//		String sessionUserId = Util.getHeader(req, res);
//		if (sessionUserId != null && !"".equals(sessionUserId)) {
//			GlobalUserLoginBean.setGLOBAL_USER_LOGIN(sessionUserId);
//		} else {
//			GlobalUserLoginBean.setGLOBAL_USER_LOGIN("UNKNOWN");
//		}
		menuService.addNewMenu(menuBean);

	}

	@RequestMapping(value = "addnewmenu/{parentId}", method = RequestMethod.POST)
	public void addNewMenu(HttpServletRequest req, HttpServletResponse res, @RequestBody MenuBean menuBean,
			@PathVariable("parentId") String parentId) {
		if ( !"".equals(parentId)) {
			MenuBean parentMenuBean = new MenuBean();
			parentMenuBean.setIdMenu(parentId);
			menuBean.setParent(parentMenuBean);
		}
		menuService.addNewMenu(menuBean);

	}

	@RequestMapping(value = "getlistOfAllMenu", method = RequestMethod.GET)
	public List<MenuBean> getlistOfAllMenu(HttpServletRequest req, HttpServletResponse res) {

		List<MenuBean> listOfAllMenu = menuService.getlistOfAllMenu();

		return listOfAllMenu;
	}

	@RequestMapping(value = "getlistOfMenuByMenuId/{menuId}", method = RequestMethod.GET)
	public List<MenuBean> getlistOfMenuByMenuId(HttpServletRequest req, HttpServletResponse res,
			@PathVariable("MenuId") Iterable<String> idMenu) {
		return menuService.getlistOfMenuByMenuId(idMenu);

	}

	@RequestMapping(value = "getMenuByMenuId/{menuId}", method = RequestMethod.GET)
	public MenuBean getMenuByMenuId(HttpServletRequest req, HttpServletResponse res,
			@PathVariable("menuId") String idMenu) {
		return menuService.getMenuByMenuId(idMenu);
	}

}
