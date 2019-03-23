package com.bypx.service;

import com.bypx.bean.JxcType;
import com.bypx.bean.ResultInfo;
import com.bypx.dao.JxcTypeDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class JxcTypeService {

    @Resource
    JxcTypeDao jxcTypeDao;

    public List<JxcType> getAllType(){
        List<JxcType> allType = jxcTypeDao.getAllType();;
        return allType;
    };
    public ResultInfo insertOrUpdate(JxcType jxcType){
        ResultInfo info = new ResultInfo();
        try {
            if (jxcType.getId()!=""){
                jxcTypeDao.update(jxcType);
                info.setFlag(true);
            }else {
                jxcType.setId(UUID.randomUUID().toString().replaceAll("-",""));
                jxcTypeDao.insert(jxcType);
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
            long i = jxcTypeDao.countPidEqualId(id);
            System.out.println("查询结果---------------"+i);
            //2.判断count=select count(*) from jxc_Type where pid=#{id}是否大于>0
            if(i!=0){
                //3.if(count>0){retun错误信息}
                info.setFlag(false);
                info.setErrorMsg("抱歉，有下级不删");
                return info;
            }else{
                //4.if通过就执行删除jxcTypeDao.delete（String id）
                jxcTypeDao.delete(id);
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
    
}
