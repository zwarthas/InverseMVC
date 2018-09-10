package com.zw.handle.resolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zw.annotation.InService;

@InService("requestResolverImpl")
public class RequestResolverImpl implements ParameterResolver {

	@Override
	public boolean support(Class<?> clazz,Method method,int index) {
		return clazz.isAssignableFrom(HttpServletRequest.class);
	}

	@Override
	public Object resolve(HttpServletRequest request, HttpServletResponse response, Method method, int index) {
		// TODO Auto-generated method stub
		return request;
	}

}
