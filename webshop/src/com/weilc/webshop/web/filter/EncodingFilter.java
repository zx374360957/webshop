package com.weilc.webshop.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EncodingFilter implements Filter{
	public void init(FilterConfig fConfig) throws ServletException{
		
	}
	
	public void doFilter(ServletRequest req,ServletResponse resp,FilterChain chain) throws IOException,ServletException{
		//1. 强转
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		//2. 设置编码
		request.setCharacterEncoding("utf-8");
		//3. 创建自定义request
		MyRequest myRequest = new MyRequest(request);
		//4. 放行，使用自定义request
		chain.doFilter(myRequest, response);
	}
	
	public void destroy() {
		
	}
}
