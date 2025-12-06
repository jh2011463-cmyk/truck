package com.example.springboot.service.impl;

import com.example.springboot.entity.Truck;
import com.example.springboot.mapper.TruckMapper;
import com.example.springboot.service.ITruckService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2024-02-28
 */
@Service
public class TruckServiceImpl extends ServiceImpl<TruckMapper, Truck> implements ITruckService {

}
