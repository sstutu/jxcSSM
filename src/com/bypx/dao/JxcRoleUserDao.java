package com.bypx.dao;

import com.bypx.bean.JxcRoleUser;
import org.springframework.stereotype.Repository;

@Repository
public interface JxcRoleUserDao {
    //user关联role前的删除
    public void deleteByUserId(String roleId);
    //user关联role删除后执行的新增
    public void insertByUserIdRoleId(JxcRoleUser jxcRoleUser);
}
