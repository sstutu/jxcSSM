package com.bypx.controller;

import com.bypx.bean.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("login")
    public String findByUserNameAndPassword(User user) {
        //shiro对所有的失败都要求抛出异常，所以在ctroller里要try catch异常
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);//该行代码会将用户数据传递到shirorenlm类的认证方法里
            return "redirect:/view/index.html";
        } catch (Exception e) {
            e.printStackTrace();//失败，重定向到登录页
            return "redirect:/login.html";
        }
    }

    @RequestMapping("logout")
    public String logout() {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
            return "redirect:/login.html";
    }
}
