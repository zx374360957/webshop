package com.weilc.webshop.web.filter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MyRequest extends HttpServletRequestWrapper{
	//是否已经被编码，默认false，没有被编码
	private boolean encoded = false;
	public MyRequest(HttpServletRequest request) {
		super(request);
	}
	
	/**
	 * 获得指定名称的第一个参数
	 */
	public String getParameter(String name) {
		String [] all = getParameterValues(name);
		if(all == null) {
			return null;
		}
		return all[0];
	}
	
	/**
	 * 获得指定名称的所有参数
	 */
	public String[] getParameterValues(String name) {
		return getParameterMap().get(name);
	}
	
	public Map<String , String[]> getParameterMap() {
		try {
			//1. 获得原始数据
			Map<String , String[]> map = super.getParameterMap();
			//2. 如果是get请求，存放栏目
			if(!encoded) {
				if("GET".equalsIgnoreCase(super.getMethod())) {
					//遍历map，并遍历数组值
					for(Map.Entry<String, String[]> entry : map.entrySet()) {
						String[] allValue = entry.getValue();
						for(int i=0;i<allValue.length;i++) {
							String encoding = super.getCharacterEncoding();
							if(encoding == null) {
								encoding = "utf-8";
							}
							allValue[i] = new String(allValue[i].getBytes("ISO-8859-1"),encoding);
						}
					}
					encoded = true;
				}
			}
			return map;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
