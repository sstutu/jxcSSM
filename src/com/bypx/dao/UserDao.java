package com.bypx.dao;

import com.bypx.bean.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
    //用于"ShiroRealm"认证类，认证成功后需要保存user对象到session
    public User queryUserByUserName(String username);
    //通过id查用户
    public User queryUserById(String id);
    //通过id查用户角色
    public User queryRoleById(String id);


    ///////////User表管理////////////////////////
    //查询所有的用户 组成Table  通过name 和limit的两个字段
    public List<User>findOnePageUser(User user);
    //查询根据上面这个User user里面的name 得出long行的结果  查询符合条的count数
    public int countByUserName(@Param("name") String name);

    //新增和编辑的insert和Update
    public void insert(User user);
    public void update(User User);
    public void delete(String id);

}
