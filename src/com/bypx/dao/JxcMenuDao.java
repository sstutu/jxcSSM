package com.bypx.dao;

import com.bypx.bean.Menu;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JxcMenuDao {
    //通过id查用户菜单,左侧的菜单
    public List<Menu> queryMenuByUserId(String id);
    //菜单管理页的ztree树
    public List<Menu> queryAllMenu();
    /**
     * 菜单管理页的ztree树  节点编辑的时候 显示菜单内容到模态框
     * 虽然结果只有一个，但这里未用map类型的原因是Memu的Bean里面定义了private List<JxcMenu> menus;
     * @param id
     * @return
     */
    public Menu queryMenuByMenuId(String id);

    //菜单的insert
    public void insert(Menu menu);
    //菜单的upadte
    public void update(Menu menu);

    //用于在ztree上进行末节点时判断阶段该节点是否有子节点
    public long countPidEqualId(String id);
    //用于删除节点
    public void delete(String id);
    //根据角色id查菜单集合，用于角色管理的菜单关联
    public List<Menu> findByRoleId(String roleId);
}
