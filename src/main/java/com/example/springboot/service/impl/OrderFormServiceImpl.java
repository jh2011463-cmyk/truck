package com.example.springboot.service.impl;

import com.example.springboot.entity.OrderForm;
import com.example.springboot.mapper.OrderFormMapper;
import com.example.springboot.service.IOrderFormService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-02-29
 */
@Service
public class OrderFormServiceImpl extends ServiceImpl<OrderFormMapper, OrderForm> implements IOrderFormService {

}
