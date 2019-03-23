package com.bypx.service;

import com.bypx.bean.Menu;
import com.bypx.bean.ResultInfo;
import com.bypx.dao.JxcMenuDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class JxcMenuService {

    @Resource
    JxcMenuDao jxcMenuDao;

    //左侧菜单
    public List<Menu> loadSideMenu(String id){
        return jxcMenuDao.queryMenuByUserId(id);
    }

    //菜单管理页的树
    public List<Menu> queryAllMenu() throws IOException {
        List<Menu> allMenu = jxcMenuDao.queryAllMenu();
//        allMenu= Menu.formatMenu(allMenu);
        return allMenu;
    };

    /**
     * 菜单管理页的ztree树  节点编辑的时候 显示菜单内容到模态框
     * 虽然结果只有一个，但这里未用map类型的原因是Memu的Bean里面定义了private List<JxcMenu> menus;
     * @param id
     * @return
     */
    public Menu queryMenuByMenuId(String id){
        return jxcMenuDao.queryMenuByMenuId(id);
    };
    public ResultInfo insertOrUpdate(Menu menu){
        ResultInfo info = new ResultInfo();
        try {
            if (menu.getId()!=""){
                jxcMenuDao.update(menu);
                info.setFlag(true);
            }else {
                menu.setId(UUID.randomUUID().toString().replaceAll("-",""));
                jxcMenuDao.insert(menu);
                info.setFlag(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("出错了请检查！");
        }
        return info;
    }
    public ResultInfo delete(String id){
        ResultInfo info=new ResultInfo();
        //1.传进来的的id首先先去查一下有没有pid于传进来的值相等
        try {
            long i = jxcMenuDao.countPidEqualId(id);
            System.out.println("查询结果---------------"+i);
            //2.判断count=select count(*) from jxc_menu where pid=#{id}是否大于>0
            if(i!=0){
                //3.if(count>0){retun错误信息}
                info.setFlag(false);
                info.setErrorMsg("抱歉，有下级不删");
                return info;
            }else{
                //4.if通过就执行删除jxcMenuDao.delete（String id）
                jxcMenuDao.delete(id);
                //5.成功信息
                info.setFlag(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("抱歉，有下级不删");
        }
        return info;
    }

    //根据角色id查菜单集合，用于角色管理的菜单关联
    public ResultInfo findByRoleId(String roleId){
        ResultInfo info = new ResultInfo();
        try {
            List<Menu> menus = jxcMenuDao.findByRoleId(roleId);
            info.setFlag(true);
            info.setData(menus);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("roleId查用户失败，请检查。");
        }
        return info;
    };
}
