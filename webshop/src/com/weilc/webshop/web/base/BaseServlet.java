package com.weilc.webshop.web.base;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public void service(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
		try {
			//1.获得请求参数method
			String methodName = request.getParameter("method");
			// 默认方法名
			if(methodName == null) {
				methodName = "execute";
			}
			//2. 获取当前运行类，需要指定的方法
			Method method = this.getClass().getMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
			//3. 执行方法
			String jspPath = (String) method.invoke(this, request,response);
			//4. 如果子类有返回值，将请求到指定的jsp页面
			if(jspPath!=null) {
				request.getRequestDispatcher(jspPath).forward(request, response);
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String execute(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
		return null;
	}
}
