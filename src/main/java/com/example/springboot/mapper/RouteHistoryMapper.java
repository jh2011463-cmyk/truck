package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.RouteHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 历史线路表 Mapper 接口
 * </p>
 *
 * @author
 */
@Mapper
public interface RouteHistoryMapper extends BaseMapper<RouteHistory> {

}