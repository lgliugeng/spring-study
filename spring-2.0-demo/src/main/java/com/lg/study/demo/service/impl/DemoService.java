package com.lg.study.demo.service.impl;

import com.lg.study.demo.service.IDemoService;
import com.lg.study.framework.annotation.LgService;

/**
 * 核心业务逻辑
 */
@LgService
public class DemoService implements IDemoService {

	@Override
	public String get(String name) {
		return "My name is " + name + ",from service.";
	}

}
