<template>
  <div class="home-dashboard">
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="greeting">
          <h1>欢迎使用智能车辆管理系统</h1>
          <p>高效管理您的运输任务，优化每一条线路。</p>
        </div>
        <div class="user-info">
          <el-avatar icon="el-icon-user-solid" size="medium"></el-avatar>
          <span class="username">{{ user.nickname || user.username || '管理员' }}</span>
        </div>
      </div>
    </el-card>


    <el-row :gutter="20" class="data-stats">
      <el-col :span="6">
        <el-card shadow="hover" class="stats-item total-routes">
          <i class="el-icon-truck stats-icon"></i>
          <div class="stats-detail">
            <div class="stats-value">{{ stats.totalRoutes }}</div>
            <div class="stats-label">总线路数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-item active-vehicles">
          <i class="el-icon-s-flag stats-icon"></i>
          <div class="stats-detail">
            <div class="stats-value">{{ stats.activeVehicles }}</div>
            <div class="stats-label">空闲车辆 (台)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-item pending-orders">
          <i class="el-icon-s-order stats-icon"></i>
          <div class="stats-detail">
            <div class="stats-value">{{ stats.pendingOrders }}</div>
            <div class="stats-label">待审批订单 (个)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-item avg-efficiency">
          <i class="el-icon-data-line stats-icon"></i>
          <div class="stats-detail">
            <div class="stats-value">{{ stats.avgEfficiency }}%</div>
            <div class="stats-label">线路平均效率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card class="module-access-card" shadow="hover">
          <div slot="header">
            <span>核心功能模块</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="module-item" @click="goToRoutePlan">
                <div class="module-icon plan"><i class="el-icon-map-location"></i></div>
                <div class="module-text">
                  <h4>智能线路规划</h4>
                  <p>基于多目标（时间、距离、成本、安全）优化，快速生成最优运输路径。</p>
                </div>
              </div>
            </el-col>

            <el-col :span="12">
              <div class="module-item" @click="goToRouteList">
                <div class="module-icon manage"><i class="el-icon-s-operation"></i></div>
                <div class="module-text">
                  <h4>线路档案管理</h4>
                  <p>对所有已存档线路进行查询、编辑和维护，确保数据准确性。</p>
                </div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="12">
              <div class="module-item" @click="goToRouteAnalysis">
                <div class="module-icon analysis"><i class="el-icon-pie-chart"></i></div>
                <div class="module-text">
                  <h4>运营数据分析</h4>
                  <p>深度分析历史线路数据，评估效率、成本和安全风险，提供优化建议。</p>
                </div>
              </div>
            </el-col>

            <el-col :span="12">
              <div class="module-item" @click="goToTruckManagement">
                <div class="module-icon vehicle"><i class="el-icon-truck"></i></div>
                <div class="module-text">
                  <h4>车辆与司机管理</h4>
                  <p>集中管理车辆档案、状态和驾驶员信息，高效进行任务调度和分配。</p>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card class="todo-list-card" shadow="hover">
          <div slot="header">
            <span>待办事项 / 快速操作</span>
          </div>
          <div class="todo-list">
            <div class="todo-item" @click="goToPendingOrders">
              <i class="el-icon-s-check todo-icon"></i>
              <span class="todo-text">有 **{{ stats.pendingOrders }}** 个订单待审批</span>
              <el-button type="primary" size="mini">立即处理</el-button>
            </div>
            <el-divider></el-divider>
            <div class="todo-item" @click="goToTruckMaintenance">
              <i class="el-icon-setting todo-icon maintenance"></i>
              <span class="todo-text">需处理 **2** 辆车的保养提醒</span>
              <el-button type="warning" size="mini" plain>查看详情</el-button>
            </div>
            <el-divider></el-divider>
            <div class="todo-item" @click="goToRouteList">
              <i class="el-icon-plus todo-icon"></i>
              <span class="todo-text">新增一条华东区域重点线路</span>
              <el-button type="success" size="mini" plain>去新增</el-button>
            </div>
            <el-divider></el-divider>
            <div class="todo-item" @click="goToTruckManagement">
              <i class="el-icon-warning todo-icon risk"></i>
              <span class="todo-text">有 **1** 辆车状态异常（故障）</span>
              <el-button type="danger" size="mini" plain>查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card" shadow="hover" style="margin-top: 20px;">
      <div slot="header">
        <span>实时线路使用趋势</span>
      </div>
      <div class="chart-container">
        <div class="chart-placeholder">
          <i class="el-icon-data-analysis" style="font-size: 48px; color: #909399; margin-bottom: 10px;"></i>
          <p>ECharts/数据可视化组件集成区</p>
          <p style="font-size: 14px; color: #b0b0b0;">可展示近七天线路使用次数或平均效率变化</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem("user") || '{"username": "SystemUser"}'),
      stats: {
        totalRoutes: 156,
        activeVehicles: 35,
        pendingOrders: 12, // 待审批订单数
        avgEfficiency: 85
      },
    };
  },
  created() {
    this.fetchDashboardData();
  },
  methods: {
    // 异步获取仪表盘数据
    fetchDashboardData() {
      // 实际项目中应调用您的 EchartsController 或 DashboardController
      request.get("/echarts/data").then(res => {
        if (res.code === '200' && res.data) {
          this.stats = {
            totalRoutes: res.data.routeCount || this.stats.totalRoutes,
            activeVehicles: res.data.truckActiveCount || this.stats.activeVehicles,
            pendingOrders: res.data.orderPendingCount || this.stats.pendingOrders,
            avgEfficiency: res.data.avgEfficiency || this.stats.avgEfficiency,
          };
        }
      }).catch(() => {
        console.warn("未能获取实时仪表盘数据，正在使用默认数据。");
      });
    },

    // ===================================
    // 路由跳转方法 (已根据 router/index.js 修复路径，并添加了 catch 确保不报错)
    // ===================================
    goToRoutePlan() {
      // 尝试跳转到 /routeplan
      this.$router.push('/routeplan').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error("跳转错误：/routeplan", err);
          this.$message.error("智能线路规划页面跳转失败，请检查路由配置和菜单数据。");
        }
      });
    },
    goToRouteList() {
      // 尝试跳转到 /routelist
      this.$router.push('/routelist').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error("跳转错误：/routelist", err);
          this.$message.error("线路档案管理页面跳转失败，请检查路由配置和菜单数据。");
        }
      });
    },
    goToRouteAnalysis() {
      // 尝试跳转到 /routeanalysis
      this.$router.push('/routeanalysis').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error("跳转错误：/routeanalysis", err);
          this.$message.error("运营数据分析页面跳转失败，请检查路由配置和菜单数据。");
        }
      });
    },
    goToTruckManagement() {
      // /truck 已经验证成功
      this.$router.push('/truck').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error("跳转错误：/truck", err);
          this.$message.error("车辆管理页面跳转失败。");
        }
      });
    },
    goToPendingOrders() {
      // 假设订单审批页面路由为 /orderform (根据您提供的 OrderForm.vue 文件名推断)
      this.$router.push('/orderform').catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error("跳转错误：/orderform", err);
          this.$message.error("待处理订单页面跳转失败，请检查路由配置。");
        }
      });
    },
    goToTruckMaintenance() {
      // 跳转到 /truck，带查询参数进行筛选
      this.$router.push({ path: '/truck', query: { status: 'maintenance' } }).catch(err => {
        if (err.name !== 'NavigationDuplicated') {
          console.error("跳转错误：/truck (maintenance)", err);
          this.$message.error("车辆保养页面跳转失败，请检查路由配置。");
        }
      });
    },
  }
};
</script>

