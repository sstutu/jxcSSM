package com.bypx.service;

import com.bypx.bean.*;
import com.bypx.dao.JxcRoleUserDao;
import com.bypx.dao.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    //注入dao
    @Resource
    UserDao userDao;
    @Resource
    JxcRoleUserDao jxcRoleUserDao;

    //用于"ShiroRealm"认证类，认证成功后需要保存user对象到session
    public User queryUserByUserName(String username){
        return userDao.queryUserByUserName(username);
    };
    //通过id查用户
    public User queryUserById(String id){
        return userDao.queryUserById(id);
    };
    //通过id查用户角色
    public User queryRoleById(String id){
        return userDao.queryRoleById(id);
    };

    //查询所有的用户 组成Table
    public PageBean queryUserPageBean(String name, Integer currentPage, Integer pageSize){
        //封装
        User user = new User();
        // 1.String name,给Role去模糊查询 计算总记录数和查询结果集都要用到
        user.setName(name);
        // 2.Integer pageSize,给Role做数据库的limit第二个参数
        user.setPageSize(pageSize);
        // 3.Integer currentPage给PageBean设置currentPage字段，同时通过这个参数计算Role查数据库的limit第一个参数strat
        int start = (currentPage - 1) * pageSize;
        user.setStart(start);

        //这里得到总记录数和结果集
        int totalCount = userDao.countByUserName(name);
        //【1】三维运算符计如果totalCount模pageSize==0则为两者之商，否则为两者之商+1
        int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        //【2】设置结果List
        List<User> userList = userDao.findOnePageUser(user);
        PageBean<User> pb = new PageBean<User>();
        //设置totalCount,上面已查得
        pb.setTotalCount(totalCount);
        //按传入值设置currentPage和pageSize
        pb.setCurrentPage(currentPage);
        pb.setPageSize(pageSize);
        //按上面查询的结果设置totalPage和List<Role>
        pb.setTotalPage(totalPage);
        pb.setList(userList);

        return pb;
    };
    //新增或者更新
    public ResultInfo insertOrUpdate(User user) {
        ResultInfo info = new ResultInfo();
        if (StringUtils.isEmpty(user.getId())) {
            user.setId(UUID.randomUUID().toString());
            try {
                userDao.insert(user);
                info.setFlag(true);
            } catch (Exception e) {
                e.printStackTrace();
                info.setFlag(false);
                info.setErrorMsg("新增失败！");
            }
        } else {
            try {
                userDao.update(user);
                info.setFlag(true);

            } catch (Exception e) {
                e.printStackTrace();
                info.setFlag(false);
                info.setErrorMsg("编辑失败！");
            }
        }
        return info;
    }
    //删除
    public ResultInfo delete(String id) {
        ResultInfo info = new ResultInfo();
        try {
            userDao.delete(id);
            info.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("删除失败！");
        }
        return info;
    }
    //关联user关联role
    public ResultInfo associationRole(String userId,String[] roleArr) {
        ResultInfo info = new ResultInfo();
        try {
            //1.先删除
            jxcRoleUserDao.deleteByUserId(userId);
            System.out.println("关联前deleteByUserId完成");
            //2.新增,遍历接收的数组，在遍历中组装JxcRoleUser
            for (String roleId : roleArr) {
                JxcRoleUser roleUser = new JxcRoleUser();
                roleUser.setId(UUID.randomUUID().toString());
                roleUser.setUserId(userId);
                roleUser.setRoleId(roleId);
                System.out.println(roleId+"获得的roId");
                jxcRoleUserDao.insertByUserIdRoleId(roleUser);
            }
            System.out.println("关联insertByUserIdRoleId执行完成");
            info.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("关联失败");
            System.out.println("关联失败，请检查关联前【删除失败】还是【关联失败】");
        }
        return info;
    }




}
