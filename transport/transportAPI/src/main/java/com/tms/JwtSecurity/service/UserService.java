package com.tms.JwtSecurity.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.tms.JwtSecurity.bean.UserBean;
import com.tms.filter.criteria.bean.FilterCriteriaBean;

public interface UserService {
	public UserDetailsService userDetailsService();

	public List<UserBean> filterUsers(List<FilterCriteriaBean> filters, int limit);

	public UserBean updateUser(UserBean bean);

	public void deleteUser(String userId);

	public UserBean getUserById(String userId);
}
