package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.RouteNode;
import com.example.springboot.mapper.RouteNodeMapper;
import com.example.springboot.service.IRouteNodeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 线路节点表 服务实现类
 * </p>
 *
 * @author
 * @since 2024-01-01
 */
@Service
public class RouteNodeServiceImpl extends ServiceImpl<RouteNodeMapper, RouteNode> implements IRouteNodeService {

}