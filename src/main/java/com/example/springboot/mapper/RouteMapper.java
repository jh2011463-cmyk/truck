package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Route;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 线路表 Mapper 接口
 * </p>
 *
 * @author
 */
@Mapper
public interface RouteMapper extends BaseMapper<Route> {

}