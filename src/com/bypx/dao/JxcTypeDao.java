package com.bypx.dao;

import com.bypx.bean.JxcType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JxcTypeDao {

    public List<JxcType> getAllType();
    public void insert(JxcType jxcType);
    public void update(JxcType jxcType);
    //删除前先判断有没有下级
    public long countPidEqualId(String id);
    public void delete(String id);

}
