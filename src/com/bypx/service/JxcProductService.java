package com.bypx.service;

import com.bypx.bean.JxcProduct;
import com.bypx.bean.QueryParam;
import com.bypx.bean.Role;
import com.bypx.bean.User;
import com.bypx.dao.JxcProductDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class JxcProductService {
    @Resource
    JxcProductDao jxcProductDao;
    public Map<String, Object> GetProduct(QueryParam queryParam) {
        //查出bootstrap-table要的total
        long total = jxcProductDao.productTotal(queryParam.getTypeName());
        //查出bootstrap-table要的rows
        List<JxcProduct> rows = jxcProductDao.productRows(queryParam);
        //put到map里返回
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", rows);
        map.put("total",total);
        return map;
    }

    public List<User> GetAuditor(String flag) {
        return jxcProductDao.GetAuditor(flag);
    }
}
