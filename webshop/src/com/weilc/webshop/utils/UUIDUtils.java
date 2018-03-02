package com.weilc.webshop.utils;

import java.util.UUID;

public class UUIDUtils {
	
	//32长度的UUID
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	//64长度的UUID
	public static String getUUID64() {
		return getUUID() + getUUID();
	}
}
