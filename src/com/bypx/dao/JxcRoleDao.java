package com.bypx.dao;

import com.bypx.bean.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JxcRoleDao {
    //用于在页面展示表格
    public List<Role>queryAllRole();
    //用于根据查询用户输入的角色名称查询总记录数
    public int countByRoleName(@Param("name") String name);
    //根据用户输入的字段模糊查询和和用户输入的页数和大小进行查询结果集合*****【获得一页角色】
    public List<Role>findOnePageRole(Role role);

    public void insert(Role role);
    public void update(Role role);
    public void delete(String id);
    //为了select的下拉菜单复选框为选中状态的方法
    public List<Role> getByUserId(String userId);
}
