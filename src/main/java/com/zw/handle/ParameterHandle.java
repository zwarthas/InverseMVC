package com.zw.handle;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zw.annotation.InService;
import com.zw.handle.resolver.ParameterResolver;

@InService("parameterHandle")
public class ParameterHandle {
	public Object[] handleParam(HttpServletRequest request,HttpServletResponse response,Method method,Map<String,Object> beans) {
		
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] objects =new Object[parameterTypes.length];
		int index = 0;
		for(Class<?> parameterType:parameterTypes) {
			
			for(Map.Entry<String,Object> entry:getResolvers(beans,ParameterResolver.class).entrySet()) {
				ParameterResolver resolver = (ParameterResolver) entry.getValue();
				if(resolver.support(parameterType, method, index)) {
					Object ret = resolver.resolve(request, response, method, index);
					objects[index] = ret;
				}else {
					continue;
				}
			}
			index+=1;
		}
		return objects;

	}
	
	private Map<String,Object> getResolvers(Map<String,Object> beans,Class<?> clazz){
		Map<String,Object> resolvers = new HashMap<String, Object>();
		for(Map.Entry<String, Object> entry:beans.entrySet()) {
			Object instance = entry.getValue();
			Class<?>[] interfaces = instance.getClass().getInterfaces();
			if(interfaces!=null && interfaces.length>0) {
				for(Class<?> objectInterface:interfaces) {
					if(clazz.isAssignableFrom(objectInterface)) {
						resolvers.put(entry.getKey(), instance);
					}else {
						continue;
					}
				}
			}else {
				continue;
			}
		}
		return resolvers;
	}
	
	
}
