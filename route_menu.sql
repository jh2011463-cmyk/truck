-- 添加线路管理菜单项
-- 数据库名称: vip-truck
-- 创建时间: 2024-01-01

-- 1. 添加一级菜单 - 线路管理
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路管理', '/route', 'el-icon-location', '线路规划与管理', NULL, NULL, '10');

-- 2. 获取刚插入的一级菜单ID（假设为最新插入的ID）
SET @parent_menu_id = LAST_INSERT_ID();

-- 3. 添加二级菜单 - 线路列表
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路列表', 'routelist', 'el-icon-document', '线路信息管理', @parent_menu_id, 'Route', '1');

-- 4. 添加二级菜单 - 线路规划
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路规划', 'routeplan', 'el-icon-guide', '智能线路规划', @parent_menu_id, 'RoutePlan', '2');

-- 5. 添加二级菜单 - 线路分析
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路分析', 'routeanalysis', 'el-icon-data-analysis', '线路数据分析', @parent_menu_id, 'RouteAnalysis', '3');

-- 6. 为管理员角色添加菜单权限（假设管理员角色ID为1）
-- 获取刚插入的所有菜单ID
SET @menu1_id = @parent_menu_id;
SET @menu2_id = LAST_INSERT_ID() - 2;
SET @menu3_id = LAST_INSERT_ID() - 1;
SET @menu4_id = LAST_INSERT_ID();

-- 为管理员角色添加菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu1_id);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu2_id);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu3_id);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu4_id);