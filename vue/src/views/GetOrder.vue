<template>
  <div>
    <div style="margin: 10px">
      <div style="display: flex">
        <div style="width: 60%">
          <el-card>
            <div style="font-size: 18px; color: grey">
              可接订单
            </div>
            <el-divider />
            <div>
<!--              <div style="margin: 10px 0">-->
<!--                <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>-->
<!--                <el-button class="ml-5" type="primary" @click="load">搜索</el-button>-->
<!--                <el-button type="warning" @click="reset">重置</el-button>-->
<!--              </div>-->

              <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
                <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
                <el-table-column prop="amount" label="金额">
                  <template v-slot="scope">
                    <div>
                      <el-tag type="success">{{ scope.row.amount }}元</el-tag>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="date" width="180px" label="下单时间"></el-table-column>
                <el-table-column prop="address" label="地址"></el-table-column>
                <el-table-column prop="sendTime" width="180px" label="送达时间">
                  <template v-slot="scope">
                    <div v-if="scope.row.sendTime == null">
                      <el-tag>未知</el-tag>
                    </div>
                    <div v-if="scope.row.sendTime != null">
                      <div>{{ scope.row.sendTime }}</div>
                    </div>

                  </template>
                </el-table-column>
                <el-table-column prop="detail" label="详情"></el-table-column>
                <el-table-column prop="userName" label="客户"></el-table-column>
                <el-table-column width="150px" prop="driverName" label="司机">
                  <template v-slot="scope">
                    <div v-if="scope.row.driverName === null">
                      暂未安排司机
                    </div>
                    <div v-if="scope.row.driverName != null">
                      分配司机：{{ scope.row.driverName }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column width="150px" prop="truckName" label="车辆">
                  <template v-slot="scope">
                    <div v-if="scope.row.truckName === null">
                      暂未安排车辆
                    </div>
                    <div v-if="scope.row.truckName != null">
                      分配司机：{{ scope.row.truckName }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column width="150px" prop="status" label="订单状态">
                  <template v-slot="scope">
                    <div v-if="scope.row.status === 'WAIT'">
                      <el-tag>待分配</el-tag>
                    </div>
                    <div v-if="scope.row.status === 'NO_GET'">
                      <el-tag>未接单</el-tag>
                    </div>
                    <div v-if="scope.row.status === 'SENDING'">
                      <el-tag>配送中</el-tag>
                    </div>
                    <div v-if="scope.row.status === 'USER_COMMENT'">
                      <el-tag>等待用户填写问卷</el-tag>
                    </div>
                    <div v-if="scope.row.status === 'DRIVER_COMMENT'">
                      <el-tag>等待司机填写问卷</el-tag>
                    </div>
                    <div v-if="scope.row.status === 'DONE'">
                      <el-tag>已完成</el-tag>
                    </div>
                  </template>
                </el-table-column>

                <el-table-column label="操作"  width="180" align="center">
                  <template slot-scope="scope">
                    <div v-if="scope.row.status === 'DRIVER_COMMENT'">
                      <el-button type="success" @click="handleQuestion(scope.row)">司机填写问卷</el-button>
                    </div>
                    <div v-if="scope.row.status === 'NO_GET'">
                      <el-button type="success" @click="getOrder(scope.row.id)">接单</el-button>
                    </div>
                    <div v-if="scope.row.status === 'SENDING'">
                      <el-button type="warning" @click="haveSend(scope.row.id)">已送达</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div style="padding: 10px 0">
                <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="pageNum"
                    :page-sizes="[2, 5, 10, 20]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
                </el-pagination>
              </div>

            </div>
          </el-card>
        </div>
        <!--填写调查问卷-->
        <el-dialog title="信息" :visible.sync="questionnaireVisible" width="40%" :close-on-click-modal="false">
          <el-form label-width="120px" size="small" style="width: 80%; margin: 0 auto">
            <el-form-item label="评价类型">
              <el-select v-model="questionForm.type"  placeholder="请选择">
                <el-option
                    v-for="item in questionOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="详情">
              <el-input v-model="questionForm.detail" type="textarea" rows="4" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="司机id">
              <el-input v-model="questionForm.driverId" disabled autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="订单id">
              <el-input v-model="questionForm.orderId" disabled autocomplete="off"></el-input>
            </el-form-item>

          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" @click="saveQuestion">确 定</el-button>
          </div>
        </el-dialog>

        <div style="width: 35%; margin-left: 10px">
          <div style="margin-top: 10px">
            <el-card style="width: 100%; border-radius: 10px">
              <div style="margin-bottom: 10px; font-size: 20px"><b>当前天气</b></div>
              <div>
                <div id="he-plugin-standard"></div>
              </div>
            </el-card>
          </div>

          <div style="margin-top: 10px">
            <img src="../assets/货车01.jpg" width="100%" style="border-radius: 10px" />
          </div>
          <div style="margin-top: 10px">
            <img src="../assets/货车02.jpg" width="100%" style="border-radius: 10px" />
          </div>
          <div style="margin-top: 10px">
            <img src="../assets/货车03.jpg" width="100%" style="border-radius: 10px" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "GetOrder",
  data() {
    return {
      questionOptions:['好评','中评','差评'],
      questionForm:{},
      questionnaireVisible:false,
      truckList: [],
      userList:[],
      driverList:[],
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  mounted() {
    const script = document.createElement('script');
    script.src = 'https://widget.qweather.net/standard/static/js/he-standard-common.js?v=2.0';
    script.async = true;
    script.onload = () => {
      window.WIDGET = {
        CONFIG: {
          layout: '1',
          width: '450',
          height: '150',
          background: '1',
          dataColor: 'FFFFFF',
          key: 'ecca9335db334189a897e6f065dc450f'
        }
      };
    };
    document.body.appendChild(script);
  },
  created() {
    this.load()
  },
  methods: {
    saveQuestion(){
      this.request.post("/questionnaire/driverComment",this.questionForm).then(res => {
        if (res.code === '200'){
          this.$message.success("司机填写问卷成功！")
          this.questionnaireVisible = false
          this.load()
        }else {
          this.$message.error("问卷填写失败，失败原因："+res.msg)
        }
      })
    },
    handleQuestion(row){
      this.questionForm.driverId = JSON.parse(JSON.stringify(row)).driverId
      this.questionForm.orderId = JSON.parse(JSON.stringify(row)).id
      this.questionnaireVisible = true
    },
    haveSend(orderId){
      this.form.id = orderId
      this.request.post("/orderForm/haveSend",this.form).then(res => {
        if (res.code === '200'){
          this.$message.success("运输送达，请等待客户完成调查问卷")
          this.load()
        }else {
          this.$message.error("确认送达，失败原因："+res.msg)
        }
      })
    },
    getOrder(orderId){
      this.form.id = orderId
      this.request.post("/orderForm/getOrder",this.form).then(res => {
        if (res.code === '200'){
          this.$message.success("接单成功")
          this.load()
        }else {
          this.$message.error("接单失败，失败原因："+res.msg)
        }
      })
    },
    load() {
      this.request.get("/orderForm/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          type: "driver_order_usable"
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },

    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
  }
}
</script>

<style scoped>

</style>