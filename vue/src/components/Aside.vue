<template>
  <el-menu :default-openeds="opens" style="min-height: 100%; overflow-x: hidden"
           background-color="rgb(48, 65, 86)"
           text-color="#fff"
           active-text-color="#ffd04b"
           :collapse-transition="false"
           :collapse="isCollapse"
           router
  >
    <div style="height: 60px; line-height: 60px; text-align: center">
      <img src="../assets/logo.png" alt="" style="width: 40px; position: relative; top: 5px;">
      <b style="color: white; margin-left: 5px" v-show="logoTextShow">货车运输管理系统</b>
    </div>
    <div v-for="item in menus" :key="item.id">
      <div v-if="item.path">
        <el-menu-item :index="item.path">
          <i :class="item.icon"></i>
          <span slot="title">{{ item.name }}</span>
        </el-menu-item>
      </div>
      <div v-else>
        <el-submenu :index="item.id + ''">
          <template slot="title">
            <i :class="item.icon"></i>
            <span slot="title">{{ item.name }}</span>
          </template>
          <div  v-for="subItem in item.children" :key="subItem.id">
            <el-menu-item :index="subItem.path">
              <i :class="subItem.icon"></i>
              <span slot="title">{{ subItem.name }}</span>
            </el-menu-item>
          </div>
        </el-submenu>
      </div>
    </div>
  </el-menu>
</template>

<script>
export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean
  },
  data() {
    // 默认菜单数据，确保即使没有从后端获取到菜单数据，侧边栏也能正常显示
    const defaultMenus = [
      {
        id: 1,
        name: "首页",
        path: "/home",
        icon: "el-icon-s-home",
        children: []
      },
      {
        id: 2,
        name: "线路管理",
        path: "",
        icon: "el-icon-location",
        children: [
          {
            id: 21,
            name: "线路列表",
            path: "/routelist",
            icon: "el-icon-document",
            children: []
          },
          {
            id: 22,
            name: "线路规划",
            path: "/routeplan",
            icon: "el-icon-guide",
            children: []
          },
          {
            id: 23,
            name: "线路分析",
            path: "/routeanalysis",
            icon: "el-icon-data-analysis",
            children: []
          }
        ]
      },
      {
        id: 3,
        name: "车辆管理",
        path: "/truck",
        icon: "el-icon-truck",
        children: []
      },
      {
        id: 4,
        name: "订单管理",
        path: "/orderform",
        icon: "el-icon-s-order",
        children: []
      },
      {
        id: 5,
        name: "用户管理",
        path: "/user",
        icon: "el-icon-user",
        children: []
      },
      {
        id: 6,
        name: "系统设置",
        path: "",
        icon: "el-icon-setting",
        children: [
          {
            id: 61,
            name: "菜单管理",
            path: "/menu",
            icon: "el-icon-menu",
            children: []
          },
          {
            id: 62,
            name: "角色管理",
            path: "/role",
            icon: "el-icon-s-custom",
            children: []
          }
        ]
      }
    ];
    
    const storedMenus = localStorage.getItem("menus") ? JSON.parse(localStorage.getItem("menus")) : defaultMenus;
    
    return {
      menus: storedMenus,
      opens: storedMenus.map(v => v.id + '')
    }
  },
  created() {
    // 如果localStorage中没有菜单数据，则设置默认菜单数据
    if (!localStorage.getItem("menus")) {
      localStorage.setItem("menus", JSON.stringify(this.menus));
      console.log("已设置默认菜单数据到localStorage");
    }
  }
}
</script>

<style scoped>
.el-menu-item.is-active {
  background-color: rgb(38, 52, 69) !important;
}
.el-menu-item:hover {
  background-color: rgb(38, 52, 69) !important;
}

.el-submenu__title:hover {
  background-color: rgb(38, 52, 69) !important;
}
/*解决收缩菜单文字不消失问题*/
.el-menu--collapse span {
  visibility: hidden;
}
</style>