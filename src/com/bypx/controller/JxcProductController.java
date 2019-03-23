package com.bypx.controller;

import com.bypx.bean.QueryParam;
import com.bypx.bean.Role;
import com.bypx.bean.User;
import com.bypx.service.JxcProductService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("Product")
public class JxcProductController {
    @Resource
    JxcProductService jxcProductService;

    /**
     * bootstrap-table显示表格使用
     * @param queryParam
     * @return
     */
    @RequestMapping("GetProduct")
    @ResponseBody
    public Map<String, Object> GetProduct(QueryParam queryParam) {
        return jxcProductService.GetProduct(queryParam);
    }

    /**
     * Product页面根据角色不同控制【采购入库】【销售出库】显示使用
     * @return
     */
    @RequestMapping("GetRoleList")
    @ResponseBody
    public List<Role> GetRoleList(){
        List<Role> roleList = (List<Role>) SecurityUtils.getSubject().getSession().getAttribute("roleList");
        return roleList;
    }

    /**
     * 获取审核人员到前台的出入库模态框的输入选项
     * @return
     */
    @RequestMapping("GetAuditor")
    @ResponseBody
    public List<User> GetAuditor(){
        //为维护便捷，下面这个字段提供管理员标识自定义
        String flag="manager";
       return jxcProductService.GetAuditor(flag);

    }
}
