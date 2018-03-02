package com.weilc.webshop.utils;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;

public class MyBeanUtils extends BeanUtils{
	public static <T> T populate(Class<T> beanClass,Map<String,String[]> properties) {
		try {
			//1.使用反射创建实例
			T bean = beanClass.newInstance();
			//2.1 创建BeanUtils提供时间转换器
			DateConverter dateConverter = new DateConverter();
			//2.2 设置需要转换的格式
			dateConverter.setPatterns(new String[] {"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"});
			//2.3 注册转换器
			ConvertUtils.register(dateConverter, java.util.Date.class);
			//3. 封装数据
			BeanUtils.populate(bean, properties);
			
			return bean;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
