package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.OrderStatus;
import com.example.springboot.entity.OrderForm;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.service.IOrderFormService;
import com.example.springboot.service.IUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.common.Result;
import org.springframework.web.multipart.MultipartFile;
import com.example.springboot.entity.User;
import com.example.springboot.utils.TokenUtils;

import com.example.springboot.service.IQuestionnaireService;
import com.example.springboot.entity.Questionnaire;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 */
@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    @Resource
    private IQuestionnaireService questionnaireService;

    @Resource
    private IOrderFormService orderFormService;

    @Resource
    private IUserService userService;

    private final String now = DateUtil.now();


    @PostMapping("/driverComment")
    @Transactional
    public Result driverComment(@RequestBody Questionnaire questionnaire){
        Integer orderId = questionnaire.getOrderId();
        OrderForm orderForm = orderFormService.getById(orderId);
        if (orderForm != null){
            orderForm.setStatus(String.valueOf(OrderStatus.DONE));
            orderFormService.updateById(orderForm);
        }
        String type = questionnaire.getType();
        if (type == null){
            throw new ServiceException("500","评价类型不能为空");
        }

        if (type.equals("好评")){
            if (orderForm != null){
                Integer userId = orderForm.getUserId();
                User user = userService.getById(userId);
                Integer level = user.getLevel();
                level += 2;
                if (orderForm.getAmount().compareTo(new BigDecimal("5000.00")) > 0){
                    level += 10;
                }
                else if (orderForm.getAmount().compareTo(new BigDecimal("3000.00")) > 0){
                    level += 5;
                }
                user.setLevel(level);
                userService.updateById(user);
            }
        }

        if (type.equals("中评")){
            if (orderForm != null){
                Integer userId = orderForm.getUserId();
                User user = userService.getById(userId);
                Integer level = user.getLevel();
                level += 1;
                if (orderForm.getAmount().compareTo(new BigDecimal("5000.00")) > 0){
                    level += 5;
                }
                else if (orderForm.getAmount().compareTo(new BigDecimal("3000.00")) > 0){
                    level += 2;
                }
                user.setLevel(level);
                userService.updateById(user);
            }
        }

        if (type.equals("差评")){
            if (orderForm != null){
                Integer userId = orderForm.getUserId();
                User user = userService.getById(userId);
                Integer level = user.getLevel();
                level -= 1;
                if (orderForm.getAmount().compareTo(new BigDecimal("5000.00")) > 0){
                    level += 3;
                }
                else if (orderForm.getAmount().compareTo(new BigDecimal("3000.00")) > 0){
                    level += 3;
                }
                user.setLevel(level);
                userService.updateById(user);
            }
        }

        questionnaire.setDate(now);
        questionnaireService.save(questionnaire);
        return Result.success();
    }



    @PostMapping("/userComment")
    @Transactional
    public Result userComment(@RequestBody Questionnaire questionnaire){
        Integer orderId = questionnaire.getOrderId();
        OrderForm orderForm = orderFormService.getById(orderId);
        if (orderForm != null){
            orderForm.setStatus(String.valueOf(OrderStatus.DRIVER_COMMENT));
            orderFormService.updateById(orderForm);
        }
        String type = questionnaire.getType();
        if (type == null){
            throw new ServiceException("500","评价类型不能为空");
        }

        if (type.equals("好评")){
            if (orderForm != null){
                Integer driverId = orderForm.getDriverId();
                User driver = userService.getById(driverId);
                Integer level = driver.getLevel();
                level += 2;
                driver.setLevel(level);
                userService.updateById(driver);
            }
        }

        if (type.equals("中评")){
            if (orderForm != null){
                Integer driverId = orderForm.getDriverId();
                User driver = userService.getById(driverId);
                Integer level = driver.getLevel();
                level += 1;
                driver.setLevel(level);
                userService.updateById(driver);
            }
        }

        if (type.equals("差评")){
            if (orderForm != null){
                Integer driverId = orderForm.getDriverId();
                User driver = userService.getById(driverId);
                Integer level = driver.getLevel();
                level -= 1;
                driver.setLevel(level);
                userService.updateById(driver);
            }
        }

        questionnaire.setDate(now);
        questionnaireService.save(questionnaire);
        return Result.success();
    }

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Questionnaire questionnaire) {
        if (questionnaire.getId() == null) {
            //questionnaire.setTime(DateUtil.now());
            //questionnaire.setUser(TokenUtils.getCurrentUser().getUsername());
        }
        questionnaireService.saveOrUpdate(questionnaire);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        questionnaireService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        questionnaireService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(questionnaireService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(questionnaireService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Questionnaire> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
//        User currentUser = TokenUtils.getCurrentUser();
//        if (currentUser.getRole().equals("ROLE_USER")) {
//            queryWrapper.eq("user", currentUser.getUsername());
//        }
        Page<Questionnaire> page = questionnaireService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Questionnaire> records = page.getRecords();
        for (Questionnaire record : records) {
            Integer driverId = record.getDriverId();
            if (driverId != null){
                User driver = userService.getById(driverId);
                if (driver != null){
                    record.setDriverName(driver.getNickname());
                }
            }
            Integer userId = record.getUserId();
            if (userId != null){
                User user = userService.getById(userId);
                if (user != null){
                    record.setUserName(user.getNickname());
                }
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
        List<Questionnaire> list = questionnaireService.list();
        // 在内存操作，写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("Questionnaire信息表", "UTF-8");
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
        List<Questionnaire> list = reader.readAll(Questionnaire.class);

        questionnaireService.saveBatch(list);
        return Result.success();
    }

    private User getUser() {
        return TokenUtils.getCurrentUser();
    }

}

