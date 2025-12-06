package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Route;
import com.example.springboot.mapper.RouteMapper;
import com.example.springboot.service.IRouteService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 线路表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-01-01
 */
@Service
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements IRouteService {

}