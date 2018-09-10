package com.zw.sample.service;

import com.zw.annotation.InService;

@InService("ZwServiceImpl")
public class ZwServiceImpl implements ZwService{

	@Override
	public String doQuery(String name, String age) {
		return "name : "+name+", age = "+age;
		
	}

}
