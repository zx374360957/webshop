package com.weilc.webshop.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weilc.webshop.web.base.BaseServlet;

public class IndexServlet extends BaseServlet{

	private static final long serialVersionUID = 1L;
	
	public String execute(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		return "/front/index.jsp";
	}
}
