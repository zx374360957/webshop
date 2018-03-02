package com.weilc.webshop.dao;

import java.sql.SQLException;

import com.weilc.webshop.domain.User;

public interface UserDao {

	/**
	 * 通过用户名寻找用户
	 * author：weilc
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	User findByUsername(String username) throws SQLException;

	/**
	 * 保存用户
	 * author：weilc
	 * @param user
	 * @throws SQLException 
	 */
	void save(User user) throws SQLException;
	
}
