package com.weilc.webshop.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weilc.webshop.domain.User;
import com.weilc.webshop.service.UserService;
import com.weilc.webshop.service.impl.UserServiceImpl;
import com.weilc.webshop.utils.MyBeanUtils;
import com.weilc.webshop.utils.UUIDUtils;
import com.weilc.webshop.web.base.BaseServlet;

public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 测试方法
	 * author：weilc
	 * @param request
	 * @param response
	 */
	public void findAll(HttpServletRequest request,HttpServletResponse response) {
		System.out.println("findAll");
	}
	
	/**
	 * 跳转到注册页面
	 * author：weilc
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String registerUI(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		return "/front/register.jsp";
	}
	
	/**
	 * 验证用户名是否存在
	 * author：weilc
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void checkUsername(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException, SQLException{
		//接收文本框的值
		String username = request.getParameter("username");
		//调用业务层查询
		UserService userService = new UserServiceImpl();
		User existUser = userService.findByUsername(username);
		//判断
		if(existUser == null) {
			//用户名没有使用
			response.getWriter().println(1);
		} else {
			//用户名已经被使用
			response.getWriter().println(2);
		}
	}
	
	public String regist(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//1. 获得数据并封装
		User user = MyBeanUtils.populate(User.class, request.getParameterMap());
		//1.1 处理服务器自动生成
		user.setUid(UUIDUtils.getUUID());
		user.setCode(UUIDUtils.getUUID64()); //激活码
		user.setState(0); //0:未激活
		
		//2. 处理
		UserService userService = new UserServiceImpl();
		userService.regist(user);
		
		//3. 成功提示
		request.setAttribute("msg", "注册成功，请邮件激活后登录");
		
		//4. 注册成功登录
		return "/front/login.jsp";
	}
}
