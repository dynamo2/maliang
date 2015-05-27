package com.maliang.core.ui.controller;

import javax.servlet.http.HttpServletRequest;

public class BasicController {
	public Integer getInt(HttpServletRequest request,String name){
		return this.getInt(request, name,0);
	}
	
	public Integer getInt(HttpServletRequest request,String name,int dev){
		try {
			return Integer.valueOf(request.getParameter(name));
		}catch(Exception e){
			return dev;
		}
	}
}
