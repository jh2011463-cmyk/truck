package com.example.springboot.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Route;
import com.example.springboot.entity.RouteHistory;
import com.example.springboot.entity.RouteNode;
import com.example.springboot.entity.User;
import com.example.springboot.service.IRouteHistoryService;
import com.example.springboot.service.IRouteNodeService;
import com.example.springboot.service.IRouteService;
import com.example.springboot.service.IRouteAlgorithmService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * <p>
 * 线路规划控制器
 * </p>
 *
 * @author
 */
@RestController
@RequestMapping("/route")
public class RouteController {

    @Resource
    private IRouteService routeService;

    @Resource
    private IRouteNodeService routeNodeService;

    @Resource
    private IRouteHistoryService routeHistoryService;

    @Resource
    private IRouteAlgorithmService routeAlgorithmService;

    private final String now = DateUtil.now();

    // 新增或更新线路
    @PostMapping
    public Result save(@RequestBody Route route) {
        if (route.getId() == null) {
            route.setCreatedTime(java.time.LocalDateTime.now());
        }
        route.setUpdatedTime(java.time.LocalDateTime.now());
        
        routeService.saveOrUpdate(route);
        return Result.success();
    }

    // 删除线路
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        routeService.removeById(id);
        return Result.success();
    }

    // 批量删除线路
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        routeService.removeByIds(ids);
        return Result.success();
    }

    // 查询所有线路
    @GetMapping
    public Result findAll() {
        return Result.success(routeService.list());
    }

    // 列表查询（支持分页和过滤）
    @GetMapping("/list")
    public Result listRoute(@RequestParam(defaultValue = "") String routeName,
                           @RequestParam(defaultValue = "") String startAddress,
                           @RequestParam(defaultValue = "") String endAddress,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(required = false) Integer isRecommended) {
        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_time");

        if (routeName != null && !routeName.isEmpty()) {
            queryWrapper.like("route_name", routeName);
        }
        if (startAddress != null && !startAddress.isEmpty()) {
            queryWrapper.like("start_address", startAddress);
        }
        if (endAddress != null && !endAddress.isEmpty()) {
            queryWrapper.like("end_address", endAddress);
        }
        if (isRecommended != null) {
            queryWrapper.eq("is_recommended", isRecommended);
        }

        Page<Route> page = routeService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    // 根据ID查询线路
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        Route route = routeService.getById(id);
        return Result.success(route);
    }

    // 保存规划方案
    @PostMapping("/savePlan")
    public Result savePlan(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> planForm = (Map<String, Object>) request.get("planForm");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> planResults = (List<Map<String, Object>>) request.get("planResults");
            
            if (planForm == null || planResults == null || planResults.isEmpty()) {
                return Result.error("400", "规划方案数据不完整");
            }
            
            // 保存每个规划方案到数据库
            for (Map<String, Object> result : planResults) {
                Route route = new Route();
                route.setRouteName((String) result.get("routeName"));
                route.setStartAddress((String) result.get("startAddress"));
                route.setEndAddress((String) result.get("endAddress"));
                
                // 处理数值类型
                Object distance = result.get("distance");
                if (distance instanceof Number) {
                    route.setDistance(((Number) distance).doubleValue());
                }
                
                Object estimatedTime = result.get("estimatedTime");
                if (estimatedTime instanceof Number) {
                    route.setEstimatedTime(((Number) estimatedTime).doubleValue());
                }
                
                Object routeScore = result.get("routeScore");
                if (routeScore instanceof Number) {
                    route.setRouteScore(((Number) routeScore).doubleValue());
                }
                
                Object isRecommended = result.get("isRecommended");
                if (isRecommended instanceof Boolean) {
                    route.setIsRecommended(((Boolean) isRecommended) ? 1 : 0);
                }
                
                route.setRemark((String) result.get("description"));
                route.setCreatedTime(java.time.LocalDateTime.now());
                route.setUpdatedTime(java.time.LocalDateTime.now());
                
                routeService.save(route);
            }
            
            return Result.success("规划方案保存成功");
        } catch (Exception e) {
            return Result.error("500", "规划方案保存失败: " + e.getMessage());
        }
    }

    // 智能线路规划
    @PostMapping("/plan")
    public Result planRoute(@RequestBody Map<String, String> request) {
        String startAddress = request.get("startAddress");
        String endAddress = request.get("endAddress");
        
        if (startAddress == null || endAddress == null) {
            return Result.error("400", "起点和终点地址不能为空");
        }

        // 提取约束条件
        Map<String, Object> constraints = new HashMap<>();
        if (request.containsKey("timeLimit")) {
            constraints.put("timeLimit", request.get("timeLimit"));
        }
        if (request.containsKey("maxDistance")) {
            constraints.put("maxDistance", request.get("maxDistance"));
        }

        // 使用智能算法服务规划线路
        Route optimalRoute = routeAlgorithmService.planOptimalRoute(startAddress, endAddress, constraints);
        
        // 如果是新规划的线路，保存到数据库
        if (optimalRoute.getId() == null) {
            routeService.save(optimalRoute);
        }
        
        return Result.success(optimalRoute);
    }

    // 多目标线路规划
    @PostMapping("/plan/multi")
    public Result planMultiObjectiveRoutes(@RequestBody Map<String, Object> request) {
        String startAddress = (String) request.get("startAddress");
        String endAddress = (String) request.get("endAddress");
        
        if (startAddress == null || endAddress == null) {
            return Result.error("400", "起点和终点地址不能为空");
        }

        @SuppressWarnings("unchecked")
        List<String> objectives = (List<String>) request.get("objectives");
        if (objectives == null || objectives.isEmpty()) {
            objectives = Arrays.asList("time", "cost", "safety");
        }

        List<Route> routes = routeAlgorithmService.planMultiObjectiveRoutes(startAddress, endAddress, objectives);
        return Result.success(routes);
    }

    // 线路评分计算
    @PostMapping("/calculateScore/{id}")
    public Result calculateScore(@PathVariable Integer id) {
        Route route = routeService.getById(id);
        if (route == null) {
            return Result.error("404", "线路不存在");
        }

        // 使用智能算法服务计算综合评分
        double score = routeAlgorithmService.calculateComprehensiveScore(route);
        route.setRouteScore(score);
        
        // 如果评分高于阈值，标记为推荐线路
        if (score > 0.75) {
            route.setIsRecommended(1);
        }
        
        routeService.updateById(route);
        
        // 返回评分详情
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("riskLevel", routeAlgorithmService.assessRouteRisk(route));
        result.put("trafficLevel", routeAlgorithmService.evaluateRealTimeTraffic(route));
        result.put("suggestions", routeAlgorithmService.generateOptimizationSuggestions(route));
        
        return Result.success(result);
    }

    // 线路风险评估
    @GetMapping("/assessRisk/{id}")
    public Result assessRisk(@PathVariable Integer id) {
        Route route = routeService.getById(id);
        if (route == null) {
            return Result.error("404", "线路不存在");
        }

        int riskLevel = routeAlgorithmService.assessRouteRisk(route);
        return Result.success(riskLevel);
    }

    // 实时交通评估
    @GetMapping("/evaluateTraffic/{id}")
    public Result evaluateTraffic(@PathVariable Integer id) {
        Route route = routeService.getById(id);
        if (route == null) {
            return Result.error("404", "线路不存在");
        }

        int trafficLevel = routeAlgorithmService.evaluateRealTimeTraffic(route);
        return Result.success(trafficLevel);
    }

    // 线路优化建议
    @GetMapping("/optimizationSuggestions/{id}")
    public Result getOptimizationSuggestions(@PathVariable Integer id) {
        Route route = routeService.getById(id);
        if (route == null) {
            return Result.error("404", "线路不存在");
        }

        List<String> suggestions = routeAlgorithmService.generateOptimizationSuggestions(route);
        return Result.success(suggestions);
    }

    // 线路预测
    @PostMapping("/predict/{id}")
    public Result predictRouteConditions(@PathVariable Integer id, @RequestParam(defaultValue = "2") int timeWindow) {
        Route route = routeService.getById(id);
        if (route == null) {
            return Result.error("404", "线路不存在");
        }

        Map<String, Object> predictions = routeAlgorithmService.predictRouteConditions(route, timeWindow);
        return Result.success(predictions);
    }

    // 相似线路推荐
    @GetMapping("/similar/{id}")
    public Result recommendSimilarRoutes(@PathVariable Integer id, @RequestParam(defaultValue = "5") int limit) {
        Route route = routeService.getById(id);
        if (route == null) {
            return Result.error("404", "线路不存在");
        }

        List<Route> similarRoutes = routeAlgorithmService.recommendSimilarRoutes(route, limit);
        return Result.success(similarRoutes);
    }

    // 线路对比分析
    @PostMapping("/compare")
    public Result compareRoutes(@RequestBody List<Integer> routeIds) {
        if (routeIds == null || routeIds.isEmpty()) {
            return Result.error("400", "请提供要对比的线路ID列表");
        }

        List<Route> routes = routeService.listByIds(routeIds);
        if (routes.size() != routeIds.size()) {
            return Result.error("404", "部分线路不存在");
        }

        Map<String, Object> comparison = routeAlgorithmService.compareRoutes(routes);
        return Result.success(comparison);
    }

    // 添加线路节点
    @PostMapping("/{routeId}/nodes")
    public Result addNode(@PathVariable Integer routeId, @RequestBody RouteNode routeNode) {
        routeNode.setRouteId(routeId);
        routeNodeService.save(routeNode);
        return Result.success();
    }

    // 获取线路的所有节点
    @GetMapping("/{routeId}/nodes")
    public Result getNodes(@PathVariable Integer routeId) {
        QueryWrapper<RouteNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("route_id", routeId);
        queryWrapper.orderByAsc("node_order");
        List<RouteNode> nodes = routeNodeService.list(queryWrapper);
        return Result.success(nodes);
    }

    // 记录线路历史
    @PostMapping("/history")
    public Result addHistory(@RequestBody RouteHistory routeHistory) {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser != null) {
            routeHistory.setDriverId(currentUser.getId());
        }
        
        routeHistory.setCreatedTime(java.time.LocalDateTime.now());
        routeHistoryService.save(routeHistory);
        
        // 更新线路评分
        Route route = routeService.getById(routeHistory.getRouteId());
        if (route != null) {
            double newScore = calculateRouteScore(route);
            route.setRouteScore(newScore);
            routeService.updateById(route);
        }
        
        return Result.success();
    }

    // 获取线路历史记录
    @GetMapping("/{routeId}/history")
    public Result getHistory(@PathVariable Integer routeId,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<RouteHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("route_id", routeId);
        queryWrapper.orderByDesc("created_time");
        
        Page<RouteHistory> page = routeHistoryService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    // 获取推荐线路列表
    @GetMapping("/recommended")
    public Result getRecommendedRoutes() {
        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_recommended", 1);
        queryWrapper.orderByDesc("route_score");
        queryWrapper.last("LIMIT 10");
        
        List<Route> recommendedRoutes = routeService.list(queryWrapper);
        return Result.success(recommendedRoutes);
    }

    // 移除旧的算法方法，使用新的智能算法服务
    // 智能算法：计算最优线路
    private Route calculateOptimalRoute(String startAddress, String endAddress) {
        return routeAlgorithmService.planOptimalRoute(startAddress, endAddress, new HashMap<>());
    }

    // 计算线路评分
    private double calculateRouteScore(Route route) {
        return routeAlgorithmService.calculateComprehensiveScore(route);
    }

    // 批量优化线路
    @PostMapping("/optimize/batch")
    public Result optimizeBatch(@RequestBody List<Integer> ids) {
        try {
            List<Route> optimizedRoutes = new ArrayList<>();
            for (Integer id : ids) {
                Route route = routeService.getById(id);
                if (route != null) {
                    // 使用智能算法服务优化线路
                    Route optimizedRoute = routeAlgorithmService.optimizeRoute(route);
                    routeService.updateById(optimizedRoute);
                    optimizedRoutes.add(optimizedRoute);
                }
            }
            return Result.success(optimizedRoutes);
        } catch (Exception e) {
            return Result.error("批量优化失败: " + e.getMessage());
        }
    }

    // 单个线路优化
    @PostMapping("/optimize/{id}")
    public Result optimizeRoute(@PathVariable Integer id) {
        try {
            Route route = routeService.getById(id);
            if (route == null) {
                return Result.error("404", "线路不存在");
            }
            
            // 使用智能算法服务优化线路
            Route optimizedRoute = routeAlgorithmService.optimizeRoute(route);
            routeService.updateById(optimizedRoute);
            
            return Result.success(optimizedRoute);
        } catch (Exception e) {
            return Result.error("线路优化失败: " + e.getMessage());
        }
    }

    // 线路分析接口
    @PostMapping("/analysis")
    public Result analysisRoute(@RequestBody Map<String, Object> params) {
        try {
            Map<String, Object> result = routeAlgorithmService.analyzeRoutes(params);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("线路分析失败: " + e.getMessage());
        }
    }

    // 生成分析报告
    @PostMapping("/analysis/report")
    public Result generateAnalysisReport(@RequestBody Map<String, Object> params) {
        try {
            Map<String, Object> report = routeAlgorithmService.generateAnalysisReport(params);
            return Result.success(report);
        } catch (Exception e) {
            return Result.error("报告生成失败: " + e.getMessage());
        }
    }

    // 导出分析报告
    @GetMapping("/analysis/export")
    public void exportAnalysisReport(HttpServletResponse response,
                                     @RequestParam(defaultValue = "") String startDate,
                                     @RequestParam(defaultValue = "") String endDate,
                                     @RequestParam(defaultValue = "") String startRegion,
                                     @RequestParam(defaultValue = "") String endRegion,
                                     @RequestParam(defaultValue = "") String distanceRange,
                                     @RequestParam(defaultValue = "") String analysisTypes) throws Exception {
        
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("startRegion", startRegion);
        params.put("endRegion", endRegion);
        params.put("distanceRange", distanceRange);
        params.put("analysisTypes", analysisTypes);
        
        Map<String, Object> reportData = routeAlgorithmService.generateAnalysisReport(params);
        
        // 在内存操作，写出到浏览器
        cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
        
        // 写入报告内容
        writer.writeCellValue(0, 0, "线路数据分析报告");
        writer.writeCellValue(0, 1, "生成时间: " + reportData.get("generateTime"));
        writer.writeCellValue(0, 2, "总线路数: " + reportData.get("totalRoutes"));
        writer.writeCellValue(0, 3, "最热门线路: " + reportData.get("mostPopularRoute"));
        writer.writeCellValue(0, 4, "使用次数: " + reportData.get("mostPopularCount"));
        writer.writeCellValue(0, 5, "平均效率: " + reportData.get("avgEfficiency") + "%");
        writer.writeCellValue(0, 6, "平均成本: ¥" + reportData.get("avgCost"));
        
        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = java.net.URLEncoder.encode("线路分析报告", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        javax.servlet.ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    // 导出线路数据
    @GetMapping("/export")
    public void exportRouteData(HttpServletResponse response,
                               @RequestParam(defaultValue = "") String startAddress,
                               @RequestParam(defaultValue = "") String endAddress,
                               @RequestParam(required = false) Integer isRecommended) throws Exception {
            
        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        if (startAddress != null && !startAddress.isEmpty()) {
            queryWrapper.like("start_address", startAddress);
        }
        if (endAddress != null && !endAddress.isEmpty()) {
            queryWrapper.like("end_address", endAddress);
        }
        if (isRecommended != null) {
            queryWrapper.eq("is_recommended", isRecommended);
        }
        queryWrapper.orderByDesc("created_time");
            
        List<Route> routes = routeService.list(queryWrapper);
            
        // 在内存中构建表格
        cn.hutool.poi.excel.ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
            
        // 写入表格标题
        writer.writeHeadRow(java.util.Arrays.asList(
            "ID", "线路名称", "起点", "终点", 
            "距离(km)", "预计时间(分钟)", "交通状况", 
            "线路评分", "是否推荐", "创建时间"
        ));
            
        // 写入数据行
        int rowNum = 1;
        for (Route route : routes) {
            writer.writeCellValue(0, rowNum, route.getId());
            writer.writeCellValue(1, rowNum, route.getRouteName());
            writer.writeCellValue(2, rowNum, route.getStartAddress());
            writer.writeCellValue(3, rowNum, route.getEndAddress());
            writer.writeCellValue(4, rowNum, route.getDistance());
            writer.writeCellValue(5, rowNum, route.getEstimatedTime());
            writer.writeCellValue(6, rowNum, route.getTrafficLevel());
            writer.writeCellValue(7, rowNum, route.getRouteScore());
            writer.writeCellValue(8, rowNum, route.getIsRecommended() == 1 ? "是" : "否");
            writer.writeCellValue(9, rowNum, route.getCreatedTime());
            rowNum++;
        }
            
        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = java.net.URLEncoder.encode("线路数据", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
    
        javax.servlet.ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }
    
    // 模拟计算距离（实际应用中应调用地图API）
    private double calculateDistance(String startAddress, String endAddress) {
        // 这里简化处理，实际应该调用地图API计算实际距离
        return 100.0 + Math.random() * 900; // 模拟100-1000公里的距离
    }

    // 模拟计算预估时间（实际应用中应基于距离和平均速度计算）
    private int calculateEstimatedTime(double distance) {
        // 假设平均速度60km/h
        return (int) (distance / 60 * 60); // 转换为分钟
    }

}