<style scoped>
.home-dashboard {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 50px);
}

/* 欢迎卡片样式 */
.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(to right, #409EFF, #66b1ff);
  color: white;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content h1 {
  margin: 0;
  font-size: 24px;
}

.welcome-content p {
  margin-top: 5px;
  font-size: 14px;
  opacity: 0.9;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info .el-avatar {
  margin-right: 10px;
  border: 2px solid white;
}

.user-info .username {
  font-weight: bold;
}

/* 统计卡片样式 */
.data-stats {
  margin-bottom: 20px;
}

.stats-item {
  height: 100px;
}

.stats-item .el-card__body {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.stats-icon {
  font-size: 40px;
  color: #409EFF;
}

.stats-detail {
  text-align: right;
}

.stats-value {
  font-size: 30px;
  font-weight: bold;
  color: #303133;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

/* 模块快速入口样式 */
.module-access-card {
  height: 100%;
}

.module-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #ffffff;
}

.module-item:hover {
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #409EFF;
}

.module-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}

.module-icon i {
  font-size: 24px;
  color: white;
}

/* 模块特定颜色 */
.module-icon.plan { background-color: #409EFF; }
.module-icon.manage { background-color: #67C23A; }
.module-icon.analysis { background-color: #E6A23C; }
.module-icon.vehicle { background-color: #F56C6C; }

.module-text h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.module-text p {
  margin: 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

/* 待办事项样式 */
.todo-list-card {
  height: 100%;
}

.todo-list {
  padding: 0 10px;
}

.todo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 5px 0;
  cursor: pointer;
}

.todo-item:hover .todo-text {
  color: #409EFF;
}

.todo-icon {
  font-size: 18px;
  color: #409EFF;
  margin-right: 10px;
}

.todo-icon.maintenance {
  color: #E6A23C;
}

.todo-icon.risk {
  color: #F56C6C;
}

.todo-text {
  flex-grow: 1;
  font-size: 14px;
  color: #303133;
}

.el-divider {
  margin: 10px 0;
}

/* 图表区域 */
.chart-card {
  margin-top: 20px;
}

.chart-container {
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
}
</style>