package com.zmj.wkt.service;

import com.zmj.wkt.entity.Bs_permission;
import com.zmj.wkt.common.CommonManager;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zmj
 * @since 2018-01-23
 */
public interface Bs_permissionService extends CommonManager<Bs_permission> {
    List<Bs_permission> findAllByPersonId(String ClientID);
}
