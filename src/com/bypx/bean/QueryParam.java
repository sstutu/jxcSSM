package com.bypx.bean;

public class QueryParam {
    private int limit;//页面大小
    private int offset;//页码 排除
//    private String sort;//排序字段，用于sql的order by
//    private String order;//排序方式 asc desc

    //在途订单相关（总结查出自己的即可，status）
    private String typeName;//搜索的名字就是产品类别名【也是单据审核表查询字段】
    private String createrId;//项目要求只能显示自己的(由后台通过session获得用户id 设置进来)
    //下面两字段决定不在sql中使用
    private Integer orderType;//待理解，好像和createrId重复 【也是单据审核表查询字段】
    private Integer status;//状态，包括被驳回，所以这个字段不做限制

    private String auditorId;//审核人Id,（审核人的用户Id)【！只做！单据审核表查询字段】

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
