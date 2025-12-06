package com.example.springboot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线路规划算法配置类
 * 用于管理算法权重参数，支持动态配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "route.algorithm")
public class RouteAlgorithmConfig {
    
    /**
     * 时间权重（0-1）
     */
    private double timeWeight = 0.35;
    
    /**
     * 距离权重（0-1）
     */
    private double distanceWeight = 0.25;
    
    /**
     * 交通状况权重（0-1）
     */
    private double trafficWeight = 0.20;
    
    /**
     * 成本权重（0-1）
     */
    private double costWeight = 0.15;
    
    /**
     * 安全权重（0-1）
     */
    private double safetyWeight = 0.05;
    
    /**
     * 推荐线路评分阈值
     */
    private double recommendedThreshold = 0.75;
    
    /**
     * 高风险阈值
     */
    private double highRiskThreshold = 0.8;
    
    /**
     * 中等风险阈值
     */
    private double mediumRiskThreshold = 0.6;
    
    /**
     * 交通拥堵阈值（分钟/公里）
     */
    private double trafficCongestionThreshold = 2.0;
    
    /**
     * 最大规划线路数量
     */
    private int maxRouteCount = 10;
    
    /**
     * 相似线路推荐数量
     */
    private int similarRouteLimit = 5;
    
    /**
     * 预测时间窗口（小时）
     */
    private int predictionTimeWindow = 2;
    
    /**
     * 是否启用实时交通数据
     */
    private boolean enableRealTimeTraffic = true;
    
    /**
     * 是否启用历史数据分析
     */
    private boolean enableHistoricalAnalysis = true;
    
    /**
     * 验证权重配置是否合法
     */
    public boolean validateWeights() {
        double totalWeight = timeWeight + distanceWeight + trafficWeight + costWeight + safetyWeight;
        return Math.abs(totalWeight - 1.0) < 0.001;
    }
    
    /**
     * 获取权重配置摘要
     */
    public String getWeightSummary() {
        return String.format("时间:%.2f, 距离:%.2f, 交通:%.2f, 成本:%.2f, 安全:%.2f", 
                timeWeight, distanceWeight, trafficWeight, costWeight, safetyWeight);
    }
}