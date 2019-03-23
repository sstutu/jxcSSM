package com.bypx.bean;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Menu {
    private String id;
    private String text;
    private String icon;
    private String url;
    private String pid;
    private int order;
    private String flag;
    private List<Menu> menus;
    //下方这连个字段为使用到可以直接删除，如果删除，记得将menu的mapper,xml文件的result对应的去删除
    private int canParent;//这个字段用于用于InsertOrUpdate，解决传两个参数最好封装到bean的建议
    private int count;//mybatis判断Id是否存在

    //排序  通过order来排序
    private static Comparator<Menu> order(){
        Comparator<Menu> comparator = new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                if (o1.getOrder() != o2.getOrder()) {
                    return o1.getOrder() - o2.getOrder();
                }
                return 0;
            }
        };

        return comparator;
    }
    private static List<Menu> getChild(String id, List<Menu> allMenu) {
        //子菜单    
        List<Menu> childList = new ArrayList<Menu>();
        for (Menu nav : allMenu) {// 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较     
            //相等说明：为该根节点的子节点。      
            if (id.equals(nav.getPid())) {
                childList.add(nav);
            }
        }
        //递归   
        for (Menu nav : childList) {
            nav.setMenus(getChild(nav.getId(), allMenu));
        }
        Collections.sort(childList, order());//排序   
        //如果节点下没有子节点，返回一个空List（递归退出）  
        if (childList.size() == 0) {
            return new ArrayList<Menu>();
        }
        return childList;
    }

    /**
     * 菜单格式化
     * @param allMenu
     * @return
     * @throws IOException
     */
    public static List<Menu> formatMenu(List<Menu> allMenu) throws IOException {
        //获取一级菜单  
        List<Menu> rootMenu = new ArrayList<Menu>();
        for (Menu nav : allMenu) {
            if (StringUtils.isEmpty(nav.getPid()) || "0".equals(nav.getPid())) {//父节点是0的，为根节点。        
                rootMenu.add(nav);
            }
        }
        Collections.sort(rootMenu, order());
        //为一级菜单设置子菜单，getClild是递归调用的    
        for (Menu nav : rootMenu) {
            List<Menu> childList = getChild(nav.getId(), allMenu);
            nav.setMenus(childList);
        }
        return rootMenu;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCanParent() {
        return canParent;
    }

    public void setCanParent(int canParent) {
        this.canParent = canParent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
