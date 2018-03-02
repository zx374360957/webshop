package com.weilc.webshop.service.impl;

import java.sql.SQLException;

import com.weilc.webshop.dao.UserDao;
import com.weilc.webshop.dao.impl.UserDaoImpl;
import com.weilc.webshop.domain.User;
import com.weilc.webshop.service.UserService;

public class UserServiceImpl implements UserService {

	private UserDao userDao = new UserDaoImpl();
	
	@Override
	public User findByUsername(String username) throws SQLException {
		// TODO Auto-generated method stub
		return userDao.findByUsername(username);
	}

	@Override
	public void regist(User user) throws SQLException {
		userDao.save(user);
	}
}
