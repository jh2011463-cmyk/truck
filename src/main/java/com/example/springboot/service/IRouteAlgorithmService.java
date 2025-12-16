package com.example.springboot.service;

import com.example.springboot.entity.Route;
import com.example.springboot.entity.RouteHistory;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 线路智能算法服务接口
 * </p>
 *
 * @author
 * @since 2024-01-01
 */
public interface IRouteAlgorithmService {

    /**
     * 智能线路规划
     * @param startAddress 起点地址
     * @param endAddress 终点地址
     * @param constraints 约束条件（如时间限制、成本限制等）
     * @return 最优线路
     */
    Route planOptimalRoute(String startAddress, String endAddress, Map<String, Object> constraints);

    /**
     * 多目标线路规划
     * @param startAddress 起点地址
     * @param endAddress 终点地址
     * @param objectives 优化目标（如最短时间、最低成本、最少拥堵等）
     * @return 推荐线路列表
     */
    List<Route> planMultiObjectiveRoutes(String startAddress, String endAddress, List<String> objectives);

    /**
     * 计算线路综合评分
     * @param route 线路对象
     * @return 综合评分（0-1）
     */
    double calculateComprehensiveScore(Route route);

    double calculateComprehensiveScore(Route route, List<RouteHistory> histories);

    /**
     * 实时交通状况评估
     * @param route 线路对象
     * @return 交通状况评分（1-5，1为最差，5为最好）
     */
    int evaluateRealTimeTraffic(Route route);

    /**
     * 线路风险评估
     * @param route 线路对象
     * @return 风险等级（1-5，1为最低风险，5为最高风险）
     */
    int assessRouteRisk(Route route);

    /**
     * 成本效益分析
     * @param route 线路对象
     * @return 成本效益比
     */
    double analyzeCostBenefit(Route route);

    /**
     * 线路优化建议
     * @param route 线路对象
     * @return 优化建议列表
     */
    List<String> generateOptimizationSuggestions(Route route);

    /**
     * 预测线路未来状况
     * @param route 线路对象
     * @param timeWindow 时间窗口（小时）
     * @return 预测结果
     */
    Map<String, Object> predictRouteConditions(Route route, int timeWindow);

    /**
     * 相似线路推荐
     * @param route 参考线路
     * @param limit 推荐数量限制
     * @return 相似线路列表
     */
    List<Route> recommendSimilarRoutes(Route route, int limit);

    /**
     * 线路对比分析
     * @param routes 线路列表
     * @return 对比分析结果
     */
    Map<String, Object> compareRoutes(List<Route> routes);

    /**
     * 线路优化
     * @param route 线路对象
     * @return 优化后的线路
     */
    Route optimizeRoute(Route route);

    /**
     * 按目标列表返回多方案优化结果
     * @param route 线路对象
     * @param objectives 优化目标集合
     * @return 多个优化候选方案
     */
    List<Route> optimizeRoute(Route route, List<String> objectives);

    /**
     * 线路分析
     * @param params 分析参数
     * @return 分析结果
     */
    Map<String, Object> analyzeRoutes(Map<String, Object> params);

    /**
     * 生成分析报告
     * @param params 分析参数
     * @return 分析报告
     */
    Map<String, Object> generateAnalysisReport(Map<String, Object> params);

    Route optimizeRoute(Route route, String objective);

    Map<String, Object> analyzeRoutes(List<Route> routes);

    byte[] generateAnalysisReport(List<Route> routes);
}