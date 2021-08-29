package com.lg.study.demo.action;

import com.lg.study.demo.service.IDemoService;
import com.lg.study.framework.annotation.LgAutowired;
import com.lg.study.framework.annotation.LgController;
import com.lg.study.framework.annotation.LgRequestMapping;
import com.lg.study.framework.annotation.LgRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//虽然，用法一样，但是没有功能
@LgController
@LgRequestMapping("/demo")
public class DemoAction {

  	@LgAutowired
	private IDemoService demoService;

	@LgRequestMapping("/query")
	public void query(HttpServletRequest req, HttpServletResponse resp,
                      @LgRequestParam("name") String name){
		String result2 = demoService.get(name);
		String result = "My name is " + name;
		try {
			resp.getWriter().write(result+","+result2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@LgRequestMapping("/add")
	public void add(HttpServletRequest req, HttpServletResponse resp,
					@LgRequestParam("a") Integer a, @LgRequestParam("b") Integer b){
		try {
			resp.getWriter().write(a + "+" + b + "=" + (a + b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@LgRequestMapping("/sub")
	public void sub(HttpServletRequest req, HttpServletResponse resp,
                    @LgRequestParam("a") Double a, @LgRequestParam("b") Double b){
		try {
			resp.getWriter().write(a + "-" + b + "=" + (a - b));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@LgRequestMapping("/remove")
	public String  remove(@LgRequestParam("id") Integer id){
		return "" + id;
	}

}
