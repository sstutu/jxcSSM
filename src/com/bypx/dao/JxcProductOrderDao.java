package com.bypx.dao;

import com.bypx.bean.JxcProduct;
import com.bypx.bean.JxcProductOrder;
import com.bypx.bean.QueryParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JxcProductOrderDao {
    //新增jxc_product_order在途单
    public void insert(JxcProductOrder jxcProductOrder);

    //【在途单】
    //查询在途单total
    public long productOrderOnTotal(QueryParam queryParam);
    //查询在途单rows的List集
    public List<JxcProduct> productOrderOnRows(QueryParam queryParam);

    //【单据审核】
    //查询审核员要审核单据total
    public long productOrdersReviewTotal(QueryParam queryParam);
    //查询审核员要审核单据rows的List集
    public List<JxcProduct> productOrdersReviewRows(QueryParam queryParam);

    //供于单据审核的时候 审核员通过产品订单id获得订单提交的信息
    public JxcProductOrder getByOrderId(String orderId);

    public void saveOrUpdate(JxcProductOrder order);//更新一个status和remark吧

    public void deleteOrder(String orderId);

    public void updateOrder(JxcProductOrder jxcProductOrder);
}
