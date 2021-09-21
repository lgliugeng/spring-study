package com.lg.study.demo.action;

import com.lg.study.demo.service.IModifyService;
import com.lg.study.demo.service.IQueryService;
import com.lg.study.framework.annotation.LgAutowired;
import com.lg.study.framework.annotation.LgController;
import com.lg.study.framework.annotation.LgRequestMapping;
import com.lg.study.framework.annotation.LgRequestParam;
import com.lg.study.framework.webmvc.servlet.LgModelAndView;

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
	public LgModelAndView query(HttpServletRequest request, HttpServletResponse response,
								@LgRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	
	@LgRequestMapping("/add*.json")
	public LgModelAndView add(HttpServletRequest request, HttpServletResponse response,
                    @LgRequestParam("name") String name, @LgRequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		return out(response,result);
	}
	
	@LgRequestMapping("/remove.json")
	public LgModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                       @LgRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@LgRequestMapping("/edit.json")
	public LgModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                     @LgRequestParam("id") Integer id,
                     @LgRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}

	private LgModelAndView out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
