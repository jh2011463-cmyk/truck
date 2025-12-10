-- Add route management menu items
INSERT INTO sys_menu (name, path, icon, description, pid, page_path, sort_num) 
VALUES ('线路管理', '/route', 'el-icon-location', '线路规划与管理', NULL, NULL, 10);

INSERT INTO sys_menu (name, path, icon, description, pid, page_path, sort_num) 
VALUES ('线路列表', 'routelist', 'el-icon-document', '线路信息管理', 1, 'Route', 1);

INSERT INTO sys_menu (name, path, icon, description, pid, page_path, sort_num) 
VALUES ('线路规划', 'routeplan', 'el-icon-guide', '智能线路规划', 1, 'RoutePlan', 2);

INSERT INTO sys_menu (name, path, icon, description, pid, page_path, sort_num) 
VALUES ('线路分析', 'routeanalysis', 'el-icon-data-analysis', '线路数据分析', 1, 'RouteAnalysis', 3);

-- Add menu permissions for admin role
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4);