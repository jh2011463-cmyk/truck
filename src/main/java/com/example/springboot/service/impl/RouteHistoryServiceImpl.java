package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.RouteHistory;
import com.example.springboot.mapper.RouteHistoryMapper;
import com.example.springboot.service.IRouteHistoryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 历史线路表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-01-01
 */
@Service
public class RouteHistoryServiceImpl extends ServiceImpl<RouteHistoryMapper, RouteHistory> implements IRouteHistoryService {

}