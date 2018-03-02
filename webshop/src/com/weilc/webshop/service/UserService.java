package com.weilc.webshop.service;

import java.sql.SQLException;

import com.weilc.webshop.domain.User;

public interface UserService {

	/**
	 * 通过用户名查询
	 * author：weilc
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	User findByUsername(String username) throws SQLException;

	/**
	 * 注册
	 * author：weilc
	 * @param user
	 * @throws SQLException
	 */
	void regist(User user) throws SQLException;

}
