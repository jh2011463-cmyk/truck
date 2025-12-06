<template>
  <div>
    <div style="margin: 10px">
      <div style="display: flex">
        <div style="width: 60%">
          <el-card>
            <div style="font-size: 18px; color: grey">
              我的订单
            </div>
            <el-divider />
            <div>

              <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
                <el-table-column type="selection"  width="55"></el-table-column>
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
                    <div v-if="scope.row.status === 'USER_COMMENT'">
                        <el-button type="primary" @click="handleQuestion(scope.row)">客户填写问卷</el-button>
                    </div>
                    <el-button type="success" @click="handleEdit(scope.row)" v-if="scope.row.status === 'WAIT'">编辑 <i class="el-icon-edit"></i></el-button>
                    <el-popconfirm
                        class="ml-5"
                        confirm-button-text='确定'
                        cancel-button-text='我再想想'
                        icon="el-icon-info"
                        icon-color="red"
                        title="您确定删除吗？"
                        @confirm="del(scope.row.id)"
                    >
                      <el-button type="danger" slot="reference" v-if="scope.row.status === 'WAIT'">删除 <i class="el-icon-remove-outline"></i></el-button>
                    </el-popconfirm>
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

              <el-dialog title="信息" :visible.sync="dialogFormVisible" width="40%" :close-on-click-modal="false">
                <el-form label-width="120px" size="small" style="width: 80%; margin: 0 auto">
                  <el-form-item label="金额(元)">
                    <el-input-number v-model="form.amount" autocomplete="off"></el-input-number>
                  </el-form-item>
                  <el-form-item label="地址">
                    <el-input v-model="form.address" type="textarea" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item label="详情">
                    <el-input v-model="form.detail" type="textarea" rows="5" autocomplete="off"></el-input>
                  </el-form-item>

                </el-form>
                <div slot="footer" class="dialog-footer">
                  <el-button @click="dialogFormVisible = false">取 消</el-button>
                  <el-button type="primary" @click="save">确 定</el-button>
                </div>
              </el-dialog>

            <!--用户填写调查问卷-->
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
                  <el-form-item label="客户id">
                    <el-input v-model="questionForm.userId" disabled autocomplete="off"></el-input>
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

            </div>
          </el-card>
        </div>
        <div style="width: 35%; margin-left: 10px">
          <el-card>
            <div style="text-align: center">
              <b style="font-size: 25px">添加订单</b>
            </div>
            <el-divider />
            <div style="text-align: center; cursor: pointer" @click="handleAdd">
              <img src="../assets/添加.png"  />
            </div>
          </el-card>
          <div style="margin-top: 10px">
            <img src="../assets/货车01.jpg" width="100%" style="border-radius: 10px" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "TakeOrder",
  data() {
    return {
      questionOptions:[
          '好评','中评','差评'
      ],
      questionnaireVisible: false,
      truckList: [],
      userList:[],
      driverList:[],
      tableData: [],
      questionForm:{},
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
  created() {
    this.load()
  },
  methods: {
    saveQuestion(){
      this.request.post("/questionnaire/userComment",this.questionForm).then(res => {
        if (res.code === '200'){
          this.$message.success("用户填写问卷成功！")
          this.questionnaireVisible = false
          this.load()
        }else {
          this.$message.error("问卷填写失败，失败原因："+res.msg)
        }
      })
    },
    handleQuestion(row){
      this.questionForm.userId = JSON.parse(JSON.stringify(row)).userId
      this.questionForm.orderId = JSON.parse(JSON.stringify(row)).id
      this.questionnaireVisible = true
    },
    load() {
      //司机集合
      this.request.get("/user/driver").then(res => {
        this.driverList = res.data
        console.log(this.driverList)
      })

      //普通用户集合
      this.request.get("/user/userOnly").then(res => {
        this.userList = res.data
        console.log(this.userList)
      })

      //获取可用车辆集合
      this.request.get("/truck/usable").then(res => {
        this.truckList = res.data
      })


      this.request.get("/orderForm/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      this.request.post("/orderForm", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.$nextTick(() => {
        if(this.$refs.img) {
          this.$refs.img.clearFiles();
        }
        if(this.$refs.file) {
          this.$refs.file.clearFiles();
        }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
      this.$nextTick(() => {
        if(this.$refs.img) {
          this.$refs.img.clearFiles();
        }
        if(this.$refs.file) {
          this.$refs.file.clearFiles();
        }
      })
    },
    del(id) {
      this.request.delete("/orderForm/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/orderForm/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.load()
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
    handleFileUploadSuccess(res) {
      this.form.file = res
    },
    handleImgUploadSuccess(res) {
      this.form.img = res
    },
    download(url) {
      window.open(url)
    },
    exp() {
      window.open("http://localhost:9090/orderForm/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
      this.load()
    }
  }
}
</script>

<style scoped>

</style>