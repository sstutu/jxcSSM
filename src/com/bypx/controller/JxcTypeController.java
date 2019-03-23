package com.bypx.controller;

import com.bypx.bean.JxcType;
import com.bypx.bean.ResultInfo;
import com.bypx.service.JxcTypeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("type")
public class JxcTypeController {

    @Resource
    JxcTypeService jxcTypeService;
    //init初始化JxcType的ztree
    @RequestMapping("init")
    @ResponseBody
    public List<JxcType> getAllType(){
        return jxcTypeService.getAllType();
    };
    //材料类别JxcType的增加或者修改
    @RequestMapping("insertOrUpdate")
    @ResponseBody
    public ResultInfo insertOrUpdate(JxcType jxcType){
        return jxcTypeService.insertOrUpdate(jxcType);
    }
    //删除节点，项目不要求删除带子节点的父节点删除，所以一次删除一个末节点
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(String id){ ;
        return jxcTypeService.delete(id);
    }
}
