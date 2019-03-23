package com.bypx.controller;

import com.bypx.bean.JxcProductOrder;
import com.bypx.bean.QueryParam;
import com.bypx.bean.ResultInfo;
import com.bypx.service.JxcProductOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@RequestMapping("jxcProductOrder")
@Controller
public class JxcProductOrderController {
    @Resource
    JxcProductOrderService jxcProductOrderService;

    @RequestMapping("creatTask")
    @ResponseBody
    public ResultInfo insert(JxcProductOrder jxcProductOrder) {
        return jxcProductOrderService.insert(jxcProductOrder);
    }

    /**
     * bootstrap-table显示表格使用
     * @param queryParam
     * @return
     */
    @RequestMapping("GetProductOrderOn")
    @ResponseBody
    public Map<String, Object> GetProductOrderOn(QueryParam queryParam) {//获取在途单
        return jxcProductOrderService.GetProductOrderOn(queryParam);
    }

    /**
     * bootstrap-table显示表格使用
     * @param queryParam
     * @return
     */
    @RequestMapping("GetProductOrdersReview")
    @ResponseBody
    public Map<String, Object> GetProductOrdersReview(QueryParam queryParam) {//获取要审核的单据，叫做单据审核
        return jxcProductOrderService.GetProductOrdersReview(queryParam);
    }
    @RequestMapping("auditorOperateOrder")
    @ResponseBody
    public ResultInfo auditorOperateOrder(boolean agreeFlag,String orderId,String remark){
        return jxcProductOrderService.auditorOperateOrder(agreeFlag,orderId,remark);
    }

    /**
     * 撤销流程 要删除jxc_product_order订单
     * @param orderId
     * @return
     */
    @RequestMapping("deleteOrder")
    @ResponseBody
    public ResultInfo deleteOrder(String orderId){
        return jxcProductOrderService.deleteOrder(orderId);
    }
    @RequestMapping("modifyOrder")
    @ResponseBody
    public ResultInfo modifyOrder(JxcProductOrder jxcProductOrder){
        return jxcProductOrderService.modifyOrder(jxcProductOrder);
    }
}