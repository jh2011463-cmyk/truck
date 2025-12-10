<template>
  <div class="route-analysis">
    <div class="header">
      <h2>线路数据分析</h2>
      <div class="operation-buttons">
        <el-button type="primary" @click="handleAnalyze">开始分析</el-button>
        <el-button type="success" @click="handleExport">导出报告</el-button>
        <el-button type="info" @click="handleRefresh">刷新数据</el-button>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧筛选区域 -->
      <el-col :span="6">
        <el-card class="filter-card">
          <div slot="header">
            <span>分析条件</span>
          </div>
          <el-form :model="filterForm" label-width="80px" size="small">
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="filterForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="起点区域">
              <el-select v-model="filterForm.startRegion" placeholder="请选择" style="width: 100%">
                <el-option label="全部" value=""></el-option>
                <el-option label="华北地区" value="north"></el-option>
                <el-option label="华东地区" value="east"></el-option>
                <el-option label="华南地区" value="south"></el-option>
                <el-option label="华中地区" value="central"></el-option>
                <el-option label="西南地区" value="southwest"></el-option>
                <el-option label="西北地区" value="northwest"></el-option>
                <el-option label="东北地区" value="northeast"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="终点区域">
              <el-select v-model="filterForm.endRegion" placeholder="请选择" style="width: 100%">
                <el-option label="全部" value=""></el-option>
                <el-option label="华北地区" value="north"></el-option>
                <el-option label="华东地区" value="east"></el-option>
                <el-option label="华南地区" value="south"></el-option>
                <el-option label="华中地区" value="central"></el-option>
                <el-option label="西南地区" value="southwest"></el-option>
                <el-option label="西北地区" value="northwest"></el-option>
                <el-option label="东北地区" value="northeast"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="距离范围">
              <el-slider
                v-model="filterForm.distanceRange"
                range
                :min="0"
                :max="2000"
                :step="50"
                show-stops
                show-input
                style="width: 100%">
              </el-slider>
            </el-form-item>
            <el-form-item label="分析类型">
              <el-checkbox-group v-model="filterForm.analysisTypes">
                <el-checkbox label="efficiency">效率分析</el-checkbox>
                <el-checkbox label="cost">成本分析</el-checkbox>
                <el-checkbox label="safety">安全分析</el-checkbox>
                <el-checkbox label="traffic">交通分析</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧分析结果区域 -->
      <el-col :span="18">
        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <div class="stats-icon" style="background-color: #409EFF;">
                  <i class="el-icon-location"></i>
                </div>
                <div class="stats-info">
                  <div class="stats-value">{{ stats.totalRoutes }}</div>
                  <div class="stats-label">总线路数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <div class="stats-icon" style="background-color: #67C23A;">
                  <i class="el-icon-s-flag"></i>
                </div>
                <div class="stats-info">
                  <div class="stats-value">{{ stats.avgEfficiency }}%</div>
                  <div class="stats-label">平均效率</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <div class="stats-icon" style="background-color: #E6A23C;">
                  <i class="el-icon-coin"></i>
                </div>
                <div class="stats-info">
                  <div class="stats-value">¥{{ stats.avgCost }}</div>
                  <div class="stats-label">平均成本</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stats-card">
              <div class="stats-content">
                <div class="stats-icon" style="background-color: #F56C6C;">
                  <i class="el-icon-warning"></i>
                </div>
                <div class="stats-info">
                  <div class="stats-value">{{ stats.avgRisk }}</div>
                  <div class="stats-label">平均风险</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 图表区域 -->
        <el-tabs v-model="activeTab" class="chart-tabs">
          <el-tab-pane label="效率分析" name="efficiency" v-if="filterForm.analysisTypes.includes('efficiency')">
            <el-card class="chart-card">
              <div id="efficiencyChart" class="chart-container"></div>
            </el-card>
          </el-tab-pane>
          <el-tab-pane label="成本分析" name="cost" v-if="filterForm.analysisTypes.includes('cost')">
            <el-card class="chart-card">
              <div id="costChart" class="chart-container"></div>
            </el-card>
          </el-tab-pane>
          <el-tab-pane label="安全分析" name="safety" v-if="filterForm.analysisTypes.includes('safety')">
            <el-card class="chart-card">
              <div id="safetyChart" class="chart-container"></div>
            </el-card>
          </el-tab-pane>
          <el-tab-pane label="交通分析" name="traffic" v-if="filterForm.analysisTypes.includes('traffic')">
            <el-card class="chart-card">
              <div id="trafficChart" class="chart-container"></div>
            </el-card>
          </el-tab-pane>
        </el-tabs>

        <!-- 热门线路表格 -->
        <el-card class="table-card">
          <div slot="header">
            <span>热门线路分析</span>
          </div>
          <el-table :data="popularRoutes" border style="width: 100%">
            <el-table-column prop="routeName" label="线路名称"></el-table-column>
            <el-table-column prop="startAddress" label="起点"></el-table-column>
            <el-table-column prop="endAddress" label="终点"></el-table-column>
            <el-table-column prop="usageCount" label="使用次数" width="100"></el-table-column>
            <el-table-column prop="avgTime" label="平均时间(分钟)" width="130"></el-table-column>
            <el-table-column prop="avgCost" label="平均成本" width="100">
              <template slot-scope="scope">
                ¥{{ scope.row.avgCost }}
              </template>
            </el-table-column>
            <el-table-column prop="efficiency" label="效率评分" width="120">
              <template slot-scope="scope">
                <el-progress :percentage="scope.row.efficiency" :color="getProgressColor(scope.row.efficiency)"></el-progress>
              </template>
            </el-table-column>
            <el-table-column prop="riskLevel" label="风险等级" width="100">
              <template slot-scope="scope">
                <el-tag :type="getRiskTagType(scope.row.riskLevel)">{{ getRiskText(scope.row.riskLevel) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分析报告对话框 -->
    <el-dialog title="分析报告" :visible.sync="reportDialogVisible" width="80%">
      <div class="report-content" v-if="analysisReport">
        <h2>线路数据分析报告</h2>
        <p class="report-time">生成时间: {{ analysisReport.generateTime }}</p>
        
        <div class="report-section">
          <h3>1. 总体概况</h3>
          <p>本报告基于 {{ analysisReport.totalRoutes }} 条线路数据，分析了时间范围内的线路使用情况、效率、成本和风险等方面。</p>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="report-item">
                <h4>线路使用情况</h4>
                <p>最热门线路: {{ analysisReport.mostPopularRoute }}</p>
                <p>使用次数: {{ analysisReport.mostPopularCount }} 次</p>
                <p>冷门线路数量: {{ analysisReport.coldRouteCount }} 条</p>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="report-item">
                <h4>效率与成本</h4>
                <p>平均效率: {{ analysisReport.avgEfficiency }}%</p>
                <p>平均成本: ¥{{ analysisReport.avgCost }}</p>
                <p>成本效益比: {{ analysisReport.costBenefitRatio }}</p>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="report-section">
          <h3>2. 区域分析</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="report-item">
                <h4>热门起点区域</h4>
                <ul>
                  <li v-for="(region, index) in analysisReport.popularStartRegions" :key="index">
                    {{ region.name }}: {{ region.count }} 条线路
                  </li>
                </ul>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="report-item">
                <h4>热门终点区域</h4>
                <ul>
                  <li v-for="(region, index) in analysisReport.popularEndRegions" :key="index">
                    {{ region.name }}: {{ region.count }} 条线路
                  </li>
                </ul>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="report-section">
          <h3>3. 优化建议</h3>
          <ul>
            <li v-for="(suggestion, index) in analysisReport.suggestions" :key="index">
              {{ suggestion }}
            </li>
          </ul>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reportDialogVisible = false">关 闭</el-button>
        <el-button type="primary" @click="exportReport">导出报告</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 引入ECharts
import * as echarts from 'echarts';
import request from '@/utils/request';

export default {
  name: 'RouteAnalysis',
  data() {
    return {
      // 筛选表单
      filterForm: {
        dateRange: [],
        startRegion: '',
        endRegion: '',
        distanceRange: [0, 500],
        analysisTypes: ['efficiency', 'cost', 'safety', 'traffic']
      },
      
      // 当前激活的标签页
      activeTab: 'efficiency',
      
      // 统计数据
      stats: {
        totalRoutes: 156,
        avgEfficiency: 85,
        avgCost: 320,
        avgRisk: 2.3
      },
      
      // 热门线路数据
      popularRoutes: [
        {
          routeName: '北京-上海专线',
          startAddress: '北京市朝阳区',
          endAddress: '上海市浦东新区',
          usageCount: 45,
          avgTime: 720,
          avgCost: 1200,
          efficiency: 92,
          riskLevel: 2
        },
        {
          routeName: '广州-深圳快线',
          startAddress: '广州市天河区',
          endAddress: '深圳市南山区',
          usageCount: 38,
          avgTime: 90,
          avgCost: 180,
          efficiency: 88,
          riskLevel: 1
        },
        {
          routeName: '成都-重庆干线',
          startAddress: '成都市武侯区',
          endAddress: '重庆市渝中区',
          usageCount: 32,
          avgTime: 210,
          avgCost: 350,
          efficiency: 78,
          riskLevel: 3
        },
        {
          routeName: '杭州-南京专线',
          startAddress: '杭州市西湖区',
          endAddress: '南京市鼓楼区',
          usageCount: 28,
          avgTime: 180,
          avgCost: 280,
          efficiency: 85,
          riskLevel: 2
        },
        {
          routeName: '西安-兰州干线',
          startAddress: '西安市雁塔区',
          endAddress: '兰州市城关区',
          usageCount: 25,
          avgTime: 300,
          avgCost: 420,
          efficiency: 75,
          riskLevel: 3
        }
      ],
      
      // 分析报告
      analysisReport: null,
      reportDialogVisible: false,
      
      // 图表实例
      charts: {
        efficiency: null,
        cost: null,
        safety: null,
        traffic: null
      }
    }
  },
  mounted() {
    // 初始化图表
    this.$nextTick(() => {
      // 确保 DOM 已完全加载后再初始化图表
      setTimeout(() => {
        this.initCharts();
      }, 300);
    });
    
    // 添加窗口大小变化监听
    window.addEventListener('resize', this.handleResize);
  },
  watch: {
    // 监听标签页切换，重新初始化对应的图表
    activeTab(newTab) {
      this.$nextTick(() => {
        setTimeout(() => {
          switch(newTab) {
            case 'efficiency':
              if (this.filterForm.analysisTypes.includes('efficiency')) {
                this.initEfficiencyChart();
              }
              break;
            case 'cost':
              if (this.filterForm.analysisTypes.includes('cost')) {
                this.initCostChart();
              }
              break;
            case 'safety':
              if (this.filterForm.analysisTypes.includes('safety')) {
                this.initSafetyChart();
              }
              break;
            case 'traffic':
              if (this.filterForm.analysisTypes.includes('traffic')) {
                this.initTrafficChart();
              }
              break;
          }
        }, 100);
      });
    }
  },
  methods: {
    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        // 初始化效率分析图表
        this.initEfficiencyChart();
        
        // 初始化成本分析图表
        this.initCostChart();
        
        // 初始化安全分析图表
        this.initSafetyChart();
        
        // 初始化交通分析图表
        this.initTrafficChart();
      });
    },
    
    // 初始化效率分析图表
    initEfficiencyChart() {
      this.$nextTick(() => {
        const chartDom = document.getElementById('efficiencyChart');
        if (!chartDom || chartDom.clientWidth === 0 || chartDom.clientHeight === 0) {
          // DOM 尚未准备好，稍后重试
          setTimeout(() => this.initEfficiencyChart(), 200);
          return;
        }
        
        this.charts.efficiency = echarts.init(chartDom);
        const option = {
          title: {
            text: '线路效率分析',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['平均效率', '时间效率', '成本效率'],
            top: 30
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
          },
          yAxis: {
            type: 'value',
            max: 100
          },
          series: [
            {
              name: '平均效率',
              type: 'line',
              data: [85, 88, 82, 90, 86, 78, 75]
            },
            {
              name: '时间效率',
              type: 'line',
              data: [80, 85, 78, 88, 82, 75, 70]
            },
            {
              name: '成本效率',
              type: 'line',
              data: [90, 91, 86, 92, 90, 81, 80]
            }
          ]
        };
        
        this.charts.efficiency.setOption(option);
      });
    },
    
    // 初始化成本分析图表
    initCostChart() {
      this.$nextTick(() => {
        const chartDom = document.getElementById('costChart');
        if (!chartDom || chartDom.clientWidth === 0 || chartDom.clientHeight === 0) {
          // DOM 尚未准备好，稍后重试
          setTimeout(() => this.initCostChart(), 200);
          return;
        }
        
        this.charts.cost = echarts.init(chartDom);
        const option = {
          title: {
            text: '线路成本分析',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          legend: {
            data: ['燃油成本', '人工成本', '过路费', '其他成本'],
            top: 30
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: ['短途(<100km)', '中途(100-500km)', '长途(>500km)']
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '燃油成本',
              type: 'bar',
              stack: 'total',
              data: [120, 200, 350]
            },
            {
              name: '人工成本',
              type: 'bar',
              stack: 'total',
              data: [80, 150, 300]
            },
            {
              name: '过路费',
              type: 'bar',
              stack: 'total',
              data: [20, 80, 150]
            },
            {
              name: '其他成本',
              type: 'bar',
              stack: 'total',
              data: [30, 50, 100]
            }
          ]
        };
        
        this.charts.cost.setOption(option);
      });
    },
    
    // 初始化安全分析图表
    initSafetyChart() {
      this.$nextTick(() => {
        const chartDom = document.getElementById('safetyChart');
        if (!chartDom || chartDom.clientWidth === 0 || chartDom.clientHeight === 0) {
          // DOM 尚未准备好，稍后重试
          setTimeout(() => this.initSafetyChart(), 200);
          return;
        }
        
        this.charts.safety = echarts.init(chartDom);
        const option = {
          title: {
            text: '线路安全风险分析',
            left: 'center'
          },
          tooltip: {
            trigger: 'item'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            top: 30
          },
          series: [
            {
              name: '风险等级',
              type: 'pie',
              radius: '50%',
              data: [
                { value: 65, name: '低风险' },
                { value: 25, name: '中风险' },
                { value: 10, name: '高风险' }
              ],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              }
            }
          ]
        };
        
        this.charts.safety.setOption(option);
      });
    },
    
    // 初始化交通分析图表
    initTrafficChart() {
      this.$nextTick(() => {
        const chartDom = document.getElementById('trafficChart');
        if (!chartDom || chartDom.clientWidth === 0 || chartDom.clientHeight === 0) {
          // DOM 尚未准备好，稍后重试
          setTimeout(() => this.initTrafficChart(), 200);
          return;
        }
        
        this.charts.traffic = echarts.init(chartDom);
        const option = {
          title: {
            text: '交通状况分析',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['畅通', '缓行', '拥堵'],
            top: 30
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: ['0-6时', '6-9时', '9-12时', '12-14时', '14-17时', '17-20时', '20-24时']
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '畅通',
              type: 'bar',
              stack: 'total',
              data: [60, 20, 40, 35, 30, 15, 55]
            },
            {
              name: '缓行',
              type: 'bar',
              stack: 'total',
              data: [30, 40, 35, 40, 45, 35, 35]
            },
            {
              name: '拥堵',
              type: 'bar',
              stack: 'total',
              data: [10, 40, 25, 25, 25, 50, 10]
            }
          ]
        };
        
        this.charts.traffic.setOption(option);
      });
    },
    
    // 开始分析
    handleAnalyze() {
      this.$message.info('正在加载数据...');
      // 调用后端 API进行分析
      request.post("/route/analysis", this.filterForm).then(res => {
        console.log('后端响应:', res);
        
        // 检查响应是否成功
        if (res && (res.code === '200' || res.code === 200)) {
          // 抽取data中的数据
          const analysisData = res.data || {};
          console.log('分析数据:', analysisData);
          
          // 检查是否有接收到stats数据
          if (analysisData.stats) {
            this.updateAnalysisData(analysisData);
            this.$message.success('分析完成');
          } else {
            this.$message.warning('后端数据为空，已使用示例数据');
            this.useDefaultAnalysisData();
          }
        } else {
          // 前端错误或后端错误
          const errorMsg = res ? (res.msg || res.message || '不明错误') : '没有接收到任何响应';
          this.$message.error('分析失败：' + errorMsg);
          this.useDefaultAnalysisData();
        }
      }).catch((error) => {
        console.error('分析错误:', error);
        // 显示具体错误信息
        let errorMsg = '没有接收到任何响应';
        if (error && error.message) {
          errorMsg = error.message;
        } else if (error && error.response && error.response.data) {
          errorMsg = error.response.data.msg || error.response.data.message || JSON.stringify(error.response.data);
        }
        
        this.$message.warning('分析失败，已使用示例数据应答（错误：' + errorMsg + '）');
        this.useDefaultAnalysisData();
      });
    },
        
    // 使用默认的分析数据
    useDefaultAnalysisData() {
      this.updateAnalysisData({
        stats: {
          totalRoutes: 156,
          avgEfficiency: 85,
          avgCost: 320,
          avgRisk: 2.3
        },
        popularRoutes: this.popularRoutes,
        analysisReport: null
      });
    },
    
    // 更新分析数据
    updateAnalysisData(data) {
      // 根据后端返回的数据结构更新前端
      
      // 处理统计数据
      if (data.stats) {
        // 后端返回的是已经检每的数据结构
        this.stats = {
          totalRoutes: data.stats.totalRoutes || 156,
          avgEfficiency: data.stats.avgEfficiency || 85,
          avgCost: data.stats.avgCost || 320,
          avgRisk: data.stats.avgRisk || 2.3
        };
      }
      
      // 处理热门线路数据
      if (data.popularRoutes && data.popularRoutes.length > 0) {
        this.popularRoutes = data.popularRoutes.map(route => ({
          routeName: route.routeName || route.name || '',
          startAddress: route.startAddress || '',
          endAddress: route.endAddress || '',
          usageCount: route.usageCount || 0,
          avgTime: route.estimatedTime || route.avgTime || 0,
          avgCost: route.avgCost || 0,
          efficiency: route.efficiency || 0,
          riskLevel: route.riskLevel || 0
        }));
      }
      
      // 处理分析报告
      if (data.analysisReport) {
        this.analysisReport = data.analysisReport;
      }
      
      // 更新图表
      this.updateCharts();
    },
    
    // 更新图表
    updateCharts() {
      // 根据后端返回的数据更新图表
      this.$nextTick(() => {
        // 稍估一下时间，确保 DOM 完全渲染
        setTimeout(() => {
          // 重新初始化図表，以便花表数据能正常改变
          if (this.filterForm.analysisTypes.includes('efficiency') && this.charts.efficiency) {
            this.charts.efficiency.resize();
            // 更新效率图表数据
            this.updateEfficiencyChart();
          }
          if (this.filterForm.analysisTypes.includes('cost') && this.charts.cost) {
            this.charts.cost.resize();
            // 更新成本图表数据
            this.updateCostChart();
          }
          if (this.filterForm.analysisTypes.includes('safety') && this.charts.safety) {
            this.charts.safety.resize();
            // 更新安全图表数据
            this.updateSafetyChart();
          }
          if (this.filterForm.analysisTypes.includes('traffic') && this.charts.traffic) {
            this.charts.traffic.resize();
            // 更新交通图表数据
            this.updateTrafficChart();
          }
        }, 100);
      });
    },
    
    // 更新效率分析图表
    updateEfficiencyChart() {
      if (!this.charts.efficiency) return;
      const option = {
        title: {
          text: '线路效率分析',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['平均效率', '时间效率', '成本效率'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value',
          max: 100
        },
        series: [
          {
            name: '平均效率',
            type: 'line',
            data: [85, 88, 82, 90, 86, 78, 75]
          },
          {
            name: '时间效率',
            type: 'line',
            data: [80, 85, 78, 88, 82, 75, 70]
          },
          {
            name: '成本效率',
            type: 'line',
            data: [90, 91, 86, 92, 90, 81, 80]
          }
        ]
      };
      this.charts.efficiency.setOption(option);
    },
    
    // 更新成本分析图表
    updateCostChart() {
      if (!this.charts.cost) return;
      const option = {
        title: {
          text: '线路成本分析',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['燃油成本', '人工成本', '过路费', '其他成本'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['短途(<100km)', '中途(100-500km)', '长途(>500km)']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '燃油成本',
            type: 'bar',
            stack: 'total',
            data: [120, 200, 350]
          },
          {
            name: '人工成本',
            type: 'bar',
            stack: 'total',
            data: [80, 150, 300]
          },
          {
            name: '过路费',
            type: 'bar',
            stack: 'total',
            data: [20, 80, 150]
          },
          {
            name: '其他成本',
            type: 'bar',
            stack: 'total',
            data: [30, 50, 100]
          }
        ]
      };
      this.charts.cost.setOption(option);
    },
    
    // 更新安全分析图表
    updateSafetyChart() {
      if (!this.charts.safety) return;
      const option = {
        title: {
          text: '线路安全风险分析',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 30
        },
        series: [
          {
            name: '风险等级',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 65, name: '低风险' },
              { value: 25, name: '中风险' },
              { value: 10, name: '高风险' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      this.charts.safety.setOption(option);
    },
    
    // 更新交通分析图表
    updateTrafficChart() {
      if (!this.charts.traffic) return;
      const option = {
        title: {
          text: '交通状况分析',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['畅通', '缓行', '拥堵'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['0-6时', '6-9时', '9-12时', '12-14时', '14-17时', '17-20时', '20-24时']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '畅通',
            type: 'bar',
            stack: 'total',
            data: [60, 20, 40, 35, 30, 15, 55]
          },
          {
            name: '缓行',
            type: 'bar',
            stack: 'total',
            data: [30, 40, 35, 40, 45, 35, 35]
          },
          {
            name: '拥堵',
            type: 'bar',
            stack: 'total',
            data: [10, 40, 25, 25, 25, 50, 10]
          }
        ]
      };
      this.charts.traffic.setOption(option);
    },
    
    // 导出报告
    handleExport() {
      this.$confirm('确认导出线路数据吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        // 调用后端API导出数据
        const params = new URLSearchParams();
        // 处理日期范围
        if (this.filterForm.dateRange && this.filterForm.dateRange.length > 0) {
          const startDate = this.formatDate(this.filterForm.dateRange[0]);
          const endDate = this.formatDate(this.filterForm.dateRange[1]);
          params.append('startDate', startDate);
          params.append('endDate', endDate);
        }
        if (this.filterForm.startRegion) {
          params.append('startRegion', this.filterForm.startRegion);
        }
        if (this.filterForm.endRegion) {
          params.append('endRegion', this.filterForm.endRegion);
        }
        if (this.filterForm.distanceRange) {
          params.append('distanceRange', this.filterForm.distanceRange.join(','));
        }
        if (this.filterForm.analysisTypes) {
          params.append('analysisTypes', this.filterForm.analysisTypes.join(','));
        }
            
        window.open("/route/analysis/export?" + params.toString());
        this.$message.success("导出成功")
      }).catch(() => {
        this.$message.info('已取消导出')
      })
    },
    
    // 日期格式化方法
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    // 导出报告文件
    exportReport() {
      // 调用后端API导出报告文件
      window.open("/route/analysis/export");
      this.$message.success("报告导出成功");
    },
    
    // 刷新数据
    handleRefresh() {
      this.handleAnalyze();
    },
    
    // 获取进度条颜色
    getProgressColor(percentage) {
      if (percentage >= 90) return '#67C23A';
      if (percentage >= 70) return '#E6A23C';
      return '#F56C6C';
    },
    
    // 获取风险等级标签类型
    getRiskTagType(level) {
      if (level <= 2) return 'success';
      if (level <= 3) return 'warning';
      return 'danger';
    },
    
    // 获取风险等级文本
    getRiskText(level) {
      if (level <= 2) return '低';
      if (level <= 3) return '中';
      return '高';
    },
    
    // 处理窗口大小变化
      handleResize() {
        // 延迟执行，避免频繁触发
        setTimeout(() => {
          Object.values(this.charts).forEach(chart => {
            if (chart) {
              chart.resize();
            }
          });
        }, 300);
      }
    },
    beforeDestroy() {
      // 移除窗口大小变化监听
      window.removeEventListener('resize', this.handleResize);
      
      // 销毁图表实例
      Object.values(this.charts).forEach(chart => {
        if (chart) {
          chart.dispose();
        }
      });
    }
  }
</script>

<style scoped>
.route-analysis {
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

.filter-card, .chart-card, .table-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 100px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}

.stats-icon i {
  font-size: 24px;
  color: white;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.chart-tabs {
  margin-bottom: 20px;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.report-content {
  max-height: 60vh;
  overflow-y: auto;
}

.report-content h2 {
  text-align: center;
  margin-bottom: 10px;
}

.report-time {
  text-align: center;
  color: #909399;
  margin-bottom: 20px;
}

.report-section {
  margin-bottom: 30px;
}

.report-section h3 {
  margin-bottom: 15px;
  color: #303133;
}

.report-item {
  margin-bottom: 15px;
}

.report-item h4 {
  margin-bottom: 10px;
  color: #606266;
}

.report-item ul {
  padding-left: 20px;
}

.report-item li {
  margin-bottom: 5px;
}
</style>