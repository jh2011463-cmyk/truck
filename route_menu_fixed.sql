-- Add route management menu items
-- Database: vip-truck
-- Created: 2024-01-01

-- 1. Add first level menu - Route Management
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路管理', '/route', 'el-icon-location', '线路规划与管理', NULL, NULL, '10');

-- 2. Get the inserted first level menu ID
SET @parent_menu_id = LAST_INSERT_ID();

-- 3. Add second level menu - Route List
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路列表', 'routelist', 'el-icon-document', '线路信息管理', @parent_menu_id, 'Route', '1');

-- 4. Add second level menu - Route Planning
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路规划', 'routeplan', 'el-icon-guide', '智能线路规划', @parent_menu_id, 'RoutePlan', '2');

-- 5. Add second level menu - Route Analysis
INSERT INTO `sys_menu` (`name`, `path`, `icon`, `description`, `pid`, `page_path`, `sort_num`) 
VALUES ('线路分析', 'routeanalysis', 'el-icon-data-analysis', '线路数据分析', @parent_menu_id, 'RouteAnalysis', '3');

-- 6. Add menu permissions for admin role (assuming role_id=1)
-- Get all inserted menu IDs
SET @menu1_id = @parent_menu_id;
SET @menu2_id = LAST_INSERT_ID() - 2;
SET @menu3_id = LAST_INSERT_ID() - 1;
SET @menu4_id = LAST_INSERT_ID();

-- Add menu permissions for admin role
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu1_id);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu2_id);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu3_id);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @menu4_id);