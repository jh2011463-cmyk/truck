<template>
  <div class="route-list-container">
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="线路名称">
          <el-input v-model="queryParams.routeName" placeholder="请输入线路名称" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="起点">
          <el-input v-model="queryParams.startAddress" placeholder="请输入起点" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="终点">
          <el-input v-model="queryParams.endAddress" placeholder="请输入终点" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
          >导出</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="info"
            plain
            icon="el-icon-upload2"
            size="mini"
            @click="handleImport"
          >导入</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="routeList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="线路ID" align="center" prop="id" width="80" />
        <el-table-column label="线路名称" align="center" prop="routeName" :show-overflow-tooltip="true" />
        <el-table-column label="起点" align="center" prop="startAddress" :show-overflow-tooltip="true" />
        <el-table-column label="终点" align="center" prop="endAddress" :show-overflow-tooltip="true" />
        <el-table-column label="距离(km)" align="center" prop="distance" width="100" />
        <el-table-column label="预计时间(min)" align="center" prop="estimatedTime" width="120" />
        <el-table-column label="交通状况" align="center" prop="trafficLevel" width="100">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.trafficLevel <= 2 ? 'danger' : scope.row.trafficLevel <= 3 ? 'warning' : 'success'"
            >{{ getTrafficLevelText(scope.row.trafficLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" align="center" prop="routeScore" width="80">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.routeScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}">
            </el-rate>
          </template>
        </el-table-column>
        <el-table-column label="推荐" align="center" prop="isRecommended" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isRecommended === 1 ? 'success' : 'info'">
              {{ scope.row.isRecommended === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
            >删除</el-button>
            <el-dropdown @command="(command) => handleCommand(command, scope.row)" style="margin-left: 10px">
              <span class="el-dropdown-link">
                更多<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="optimize">优化线路</el-dropdown-item>
                <el-dropdown-item command="nodes">节点管理</el-dropdown-item>
                <el-dropdown-item command="history">历史记录</el-dropdown-item>
                <el-dropdown-item command="recommend">推荐线路</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改线路对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="线路名称" prop="routeName">
          <el-input v-model="form.routeName" placeholder="请输入线路名称" />
        </el-form-item>
        <el-form-item label="起点" prop="startAddress">
          <el-input v-model="form.startAddress" placeholder="请输入起点" />
        </el-form-item>
        <el-form-item label="终点" prop="endAddress">
          <el-input v-model="form.endAddress" placeholder="请输入终点" />
        </el-form-item>
        <el-form-item label="距离(km)" prop="distance">
          <el-input-number v-model="form.distance" :min="0" :precision="2" placeholder="请输入距离" />
        </el-form-item>
        <el-form-item label="预计时间(min)" prop="estimatedTime">
          <el-input-number v-model="form.estimatedTime" :min="0" :precision="2" placeholder="请输入预计时间" />
        </el-form-item>
        <el-form-item label="交通状况" prop="trafficLevel">
          <el-rate
            v-model="form.trafficLevel"
            :max="5"
            show-text
            :texts="['严重拥堵', '拥堵', '缓行', '畅通', '非常畅通']">
          </el-rate>
        </el-form-item>
        <el-form-item label="推荐" prop="isRecommended">
          <el-radio-group v-model="form.isRecommended">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 优化线路对话框 -->
    <el-dialog title="优化线路" :visible.sync="optimizeOpen" width="500px" append-to-body>
      <el-form ref="optimizeForm" :model="optimizeForm" label-width="80px">
        <el-form-item label="优化目标">
          <el-radio-group v-model="optimizeForm.objective">
            <el-radio label="time">时间最短</el-radio>
            <el-radio label="distance">距离最短</el-radio>
            <el-radio label="cost">成本最低</el-radio>
            <el-radio label="safety">安全最高</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitOptimize">确 定</el-button>
        <el-button @click="optimizeOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的线路数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRoute, getRoute, delRoute, addRoute, updateRoute, exportRoute, optimizeRoute } from "@/api/route";
import { getToken } from "@/utils/auth";

export default {
  name: "RouteList",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 线路表格数据
      routeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示优化弹出层
      optimizeOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        routeName: null,
        startAddress: null,
        endAddress: null
      },
      // 表单参数
      form: {},
      // 优化表单参数
      optimizeForm: {
        objective: "time"
      },
      // 表单校验
      rules: {
        routeName: [
          { required: true, message: "线路名称不能为空", trigger: "blur" }
        ],
        startAddress: [
          { required: true, message: "起点不能为空", trigger: "blur" }
        ],
        endAddress: [
          { required: true, message: "终点不能为空", trigger: "blur" }
        ],
        distance: [
          { required: true, message: "距离不能为空", trigger: "blur" }
        ],
        estimatedTime: [
          { required: true, message: "预计时间不能为空", trigger: "blur" }
        ]
      },
      // 当前选中的线路
      currentRoute: null,
      // 导入参数
      upload: {
        // 是否显示弹出层（导入）
        open: false,
        // 弹出层标题（导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/route/importData"
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询线路列表 */
    getList() {
      this.loading = true;
      listRoute(this.queryParams).then(response => {
        this.routeList = response.data.records;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        routeName: null,
        startAddress: null,
        endAddress: null,
        distance: null,
        estimatedTime: null,
        trafficLevel: 3,
        isRecommended: 0
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        routeName: null,
        startAddress: null,
        endAddress: null
      };
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加线路";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getRoute(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改线路";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRoute(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addRoute(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除线路编号为"' + ids + '"的数据项？').then(function() {
        return delRoute(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('route/export', {
        ...this.queryParams
      }, `route_${new Date().getTime()}.xlsx`);
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "线路导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('route/importTemplate', {
      }, `route_template_${new Date().getTime()}.xlsx`);
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    /** 更多操作处理 */
    handleCommand(command, row) {
      switch (command) {
        case 'optimize':
          this.handleOptimize(row);
          break;
        case 'nodes':
          this.$router.push({ path: '/route/nodes', query: { routeId: row.id } });
          break;
        case 'history':
          this.$router.push({ path: '/route/history', query: { routeId: row.id } });
          break;
        case 'recommend':
          this.handleRecommend(row);
          break;
        default:
          break;
      }
    },
    /** 优化线路操作 */
    handleOptimize(row) {
      this.currentRoute = row;
      this.optimizeOpen = true;
    },
    /** 提交优化 */
    submitOptimize() {
      const data = {
        routeId: this.currentRoute.id,
        objective: this.optimizeForm.objective
      };
      optimizeRoute(data).then(response => {
        this.$modal.msgSuccess("优化成功");
        this.optimizeOpen = false;
        this.getList();
      });
    },
    /** 推荐线路操作 */
    handleRecommend(row) {
      const data = {
        ...row,
        isRecommended: row.isRecommended === 1 ? 0 : 1
      };
      updateRoute(data).then(response => {
        this.$modal.msgSuccess(row.isRecommended === 1 ? "取消推荐成功" : "推荐成功");
        this.getList();
      });
    },
    /** 获取交通状况文本 */
    getTrafficLevelText(level) {
      const texts = ['', '严重拥堵', '拥堵', '缓行', '畅通', '非常畅通'];
      return texts[level] || '未知';
    }
  }
};
</script>

<style scoped>
.route-list-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
</style>