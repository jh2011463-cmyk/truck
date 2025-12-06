<template>
  <div style="color: #666;font-size: 14px;">
    <div style="padding-bottom: 20px">
      <b>您好！{{ user.nickname }}</b>
    </div>
    <el-card>
      <div style="color: red;font-size: 25px">
        <b>欢迎使用货车运输管理系统!</b>
      </div>
      <el-divider />
      <div style="display: flex">
        <div style="width: 50%">
          <div>
            <el-card>
              <div style="font-size: 20px"><b>系统介绍</b></div>
              <el-divider />
              <div>&ensp;&ensp;</div>
            </el-card>
          </div>
          <div style="margin-top: 10px">
            <el-card>
              <div style="font-size: 20px"><b>项目技术栈</b></div>
              <el-divider />
              <div style="color: #666666; font-size: 18px">

              </div>
              <div style="margin-top: 30px; font-size: 18px; color: #666666">

              </div>
            </el-card>
          </div>


        </div>
        <div style="width: 50%; margin-left: 10px">
          <el-card style="height: 500px">
            <el-carousel height="450px" :interval="10000">
              <el-carousel-item v-for="item in imgs" :key="item">
                <img :src="item" alt="" style="width: 100%; height: 100%">
              </el-carousel-item>
            </el-carousel>
          </el-card>
        </div>
      </div>
    </el-card>

  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      imgs:[
        'https://img1.baidu.com/it/u=30614931,2254452573&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=303',
          'https://img2.baidu.com/it/u=2672012757,342050636&fm=253&fmt=auto&app=138&f=JPEG?w=450&h=300',
          'https://img0.baidu.com/it/u=2509718580,2012924334&fm=253&fmt=auto&app=138&f=JPEG?w=499&h=319'
      ],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  }
}
</script>
