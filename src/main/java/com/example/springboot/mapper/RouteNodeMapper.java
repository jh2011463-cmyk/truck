package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.RouteNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 线路节点表 Mapper 接口
 * </p>
 *
 * @author
 */
@Mapper
public interface RouteNodeMapper extends BaseMapper<RouteNode> {

}