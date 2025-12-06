package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.db.sql.Order;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;

import javax.management.StringValueExp;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.OrderStatus;
import com.example.springboot.common.RoleEnum;
import com.example.springboot.config.ThreadPoolConfig;
import com.example.springboot.entity.Role;
import com.example.springboot.entity.Truck;
import com.example.springboot.service.ITruckService;
import com.example.springboot.service.IUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.example.springboot.entity.User;
import com.example.springboot.utils.TokenUtils;

import com.example.springboot.service.IOrderFormService;
import com.example.springboot.entity.OrderForm;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 */
@RestController
@RequestMapping("/orderForm")
public class OrderFormController {

    @Resource
    private IOrderFormService orderFormService;

    @Resource
    private IUserService userService;

    @Resource
    private ITruckService truckService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody OrderForm orderForm) {
        if (orderForm.getId() == null) {
            User user = getUser();
            if (user.getRole().equals("ROLE_USER")){
                orderForm.setUserId(user.getId());
                orderForm.setDate(now);
                if (orderForm.getStatus() == null){
                    orderForm.setStatus(String.valueOf(OrderStatus.WAIT));
                }
            }
            Integer level = user.getLevel();
            orderForm.setUserLevel(level);
        }

        orderFormService.saveOrUpdate(orderForm);
        return Result.success();
    }

    //司机确认送达
    @PostMapping("/haveSend")
    public Result haveSend(@RequestBody OrderForm orderForm){
        orderForm.setStatus(String.valueOf(OrderStatus.USER_COMMENT));
        orderForm.setSendTime(now);
        orderFormService.updateById(orderForm);
        return Result.success();
    }

    //司机接单
    @PostMapping("/getOrder")
    public Result getOrder(@RequestBody OrderForm orderForm){
        orderForm.setStatus(String.valueOf(OrderStatus.SENDING));
        orderFormService.updateById(orderForm);
        return Result.success();
    }

    //分配
    @PostMapping("/distribution")
    public Result distribution(@RequestBody OrderForm orderForm){
        orderForm.setStatus(String.valueOf(OrderStatus.NO_GET));
        orderFormService.updateById(orderForm);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        orderFormService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        orderFormService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(orderFormService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(orderFormService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String type
                           ) {
        QueryWrapper<OrderForm> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("user_level");

        if (!"".equals(name)) {
            queryWrapper.eq("id", name);
        }
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals("ROLE_USER")) {
            queryWrapper.eq("user_id", currentUser.getId());
        }

        if (type.equals("driver_order_usable")){
            queryWrapper.eq("driver_id",currentUser.getId());
        }



        Page<OrderForm> page = orderFormService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<OrderForm> records = page.getRecords();
        for (OrderForm record : records) {
            Integer truckId = record.getTruckId();
            Truck truck = truckService.getById(truckId);
            if (truck != null){
                record.setTruckName(truck.getType());
            }

            Integer userId = record.getUserId();
            User user = userService.getById(userId);
            if (user != null){
                record.setUserName(user.getNickname());
            }

            Integer driverId = record.getDriverId();
            User driver = userService.getById(driverId);
            if (driver != null){
                record.setDriverName(driver.getNickname());
            }

        }

        return Result.success(page);
    }


    /**
    * 导出接口
    */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        // 从数据库查询出所有的数据
        List<OrderForm> list = orderFormService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("OrderForm信息表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();

        }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        List<OrderForm> list = reader.readAll(OrderForm.class);

        orderFormService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

