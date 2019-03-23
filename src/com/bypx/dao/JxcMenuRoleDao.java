package com.bypx.dao;

import com.bypx.bean.JxcMenuRole;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface JxcMenuRoleDao {
    //role关联menu前的删除
    public void deleteByRoleId(String roleId);
    //role关联menu删除后执行的新增
    public void insertByRoleIdMenuId(JxcMenuRole jxcMenuRole);
}
