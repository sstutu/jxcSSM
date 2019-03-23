package com.bypx.bean;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class JxcProduct {
    private String id;//产品主键id，也就是这个中间表的主键Id
    private String typeId;//数据库Type的产品id
    private String typeName;//产品名称 也就是type的name，这里设置本字段为了页面显示名字
    private Integer total;//总记录数
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    private Integer price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
