package com.lg.study.demo.action;

import com.lg.study.demo.service.IModifyService;
import com.lg.study.demo.service.IQueryService;
import com.lg.study.framework.annotation.LgAutowired;
import com.lg.study.framework.annotation.LgController;
import com.lg.study.framework.annotation.LgRequestMapping;
import com.lg.study.framework.annotation.LgRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 公布接口url
 * @author lg
 *
 */
@LgController
@LgRequestMapping("/web")
public class MyAction {

	@LgAutowired
	IQueryService queryService;
	@LgAutowired
	private IModifyService modifyService;

	@LgRequestMapping("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
                      @LgRequestParam("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@LgRequestMapping("/add*.json")
	public void add(HttpServletRequest request, HttpServletResponse response,
                    @LgRequestParam("name") String name, @LgRequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@LgRequestMapping("/remove.json")
	public void remove(HttpServletRequest request, HttpServletResponse response,
                       @LgRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@LgRequestMapping("/edit.json")
	public void edit(HttpServletRequest request, HttpServletResponse response,
                     @LgRequestParam("id") Integer id,
                     @LgRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}
	
	
	
	private void out(HttpServletResponse resp, String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
