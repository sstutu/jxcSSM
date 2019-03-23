package com.bypx.service;

import com.bypx.bean.*;
import com.bypx.dao.JxcProductDao;
import com.bypx.dao.JxcProductOrderDao;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class JxcProductOrderService {
    @Resource
    JxcProductDao jxcProductDao;//用户查库存
    @Resource
    JxcProductOrderDao jxcProductOrderDao;//insert单据到jxc_product_order
    @Resource
    RuntimeService runtimeService;//用于创建流程实例，老师说的相当于从User 到new User()
    @Resource
    TaskService taskService;//用于任务认领


    public ResultInfo insert(JxcProductOrder jxcProductOrder) {
        ResultInfo info = new ResultInfo();
        //如果是出库 即jxcProductOrder.getOrderType()==2表示出库
        JxcProduct product = jxcProductDao.getByTypeId(jxcProductOrder.getTypeId());
        if (product == null) {//产品不存在
            //无法出库
            if (jxcProductOrder.getOrderType() == 2) {//库存存在但库存不足
                info.setFlag(false);
                info.setErrorMsg("库存不足，无法创建单据");
                return info;
            }
        } else {//产品存在
            if (2 == jxcProductOrder.getOrderType()) {
                if (product.getTotal() < jxcProductOrder.getNumber()) {
                    info.setFlag(false);
                    info.setErrorMsg("库存不足，无法创建单据");
                    return info;
                }
            }
        }
        //属于入库不做库存判断，或则出库库存充足，总之：需要创建单据,【开始组装单据1.setId 2.setCreaterId.3.setCreater 4.setCreateDate 5.setStatus】
        //设置单据id 为uuid和创建流程使用 后面用get保证是同一个
        jxcProductOrder.setId(UUID.randomUUID().toString());
        //获得登录用户设置setCreaterId和setCreater
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        jxcProductOrder.setCreaterId(user.getId());
        jxcProductOrder.setCreater(user.getName());
        //设置单据创建时间
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式 df.format(new Date())
        jxcProductOrder.setCreateDate(new Date());
        //设置单据为在途获
        jxcProductOrder.setStatus(1);//1.表示在途 2.表示驳回 3.通过
        //提交订单
        //********************************************************************************
        //将单据的id与流程关联起来
        //会在hi_procinst里创建流程实例的数据，
        //单据的id会保存在BUSINESS_KEY_字段里
        runtimeService.startProcessInstanceByKey("process", jxcProductOrder.getId());
        //根据单据id，查询当前流程实例处在的节点
        //相当于利用act_hi_procinst的BUSINESS_KEY_字段与act_ru_task表的关联查询
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(jxcProductOrder.getId()).singleResult();
        taskService.claim(task.getId(), jxcProductOrder.getCreaterId());
        //结束当前的任务，流程会自动的进入下一个节点
        taskService.complete(task.getId());
        //重新查询ru-task表的数据，该数据属于管理员，新数据
        Task task2 = taskService.createTaskQuery().
                processInstanceBusinessKey(jxcProductOrder.getId()).singleResult();
        //将管理员的id与新的任务关联起来
        taskService.claim(task2.getId(), jxcProductOrder.getAuditorId());
        System.out.println("已提交审核");
        //********************************************************************************

        jxcProductOrderDao.insert(jxcProductOrder);
        System.out.println("单据已创建,未提交审核");

        info.setFlag(true);
        return info;

    }

    public Map<String, Object> GetProductOrderOn(QueryParam queryParam) {
        //获取要查询的用户id 设置到createrId字段用于sql
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        queryParam.setCreaterId(user.getId());
        //查出bootstrap-table要的total
        long total = jxcProductOrderDao.productOrderOnTotal(queryParam);//需要通过typeName和createrId查询所以这里传整个bean求处需要的total
        //查出bootstrap-table要的rows
        List<JxcProduct> rows = jxcProductOrderDao.productOrderOnRows(queryParam);
        //put到map里返回
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", rows);
        map.put("total", total);
        return map;
    }

    public Map<String, Object> GetProductOrdersReview(QueryParam queryParam) {
        //获取要查询的登录用户id设置到审核人id auditorId   act_ru_task_
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        queryParam.setAuditorId(user.getId());
        //查出bootstrap-table要的total
        long total = jxcProductOrderDao.productOrdersReviewTotal(queryParam);//需要通过typeName和createrId查询所以这里传整个bean求处需要的total
        //查出bootstrap-table要的rows
        List<JxcProduct> rows = jxcProductOrderDao.productOrdersReviewRows(queryParam);
        //put到map里返回
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", rows);
        map.put("total", total);
        return map;
    }

    public ResultInfo auditorOperateOrder(boolean agreeFlag, String orderId, String remark) {
        ResultInfo info = new ResultInfo();
        JxcProductOrder order = jxcProductOrderDao.getByOrderId(orderId);//获得订单提交数
        JxcProduct jxcProduct = jxcProductDao.getByOrderId(orderId);//先获得库存
        //System.out.println("库存数：["+jxcProduct.getTotal()+"]"+"出库提交数：["+order.getNumber()+"]");
        if (agreeFlag) {//同意
            order.setStatus(3);
            if (jxcProduct == null) {//产品不存在无法出库
                if (2 == order.getOrderType()) {//销售出库
                    order.setStatus(2);//标识上2标识驳回
                    order.setRemark("产品库存不足，待补足后再申请");
                    activitOperation(false, order);//驳回
                    info.setFlag(false);
                    info.setErrorMsg("库存不足，无法出库");
                } else {//采购入库  上面已经确认库存不存在，所以采购入库就创建一个库存
                    //直接新建一个产品统计数据
                    jxcProduct = new JxcProduct();
                    jxcProduct.setId(UUID.randomUUID().toString());//设置uuid
                    jxcProduct.setTypeId(order.getTypeId());//设置要新增的类型
                    jxcProduct.setTotal(order.getNumber());//设置要新增的数量
                    jxcProduct.setPrice(order.getPrice());//设置入库价格
                    jxcProductDao.insert(jxcProduct);

                    activitOperation(true, order);//通过
                    info.setFlag(true);
                }
            } else {//库存存在
                if (2 == order.getOrderType()) {//出库
                    if (jxcProduct.getTotal() < order.getNumber()) {//库存存在，但是库存小于出库
                        order.setStatus(2);//设置驳回
                        order.setRemark("产品库存不足，待补足后再申请");
                        activitOperation(false, order);//驳回
                        info.setFlag(false);
                        info.setErrorMsg("库存不足，无法出库");
                    } else {//库存数于出库数 相等删除数据，大于出库数就扣除
                        jxcProduct.setTotal(jxcProduct.getTotal() - order.getNumber());
                        activitOperation(true, order);//通过
                        jxcProductDao.update(jxcProduct);
                        if (jxcProduct.getTotal() == 0) {//库存扣成零就删除数据
                            jxcProductDao.delete(jxcProduct.getId());
                        }
                        info.setFlag(true);
                    }
                } else {//采购入库 用updage 矛盾这里是否更新价格
                    jxcProduct.setTotal(order.getNumber() + jxcProduct.getTotal());
                    jxcProductDao.update(jxcProduct);//带过去id和数量
                    activitOperation(true, order);//通过
                    info.setFlag(true);
                }
            }
        } else {//选了 【驳回】
            order.setStatus(2);
            order.setRemark(remark);
            activitOperation(false, order);//驳回
            info.setFlag(true);

        }
        jxcProductOrderDao.saveOrUpdate(order);
        return info;
    }

    //采购或者销售取消订单
    public ResultInfo deleteOrder(String orderId) {
        ResultInfo info = new ResultInfo();
        //撤销、结束流程
        deleteProcessinstance(orderId);
        //删除订单
        try {
            jxcProductOrderDao.deleteOrder(orderId);
            info.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("订单删除失败，请检查！");
        }
        return info;
    }

    public ResultInfo modifyOrder(JxcProductOrder jxcProductOrder) {
        ResultInfo info = new ResultInfo();
        try {
            //设置在途
            jxcProductOrder.setStatus(1);
            //更新订单，将订单的状态价格数量进行更新 where 里面的id
            jxcProductOrderDao.updateOrder(jxcProductOrder);
            info.setFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
            info.setFlag(false);
            info.setErrorMsg("重新提交时更新订单失败！");
        }
        //重新提交审核,先通过
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        jxcProductOrder.setCreaterId(user.getId());
        activitSubmit(jxcProductOrder);//参数要包含orderId 审核员Id，单据创建人的id

        return info;
    }

    /**
     * activit提交管理员
     * 要求带有1.orderId, 2.createrId, 3.auditorId
     * @param jxcProductOrder
     */
    public void activitSubmit(JxcProductOrder jxcProductOrder){
        //将单据的id与流程关联起来
        //会在hi_procinst里创建流程实例的数据，
        //单据的id会保存在BUSINESS_KEY_字段里
//        runtimeService.startProcessInstanceByKey("process", jxcProductOrder.getId());
        //根据单据id，查询当前流程实例处在的节点
        //相当于利用act_hi_procinst的BUSINESS_KEY_字段与act_ru_task表的关联查询
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(jxcProductOrder.getId()).singleResult();
        taskService.claim(task.getId(), jxcProductOrder.getCreaterId());
        //结束当前的任务，流程会自动的进入下一个节点
        taskService.complete(task.getId());
        //重新查询ru-task表的数据，该数据属于管理员，新数据
        Task task2 = taskService.createTaskQuery().
                processInstanceBusinessKey(jxcProductOrder.getId()).singleResult();
        //将管理员的id与新的任务关联起来
        taskService.claim(task2.getId(), jxcProductOrder.getAuditorId());
        System.out.println("已提交审核");
    }

    //用于操作通过和驳回
    public void activitOperation(boolean b, JxcProductOrder order) {
        if (b) {//activit做通过
            Task task = taskService.createTaskQuery().processInstanceBusinessKey(order.getId()).singleResult();
            Map<String, Object> map = new HashMap<>();
            map.put("agree", true);
            taskService.complete(task.getId(), map);
        } else {//activit做驳回
            Task task = taskService.createTaskQuery().processInstanceBusinessKey(order.getId()).singleResult();
            Map<String, Object> map = new HashMap<>();
            map.put("agree", false);
            taskService.complete(task.getId(), map);
            //查询出销售手上的新任务，该任务是驳回后重新生成的
            Task task2 = taskService.createTaskQuery().
                    processInstanceBusinessKey(order.getId()).singleResult();
            //将销售id与新的任务关联起来
            taskService.claim(task2.getId(), order.getCreaterId());
        }

    }

    //撤销按钮的结束流程
    public void deleteProcessinstance(String orderId) {
        Task task = taskService.createTaskQuery().processInstanceBusinessKey(orderId).singleResult();
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), "提交人主动撤销");
    }

}
