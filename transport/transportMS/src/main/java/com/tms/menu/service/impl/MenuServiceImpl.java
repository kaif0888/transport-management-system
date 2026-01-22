package com.tms.menu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tms.menu.bean.MenuBean;
import com.tms.menu.entity.MenuEntity;
import com.tms.menu.repository.MenuRepository;
import com.tms.menu.service.MenuService;


@Service
public class MenuServiceImpl implements MenuService {

	public static Logger logger = LogManager.getLogger(MenuServiceImpl.class);

	@Autowired
	MenuRepository menuRepository;

	@Override
	public void addNewMenu(MenuBean menuBean) {
		MenuEntity meuEntity = null;
		if (menuBean != null & !"".equals(menuBean)) {
			meuEntity = new MenuEntity();
			BeanUtils.copyProperties(menuBean, meuEntity);
			if (menuBean.getParent() != null && menuBean.getParent().getIdMenu() != 0) {
				MenuEntity parentMenuEntity = new MenuEntity();
				parentMenuEntity.setIdMenu(menuBean.getParent().getIdMenu());
				meuEntity.setParent(parentMenuEntity);
			}
			menuRepository.save(meuEntity);
		}
	}

	@Override
	public List<MenuBean> getlistOfAllMenu() {
		List<MenuBean> listOfMenuBean = null;
		List<MenuEntity> listOfMenuByNullParent = menuRepository.findByParentIsNull();

		if (listOfMenuByNullParent != null && !listOfMenuByNullParent.isEmpty()) {
			listOfMenuBean = new ArrayList<>();
			for (MenuEntity menuEntity : listOfMenuByNullParent) {
				MenuBean menuBean = new MenuBean();
				BeanUtils.copyProperties(menuEntity, menuBean);
				if (menuEntity != null) {
					if (menuEntity.getListOfChildMenu().size() > 0) {
						menuBean.setListOfChildMenu(childMenu(menuEntity));
					}
				}

				listOfMenuBean.add(menuBean);
			}
			return listOfMenuBean;
		}

		return listOfMenuBean;
	}

	private List<MenuBean> childMenu(MenuEntity menuEntity) {

		List<MenuBean> listOfChildMenuBean = null;
		if (menuEntity.getListOfChildMenu() != null) {
			listOfChildMenuBean = new ArrayList<>();
			for (MenuEntity mE : menuEntity.getListOfChildMenu()) {
				MenuBean mB = new MenuBean();
				BeanUtils.copyProperties(mE, mB);
				if (mE.getParent() != null && mE.getParent().getIdMenu() != 0) {
					MenuBean menuBeanParent = new MenuBean();
					menuBeanParent.setIdMenu(mE.getParent().getIdMenu());
					menuBeanParent.setMenuName(mE.getParent().getMenuName());
					mB.setParent(menuBeanParent);
				}
				if (mE != null) {
					if (mE.getListOfChildMenu().size() > 0) {
						mB.setListOfChildMenu(childMenu(mE));
					}
				}
				listOfChildMenuBean.add(mB);
			}
			return listOfChildMenuBean;
		}
		return listOfChildMenuBean;
	}

	@Override
	public MenuBean getMenuByMenuId(Long idMenu) {
		MenuBean menuBean = null;
		Optional<MenuEntity> menuEntity = menuRepository.findById(idMenu);
		if (menuEntity != null) {
			menuBean = new MenuBean();
			BeanUtils.copyProperties(menuEntity.get(), menuBean);
		}
		return menuBean;
	}

	@Override
	public List<MenuBean> getlistOfMenuByMenuId(Iterable<Long> idMenu) {
		List<MenuEntity> listOfMenuEntity = (List<MenuEntity>) menuRepository.findAllById(idMenu);
		List<MenuBean> listOfMenuBean = null;
		if (listOfMenuEntity != null && !listOfMenuEntity.isEmpty()) {
			listOfMenuBean = new ArrayList<MenuBean>();
			for (MenuEntity menuEntity : listOfMenuEntity) {
				MenuBean menuBean = new MenuBean();
				BeanUtils.copyProperties(menuEntity, menuBean);
				listOfMenuBean.add(menuBean);
			}
		}
		return listOfMenuBean;
	}

}
