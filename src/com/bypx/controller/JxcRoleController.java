package com.bypx.controller;

import com.bypx.bean.JxcMenuRole;
import com.bypx.bean.PageBean;
import com.bypx.bean.ResultInfo;
import com.bypx.bean.Role;
import com.bypx.dao.JxcRoleDao;
import com.bypx.service.JxcRoleService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("role")
public class JxcRoleController {
    @Resource
    JxcRoleService jxcRoleService;
    @RequestMapping("queryAllRole")
    @ResponseBody
    public List<Role> queryAllRole(){
        return jxcRoleService.queryAllRole();
    };

    /**
     * 实体类要有limit的两个值，即start和pageSize两字段
     * 根据用户传入的模糊Role的模糊name，和start和size，三者不传的默认值分别为name="",或者null，satat=1 size=5；
     * 是指默认当前页面以防当前页码被上一页运算成了小于1
     * @param name
     * @param pageSize
     * @param currentPage
     * @return
     */
    @RequestMapping("queryRolePageBean")
    @ResponseBody
    public PageBean<Role> queryOnePageRole(String name,Integer currentPage,Integer pageSize){
        System.out.println("name:"+name);
        System.out.println("currentPage:"+currentPage);
        System.out.println("pageSize:"+pageSize);
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
        PageBean<Role> rolePageBean = jxcRoleService.findRolePageBean(name,pageSize,currentPage);
        return rolePageBean;
    };

    /**
     * 本方法实现role管理的新增和编辑
     * @param role
     * @return
     */
    @RequestMapping("edit")
    @ResponseBody
    public ResultInfo insertOrUpdate(Role role){
        return jxcRoleService.insertOrUpdate(role);
    }

    /**
     * 本方法实现删除
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(String id){
        return jxcRoleService.delete(id);
    }

    /**
     * 本方法用意是在下拉菜出，已经有角色的select option应该默认勾选上的用途
     * @param userId
     * @return
     */
    @RequestMapping("getByUserId")
    @ResponseBody
    public ResultInfo getByUserId(String userId){
        return jxcRoleService.getByUserId(userId);
    }

    //角色关联菜单
    @RequestMapping("roleAssociateMenu")
    @ResponseBody
    public ResultInfo roleAssociateMenu(String roleId, String menuIds){
        return jxcRoleService.roleAssociateMenu(roleId,menuIds);
    }

}
