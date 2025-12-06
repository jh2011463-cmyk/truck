package com.example.springboot.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springboot.config.RouteAlgorithmConfig;
import com.example.springboot.entity.Route;
import com.example.springboot.entity.RouteHistory;
import com.example.springboot.service.IRouteAlgorithmService;
import com.example.springboot.service.IRouteHistoryService;
import com.example.springboot.service.IRouteNodeService;
import com.example.springboot.service.IRouteService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 线路智能算法服务实现类
 * </p>
 *
 * @author
 */
@Service
public class RouteAlgorithmServiceImpl implements IRouteAlgorithmService {

    @Resource
    private IRouteService routeService;

    @Resource
    private IRouteNodeService routeNodeService;

    @Resource
    private IRouteHistoryService routeHistoryService;

    @Resource
    private RouteAlgorithmConfig algorithmConfig;

    @Override
    public Route planOptimalRoute(String startAddress, String endAddress, Map<String, Object> constraints) {
        // 1. 首先检查是否有历史推荐线路
        Route recommendedRoute = findRecommendedRoute(startAddress, endAddress);
        if (recommendedRoute != null) {
            return recommendedRoute;
        }

        // 2. 如果没有推荐线路，使用智能算法规划新线路
        Route newRoute = createNewRoute(startAddress, endAddress, constraints);
        
        // 3. 计算线路综合评分
        double score = calculateComprehensiveScore(newRoute);
        newRoute.setRouteScore(score);
        
        // 4. 如果评分较高，标记为推荐线路
        if (score > 0.75) {
            newRoute.setIsRecommended(1);
        }
        
        return newRoute;
    }

    @Override
    public List<Route> planMultiObjectiveRoutes(String startAddress, String endAddress, List<String> objectives) {
        List<Route> routes = new ArrayList<>();
        
        // 根据不同的优化目标生成多条候选线路
        for (String objective : objectives) {
            Route route = createRouteByObjective(startAddress, endAddress, objective);
            if (route != null) {
                routes.add(route);
            }
        }
        
        // 对线路进行排序
        return routes.stream()
                .sorted((r1, r2) -> Double.compare(r2.getRouteScore(), r1.getRouteScore()))
                .collect(Collectors.toList());
    }

    @Override
    public double calculateComprehensiveScore(Route route) {
        // 获取历史数据
        QueryWrapper<RouteHistory> historyQuery = new QueryWrapper<>();
        historyQuery.eq("route_id", route.getId());
        List<RouteHistory> histories = routeHistoryService.list(historyQuery);
        return calculateComprehensiveScore(route, histories);
    }

    @Override
    public double calculateComprehensiveScore(Route route, List<RouteHistory> histories) {
        // 验证权重配置
        if (!algorithmConfig.validateWeights()) {
            throw new RuntimeException("算法权重配置不合法，权重总和必须为1");
        }

        if (histories == null || histories.isEmpty()) {
            return 0.7; // 默认评分
        }

        double timeScore = calculateTimeScore(route, histories) * algorithmConfig.getTimeWeight();
        double distanceScore = calculateDistanceScore(route) * algorithmConfig.getDistanceWeight();
        double trafficScore = calculateTrafficScore(route, histories) * algorithmConfig.getTrafficWeight();
        double costScore = calculateCostScore(route) * algorithmConfig.getCostWeight();
        double safetyScore = calculateSafetyScore(route, histories) * algorithmConfig.getSafetyWeight();

        return Math.min(1.0, timeScore + distanceScore + trafficScore + costScore + safetyScore);
    }

    @Override
    public int evaluateRealTimeTraffic(Route route) {
        // 模拟实时交通评估
        // 实际应用中应调用地图API获取实时交通数据
        
        Random random = new Random();
        int baseTraffic = route.getTrafficLevel() != null ? route.getTrafficLevel() : 3;
        
        // 考虑时间因素（高峰时段交通较差）
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int timeFactor = (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19) ? -1 : 0;
        
        // 考虑天气因素（恶劣天气交通较差）
        int weatherFactor = random.nextDouble() > 0.8 ? -1 : 0;
        
        int trafficLevel = baseTraffic + timeFactor + weatherFactor;
        return Math.max(1, Math.min(5, trafficLevel));
    }

    @Override
    public int assessRouteRisk(Route route) {
        // 风险评估算法
        int riskLevel = 1; // 默认低风险
        
        // 1. 距离风险（长距离运输风险较高）
        if (route.getDistance() > 500) {
            riskLevel++;
        }
        
        // 2. 时间风险（夜间运输风险较高）
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 22 || hour <= 6) {
            riskLevel++;
        }
        
        // 3. 交通风险
        int trafficRisk = 6 - evaluateRealTimeTraffic(route); // 交通越差风险越高
        riskLevel += Math.max(0, trafficRisk - 3);
        
