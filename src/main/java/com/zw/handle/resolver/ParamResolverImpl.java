package com.zw.handle.resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zw.annotation.InRequestParameter;
import com.zw.annotation.InService;

@InService("paramResolverImpl")
public class ParamResolverImpl implements ParameterResolver {

	@Override
	public boolean support(Class<?> clazz,Method method,int index) {
		Annotation[][] anns = method.getParameterAnnotations();
		Annotation[] annotations = anns[index];
		for(Annotation annotation:annotations) {
			if(InRequestParameter.class.isAssignableFrom(annotation.getClass())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object resolve(HttpServletRequest request, HttpServletResponse response, Method method, int index) {
		Annotation[][] anns = method.getParameterAnnotations();
		Annotation[] annotations = anns[index];
		for(Annotation annotation:annotations) {
			if(InRequestParameter.class.isAssignableFrom(annotation.getClass())) {
				InRequestParameter requestParameter = (InRequestParameter) annotation;
				String key = requestParameter.value();
				String value = request.getParameter(key);
				
				return value;
				
			}
		}
		
		
		return null;
	}

	

}
