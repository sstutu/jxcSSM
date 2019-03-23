package com.bypx.controller;

import com.bypx.bean.Menu;
import com.bypx.bean.ResultInfo;
import com.bypx.bean.Role;
import com.bypx.bean.User;
import com.bypx.service.JxcMenuService;
import com.bypx.service.JxcRoleService;
import com.bypx.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

public class ShiroRealm extends AuthorizingRealm {
    @Resource
    JxcMenuService jxcMenuService;
    @Resource
    UserService userService;
    @Resource
    JxcRoleService jxcRoleService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User sessionUser = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
        User user = userService.queryUserById(sessionUser.getId());
        List<Menu> menuList = user.getMenuList();
        for (Menu menu : menuList) {
            System.out.println("角色"+menu.getFlag());
            sai.addRole(menu.getFlag());
        }
        return sai;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        User user = userService.queryUserByUserName(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        try {
            //查询到用户后，关联查询该用户的菜单，存入session中 供左侧菜单的控制层jxcMenuController响应左侧菜单的内容
            List<Menu> menulist = jxcMenuService.loadSideMenu(user.getId());
            menulist = Menu.formatMenu(menulist);
            //通过用户id获得用户的角色，传递前台 变过遍历去除角色flag，做判断显示不同的按钮
            List<Role> roleList= (List<Role>) jxcRoleService.getByUserId(user.getId()).getData();
            //保存到session
            SecurityUtils.getSubject().getSession().setAttribute("user", user);
            SecurityUtils.getSubject().getSession().setAttribute("menuList", menulist);
            SecurityUtils.getSubject().getSession().setAttribute("roleList",roleList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ShiroException("菜单解析失败");
        }
        SimpleAuthenticationInfo sai = new SimpleAuthenticationInfo(
                username,
                user.getPassword(),
                getName()
        );
        return sai;
    }
}
