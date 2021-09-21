package com.lg.study.demo.action;

import com.lg.study.demo.service.IQueryService;
import com.lg.study.framework.annotation.LgAutowired;
import com.lg.study.framework.annotation.LgController;
import com.lg.study.framework.annotation.LgRequestMapping;
import com.lg.study.framework.annotation.LgRequestParam;
import com.lg.study.framework.webmvc.servlet.LgModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author lg
 *
 */
@LgController
@LgRequestMapping("/")
public class PageAction {

    @LgAutowired
    IQueryService queryService;

    @LgRequestMapping("/first.html")
    public LgModelAndView query(@LgRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new LgModelAndView("first.html",model);
    }

}
