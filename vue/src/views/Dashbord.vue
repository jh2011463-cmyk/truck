<template>
  <div>
    <el-row :gutter="10" style="margin-bottom: 60px">
      <el-col :span="6">
        <el-card style="color: #409EFF">
          <div><i class="el-icon-user-solid" /> 客户总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            {{ userCount }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #F56C6C">
          <div><i class="el-icon-money" /> 订单完成总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            {{ finishOrderCount }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #67C23A">
          <div><i class="el-icon-bank-card" /> 总营业额</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            ￥ {{ finishOrderMoney }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #E6A23C">
          <div><i class="el-icon-s-shop" /> 司机总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            {{ driverCount }}
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="12">
        <div style="margin: 10px">
          <el-card style="border-radius: 10px">
            <div style="height: 400px; width: 100%" >
              <vue-echarts :option="pieOption"></vue-echarts>
            </div>
          </el-card>
        </div>
      </el-col>

      <el-col :span="12">
        <div style="margin: 10px">
          <el-card style="border-radius: 10px">
            <div style="height: 400px; width: 100%">
              <vue-echarts :option="lineOption"></vue-echarts>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>

  </div>
</template>

<script>

export default {
  name: "Home",
  data() {
    return {
      userCount:0,
      driverCount:0,
      finishOrderCount:0,
      finishOrderMoney:0,

      //饼图
      pieOption:  {
        title: {
          text: '用户类型占比',
          subtext: '数据如下',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '暂无数据',
            type: 'pie',
            radius: '50%',
            data: [
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
      },

      //折现图
      lineOption: {
        title: {
          text: '用户下单趋势图'
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [],
            type: 'line'
          }
        ]
      }
    }
  },
  created() {
    this.load()
  },
  mounted() {
    this.getPieData()
    this.getLineData()
  },
  methods: {
    getPieData(){
      this.request.get("/echarts/pieData").then(res => {
        const data = Array.from(res.data);
        const seriesArr = [];
        for (let i = 0; i < data.length; i++) {
          const seriesObj = {}
          seriesObj.value = data[i].count
          seriesObj.name = data[i].role
          seriesArr.push(seriesObj)
        }
        this.pieOption.series[0].data = seriesArr
      })
    },
    getLineData(){
      this.request.get("/echarts/lineData").then(res => {
        console.log(res.data)
        //利用流查看返回的res.data有没有值，如果有利用map取得每一个对象的日期分别赋值到 lineOption.xAxis.data中
        this.lineOption.xAxis.data = res.data?.map(v => v.date) || []
        //利用流查看返回的res.data有没有值，如果有利用map取得每一个对象的用户数量分别赋值到lineOption.series[0].data中
        this.lineOption.series[0].data = res.data?.map(v => v.count) || []
      })
    },
    load(){
      this.request.get("/echarts/userCount").then(res => {
        this.userCount = res.data
      })
      this.request.get("/echarts/driverCount").then(res => {
        this.driverCount = res.data
      })
      this.request.get("/echarts/finishOrderCount").then(res => {
        this.finishOrderCount = res.data
      })
      this.request.get("/echarts/finishOrderMoney").then(res => {
        this.finishOrderMoney = res.data
      })
    }
  }
}
</script>

<style scoped>

</style>
