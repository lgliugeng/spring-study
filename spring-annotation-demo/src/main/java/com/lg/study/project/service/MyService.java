package com.lg.study.project.service;


import com.lg.study.project.dao.MyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lg.
 */
@Service
public class MyService {
    //@Qualifier("dao")
    //@Resource(name="dao")
    @Autowired
    private MyDao myDao;

    public void print(){
        System.out.println(myDao);
    }
}
