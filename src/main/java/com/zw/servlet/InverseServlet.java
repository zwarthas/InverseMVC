package com.zw.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zw.annotation.InController;
import com.zw.annotation.InQualifier;
import com.zw.annotation.InRequestMapping;
import com.zw.annotation.InService;
import com.zw.handle.ParameterHandle;

public class InverseServlet extends HttpServlet {
/*
 * http://localhost:8080/InverseMVC/do/query?name=aaa&age=19
 * 
 */
	
	
	private List<String> classNames = new ArrayList<String>();
	private Map<String, Object> beans = new HashMap<String, Object>();
	private Map<String, Object> handleMaps = new HashMap<String, Object>();
	private final String PARAM_HANDLE = "parameterHandle";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//   do/query

		String requestURI = req.getRequestURI();
		String contextPath = req.getContextPath();
		String mapPath = requestURI.replace(contextPath, "");
		
		Object instance = beans.get("/"+mapPath.split("/")[1]);
				
		Method method = (Method) handleMaps.get(mapPath);
		
		ParameterHandle handle = (ParameterHandle) beans.get(PARAM_HANDLE);
		Object[] objects = handle.handleParam(req, resp, method, beans);
		
		try {
			method.invoke(instance, objects);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}

	@Override
	public void init() throws ServletException {
		// 1 扫描bean
		packageScan("com.zw");
		for (String name : classNames) {
			System.out.println(name);
		}
		// 2 添加bean到容器中
		addBeans();
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			System.out.println(entry.getValue());
		}
		// 3 依赖注入
		di();
		// 4 创建映射关系
		handleMapping();
		
	}

	// 1 扫描bean
	private void packageScan(String rootPackage) {
		URL url = this.getClass().getClassLoader().getResource("/" + rootPackage.replace(".", "/"));
		String rootPackageName = url.getFile();
		File rootFile = new File(rootPackageName);
		String[] rootFileList = rootFile.list();
		for (String nodeName : rootFileList) {
			File nodeFile = new File(rootPackageName + "/" + nodeName);
			if (nodeFile.isDirectory()) {
				packageScan(rootPackage + "." + nodeName);
			} else if (nodeFile.isFile()) {
				classNames.add(rootPackage + "." + nodeName);
			} else {
				continue;
			}

		}

	}

	// 2 添加bean到容器中
	private void addBeans() {

		for (String beanName : classNames) {
			beanName = beanName.replace(".class", "");
			try {
				Class<?> clazz = Class.forName(beanName);
				if (clazz.isAnnotationPresent(InController.class)) {
					InRequestMapping requestMapping = clazz.getAnnotation(InRequestMapping.class);
					String key = requestMapping.value();
					Object instance = clazz.newInstance();
					beans.put(key, instance);

				} else if (clazz.isAnnotationPresent(InService.class)) {
					InService service = clazz.getAnnotation(InService.class);
					String key = service.value();
					Object instance = clazz.newInstance();
					beans.put(key, instance);

				} else {
					continue;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// 3 依赖注入
	private void di() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			if (entry.getValue().getClass().isAnnotationPresent(InController.class)) {
				Object entryInstance = entry.getValue();
				Field[] fields = entry.getValue().getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(InQualifier.class)) {
						String key = field.getAnnotation(InQualifier.class).value();
						Object fieldInstance = beans.get(key);
						field.setAccessible(true);
						try {
							field.set(entryInstance, fieldInstance);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						continue;
					}
				}
			} else {
				continue;
			}
		}

	}

	// 4 创建映射关系
	private void handleMapping() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			if (entry.getValue().getClass().isAnnotationPresent(InController.class)) {
				Object entryInstance = entry.getValue();
				String rootPath = entryInstance.getClass().getAnnotation(InRequestMapping.class).value();

				Method[] methods = entryInstance.getClass().getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(InRequestMapping.class)) {
						String nodePath = method.getAnnotation(InRequestMapping.class).value();
						handleMaps.put(rootPath+nodePath, method);
					} else {
						continue;
					}

				}
			} else {
				continue;
			}
		}
	}

}
