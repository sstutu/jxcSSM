package com.bypx.dao;

import com.bypx.bean.JxcProduct;
import com.bypx.bean.QueryParam;
import com.bypx.bean.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JxcProductDao {
    public long productTotal(@Param("typeName")String typeName);//要接收一个name，方法求处总记录数row
    public List<JxcProduct>productRows(QueryParam queryParam);
    public List<User> GetAuditor(@Param("flag") String flag);
    public JxcProduct getByTypeId(String typeId);//通过订单类型id获得订单信息 order里的typeId
    public JxcProduct getByOrderId(String orderId);//通过orderId获得库存信息
    public void insert(JxcProduct jxcProduct);
    public void delete(String id);//库存被扣为零就删除数据

    public void update(JxcProduct jxcProduct);//采购入库就跟新库存数，不更新价格
}
