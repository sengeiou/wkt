package com.zmj.wkt.service;

import com.baomidou.mybatisplus.service.IService;
import com.zmj.wkt.entity.Bs_person;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 客户个人信息表 服务类
 * </p>
 *
 * @author zmj
 * @since 2017-12-27
 */
public interface Bs_personService extends IService<Bs_person> {
    public Bs_person findByName(String name) ;
    public String registered(Bs_person bs_person, String registerWay);
    public Boolean updatePersonInfo(Bs_person bs_person);
    Bs_person findByWXOpenID(String WXOpenID);
}
