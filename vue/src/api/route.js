import request from '@/utils/request'

// 查询线路列表
export function listRoute(query) {
  return request({
    url: '/route/list',
    method: 'get',
    params: query
  })
}

// 查询线路详细
export function getRoute(id) {
  return request({
    url: '/route/' + id,
    method: 'get'
  })
}

// 新增线路
export function addRoute(data) {
  return request({
    url: '/route',
    method: 'post',
    data: data
  })
}

// 修改线路
export function updateRoute(data) {
  return request({
    url: '/route',
    method: 'put',
    data: data
  })
}

// 删除线路
export function delRoute(id) {
  return request({
    url: '/route/' + id,
    method: 'delete'
  })
}

// 批量删除线路
export function delRoutes(ids) {
  return request({
    url: '/route/' + ids,
    method: 'delete'
  })
}

// 导出线路
export function exportRoute(query) {
  return request({
    url: '/route/export',
    method: 'get',
    params: query
  })
}

// 批量优化线路
export function batchOptimizeRoutes(data) {
  return request({
    url: '/route/optimize/batch',
    method: 'post',
    data: data
  })
}

// 单个线路优化
export function optimizeRoute(data) {
  return request({
    url: '/route/optimize/' + data.routeId,
    method: 'post',
    data: data
  })
}

// 线路分析
export function analyzeRoutes(data) {
  return request({
    url: '/route/analysis',
    method: 'post',
    data: data
  })
}

// 生成分析报告
export function generateAnalysisReport(data) {
  return request({
    url: '/route/analysis/report',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

// 导出分析报告
export function exportAnalysisReport(query) {
  return request({
    url: '/route/analysis/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 智能线路规划
export function planOptimalRoute(data) {
  return request({
    url: '/route/plan/optimal',
    method: 'post',
    data: data
  })
}

// 多目标线路规划
export function planMultiObjectiveRoutes(data) {
  return request({
    url: '/route/plan/multi-objective',
    method: 'post',
    data: data
  })
}

// 计算线路评分
export function calculateRouteScore(id) {
  return request({
    url: '/route/score/' + id,
    method: 'get'
  })
}

// 评估线路风险
export function assessRouteRisk(id) {
  return request({
    url: '/route/risk/' + id,
    method: 'get'
  })
}

// 评估实时交通
export function evaluateRealTimeTraffic(id) {
  return request({
    url: '/route/traffic/' + id,
    method: 'get'
  })
}

// 获取优化建议
export function getOptimizationSuggestions(id) {
  return request({
    url: '/route/suggestions/' + id,
    method: 'get'
  })
}

// 预测线路状况
export function predictRouteConditions(id, timeWindow) {
  return request({
    url: '/route/predict/' + id + '?timeWindow=' + timeWindow,
    method: 'get'
  })
}

// 推荐相似线路
export function recommendSimilarRoutes(id, limit) {
  return request({
    url: '/route/recommend/similar/' + id + '?limit=' + limit,
    method: 'get'
  })
}

// 对比线路
export function compareRoutes(data) {
  return request({
    url: '/route/compare',
    method: 'post',
    data: data
  })
}

// 添加线路节点
export function addRouteNode(data) {
  return request({
    url: '/route/node',
    method: 'post',
    data: data
  })
}

// 获取线路节点
export function getRouteNodes(routeId) {
  return request({
    url: '/route/nodes/' + routeId,
    method: 'get'
  })
}

// 记录线路历史
export function recordRouteHistory(data) {
  return request({
    url: '/route/history',
    method: 'post',
    data: data
  })
}

// 获取线路历史
export function getRouteHistories(routeId) {
  return request({
    url: '/route/history/' + routeId,
    method: 'get'
  })
}

// 获取推荐线路列表
export function getRecommendedRoutes(query) {
  return request({
    url: '/route/recommended',
    method: 'get',
    params: query
  })
}