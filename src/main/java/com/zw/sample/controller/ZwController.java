package com.zw.sample.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zw.annotation.InController;
import com.zw.annotation.InQualifier;
import com.zw.annotation.InRequestMapping;
import com.zw.annotation.InRequestParameter;
import com.zw.sample.service.ZwService;

@InController
@InRequestMapping("/do")
public class ZwController {

	@InQualifier("ZwServiceImpl")
	ZwService zwService;

	@InRequestMapping("/query")
	public void query(HttpServletRequest request, HttpServletResponse response, @InRequestParameter("name") String name,
			@InRequestParameter("age") String age) {
		try {
			PrintWriter writer = response.getWriter();
			writer.write(zwService.doQuery(name, age));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
