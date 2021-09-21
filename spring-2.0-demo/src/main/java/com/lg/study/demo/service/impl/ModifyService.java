package com.lg.study.demo.service.impl;

import com.lg.study.demo.service.IModifyService;
import com.lg.study.framework.annotation.LgService;

/**
 * 增删改业务
 * @author lg
 *
 */
@LgService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	@Override
	public String add(String name,String addr) {
		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	@Override
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	@Override
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
