-- 智能线路选择功能 - 数据库表结构
-- 数据库名称: vip-truck
-- 创建时间: 2024-01-01

-- 1. 线路表 (route)
CREATE TABLE IF NOT EXISTS `route` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `start_address` varchar(255) NOT NULL COMMENT '起点地址',
  `end_address` varchar(255) NOT NULL COMMENT '终点地址',
  `distance` decimal(8,2) DEFAULT NULL COMMENT '距离（公里）',
  `estimated_time` int(11) DEFAULT NULL COMMENT '预估时间（分钟）',
  `traffic_level` int(11) DEFAULT 3 COMMENT '拥堵等级（1-5）',
  `route_coordinates` text COMMENT '线路坐标（JSON格式）',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `route_score` decimal(3,2) DEFAULT 0.00 COMMENT '线路评分',
  `is_recommended` tinyint(1) DEFAULT 0 COMMENT '是否推荐（0-否，1-是）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_start_end` (`start_address`, `end_address`),
  KEY `idx_score` (`route_score`),
  KEY `idx_recommended` (`is_recommended`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线路信息表';

-- 2. 线路节点表 (route_node)
CREATE TABLE IF NOT EXISTS `route_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_id` int(11) NOT NULL COMMENT '线路ID',
  `node_order` int(11) NOT NULL COMMENT '节点顺序',
  `latitude` decimal(10,6) NOT NULL COMMENT '纬度',
  `longitude` decimal(10,6) NOT NULL COMMENT '经度',
  `address` varchar(255) DEFAULT NULL COMMENT '节点地址',
  `traffic_info` varchar(500) DEFAULT NULL COMMENT '交通信息',
  `estimated_arrival_time` int(11) DEFAULT NULL COMMENT '预计到达时间（分钟）',
  `node_type` tinyint(1) DEFAULT 0 COMMENT '节点类型（0-普通节点，1-关键节点）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_route_id` (`route_id`),
  KEY `idx_node_order` (`route_id`, `node_order`),
  KEY `idx_location` (`latitude`, `longitude`),
  CONSTRAINT `fk_route_node_route` FOREIGN KEY (`route_id`) REFERENCES `route` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线路节点信息表';

-- 3. 历史线路表 (route_history)
CREATE TABLE IF NOT EXISTS `route_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_id` int(11) NOT NULL COMMENT '线路ID',
  `actual_time` int(11) DEFAULT NULL COMMENT '实际耗时（分钟）',
  `traffic_condition` tinyint(1) DEFAULT 1 COMMENT '交通状况（1-畅通，2-缓行，3-拥堵）',
  `driver_id` int(11) DEFAULT NULL COMMENT '司机ID',
  `order_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `weather_condition` varchar(100) DEFAULT NULL COMMENT '天气状况',
  `average_speed` decimal(5,2) DEFAULT NULL COMMENT '平均速度（km/h）',
  `fuel_consumption` decimal(6,2) DEFAULT NULL COMMENT '燃油消耗（升）',
  `user_rating` tinyint(1) DEFAULT NULL COMMENT '用户评分（1-5）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_route_id` (`route_id`),
  KEY `idx_driver_id` (`driver_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_traffic_condition` (`traffic_condition`),
  CONSTRAINT `fk_route_history_route` FOREIGN KEY (`route_id`) REFERENCES `route` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='历史线路信息表';

-- 4. 插入示例数据
INSERT INTO `route` (`start_address`, `end_address`, `distance`, `estimated_time`, `traffic_level`, `route_coordinates`, `route_score`, `is_recommended`, `remark`) VALUES
('北京市朝阳区', '上海市浦东新区', 1200.50, 720, 3, '{"coordinates": [[116.4074,39.9042], [117.1902,39.1071], [121.4737,31.2304]]}', 0.85, 1, '京沪高速主要线路'),
('广州市天河区', '深圳市南山区', 120.30, 90, 2, '{"coordinates": [[113.2644,23.1291], [113.2806,23.1252], [113.9294,22.5335]]}', 0.92, 1, '广深高速线路'),
('成都市武侯区', '重庆市渝中区', 280.75, 210, 4, '{"coordinates": [[104.0665,30.5728], [104.0665,30.5728], [106.5516,29.5630]]}', 0.78, 0, '成渝高速线路');

INSERT INTO `route_node` (`route_id`, `node_order`, `latitude`, `longitude`, `address`, `traffic_info`, `estimated_arrival_time`, `node_type`, `remark`) VALUES
(1, 1, 39.904200, 116.407400, '北京市朝阳区', '畅通', 0, 1, '起点'),
(1, 2, 39.107100, 117.190200, '天津市', '缓行', 120, 0, '途经天津'),
(1, 3, 31.230400, 121.473700, '上海市浦东新区', '畅通', 720, 1, '终点'),
(2, 1, 23.129100, 113.264400, '广州市天河区', '畅通', 0, 1, '起点'),
(2, 2, 23.125200, 113.280600, '广州市区', '缓行', 30, 0, '广州市区路段'),
(2, 3, 22.533500, 113.929400, '深圳市南山区', '畅通', 90, 1, '终点');

INSERT INTO `route_history` (`route_id`, `actual_time`, `traffic_condition`, `driver_id`, `order_id`, `weather_condition`, `average_speed`, `fuel_consumption`, `user_rating`, `remark`) VALUES
(1, 750, 3, 1, 1001, '晴天', 96.5, 120.3, 4, '实际耗时略长于预估'),
(2, 85, 1, 2, 1002, '多云', 85.2, 15.8, 5, '路况良好，准时到达'),
(3, 220, 2, 3, 1003, '小雨', 76.8, 25.6, 3, '雨天行驶较慢');

-- 5. 创建视图 - 线路详细信息视图
CREATE VIEW `route_detail_view` AS
SELECT 
    r.id,
    r.start_address,
    r.end_address,
    r.distance,
    r.estimated_time,
    r.traffic_level,
    r.route_score,
    r.is_recommended,
    r.created_time,
    COUNT(rn.id) as node_count,
    AVG(rh.actual_time) as avg_actual_time,
    AVG(rh.user_rating) as avg_user_rating
FROM `route` r
LEFT JOIN `route_node` rn ON r.id = rn.route_id
LEFT JOIN `route_history` rh ON r.id = rh.route_id
GROUP BY r.id;

-- 6. 创建存储过程 - 计算线路评分
DELIMITER //
CREATE PROCEDURE `calculate_route_score`(IN route_id INT)
BEGIN
    DECLARE avg_time_score DECIMAL(3,2);
    DECLARE avg_traffic_score DECIMAL(3,2);
    DECLARE final_score DECIMAL(3,2);
    
    -- 计算时间评分（基于历史平均时间）
    SELECT AVG(actual_time) INTO avg_time_score 
    FROM route_history 
    WHERE route_id = route_id;
    
    -- 计算交通评分
    SELECT AVG(traffic_condition) INTO avg_traffic_score 
    FROM route_history 
    WHERE route_id = route_id;
    
    -- 综合评分（时间权重60%，交通权重40%）
    SET final_score = (1 - (avg_time_score / 1000)) * 0.6 + 
                     (1 - (avg_traffic_score / 5)) * 0.4;
    
    -- 更新线路评分
    UPDATE route SET route_score = final_score WHERE id = route_id;
END //
DELIMITER ;

-- 7. 创建索引优化查询性能
CREATE INDEX `idx_route_composite` ON `route` (`start_address`, `end_address`, `route_score`);
CREATE INDEX `idx_history_composite` ON `route_history` (`route_id`, `created_time`);

-- 8. 注释说明
-- 表结构说明：
-- route表：存储线路基本信息
-- route_node表：存储线路的详细节点信息
-- route_history表：存储线路执行的历史记录
-- 
-- 索引说明：
-- 为常用查询字段创建了索引，提高查询性能
-- 外键约束确保数据完整性
-- 
-- 使用说明：
-- 1. 在MySQL中执行此SQL文件创建表结构
-- 2. 示例数据可用于测试功能
-- 3. 存储过程可用于定期计算线路评分