        return Math.max(1, Math.min(5, riskLevel));
    }

    @Override
    public double analyzeCostBenefit(Route route) {
        // 成本效益分析
        double distance = route.getDistance() != null ? route.getDistance() : 0;
        double estimatedTime = route.getEstimatedTime() != null ? route.getEstimatedTime() : 0;

        if (distance == 0 || estimatedTime == 0) {
            return 0.0;
        }

        // 基础成本计算（燃油费、过路费等）
        double baseCost = distance * 2.5; // 每公里2.5元
        double timeCost = estimatedTime / 60.0 * 50; // 每小时50元人工成本
        double totalCost = baseCost + timeCost;

        // 效益估算（基于运输价值和效率）
        double benefit = distance * 10; // 每公里10元收益

        return benefit / totalCost;
    }

    @Override
    public List<String> generateOptimizationSuggestions(Route route) {
        List<String> suggestions = new ArrayList<>();

        double score = calculateComprehensiveScore(route);

        if (score < 0.6) {
            suggestions.add("线路评分较低，建议选择其他路线");
        }

        if (route.getDistance() > 300) {
            suggestions.add("长距离运输，建议分段运输或增加休息点");
        }

        int trafficLevel = evaluateRealTimeTraffic(route);
        if (trafficLevel <= 2) {
            suggestions.add("当前交通状况较差，建议避开高峰期");
        }

        int riskLevel = assessRouteRisk(route);
        if (riskLevel >= 4) {
            suggestions.add("线路风险较高，建议加强安全措施");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("线路状况良好，可以按计划执行");
        }

        return suggestions;
    }

    @Override
    public Map<String, Object> predictRouteConditions(Route route, int timeWindow) {
        Map<String, Object> predictions = new HashMap<>();

        // 预测未来交通状况
        int currentTraffic = evaluateRealTimeTraffic(route);
        int predictedTraffic = predictFutureTraffic(currentTraffic, timeWindow);
        predictions.put("predictedTraffic", predictedTraffic);

        // 预测行程时间
        double baseTime = route.getEstimatedTime() != null ? route.getEstimatedTime() : 0;
        double predictedTime = baseTime * (6.0 - predictedTraffic) / 3.0;
        predictions.put("predictedTime", Math.round(predictedTime));

        // 预测风险等级
        int predictedRisk = assessRouteRisk(route);
        predictions.put("predictedRisk", predictedRisk);

        return predictions;
    }

    @Override
    public List<Route> recommendSimilarRoutes(Route route, int limit) {
        if (limit <= 0) {
            limit = algorithmConfig.getSimilarRouteLimit();
        }

        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", route.getId());

        // 基于起点和终点的相似性推荐
        if (route.getStartAddress() != null && !route.getStartAddress().isEmpty()) {
            queryWrapper.like("start_address", route.getStartAddress().substring(0, Math.min(3, route.getStartAddress().length())));
        }
        if (route.getEndAddress() != null && !route.getEndAddress().isEmpty()) {
            queryWrapper.like("end_address", route.getEndAddress().substring(0, Math.min(3, route.getEndAddress().length())));
        }
        
        queryWrapper.orderByDesc("route_score");
        queryWrapper.last("LIMIT " + limit);
        
        return routeService.list(queryWrapper);
    }

    @Override
    public Map<String, Object> compareRoutes(List<Route> routes) {
        Map<String, Object> comparison = new HashMap<>();

        if (routes.isEmpty()) {
            return comparison;
        }

        // 计算各线路的评分
        List<Double> scores = routes.stream()
                .map(this::calculateComprehensiveScore)
                .collect(Collectors.toList());
        
        // 找出最优线路
        int bestIndex = 0;
        double bestScore = scores.get(0);
        for (int i = 1; i < scores.size(); i++) {
            if (scores.get(i) > bestScore) {
                bestScore = scores.get(i);
                bestIndex = i;
            }
        }
        
        comparison.put("bestRouteIndex", bestIndex);
        comparison.put("bestRouteScore", bestScore);
        comparison.put("allScores", scores);
        comparison.put("routeCount", routes.size());
        
        return comparison;
    }

    /**
     * @param route 线路对象 
     * @return
     */
    @Override
    public Route optimizeRoute(Route route) {
        return null;
    }

    /**
     * @param params 分析参数 
     * @return
     */
    @Override
    public Map<String, Object> analyzeRoutes(Map<String, Object> params) {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            // 获取所有路线数据
            List<Route> routes = routeService.list();
            
            if (routes == null || routes.isEmpty()) {
                analysis.put("error", "没有找到路线数据");
                return analysis;
            }
            
            // 根据筛选条件过滤路线
            List<Route> filteredRoutes = filterRoutes(routes, params);
            
            if (filteredRoutes.isEmpty()) {
                analysis.put("stats", new HashMap<String, Object>() {{
                    put("totalRoutes", 0);
                    put("avgDistance", 0.0);
                    put("avgTime", 0.0);
                    put("avgScore", 0.0);
                    put("avgRisk", 0.0);
                    put("avgCost", 0);
                    put("avgEfficiency", 0);
                }});
                analysis.put("popularRoutes", new ArrayList<>());
                analysis.put("analysisReport", new HashMap<>());
                return analysis;
            }
            
            // 基本统计信息
            Map<String, Object> stats = calculateStats(filteredRoutes);
            analysis.put("stats", stats);
            
            // 热门路线分析
            List<Map<String, Object>> popularRoutes = buildPopularRoutes(filteredRoutes);
            analysis.put("popularRoutes", popularRoutes);
            
            // 分析报告
            Map<String, Object> report = buildAnalysisReport(filteredRoutes, stats);
            analysis.put("analysisReport", report);
            
        } catch (Exception e) {
            e.printStackTrace();
            analysis.put("error", "分析过程中发生错误: " + e.getMessage());
        }
        
        return analysis;
    }
    
    // 提取统计计算逻辑
    private Map<String, Object> calculateStats(List<Route> routes) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRoutes", routes.size());
        
        double avgDistance = routes.stream()
            .filter(r -> r.getDistance() != null)
            .mapToDouble(Route::getDistance)
            .average()
            .orElse(0.0);
        
        double avgTime = routes.stream()
            .filter(r -> r.getEstimatedTime() != null)
            .mapToDouble(Route::getEstimatedTime)
            .average()
            .orElse(0.0);
        
        double avgScore = routes.stream()
            .filter(r -> r.getRouteScore() != null)
            .mapToDouble(Route::getRouteScore)
            .average()
            .orElse(0.0);
            
        double avgRisk = routes.stream()
            .mapToDouble(this::assessRouteRisk)
            .average()
            .orElse(0.0);
        
        double avgCost = avgDistance * 2.5 + avgTime * 0.5;
        double avgEfficiency = avgDistance > 0 && avgTime > 0 ? (avgDistance / avgTime) * 100 : 0;
        
        stats.put("avgDistance", avgDistance);
        stats.put("avgTime", avgTime);
        stats.put("avgScore", avgScore);
        stats.put("avgRisk", Math.round(avgRisk * 10.0) / 10.0);
        stats.put("avgCost", Math.round(avgCost));
        stats.put("avgEfficiency", Math.round(avgEfficiency));
        
        return stats;
    }
    
    // 提取热门路线构建逻辑
    private List<Map<String, Object>> buildPopularRoutes(List<Route> routes) {
        List<Map<String, Object>> popularRoutes = new ArrayList<>();
        
        for (Route route : routes) {
            Map<String, Object> routeData = new HashMap<>();
            routeData.put("routeName", route.getStartAddress() + "-" + route.getEndAddress());
            routeData.put("startAddress", route.getStartAddress());
            routeData.put("endAddress", route.getEndAddress());
            routeData.put("distance", route.getDistance());
            routeData.put("avgTime", route.getEstimatedTime());
            routeData.put("estimatedTime", route.getEstimatedTime());
            
            double cost = route.getDistance() != null && route.getEstimatedTime() != null ? 
                route.getDistance() * 2.5 + route.getEstimatedTime() * 0.5 : 0;
            routeData.put("avgCost", Math.round(cost));
            
            double efficiency = route.getDistance() != null && route.getEstimatedTime() != null ? 
                (route.getDistance() / route.getEstimatedTime()) * 100 : 0;
            routeData.put("efficiency", Math.round(efficiency));
            
            int riskLevel = assessRouteRisk(route);
            routeData.put("riskLevel", riskLevel);
            
            routeData.put("usageCount", (int)(Math.random() * 50) + 10);
            
            popularRoutes.add(routeData);
        }
        
        // 按使用次数排序
        popularRoutes.sort((a, b) -> Integer.compare((Integer)b.get("usageCount"), (Integer)a.get("usageCount")));
        
        // 只返回前5条
        return popularRoutes.size() > 5 ? popularRoutes.subList(0, 5) : popularRoutes;
    }
    
    // 提取分析报告构建逻辑
    private Map<String, Object> buildAnalysisReport(List<Route> routes, Map<String, Object> stats) {
        Map<String, Object> report = new HashMap<>();
        report.put("generateTime", new Date().toString());
        report.put("totalRoutes", stats.get("totalRoutes"));
        report.put("avgEfficiency", stats.get("avgEfficiency"));
        report.put("avgCost", stats.get("avgCost"));
        
        double avgCost = ((Number) stats.get("avgCost")).doubleValue();
        long avgEfficiency = ((Number) stats.get("avgEfficiency")).longValue();
        report.put("costBenefitRatio", avgEfficiency > 0 ? String.format("%.2f", avgCost / avgEfficiency) : "0");
        
        // 热门起点和终点区域
        Map<String, Long> startRegions = routes.stream()
            .collect(Collectors.groupingBy(r -> getRegionFromAddress(r.getStartAddress()), Collectors.counting()));
        
        Map<String, Long> endRegions = routes.stream()
            .collect(Collectors.groupingBy(r -> getRegionFromAddress(r.getEndAddress()), Collectors.counting()));
        
        List<Map<String, Object>> popularStartRegions = convertRegionMap(startRegions);
        List<Map<String, Object>> popularEndRegions = convertRegionMap(endRegions);
        
        report.put("popularStartRegions", popularStartRegions);
        report.put("popularEndRegions", popularEndRegions);
        
        // 优化建议
        List<String> suggestions = generateReportSuggestions(popularStartRegions, popularEndRegions, routes.size());
        report.put("suggestions", suggestions);
        
        return report;
    }
    
    // 区域Map转换
    private List<Map<String, Object>> convertRegionMap(Map<String, Long> regionMap) {
        return regionMap.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .map(e -> {
                Map<String, Object> region = new HashMap<>();
                region.put("name", e.getKey());
                region.put("count", e.getValue());
                return region;
            })
            .collect(Collectors.toList());
    }
    
    // 生成报告建议
    private List<String> generateReportSuggestions(List<Map<String, Object>> startRegions, 
                                                   List<Map<String, Object>> endRegions,
                                                   int totalRoutes) {
        List<String> suggestions = new ArrayList<>();
        String startRegion = startRegions.isEmpty() ? "华北" : (String) startRegions.get(0).get("name");
        String endRegion = endRegions.isEmpty() ? "华东" : (String) endRegions.get(0).get("name");
        
        suggestions.add("建议增加" + startRegion + "至" + endRegion + "地区的线路班次，满足高需求");
        suggestions.add("优化长途线路的成本结构，提高成本效益");
        suggestions.add("加强高风险线路的安全管理，降低风险等级");
        suggestions.add("在高峰时段增加备选线路，减少拥堵影响");
        
        return suggestions;
    }

    /**
     * @param params 分析参数 
     * @return
     */
    @Override
    public Map<String, Object> generateAnalysisReport(Map<String, Object> params) {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // 获取所有路线数据
            List<Route> routes = routeService.list();
            
            if (routes == null || routes.isEmpty()) {
                report.put("error", "没有找到路线数据");
                return report;
            }
            
            // 根据筛选条件过滤路线
            List<Route> filteredRoutes = filterRoutes(routes, params);
            
            // 基本统计信息
            report.put("generateTime", new Date().toString());
            report.put("totalRoutes", filteredRoutes.size());
            
            // 计算平均距离、平均时间等
            double avgDistance = filteredRoutes.stream()
                .filter(r -> r.getDistance() != null)
                .mapToDouble(Route::getDistance)
                .average()
                .orElse(0.0);
            
            double avgTime = filteredRoutes.stream()
                .filter(r -> r.getEstimatedTime() != null)
                .mapToDouble(Route::getEstimatedTime)
                .average()
                .orElse(0.0);
            
            double avgScore = filteredRoutes.stream()
                .filter(r -> r.getRouteScore() != null)
                .mapToDouble(Route::getRouteScore)
                .average()
                .orElse(0.0);
                
            // 计算平均成本（基于距离和时间）
            double avgCost = avgDistance * 2.5 + avgTime * 0.5; // 简单成本计算
            
            // 计算平均效率（百分比）
            double avgEfficiency = avgDistance > 0 && avgTime > 0 ? (avgDistance / avgTime) * 100 : 0;
            
            report.put("avgEfficiency", Math.round(avgEfficiency));
            report.put("avgCost", Math.round(avgCost));
            report.put("costBenefitRatio", avgEfficiency > 0 ? String.format("%.2f", avgCost / avgEfficiency) : "0");
            
            // 找出最热门路线
            Route mostPopularRoute = filteredRoutes.stream()
                .max(Comparator.comparing(r -> r.getRouteScore() != null ? r.getRouteScore() : 0))
                .orElse(null);
            
            if (mostPopularRoute != null) {
                report.put("mostPopularRoute", mostPopularRoute.getStartAddress() + "-" + mostPopularRoute.getEndAddress());
                report.put("mostPopularCount", (int)(Math.random() * 50) + 30); // 模拟使用次数
            }
            
            // 冷门路线数量
            report.put("coldRouteCount", Math.max(0, filteredRoutes.size() - 5));
            
            // 热门起点和终点区域
            Map<String, Long> startRegions = filteredRoutes.stream()
                .collect(Collectors.groupingBy(r -> getRegionFromAddress(r.getStartAddress()), Collectors.counting()));
            
            Map<String, Long> endRegions = filteredRoutes.stream()
                .collect(Collectors.groupingBy(r -> getRegionFromAddress(r.getEndAddress()), Collectors.counting()));
            
            // 转换为列表并排序
            List<Map<String, Object>> popularStartRegions = startRegions.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(e -> {
                    Map<String, Object> region = new HashMap<>();
                    region.put("name", e.getKey());
                    region.put("count", e.getValue());
                    return region;
                })
                .collect(Collectors.toList());
                
            List<Map<String, Object>> popularEndRegions = endRegions.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(e -> {
                    Map<String, Object> region = new HashMap<>();
                    region.put("name", e.getKey());
                    region.put("count", e.getValue());
                    return region;
                })
                .collect(Collectors.toList());
            
            report.put("popularStartRegions", popularStartRegions);
            report.put("popularEndRegions", popularEndRegions);
            
            // 优化建议
            List<String> suggestions = new ArrayList<>();
            suggestions.add("建议增加" + (popularStartRegions.isEmpty() ? "华北" : popularStartRegions.get(0).get("name")) + 
                          "至" + (popularEndRegions.isEmpty() ? "华东" : popularEndRegions.get(0).get("name")) + "地区的线路班次，满足高需求");
            suggestions.add("优化长途线路的成本结构，提高成本效益");
            suggestions.add("加强高风险线路的安全管理，降低风险等级");
            suggestions.add("在高峰时段增加备选线路，减少拥堵影响");
            
            report.put("suggestions", suggestions);
            
        } catch (Exception e) {
            e.printStackTrace();
            report.put("error", "报告生成过程中发生错误: " + e.getMessage());
        }
        
        return report;
    }
    
    // 辅助方法：根据参数过滤路线
    private List<Route> filterRoutes(List<Route> routes, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return routes;
        }
        
        return routes.stream()
            .filter(route -> {
                // 起点区域过滤
                if (params.containsKey("startRegion") && params.get("startRegion") != null && !params.get("startRegion").toString().isEmpty()) {
                    String startRegion = params.get("startRegion").toString();
                    String routeStartRegion = getRegionFromAddress(route.getStartAddress());
                    if (!startRegion.equals(routeStartRegion)) {
                        return false;
                    }
                }
                
                // 终点区域过滤
                if (params.containsKey("endRegion") && params.get("endRegion") != null && !params.get("endRegion").toString().isEmpty()) {
                    String endRegion = params.get("endRegion").toString();
                    String routeEndRegion = getRegionFromAddress(route.getEndAddress());
                    if (!endRegion.equals(routeEndRegion)) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }
    
    // 辅助方法：从地址中提取区域
    private String getRegionFromAddress(String address) {
        if (address == null || address.isEmpty()) {
            return "未知";
        }
        
        // 根据地址中的关键词判断区域
        if (address.contains("北京") || address.contains("天津") || address.contains("河北") || address.contains("山西") || 
            address.contains("内蒙古") || address.contains("辽宁") || address.contains("吉林") || address.contains("黑龙江")) {
            return "华北地区";
        } else if (address.contains("上海") || address.contains("江苏") || address.contains("浙江") || address.contains("安徽") || 
                   address.contains("福建") || address.contains("江西") || address.contains("山东")) {
            return "华东地区";
        } else if (address.contains("广东") || address.contains("广西") || address.contains("海南") || address.contains("香港") || 
                   address.contains("澳门")) {
            return "华南地区";
        } else if (address.contains("河南") || address.contains("湖北") || address.contains("湖南")) {
            return "华中地区";
        } else if (address.contains("重庆") || address.contains("四川") || address.contains("贵州") || address.contains("云南") || 
                   address.contains("西藏")) {
            return "西南地区";
        } else if (address.contains("陕西") || address.contains("甘肃") || address.contains("青海") || address.contains("宁夏") || 
                   address.contains("新疆")) {
            return "西北地区";
        } else if (address.contains("辽宁") || address.contains("吉林") || address.contains("黑龙江")) {
            return "东北地区";
        } else {
            return "其他地区";
        }
    }

    // 私有辅助方法
    private Route findRecommendedRoute(String startAddress, String endAddress) {
        QueryWrapper<Route> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("start_address", startAddress);
        queryWrapper.eq("end_address", endAddress);
        queryWrapper.eq("is_recommended", 1);
        queryWrapper.orderByDesc("route_score");
        queryWrapper.last("LIMIT 1");
        
        return routeService.getOne(queryWrapper);
    }
    
    private Route createNewRoute(String startAddress, String endAddress, Map<String, Object> constraints) {
        Route route = new Route();
        route.setStartAddress(startAddress);
        route.setEndAddress(endAddress);
        route.setRouteName(startAddress + " -> " + endAddress);
        
        // 设置默认值
        route.setDistance(100.0); // 默认距离
        route.setEstimatedTime(120.0); // 默认时间
        route.setTrafficLevel(3); // 默认交通等级
        route.setRouteScore(0.7); // 默认评分
        route.setIsRecommended(0); // 默认不推荐
        
        // 应用约束条件
        if (constraints != null) {
            if (constraints.containsKey("distance")) {
                route.setDistance((Double) constraints.get("distance"));
            }
            if (constraints.containsKey("time")) {
                route.setEstimatedTime((Double) constraints.get("time"));
            }
            if (constraints.containsKey("traffic")) {
                route.setTrafficLevel((Integer) constraints.get("traffic"));
            }
        }
        
        return route;
    }
    
    private Route createRouteByObjective(String startAddress, String endAddress, String objective) {
        Route route = createNewRoute(startAddress, endAddress, null);
        
        // 根据优化目标调整参数
        switch (objective.toLowerCase()) {
            case "time":
                route.setDistance(80.0); // 时间优化：距离较短
                route.setEstimatedTime(90.0); // 时间优化：时间较短
                route.setRouteName("时间优化线路：" + startAddress + " -> " + endAddress);
                break;
            case "distance":
                route.setDistance(70.0); // 距离优化：距离最短
                route.setEstimatedTime(110.0);
                route.setRouteName("距离优化线路：" + startAddress + " -> " + endAddress);
                break;
            case "cost":
                route.setDistance(90.0); // 成本优化：平衡距离和时间
                route.setEstimatedTime(130.0);
                route.setRouteName("成本优化线路：" + startAddress + " -> " + endAddress);
                break;
            case "safety":
                route.setDistance(110.0); // 安全优化：选择更安全的路线
                route.setEstimatedTime(140.0);
                route.setTrafficLevel(4); // 交通较好
                route.setRouteName("安全优化线路：" + startAddress + " -> " + endAddress);
                break;
            default:
                return null;
        }
        
        return route;
    }
    
    private double calculateTimeScore(Route route, List<RouteHistory> histories) {
        // 基于历史平均时间和当前预估时间计算时间评分
        double avgTime = histories.stream()
                .filter(h -> h.getActualTime() != null)
                .mapToDouble(RouteHistory::getActualTime)
                .average()
                .orElse(route.getEstimatedTime() != null ? route.getEstimatedTime() : 120.0);
        
        double timeRatio = route.getEstimatedTime() != null ? route.getEstimatedTime() / avgTime : 1.0;
        return Math.max(0, 1.0 - Math.abs(timeRatio - 1.0) * 0.5);
    }
    
    private double calculateDistanceScore(Route route) {
        // 距离评分：距离越短评分越高
        double maxDistance = 1000.0; // 最大距离阈值
        double distance = route.getDistance() != null ? route.getDistance() : 100.0;
        double normalizedDistance = Math.min(distance, maxDistance) / maxDistance;
        return 1.0 - normalizedDistance;
    }
    
    private double calculateTrafficScore(Route route, List<RouteHistory> histories) {
        // 交通评分：交通状况越好评分越高
        int trafficLevel = route.getTrafficLevel() != null ? route.getTrafficLevel() : 3;
        
        // 考虑历史交通状况
        if (!histories.isEmpty()) {
            double avgTraffic = histories.stream()
                    .filter(h -> h.getTrafficCondition() != null)
                    .mapToDouble(RouteHistory::getTrafficCondition)
                    .average()
                    .orElse(3.0);
            
            // 将交通状况转换为评分（1-5转换为0-1）
            trafficLevel = (int) ((trafficLevel + avgTraffic) / 2);
        }
        
        return (6.0 - trafficLevel) / 5.0; // 交通等级越高评分越低
    }
    
    private double calculateCostScore(Route route) {
        // 成本评分：成本越低评分越高
        double costPerKm = 2.5; // 每公里成本
        double distance = route.getDistance() != null ? route.getDistance() : 100.0;
        double totalCost = distance * costPerKm;
        double maxCost = 1000.0; // 最大成本阈值
        double normalizedCost = Math.min(totalCost, maxCost) / maxCost;
        return 1.0 - normalizedCost;
    }
    
    private double calculateSafetyScore(Route route, List<RouteHistory> histories) {
        // 安全评分：基于历史用户评分和当前风险评估
        double safetyScore = 0.7; // 默认安全评分
        
        // 基于用户评分
        if (!histories.isEmpty()) {
            double avgUserRating = histories.stream()
                    .filter(h -> h.getUserRating() != null)
                    .mapToDouble(RouteHistory::getUserRating)
                    .average()
                    .orElse(3.0);
            
            // 将用户评分转换为安全评分（1-5转换为0-1）
            safetyScore = avgUserRating / 5.0;
        }
        
        // 考虑当前风险评估
        int riskLevel = assessRouteRisk(route);
        double riskFactor = (6.0 - riskLevel) / 5.0; // 风险等级越高安全评分越低
        
        // 综合安全评分
        safetyScore = (safetyScore + riskFactor) / 2.0;
        
        return Math.max(0, Math.min(1.0, safetyScore));
    }
    
    private int predictFutureTraffic(int currentTraffic, int timeWindow) {
        // 简单的交通预测算法
        Random random = new Random();
        int change = random.nextInt(3) - 1; // -1, 0, 1
        int predicted = currentTraffic + change;
        
        // 考虑时间窗口因素
        if (timeWindow > 60) { // 超过1小时，交通可能变化较大
            predicted += random.nextInt(2) - 1;
        }
        
        return Math.max(1, Math.min(5, predicted));
    }

    @Override
    public Route optimizeRoute(Route route, String objective) {
        if (route == null) {
            return null;
        }

        // 创建优化后的路线副本
        Route optimizedRoute = new Route();
        BeanUtils.copyProperties(route, optimizedRoute);
        optimizedRoute.setId(null); // 清除ID，作为新路线

        // 根据优化目标调整路线参数
        switch (objective.toLowerCase()) {
            case "time":
                // 时间优化：选择更短时间但可能距离稍长的路线
                optimizedRoute.setDistance(route.getDistance() * 0.9);
                optimizedRoute.setEstimatedTime(route.getEstimatedTime() * 0.7);
                optimizedRoute.setTrafficLevel(Math.max(1, route.getTrafficLevel() - 1));
                optimizedRoute.setRouteName("时间优化: " + route.getRouteName());
                break;
            case "distance":
                // 距离优化：选择最短距离的路线
                optimizedRoute.setDistance(route.getDistance() * 0.8);
                optimizedRoute.setEstimatedTime(route.getEstimatedTime() * 0.9);
                optimizedRoute.setRouteName("距离优化: " + route.getRouteName());
                break;
            case "cost":
                // 成本优化：平衡距离和时间，选择最经济的路线
                optimizedRoute.setDistance(route.getDistance() * 0.85);
                optimizedRoute.setEstimatedTime(route.getEstimatedTime() * 0.85);
                optimizedRoute.setTrafficLevel(Math.max(1, route.getTrafficLevel() - 1));
                optimizedRoute.setRouteName("成本优化: " + route.getRouteName());
                break;
            case "safety":
                // 安全优化：选择更安全的路线，可能时间和距离会增加
                optimizedRoute.setDistance(route.getDistance() * 1.1);
                optimizedRoute.setEstimatedTime(route.getEstimatedTime() * 1.1);
                optimizedRoute.setTrafficLevel(Math.max(1, route.getTrafficLevel() - 2));
                optimizedRoute.setRouteName("安全优化: " + route.getRouteName());
                break;
            default:
                // 默认综合优化
                optimizedRoute.setDistance(route.getDistance() * 0.9);
                optimizedRoute.setEstimatedTime(route.getEstimatedTime() * 0.85);
                optimizedRoute.setTrafficLevel(Math.max(1, route.getTrafficLevel() - 1));
                optimizedRoute.setRouteName("综合优化: " + route.getRouteName());
                break;
        }

        // 重新计算路线评分
        List<RouteHistory> histories = routeHistoryService.list(
            new QueryWrapper<RouteHistory>().eq("route_id", route.getId())
        );
        double newScore = calculateComprehensiveScore(optimizedRoute, histories);
        optimizedRoute.setRouteScore(newScore);
        optimizedRoute.setIsRecommended(1); // 标记为推荐路线

        return optimizedRoute;
    }

    @Override
    public Map<String, Object> analyzeRoutes(List<Route> routes) {
        Map<String, Object> analysis = new HashMap<>();
        
        if (routes == null || routes.isEmpty()) {
            analysis.put("error", "没有提供路线数据");
            return analysis;
        }

        // 基本统计信息
        analysis.put("totalRoutes", routes.size());
        
        // 计算平均距离、平均时间等
        double totalDistance = routes.stream()
            .filter(r -> r.getDistance() != null)
            .mapToDouble(Route::getDistance)
            .sum();
        double avgDistance = routes.stream()
            .filter(r -> r.getDistance() != null)
            .mapToDouble(Route::getDistance)
            .average()
            .orElse(0.0);
        
        double totalTime = routes.stream()
            .filter(r -> r.getEstimatedTime() != null)
            .mapToDouble(Route::getEstimatedTime)
            .sum();
        double avgTime = routes.stream()
            .filter(r -> r.getEstimatedTime() != null)
            .mapToDouble(Route::getEstimatedTime)
            .average()
            .orElse(0.0);
        
        double avgScore = routes.stream()
            .filter(r -> r.getRouteScore() != null)
            .mapToDouble(Route::getRouteScore)
            .average()
            .orElse(0.0);
        
        analysis.put("totalDistance", totalDistance);
        analysis.put("avgDistance", avgDistance);
        analysis.put("totalTime", totalTime);
        analysis.put("avgTime", avgTime);
        analysis.put("avgScore", avgScore);
        
        // 按不同维度分析
        List<Map<String, Object>> routeAnalysis = new ArrayList<>();
        for (Route route : routes) {
            Map<String, Object> routeData = new HashMap<>();
            routeData.put("id", route.getId());
            routeData.put("name", route.getRouteName());
            routeData.put("startAddress", route.getStartAddress());
            routeData.put("endAddress", route.getEndAddress());
            routeData.put("distance", route.getDistance());
            routeData.put("estimatedTime", route.getEstimatedTime());
            routeData.put("routeScore", route.getRouteScore());
            
            // 计算效率指标
            double efficiency = route.getDistance() != null && route.getEstimatedTime() != null ? 
                route.getDistance() / route.getEstimatedTime() : 0;
            routeData.put("efficiency", efficiency);
            
            // 评估风险
            int riskLevel = assessRouteRisk(route);
            routeData.put("riskLevel", riskLevel);
            
            // 评估交通状况
            int trafficLevel = evaluateRealTimeTraffic(route);
            routeData.put("trafficLevel", trafficLevel);
            
            routeAnalysis.add(routeData);
        }
        
        analysis.put("routes", routeAnalysis);
        
        // 找出最佳和最差路线
        Route bestRoute = routes.stream()
            .max(Comparator.comparing(r -> r.getRouteScore() != null ? r.getRouteScore() : 0))
            .orElse(null);
        
        Route worstRoute = routes.stream()
            .min(Comparator.comparing(r -> r.getRouteScore() != null ? r.getRouteScore() : 0))
            .orElse(null);
        
        analysis.put("bestRoute", bestRoute != null ? bestRoute.getRouteName() : "无");
        analysis.put("worstRoute", worstRoute != null ? worstRoute.getRouteName() : "无");
        
        // 路线分类统计
        Map<String, Long> routeTypes = new HashMap<>();
        routeTypes.put("shortDistance", routes.stream().filter(r -> r.getDistance() != null && r.getDistance() < 50).count());
        routeTypes.put("mediumDistance", routes.stream().filter(r -> r.getDistance() != null && r.getDistance() >= 50 && r.getDistance() < 200).count());
        routeTypes.put("longDistance", routes.stream().filter(r -> r.getDistance() != null && r.getDistance() >= 200).count());
        
        analysis.put("routeTypes", routeTypes);
        
        return analysis;
    }

    @Override
    public byte[] generateAnalysisReport(List<Route> routes) {
        // 创建Excel工作簿
        ExcelWriter writer = ExcelUtil.getWriter();
        
        try {
            // 创建分析报告标题
            writer.merge(0, 0, 0, 5, "线路分析报告", true);
            
            // 设置表头
            writer.writeRow(Arrays.asList("线路名称", "起点", "终点", "距离(km)", "预计时间(min)", "评分", "效率", "风险等级", "交通状况"));
            
            // 写入路线数据
            int rowNum = 1;
            for (Route route : routes) {
                List<Object> row = new ArrayList<>();
                row.add(route.getRouteName());
                row.add(route.getStartAddress());
                row.add(route.getEndAddress());
                row.add(route.getDistance());
                row.add(route.getEstimatedTime());
                row.add(route.getRouteScore());
                
                // 计算效率
                double efficiency = route.getDistance() != null && route.getEstimatedTime() != null ? 
                    route.getDistance() / route.getEstimatedTime() : 0;
                row.add(String.format("%.2f", efficiency));
                
                // 评估风险
                int riskLevel = assessRouteRisk(route);
                row.add(riskLevel);
                
                // 评估交通状况
                int trafficLevel = evaluateRealTimeTraffic(route);
                row.add(trafficLevel);
                
                writer.writeRow(row);
                rowNum++;
            }
            
            // 添加统计信息
            rowNum += 2;
            writer.merge(rowNum, rowNum, 0, 5, "统计信息", true);
            rowNum++;
            
            // 计算统计数据
            double totalDistance = routes.stream()
                .filter(r -> r.getDistance() != null)
                .mapToDouble(Route::getDistance)
                .sum();
            double avgDistance = routes.stream()
                .filter(r -> r.getDistance() != null)
                .mapToDouble(Route::getDistance)
                .average()
                .orElse(0.0);
            
            double totalTime = routes.stream()
                .filter(r -> r.getEstimatedTime() != null)
                .mapToDouble(Route::getEstimatedTime)
                .sum();
            double avgTime = routes.stream()
                .filter(r -> r.getEstimatedTime() != null)
                .mapToDouble(Route::getEstimatedTime)
                .average()
                .orElse(0.0);
            
            double avgScore = routes.stream()
                .filter(r -> r.getRouteScore() != null)
                .mapToDouble(Route::getRouteScore)
                .average()
                .orElse(0.0);
            
            // 写入统计信息
            writer.writeRow(Arrays.asList("总路线数", routes.size()));
            writer.writeRow(Arrays.asList("总距离", totalDistance + " km"));
            writer.writeRow(Arrays.asList("平均距离", avgDistance + " km"));
            writer.writeRow(Arrays.asList("总时间", totalTime + " 分钟"));
            writer.writeRow(Arrays.asList("平均时间", avgTime + " 分钟"));
            writer.writeRow(Arrays.asList("平均评分", String.format("%.2f", avgScore)));
            
            // 找出最佳和最差路线
            Route bestRoute = routes.stream()
                .max(Comparator.comparing(r -> r.getRouteScore() != null ? r.getRouteScore() : 0))
                .orElse(null);
            
            Route worstRoute = routes.stream()
                .min(Comparator.comparing(r -> r.getRouteScore() != null ? r.getRouteScore() : 0))
                .orElse(null);
            
            if (bestRoute != null) {
                writer.writeRow(Arrays.asList("最佳路线", bestRoute.getRouteName()));
            }
            
            if (worstRoute != null) {
                writer.writeRow(Arrays.asList("最差路线", worstRoute.getRouteName()));
            }
            
            // 转换为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out, true);
            writer.close();
            return out.toByteArray();
        } catch (Exception e) {
            writer.close();
            throw new RuntimeException("生成分析报告失败: " + e.getMessage(), e);
        }
    }
}