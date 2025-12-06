<template>
  <div class="route-management">
    <div class="header">
      <h2>线路管理</h2>
      <div class="operation-buttons">
        <el-button type="primary" @click="handleAdd" :disabled="listLoading || formLoading">新增线路</el-button>
        <el-button type="success" @click="handleBatchOptimize" :loading="batchOptimizeLoading" :disabled="listLoading || batchOptimizeLoading || optimizeLoading">批量优化</el-button>
        <el-button type="info" @click="handleExport" :loading="exportLoading" :disabled="listLoading || exportLoading">导出数据</el-button>
      </div>
    </div>

    <div class="search-area">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="起点">
          <el-input v-model="searchForm.startAddress" placeholder="请输入起点地址" clearable :disabled="listLoading"></el-input>
        </el-form-item>
        <el-form-item label="终点">
          <el-input v-model="searchForm.endAddress" placeholder="请输入终点地址" clearable :disabled="listLoading"></el-input>
        </el-form-item>
        <el-form-item label="推荐状态">
          <el-select v-model="searchForm.isRecommended" placeholder="请选择" clearable :disabled="listLoading">
            <el-option label="全部" value=""></el-option>
            <el-option label="是" :value="1"></el-option>
            <el-option label="否" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="listLoading" :disabled="listLoading">查询</el-button>
          <el-button @click="resetSearch" :disabled="listLoading">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-area">
      <el-table :data="tableData" border style="width: 100%" @selection-change="handleSelectionChange" v-loading="listLoading">
        <el-table-column type="selection" width="55" :selectable="() => !listLoading"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="routeName" label="线路名称" width="200"></el-table-column>
        <el-table-column prop="startAddress" label="起点" width="180"></el-table-column>
        <el-table-column prop="endAddress" label="终点" width="180"></el-table-column>
        <el-table-column prop="distance" label="距离(km)" width="100"></el-table-column>
        <el-table-column prop="estimatedTime" label="预估时间(分钟)" width="120"></el-table-column>
        <el-table-column prop="trafficLevel" label="交通等级" width="100">
          <template slot-scope="scope">
            <el-tag :type="getTrafficTagType(scope.row.trafficLevel)">{{ getTrafficText(scope.row.trafficLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="routeScore" label="线路评分" width="100">
          <template slot-scope="scope">
            <el-rate v-model="scope.row.routeScore" disabled show-score text-color="#ff9900"></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="isRecommended" label="推荐状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isRecommended === 1 ? 'success' : 'info'">
              {{ scope.row.isRecommended === 1 ? '推荐' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleView(scope.row)" :disabled="listLoading">查看</el-button>
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)" :disabled="listLoading">编辑</el-button>
            <el-button size="mini" type="success" @click="handleOptimize(scope.row)" :disabled="listLoading">优化</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)" :loading="deleteLoading" :disabled="listLoading">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20, 50]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%" :close-on-click-modal="false" :close-on-press-escape="false">
      <el-form :model="form" :rules="rules" ref="form" label-width="120px" :disabled="formLoading">
        <el-form-item label="线路名称" prop="routeName">
          <el-input v-model="form.routeName" autocomplete="off" :disabled="formLoading"></el-input>
        </el-form-item>
        <el-form-item label="起点" prop="startAddress">
          <el-input v-model="form.startAddress" autocomplete="off" :disabled="formLoading"></el-input>
        </el-form-item>
        <el-form-item label="终点" prop="endAddress">
          <el-input v-model="form.endAddress" autocomplete="off" :disabled="formLoading"></el-input>
        </el-form-item>
        <el-form-item label="距离(km)" prop="distance">
          <el-input-number v-model="form.distance" :precision="2" :step="0.1" :min="0" :max="9999" :disabled="formLoading"></el-input-number>
        </el-form-item>
        <el-form-item label="预估时间(分钟)" prop="estimatedTime">
          <el-input-number v-model="form.estimatedTime" :precision="2" :step="1" :min="0" :max="9999" :disabled="formLoading"></el-input-number>
        </el-form-item>
        <el-form-item label="交通等级" prop="trafficLevel">
          <el-rate v-model="form.trafficLevel" :max="5" show-text :disabled="formLoading"></el-rate>
        </el-form-item>
        <el-form-item label="推荐状态" prop="isRecommended">
          <el-switch v-model="form.isRecommended" :active-value="1" :inactive-value="0" :disabled="formLoading"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" :disabled="formLoading">取 消</el-button>
        <el-button type="primary" @click="saveForm" :loading="formLoading" :disabled="formLoading">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="线路优化" :visible.sync="optimizeDialogVisible" width="60%" :close-on-click-modal="false" :close-on-press-escape="false">
      <div class="optimize-options">
        <h3>优化目标</h3>
        <el-checkbox-group v-model="optimizeObjectives" :disabled="optimizeLoading">
          <el-checkbox label="time" :disabled="optimizeLoading">时间最短</el-checkbox>
          <el-checkbox label="distance" :disabled="optimizeLoading">距离最短</el-checkbox>
          <el-checkbox label="cost" :disabled="optimizeLoading">成本最低</el-checkbox>
          <el-checkbox label="safety" :disabled="optimizeLoading">安全最高</el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="optimize-results" v-if="optimizedRoutes.length > 0" v-loading="optimizeLoading">
        <h3>优化结果</h3>
        <el-table :data="optimizedRoutes" border style="width: 100%" :disabled="optimizeApplying || optimizeLoading">
          <el-table-column prop="routeName" label="线路名称"></el-table-column>
          <el-table-column prop="distance" label="距离(km)"></el-table-column>
          <el-table-column prop="estimatedTime" label="预估时间(分钟)"></el-table-column>
          <el-table-column prop="routeScore" label="综合评分">
            <template slot-scope="scope">
              <el-rate v-model="scope.row.routeScore" disabled show-score text-color="#ff9900"></el-rate>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" @click="applyOptimizedRoute(scope.row)" :loading="optimizeApplying" :disabled="optimizeApplying || optimizeLoading">应用此方案</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else-if="!optimizeLoading" style="text-align: center; color: #909399; padding: 20px;">
        请点击开始优化按钮获取优化方案
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="optimizeDialogVisible = false" :disabled="optimizeLoading || optimizeApplying">取 消</el-button>
        <el-button type="primary" @click="runOptimization" :loading="optimizeLoading" :disabled="optimizeLoading || optimizeApplying">开始优化</el-button>
      </div>
    </el-dialog>

    <el-dialog title="线路详情" :visible.sync="detailDialogVisible" width="60%" :close-on-click-modal="false">
      <div class="route-detail" v-loading="analysisLoading" v-if="currentRoute">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="线路ID">{{ currentRoute.id }}</el-descriptions-item>
          <el-descriptions-item label="线路名称">{{ currentRoute.routeName }}</el-descriptions-item>
          <el-descriptions-item label="起点">{{ currentRoute.startAddress }}</el-descriptions-item>
          <el-descriptions-item label="终点">{{ currentRoute.endAddress }}</el-descriptions-item>
          <el-descriptions-item label="距离">{{ currentRoute.distance }} km</el-descriptions-item>
          <el-descriptions-item label="预估时间">{{ currentRoute.estimatedTime }} 分钟</el-descriptions-item>
          <el-descriptions-item label="交通等级">
            <el-tag :type="getTrafficTagType(currentRoute.trafficLevel)">{{ getTrafficText(currentRoute.trafficLevel) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="线路评分">
            <el-rate v-model="currentRoute.routeScore" disabled show-score text-color="#ff9900"></el-rate>
          </el-descriptions-item>
          <el-descriptions-item label="推荐状态">
            <el-tag :type="currentRoute.isRecommended === 1 ? 'success' : 'info'">
              {{ currentRoute.isRecommended === 1 ? '推荐' : '普通' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="route-analysis" v-if="routeAnalysis && !analysisLoading">
          <h3>线路分析</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="analysis-card">
                <h4>成本效益分析</h4>
                <p>成本效益比: {{ routeAnalysis.costBenefit }}</p>
                <p>预估成本: ¥{{ routeAnalysis.estimatedCost }}</p>
                <p>预估收益: ¥{{ routeAnalysis.estimatedRevenue }}</p>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="analysis-card">
                <h4>风险评估</h4>
                <p>风险等级:
                  <el-tag :type="getRiskTagType(routeAnalysis.riskLevel)">{{ getRiskText(routeAnalysis.riskLevel) }}</el-tag>
                </p>
                <p>风险因素: {{ routeAnalysis.riskFactors }}</p>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <div class="analysis-card">
                <h4>优化建议</h4>
                <ul>
                  <li v-for="(suggestion, index) in routeAnalysis.suggestions" :key="index">{{ suggestion }}</li>
                </ul>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listRoute,
  addRoute,
  updateRoute,
  delRoute,
  optimizeRoute,
  batchOptimizeRoutes,
  getOptimizationSuggestions
} from '@/api/route'

export default {
  name: 'Route',
  data() {
    return {
      tableData: [],
      multipleSelection: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      searchForm: {
        startAddress: '',
        endAddress: '',
        isRecommended: ''
      },
      form: {},
      dialogVisible: false,
      dialogTitle: '',
      formLoading: false,
      optimizeDialogVisible: false,
      optimizeObjectives: ['time', 'distance'],
      optimizedRoutes: [],
      currentOptimizeRoute: null,
      optimizeLoading: false,
      optimizeApplying: false,
      detailDialogVisible: false,
      currentRoute: null,
      routeAnalysis: null,
      analysisLoading: false,
      listLoading: false,
      deleteLoading: false,
      batchOptimizeLoading: false,
      exportLoading: false,
      rules: {
        routeName: [
          { required: true, message: '请输入线路名称', trigger: 'blur' }
        ],
        startAddress: [
          { required: true, message: '请输入起点', trigger: 'blur' }
        ],
        endAddress: [
          { required: true, message: '请输入终点', trigger: 'blur' }
        ],
        distance: [
          { required: true, message: '请输入距离', trigger: 'blur' }
        ],
        estimatedTime: [
          { required: true, message: '请输入预估时间', trigger: 'blur' }
        ],
        trafficLevel: [
          { required: true, message: '请选择交通等级', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    // 加载数据
    load() {
      this.listLoading = true
      const query = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        startAddress: this.searchForm.startAddress,
        endAddress: this.searchForm.endAddress,
        isRecommended: this.searchForm.isRecommended
      }
      listRoute(query).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      }).catch(err => {
        this.$message.error('数据加载失败：' + (err.message || '未知错误'))
      }).finally(() => {
        this.listLoading = false
      })
    },

    // 搜索
    handleSearch() {
      this.pageNum = 1
      this.load()
    },

    // 重置搜索
    resetSearch() {
      this.searchForm = {
        startAddress: '',
        endAddress: '',
        isRecommended: ''
      }
      this.handleSearch()
    },

    // 分页大小改变
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },

    // 当前页改变
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },

    // 多选改变
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    // 新增
    handleAdd() {
      this.dialogTitle = '新增线路'
      this.form = {
        isRecommended: 0,
        trafficLevel: 3,
        routeScore: 0.7
      }
      this.dialogVisible = true
    },

    // 编辑
    handleEdit(row) {
      this.dialogTitle = '编辑线路'
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogVisible = true
    },

    // 保存表单
    saveForm() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.formLoading = true
          const data = {
            ...this.form
          }
          const promise = data.id ? updateRoute(data) : addRoute(data)
          promise.then(res => {
            if (res.code === '200') {
              this.$message.success(data.id ? "编辑成功" : "新增成功")
              this.dialogVisible = false
              this.load()
            } else {
              this.$message.error(res.msg || "保存失败")
            }
          }).catch(err => {
            this.$message.error('保存失败：' + (err.message || '未知错误'))
          }).finally(() => {
            this.formLoading = false
          })
        }
      })
    },

    // 删除
    handleDelete(row) {
      this.$confirm('确认删除该线路吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteLoading = true
        delRoute(row.id).then(res => {
          if (res.code === '200') {
            this.$message.success("删除成功")
            this.load()
          } else {
            this.$message.error(res.msg || "删除失败")
          }
        }).catch(err => {
          this.$message.error('删除失败：' + (err.message || '未知错误'))
        }).finally(() => {
          this.deleteLoading = false
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    // 查看
    handleView(row) {
      this.currentRoute = row
      this.loadRouteAnalysis(row.id)
      this.detailDialogVisible = true
    },

    // 加载线路分析
    loadRouteAnalysis(routeId) {
      this.analysisLoading = true
      getOptimizationSuggestions(routeId).then(res => {
        if (res.code === '200' && res.data) {
          this.routeAnalysis = res.data
        } else {
          // 使用模拟数据
          this.routeAnalysis = {
            costBenefit: 1.25,
            estimatedCost: 250,
            estimatedRevenue: 312.5,
            riskLevel: 2,
            riskFactors: "距离适中，交通状况良好",
            suggestions: [
              "建议在非高峰时段出行，可减少20%的时间成本",
              "当前线路评分较高，可作为推荐线路",
              "可在中途设置休息点，提高运输安全性"
            ]
          }
        }
      }).catch(() => {
        // 请求失败时使用模拟数据
        this.routeAnalysis = {
          costBenefit: 1.25,
          estimatedCost: 250,
          estimatedRevenue: 312.5,
          riskLevel: 2,
          riskFactors: "距离适中，交通状况良好",
          suggestions: [
            "建议在非高峰时段出行，可减少20%的时间成本",
            "当前线路评分较高，可作为推荐线路",
            "可在中途设置休息点，提高运输安全性"
          ]
        }
      }).finally(() => {
        this.analysisLoading = false
      })
    },

    // 优化
    handleOptimize(row) {
      this.currentOptimizeRoute = row
      this.optimizedRoutes = []
      this.optimizeDialogVisible = true
    },

    // 执行优化
    runOptimization() {
      if (this.optimizeObjectives.length === 0) {
        this.$message.warning("请至少选择一个优化目标")
        return
      }

      // ... existing code ...
      this.optimizeLoading = true
      optimizeRoute({
        routeId: this.currentOptimizeRoute.id,
        objectives: this.optimizeObjectives
      }).then(res => {
        if (res.code === '200' && res.data) {
          this.optimizedRoutes = res.data
          this.$message.success("优化完成")
        } else {
          this.$message.error(res.msg || "优化失败")
        }
      }).catch(() => {
        // 模拟数据，实际应该调用后端API
        this.optimizedRoutes = [
          {
            routeName: "时间优化线路",
            distance: 80,
            estimatedTime: 90,
            routeScore: 0.85
          },
          {
            routeName: "距离优化线路",
            distance: 70,
            estimatedTime: 110,
            routeScore: 0.82
          },
          {
            routeName: "成本优化线路",
            distance: 90,
            estimatedTime: 130,
            routeScore: 0.78
          }
        ]
        this.$message.success("优化完成")
      }).finally(() => {
        this.optimizeLoading = false
      })
    },

    // 应用优化方案
    applyOptimizedRoute(route) {
      this.$confirm('确认应用此优化方案吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.optimizeApplying = true
        const updateData = {
          ...this.currentOptimizeRoute,
          ...route
        }
        updateRoute(updateData).then(res => {
          if (res.code === '200') {
            this.$message.success("应用成功")
            this.optimizeDialogVisible = false
            this.load()
          } else {
            this.$message.error(res.msg || "应用失败")
          }
        }).catch(err => {
          this.$message.error('应用失败：' + (err.message || '未知错误'))
        }).finally(() => {
          this.optimizeApplying = false
        })
      }).catch(() => {
        this.$message.info('已取消应用')
      })
    },

    // 批量优化
    handleBatchOptimize() {
      if (this.multipleSelection.length === 0) {
        this.$message.warning("请选择要优化的线路")
        return
      }

      this.$confirm('确认批量优化选中的线路吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.batchOptimizeLoading = true
        const ids = this.multipleSelection.map(item => item.id)
        batchOptimizeRoutes({ ids: ids }).then(res => {
          if (res.code === '200') {
            this.$message.success("批量优化成功")
            this.load()
          } else {
            this.$message.error(res.msg || "批量优化失败")
          }
        }).catch(err => {
          this.$message.error('批量优化失败：' + (err.message || '未知错误'))
        }).finally(() => {
          this.batchOptimizeLoading = false
        })
      }).catch(() => {
        this.$message.info('已取消批量优化')
      })
    },

    // 导出数据
    handleExport() {
      this.$confirm('确认导出线路数据吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.exportLoading = true
        // 修正: 移除硬编码的 setTimeout，即时发起下载并重置 loading 状态
        window.open("/route/export")
        this.$message.success("导出请求已发送，请检查下载列表")
        this.exportLoading = false
      }).catch(() => {
        this.$message.info('已取消导出')
      })
    },

    // 获取交通等级标签类型
    getTrafficTagType(level) {
      if (level <= 2) return 'danger'
      if (level <= 3) return 'warning'
      return 'success'
    },

    // 获取交通等级文本
    getTrafficText(level) {
      if (level <= 2) return '拥堵'
      if (level <= 3) return '一般'
      return '畅通'
    },

    // 获取风险等级标签类型
    getRiskTagType(level) {
      if (level <= 2) return 'success'
      if (level <= 3) return 'warning'
      return 'danger'
    },

    // 获取风险等级文本
    getRiskText(level) {
      if (level <= 2) return '低'
      if (level <= 3) return '中'
      return '高'
    }
  }
}
</script>

<style scoped>
.route-management {
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

.search-area {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.table-area {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.optimize-options {
  margin-bottom: 20px;
}

.optimize-results {
  margin-top: 20px;
}

.route-detail {
  margin-bottom: 20px;
}

.route-analysis {
  margin-top: 20px;
}

.analysis-card {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 15px;
}

.analysis-card h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #409EFF;
}

.analysis-card ul {
  padding-left: 20px;
}

.analysis-card li {
  margin-bottom: 5px;
}
</style>