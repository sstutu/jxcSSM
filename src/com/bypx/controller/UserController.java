package com.bypx.controller;

import com.bypx.bean.PageBean;
import com.bypx.bean.ResultInfo;
import com.bypx.bean.User;
import com.bypx.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {
    @Resource
    UserService userService;
    //查询所有的用户 组成Table
    @RequestMapping("queryUserPageBean")
    @ResponseBody
    public PageBean queryUserPageBean(String name, Integer currentPage, Integer pageSize){
        //先对pageSize和currentPage进行默认值设定
        if(currentPage==null||currentPage<1){
            currentPage = 1;
        }else {
            //按传入的值
        }
        if (pageSize==null){
            pageSize = 5;
        }else {
            //按传入的值
        }
        return userService.queryUserPageBean(name,currentPage,pageSize);
    };

    /**
     * 本方法实现User管理的新增和编辑
     * @param user
     * @return
     */
    @RequestMapping("edit")
    @ResponseBody
    public ResultInfo insertOrUpdate(User user){
        return userService.insertOrUpdate(user);
    }

    /**
     * 本方法实现删除
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(String id){
        return userService.delete(id);
    }
    @RequestMapping("associationRole")
    @ResponseBody
    public ResultInfo associationRole(String userId,@RequestParam("roleArr[]") String[] roleArr){
        return userService.associationRole(userId,roleArr);
    }

}
