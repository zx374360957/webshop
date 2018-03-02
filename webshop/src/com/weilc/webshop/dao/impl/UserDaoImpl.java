package com.weilc.webshop.dao.impl;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.weilc.webshop.dao.UserDao;
import com.weilc.webshop.domain.User;
import com.weilc.webshop.utils.DataSourceUtils;

public class UserDaoImpl implements UserDao {
	/**
	 * 根据用户名查找用户 
	 */
	@Override
	public User findByUsername(String username) throws SQLException {
		QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from user where user name=?";
		User existUser = queryRunner.query(sql, new BeanHandler<User>(User.class));
		return existUser;
	}

	@Override
	public void save(User user) throws SQLException {
		QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "insert into user(uid,username,password,name,email,telephone,birthday,sex,state,code) values (?,?,?,?,?,?,?,?,?,?)";
		Object[] params = {user.getUid(),user.getUsername(),user.getPassword(),user.getName(),user.getEmail(),user.getTelephone(),user.getBirthday(),user.getSex(),user.getState(),user.getCode()};
		queryRunner.update(sql,params);
	}
	
}
