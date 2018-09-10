package com.zw.handle.resolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ParameterResolver {

	public boolean support(Class<?> clazz,Method method,int index);
	
	public Object resolve(HttpServletRequest request,HttpServletResponse response,Method method,int index);
}
