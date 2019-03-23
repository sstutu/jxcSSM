package com.bypx.controller;

import com.alibaba.fastjson.JSON;
import com.bypx.bean.Menu;
import com.bypx.bean.ResultInfo;
import com.bypx.bean.Role;
import com.bypx.bean.User;
import com.bypx.service.JxcMenuService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("menu")
public class JxcMenuController {

    @Resource
    JxcMenuService jxcMenuService;
    //左侧菜单..根据登录用户的信息获取了用户id，所以，根据id获取左侧菜单不需要再传参
    @RequestMapping("init")
    @ResponseBody
    public Map<String, Object> loadSideMenu(){
        //用户的菜单
        List<Menu> menuList = (List<Menu>) SecurityUtils.getSubject().getSession().getAttribute("menuList");
        //用户的名字
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        String name = user.getName();
        //用户的roleList
        List<Role> roleList = (List<Role>) SecurityUtils.getSubject().getSession().getAttribute("roleList");
        String roleNamesArrString="[";
        for (Role role : roleList) {
            //设置role字符串形式
            roleNamesArrString+=role.getName();
        }
        roleNamesArrString+="]";
        Map<String, Object> map = new HashMap<>();
        map.put("menuList",menuList);
        map.put("name",name);
        map.put("roleNamesArrString",roleNamesArrString);
        //return menuList;
        return map;
    }

    @RequestMapping("allMenu")
    @ResponseBody
    public List<Menu> queryAllMenu() throws IOException {
        return jxcMenuService.queryAllMenu();
    };

    /*@RequestMapping(value ="findBy/{id}", method = RequestMethod.GET)//通过前台传递来的id执行查询方法，用于修改节点的时候显示在修改界面模态框内显示原值
    public Map<String, Object> findById(@PathVariable("id") String id) {
        log.info("根据id查询菜单信息");
        Menu jxcMenu = jxcMenuService.selectOne(new EntityWrapper<JxcMenu>().eq("id_", id));
        return Result.sucJsonResp(jxcMenu);
        return null;
    }*/
    /**
     * 菜单管理页的ztree树  节点编辑的时候 显示菜单内容到模态框
     * 虽然结果只有一个，但这里未用map类型的原因是Memu的Bean里面定义了private List<JxcMenu> menus;
     * @param id
     * @return
     */
    @RequestMapping("findByMenuId")
    @ResponseBody
    public Menu queryMenuByMenuId(String id){
        return jxcMenuService.queryMenuByMenuId(id);
    };
    //菜单管理的增加或者修改
    @RequestMapping("insertOrUpdate")
    @ResponseBody
    public ResultInfo insertOrUpdate(Menu menu){
        return jxcMenuService.insertOrUpdate(menu);
    }


    //删除节点，项目不要求删除带子节点的父节点删除，所以一次删除一个末节点
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(String id){ ;
        return jxcMenuService.delete(id);
    }

    //根据角色id查菜单集合，用于角色管理的菜单关联
    @RequestMapping("findByRoleId")
    @ResponseBody
    public ResultInfo findByRoleId(String roleId){
        return jxcMenuService.findByRoleId(roleId);
    }
}
