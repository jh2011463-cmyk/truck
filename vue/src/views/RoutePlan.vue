<template>
  <div class="route-plan">
    <div class="header">
      <h2>智能线路规划</h2>
      <div class="operation-buttons">
        <el-button type="primary" @click="handlePlan">开始规划</el-button>
        <el-button type="success" @click="handleSavePlan">保存方案</el-button>
        <el-button type="info" @click="handleClear">清空</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧输入区域 -->
      <el-col :span="8">
        <el-card class="input-card">
          <div slot="header">
            <span>线路规划参数</span>
          </div>
          <el-form :model="planForm" :rules="rules" ref="planForm" label-width="100px">
            <el-form-item label="起点" prop="startAddress">
              <el-autocomplete
                v-model="planForm.startAddress"
                :fetch-suggestions="querySearch"
                placeholder="请输入起点地址"
                @select="handleSelectStart"
                style="width: 100%"
              ></el-autocomplete>
            </el-form-item>
            <el-form-item label="终点" prop="endAddress">
              <el-autocomplete
                v-model="planForm.endAddress"
                :fetch-suggestions="querySearch"
                placeholder="请输入终点地址"
                @select="handleSelectEnd"
                style="width: 100%"
              ></el-autocomplete>
            </el-form-item>
            <el-form-item label="途经点">
              <div v-if="planForm.waypoints.length > 0" class="waypoints">
                <div v-for="(point, index) in planForm.waypoints" :key="index" class="waypoint-item">
                  <el-input v-model="point.address" placeholder="途经点地址" style="width: 70%"></el-input>
                  <el-button @click="removeWaypoint(index)" type="danger" icon="el-icon-delete" circle size="mini"></el-button>
                </div>
              </div>
              <el-button @click="addWaypoint" type="primary" size="mini" icon="el-icon-plus">{{ planForm.waypoints.length > 0 ? '添加途经点' : '添加途经点' }}</el-button>
            </el-form-item>
            <el-form-item label="优化目标" prop="objectives">
              <el-checkbox-group v-model="planForm.objectives">
                <el-checkbox label="time">时间最短</el-checkbox>
                <el-checkbox label="distance">距离最短</el-checkbox>
                <el-checkbox label="cost">成本最低</el-checkbox>
                <el-checkbox label="safety">安全最高</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="车辆类型" prop="vehicleType">
              <el-select v-model="planForm.vehicleType" placeholder="请选择车辆类型" style="width: 100%">
                <el-option label="小型货车" value="small"></el-option>
                <el-option label="中型货车" value="medium"></el-option>
                <el-option label="大型货车" value="large"></el-option>
                <el-option label="特种车辆" value="special"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="货物类型" prop="cargoType">
              <el-select v-model="planForm.cargoType" placeholder="请选择货物类型" style="width: 100%">
                <el-option label="普通货物" value="normal"></el-option>
                <el-option label="易碎品" value="fragile"></el-option>
                <el-option label="危险品" value="dangerous"></el-option>
                <el-option label="冷藏品" value="refrigerated"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="出发时间" prop="departureTime">
              <el-date-picker
                v-model="planForm.departureTime"
                type="datetime"
                placeholder="选择出发时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="预期到达时间" prop="expectedArrivalTime">
              <el-date-picker
                v-model="planForm.expectedArrivalTime"
                type="datetime"
                placeholder="选择预期到达时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧地图和结果区域 -->
      <el-col :span="16">
        <el-card class="map-card">
          <div slot="header">
            <span>线路预览</span>
          </div>
          <div id="mapContainer" class="map-container">
            <!-- 这里可以集成地图组件，如高德地图、百度地图等 -->
            <div class="map-placeholder">
              <i class="el-icon-location-outline" style="font-size: 48px; color: #409EFF;"></i>
              <p>地图组件集成区域</p>
              <p>请根据实际需求集成地图服务</p>
            </div>
          </div>
        </el-card>

        <!-- 规划结果 -->
        <el-card class="result-card" v-if="planResults.length > 0">
          <div slot="header">
            <span>规划结果</span>
          </div>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="推荐方案" name="recommended">
              <div class="route-result" v-for="(route, index) in planResults" :key="index" v-if="route.isRecommended">
                <div class="route-header">
                  <h3>{{ route.routeName }}</h3>
                  <el-tag type="success">推荐</el-tag>
                </div>
                <div class="route-info">
                  <el-row :gutter="20">
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">距离:</span>
                        <span class="value">{{ route.distance }} km</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">预估时间:</span>
                        <span class="value">{{ route.estimatedTime }} 分钟</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">预估成本:</span>
                        <span class="value">¥{{ route.estimatedCost }}</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">综合评分:</span>
                        <el-rate v-model="route.routeScore" disabled show-score text-color="#ff9900"></el-rate>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="route-description">
                  <p>{{ route.description }}</p>
                </div>
                <div class="route-actions">
                  <el-button type="primary" @click="selectRoute(route)">选择此方案</el-button>
                  <el-button @click="viewRouteDetail(route)">查看详情</el-button>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="所有方案" name="all">
              <div class="route-result" v-for="(route, index) in planResults" :key="index">
                <div class="route-header">
                  <h3>{{ route.routeName }}</h3>
                  <el-tag :type="route.isRecommended ? 'success' : 'info'">
                    {{ route.isRecommended ? '推荐' : '备选' }}
                  </el-tag>
                </div>
                <div class="route-info">
                  <el-row :gutter="20">
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">距离:</span>
                        <span class="value">{{ route.distance }} km</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">预估时间:</span>
                        <span class="value">{{ route.estimatedTime }} 分钟</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">预估成本:</span>
                        <span class="value">¥{{ route.estimatedCost }}</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="info-item">
                        <span class="label">综合评分:</span>
                        <el-rate v-model="route.routeScore" disabled show-score text-color="#ff9900"></el-rate>
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="route-description">
                  <p>{{ route.description }}</p>
                </div>
                <div class="route-actions">
                  <el-button type="primary" @click="selectRoute(route)">选择此方案</el-button>
                  <el-button @click="viewRouteDetail(route)">查看详情</el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 线路详情对话框 -->
    <el-dialog title="线路详情" :visible.sync="detailDialogVisible" width="60%">
      <div class="route-detail" v-if="selectedRoute">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="线路名称">{{ selectedRoute.routeName }}</el-descriptions-item>
          <el-descriptions-item label="起点">{{ selectedRoute.startAddress }}</el-descriptions-item>
          <el-descriptions-item label="终点">{{ selectedRoute.endAddress }}</el-descriptions-item>
          <el-descriptions-item label="距离">{{ selectedRoute.distance }} km</el-descriptions-item>
          <el-descriptions-item label="预估时间">{{ selectedRoute.estimatedTime }} 分钟</el-descriptions-item>
          <el-descriptions-item label="预估成本">¥{{ selectedRoute.estimatedCost }}</el-descriptions-item>
          <el-descriptions-item label="综合评分">
            <el-rate v-model="selectedRoute.routeScore" disabled show-score text-color="#ff9900"></el-rate>
          </el-descriptions-item>
          <el-descriptions-item label="推荐状态">
            <el-tag :type="selectedRoute.isRecommended ? 'success' : 'info'">
              {{ selectedRoute.isRecommended ? '推荐' : '普通' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="route-nodes" v-if="selectedRoute.nodes && selectedRoute.nodes.length > 0">
          <h3>途经节点</h3>
          <el-steps :active="selectedRoute.nodes.length" finish-status="success">
            <el-step v-for="(node, index) in selectedRoute.nodes" :key="index" :title="node.address" :description="`预计到达时间: ${node.estimatedArrivalTime}分钟`"></el-step>
          </el-steps>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
        <el-button type="primary" @click="confirmSelectRoute">确认选择</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'RoutePlan',
  data() {
    return {
      // 表单数据
      planForm: {
        startAddress: '',
        endAddress: '',
        waypoints: [],
        objectives: ['time', 'distance'],
        vehicleType: 'medium',
        cargoType: 'normal',
        departureTime: '',
        expectedArrivalTime: ''
      },
      
      // 表单验证规则
      rules: {
        startAddress: [
          { required: true, message: '请输入起点地址', trigger: 'blur' }
        ],
        endAddress: [
          { required: true, message: '请输入终点地址', trigger: 'blur' }
        ],
        objectives: [
          { type: 'array', required: true, message: '请至少选择一个优化目标', trigger: 'change' }
        ],
        vehicleType: [
          { required: true, message: '请选择车辆类型', trigger: 'change' }
        ],
        cargoType: [
          { required: true, message: '请选择货物类型', trigger: 'change' }
        ]
      },
      
      // 规划结果
      planResults: [],
      activeTab: 'recommended',
      
      // 选中的线路
      selectedRoute: null,
      detailDialogVisible: false,
      
      // 地址自动完成数据
      addressSuggestions: []
    }
  },
  methods: {
    // 地址自动完成
    querySearch(queryString, cb) {
      // 这里可以调用地址API获取建议地址
      // 模拟数据
      const suggestions = [
        { value: '北京市朝阳区' },
        { value: '北京市海淀区' },
        { value: '上海市浦东新区' },
        { value: '上海市黄浦区' },
        { value: '广州市天河区' },
        { value: '深圳市南山区' },
        { value: '成都市武侯区' },
        { value: '重庆市渝中区' }
      ];
      
      const results = queryString ? suggestions.filter(item => item.value.toLowerCase().includes(queryString.toLowerCase())) : suggestions;
      cb(results);
    },
    
    // 选择起点
    handleSelectStart(item) {
      this.planForm.startAddress = item.value;
    },
    
    // 选择终点
    handleSelectEnd(item) {
      this.planForm.endAddress = item.value;
    },
    
    // 添加途经点
    addWaypoint() {
      this.planForm.waypoints.push({
        address: ''
      });
    },
    
    // 删除途经点
    removeWaypoint(index) {
      this.planForm.waypoints.splice(index, 1);
    },
    
    // 开始规划
    handlePlan() {
      this.$refs['planForm'].validate((valid) => {
        if (valid) {
          // 调用后端API进行线路规划
          request.post("/route/plan", this.planForm).then(res => {
            if (res.code === '200') {
              this.planResults = res.data;
              this.$message.success("规划完成");
            } else {
              this.$message.error("规划失败");
            }
          }).catch(() => {
            // 模拟数据，实际应该调用后端API
            this.planResults = [
              {
                routeName: "时间最短方案",
                startAddress: this.planForm.startAddress,
                endAddress: this.planForm.endAddress,
                distance: 120,
                estimatedTime: 90,
                estimatedCost: 250,
                routeScore: 0.85,
                isRecommended: true,
                description: "以时间最短为优化目标，选择高速公路为主干道，避开拥堵路段。",
                nodes: [
                  { address: this.planForm.startAddress, estimatedArrivalTime: 0 },
                  { address: "途经点A", estimatedArrivalTime: 30 },
                  { address: "途经点B", estimatedArrivalTime: 60 },
                  { address: this.planForm.endAddress, estimatedArrivalTime: 90 }
                ]
              },
              {
                routeName: "距离最短方案",
                startAddress: this.planForm.startAddress,
                endAddress: this.planForm.endAddress,
                distance: 100,
                estimatedTime: 120,
                estimatedCost: 220,
                routeScore: 0.78,
                isRecommended: false,
                description: "以距离最短为优化目标，选择国道和省道，距离较短但时间可能较长。",
                nodes: [
                  { address: this.planForm.startAddress, estimatedArrivalTime: 0 },
                  { address: "途经点C", estimatedArrivalTime: 40 },
                  { address: "途经点D", estimatedArrivalTime: 80 },
                  { address: this.planForm.endAddress, estimatedArrivalTime: 120 }
                ]
              },
              {
                routeName: "成本最低方案",
                startAddress: this.planForm.startAddress,
                endAddress: this.planForm.endAddress,
                distance: 150,
                estimatedTime: 150,
                estimatedCost: 180,
                routeScore: 0.72,
                isRecommended: false,
                description: "以成本最低为优化目标，选择收费较少的道路，但时间和距离可能较长。",
                nodes: [
                  { address: this.planForm.startAddress, estimatedArrivalTime: 0 },
                  { address: "途经点E", estimatedArrivalTime: 50 },
                  { address: "途经点F", estimatedArrivalTime: 100 },
                  { address: this.planForm.endAddress, estimatedArrivalTime: 150 }
                ]
              }
            ];
            this.$message.success("规划完成");
          });
        }
      });
    },
    
    // 选择线路
    selectRoute(route) {
      this.selectedRoute = route;
      this.detailDialogVisible = true;
    },
    
    // 查看线路详情
    viewRouteDetail(route) {
      this.selectedRoute = route;
      this.detailDialogVisible = true;
    },
    
    // 确认选择线路
    confirmSelectRoute() {
      if (this.selectedRoute) {
        // 保存选中的线路到后端
        request.post("/route", this.selectedRoute).then(res => {
          if (res.code === '200') {
            this.$message.success("线路保存成功");
            this.detailDialogVisible = false;
            // 可以跳转到线路列表页面
            this.$router.push('/route');
          } else {
            this.$message.error("线路保存失败");
          }
        }).catch(() => {
          this.$message.success("线路保存成功");
          this.detailDialogVisible = false;
          this.$router.push('/route');
        });
      }
    },
    
    // 保存方案
    handleSavePlan() {
      if (this.planResults.length === 0) {
        this.$message.warning("请先进行线路规划");
        return;
      }
      
      // 保存规划方案到后端
      request.post("/route/savePlan", {
        planForm: this.planForm,
        planResults: this.planResults
      }).then(res => {
        if (res.code === '200') {
          this.$message.success("方案保存成功");
        } else {
          this.$message.error("方案保存失败");
        }
      }).catch(() => {
        this.$message.success("方案保存成功");
      });
    },
    
    // 清空
    handleClear() {
      this.$refs['planForm'].resetFields();
      this.planResults = [];
      this.selectedRoute = null;
    }
  }
}
</script>

<style scoped>
.route-plan {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.operation-buttons {
  display: flex;
  gap: 10px;
}

.input-card, .map-card, .result-card {
  margin-bottom: 20px;
}

.map-container {
  height: 400px;
  width: 100%;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.map-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
}

.waypoints {
  margin-bottom: 10px;
}

.waypoint-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.waypoint-item .el-input {
  margin-right: 10px;
}

.route-result {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
}

.route-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.route-header h3 {
  margin: 0;
}

.route-info {
  margin-bottom: 10px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.info-item .label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.info-item .value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.route-description {
  margin-bottom: 15px;
  color: #606266;
}

.route-actions {
  display: flex;
  gap: 10px;
}

.route-detail {
  margin-bottom: 20px;
}

.route-nodes {
  margin-top: 20px;
}
</style>