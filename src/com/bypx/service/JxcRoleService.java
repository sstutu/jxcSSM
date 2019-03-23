package com.bypx.service;

import com.bypx.bean.JxcMenuRole;
import com.bypx.bean.PageBean;
import com.bypx.bean.ResultInfo;
import com.bypx.bean.Role;
import com.bypx.dao.JxcMenuRoleDao;
import com.bypx.dao.JxcRoleDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class JxcRoleService {
    @Resource
    JxcRoleDao jxcRoleDao;
    @Resource
    JxcMenuRoleDao jxcMenuRoleDao;

    //用于在页面展示表格
    public List<Role> queryAllRole() {
        return jxcRoleDao.queryAllRole();
    }

    public PageBean<Role> findRolePageBean(String name, Integer pageSize, Integer currentPage) {//参数接收currentPage和pageSize
        //封装
        Role role = new Role();
        // 1.String name,给Role去模糊查询 计算总记录数和查询结果集都要用到
        role.setName(name);
        // 2.Integer pageSize,给Role做数据库的limit第二个参数
        role.setPageSize(pageSize);
        // 3.Integer currentPage给PageBean设置currentPage字段，同时通过这个参数计算Role查数据库的limit第一个参数strat
        int start = (currentPage - 1) * pageSize;
        role.setStart(start);

        //这里得到总记录数和结果集
        int totalCount = jxcRoleDao.countByRoleName(name);
        //【1】三维运算符计如果totalCount模pageSize==0则为两者之商，否则为两者之商+1
        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

        //【2】设置结果List
        List<Role> roleList = jxcRoleDao.findOnePageRole(role);


        PageBean<Role> pb = new PageBean<Role>();
        //设置totalCount,上面已查得
        pb.setTotalCount(totalCount);
        //按传入值设置currentPage和pageSize
        pb.setCurrentPage(currentPage);
        pb.setPageSize(pageSize);
        //按上面查询的结果设置totalPage和List<Role>
        pb.setTotalPage(totalPage);
        pb.setList(roleList);

        return pb;
    }

    public ResultInfo insertOrUpdate(Role role) {
        ResultInfo info = new ResultInfo();
        if (StringUtils.isEmpty(role.getId())) {
            role.setId(UUID.randomUUID().toString());
            try {
                jxcRoleDao.insert(role);
                info.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                info.setFlag(false);
                info.setErrorMsg("新增失败！");
            }
        } else {
            try {
                jxcRoleDao.update(role);
                info.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                info.setFlag(false);
                info.setErrorMsg("编辑失败！");
            }
        }
        return info;
    }

    public ResultInfo delete(String id) {
        ResultInfo info = new ResultInfo();
        try {
            jxcRoleDao.delete(id);
            info.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("删除失败！");
        }
        return info;
    }

    public ResultInfo getByUserId(String userId) {
        ResultInfo info = new ResultInfo();
        try {
            List<Role> roles = jxcRoleDao.getByUserId(userId);
            info.setFlag(true);
            info.setData(roles);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("通过用户id找角色role失败。");
        }
        return info;
    }

    //role关联menu的方法
    public ResultInfo roleAssociateMenu(String roleId, String menuIds) {
        ResultInfo info = new ResultInfo();
        System.out.println(roleId + "获得的roid");
        try {
            jxcMenuRoleDao.deleteByRoleId(roleId);//先删除
            System.out.println("删除旧#数据ok");
            String[] idArr = menuIds.split(",");
            for (String s : idArr) {
                System.out.println(s+"菜单id 与 1，3，4，5，11，111，33，55 进行比对");
                if(s.equals("null")) continue;
                JxcMenuRole temp = new JxcMenuRole();
                temp.setRoleId(roleId);
                temp.setMenuId(s);
                temp.setId(UUID.randomUUID().toString());
                jxcMenuRoleDao.insertByRoleIdMenuId(temp);
            }
            System.out.println("新增新#数据ok");
            info.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("失败了,请查看后台打印确认是【删除旧数据失败】<br>还是【新增新数据失败】");
        }
        return info;
    }
}
