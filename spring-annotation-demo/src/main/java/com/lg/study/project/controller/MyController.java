package com.lg.study.project.controller;

import com.lg.study.project.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by lg.
 */
@Controller
public class MyController {
    @Autowired
    private MyService service;
}
