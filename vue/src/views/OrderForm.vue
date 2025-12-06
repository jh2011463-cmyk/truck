<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入id" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
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
      <el-table-column width="150px" prop="userLevel" label="用户安全等级">
        <template v-slot="scope">
          <div style="color: red"><b>{{ scope.row.userLevel }}</b></div>
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

      <el-table-column label="操作"  width="300" align="center">
        <template slot-scope="scope">
          <el-button type="warning" @click="handleDistribution(scope.row.id)" v-if="scope.row.status === 'WAIT'">调度分配</el-button>
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
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

<!--调度分配弹窗-->
    <el-dialog title="调度分配" :visible.sync="distributionVisible" width="40%" :close-on-click-modal="false">
      <el-form label-width="120px" size="small" style="width: 80%; margin: 0 auto">
        <el-form-item label="调度车辆">
          <el-button type="success" @click="truckVisible = true">点击选择推荐车辆</el-button>
        </el-form-item>
        <el-form-item label="车辆是否选择" >
          <el-tag v-if="form.truckId == null" type="danger">未选择</el-tag>
          <el-tag v-if="form.truckId != null" type="success">已选择</el-tag>
        </el-form-item>
        <el-form-item label="调度司机">
          <el-button type="primary" @click="driverVisible = true">点击选择推荐司机</el-button>
        </el-form-item>
        <el-form-item label="司机是否选择" >
          <el-tag v-if="form.driverId == null" type="danger">未选择</el-tag>
          <el-tag v-if="form.driverId != null" type="success">已选择</el-tag>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="distributionVisible = false">取 消</el-button>
        <el-button type="primary" @click="distribution">分 配</el-button>
      </div>
    </el-dialog>

<!--选择推荐车辆弹窗-->
    <el-dialog title="选择车辆" :visible.sync="truckVisible" width="40%" :close-on-click-modal="false">
      <div v-for="item in truckList">
        <div style="cursor: pointer" @click="chooseTruck(item.id)">
          <el-card style="border-radius: 10px; margin-top: 10px">
            <div style="font-size: 20px;"><b>{{ item.type }}</b></div>
            <el-divider />
            <div style="display: flex">
              <div style="color: #666666; width: 80%">
                <div >货车尺寸：{{ item.size }}</div>
                <div style="margin-top: 30px;">状态: {{ item.status }}</div>
              </div>

              <div style="width: 25%">
                <img :src="item.img" width="100%" />
              </div>
            </div>

          </el-card>
        </div>
      </div>

    </el-dialog>

<!--选择推荐司机-->
    <el-dialog title="分配司机" :visible.sync="driverVisible" width="40%" :close-on-click-modal="false">
      <div v-for="item in driverList">
        <div style="cursor: pointer; margin-top: 10px" @click="chooseDriver(item.id)">
          <el-card style="border-radius: 10px">
            <div style="display: flex">
              <div style="font-size: 15px; width: 75%"><b>{{ item.nickname }}</b></div>
              <div style="width: 25%">
                安全等级：<b style="color: red">{{ item.level }}</b>
              </div>
            </div>
            <el-divider />
            <div style="display: flex">
              <div style="width: 65%">
                <img :src="item.avatarUrl" width="150px" />
              </div>
              <div style="flex: 1">
                <div>年龄：{{ item.age }}</div>
                <div style="margin-top: 30px">联系方式：{{ item.phone }}</div>
                <div style="margin-top: 30px" v-if="item.role === 'ROLE_DRIVER'">角色：<b style="color: red">司机</b></div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

    </el-dialog>


<!--编辑或新增弹窗-->
    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="40%" :close-on-click-modal="false">
      <el-form label-width="120px" size="small" style="width: 80%; margin: 0 auto">
        <el-form-item label="金额(元)">
          <el-input-number v-model="form.amount" autocomplete="off"></el-input-number>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker v-model="form.date" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="送达时间">
          <el-date-picker v-model="form.sendTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="详情">
          <el-input v-model="form.detail" type="textarea" rows="5" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="选择客户">
          <el-select v-model="form.userId"  placeholder="请选择">
            <el-option
                v-for="item in userList"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
            />
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "OrderForm",
  data() {
    return {
      driverVisible: false,
      truckVisible: false,
      distributionVisible: false,
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
  created() {
    this.load()
  },
  methods: {
    distribution(){
      this.request.post("/orderForm/distribution",this.form).then(res => {
        if (res.code === '200'){
          this.$message.success("分配成功")
          this.distributionVisible = false
          this.load()
        }else {
          this.$message.error("分配失败，失败原因："+res.msg)
        }
      })
    },
    chooseDriver(driverId){
      this.form.driverId = driverId
      this.driverVisible = false
    },
    chooseTruck(truckId){
      this.form.truckId = truckId
      this.truckVisible = false
    },
    handleDistribution(orderId){
      this.form.id = orderId;
      this.distributionVisible = true;
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


<style>
.headerBg {
  background: #eee!important;
}
</style>
