package com.lg.study.demo.service.impl;

import com.lg.study.demo.service.IQueryService;
import com.lg.study.framework.annotation.LgService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 * @author lg
 *
 */
@LgService
public class QueryService implements IQueryService {

	/**
	 * 查询
	 */
	@Override
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		return json;
	}

}
