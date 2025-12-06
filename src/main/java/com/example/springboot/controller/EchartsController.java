package com.example.springboot.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import com.example.springboot.entity.OrderForm;
import com.example.springboot.entity.User;
import com.example.springboot.service.IOrderFormService;
import com.example.springboot.service.IUserService;
import jdk.nashorn.internal.runtime.logging.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    private IUserService userService;

    @Resource
    private IOrderFormService orderFormService;

    @GetMapping("/lineData")
    public Result getLineData(){
        List<OrderForm> orderFormList = orderFormService.list();
        Set<String> dateSet = orderFormList.stream().map(OrderForm -> OrderForm.getDate().substring(0,10)).collect(Collectors.toSet());
        //将set集合进行排序并取出其中七个
        dateSet.stream().limit(7).sorted(Comparator.naturalOrder());
        //创建lineList集合
        List<Dict> lineList =new ArrayList<>();
        for (String date : dateSet) {
            long count = orderFormList.stream().filter(orderForm -> orderForm.getDate().substring(0,10).equals(date)).count();
            Dict dict = new Dict();
            dict.set("date",date).set("count",count);
            lineList.add(dict);
        }
        lineList.sort(Comparator.comparing(d -> LocalDate.parse((CharSequence) d.get("date"))));
        return Result.success(lineList);
    }

    @GetMapping("/pieData")
    public Result getPieData(){
        List<String> roleList = new ArrayList<>();
        roleList.add("ROLE_ADMIN");
        roleList.add("ROLE_DRIVER");
        roleList.add("ROLE_USER");
        List<Dict> pieList = new ArrayList<>();
        for (String role : roleList) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role",role);
            List<User> userList = userService.list(queryWrapper);
            Dict dict = new Dict();
            if (role.equals("ROLE_ADMIN")){
                dict.set("role","管理员").set("count",userList.size());
            }
            if (role.equals("ROLE_DRIVER")){
                dict.set("role","司机").set("count",userList.size());
            }
            if (role.equals("ROLE_USER")){
                dict.set("role","客户").set("count",userList.size());
            }
            pieList.add(dict);
        }
        return Result.success(pieList);
    }

    @GetMapping("/userCount")
    public Result getUserCount(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role","ROLE_USER");
        List<User> userList = userService.list(queryWrapper);
        return Result.success(userList.size());
    }

    @GetMapping("/driverCount")
    public Result getDriverCount(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role","ROLE_DRIVER");
        List<User> driverList = userService.list(queryWrapper);
        return Result.success(driverList.size());
    }

    @GetMapping("/finishOrderCount")
    public Result finishOrderCount(){
        QueryWrapper<OrderForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status","DONE");
        List<OrderForm> orderFormList = orderFormService.list(queryWrapper);
        return Result.success(orderFormList.size());
    }

    @GetMapping("/finishOrderMoney")
    public Result finishOrderMoney(){
        QueryWrapper<OrderForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status","DONE");
        List<OrderForm> orderFormList = orderFormService.list(queryWrapper);
        BigDecimal totalAmount = orderFormList.stream()
                .map(OrderForm::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.success(totalAmount);
    }


}
