import Vue from 'vue'
import VueRouter from 'vue-router'
import store from "@/store";

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/404',
    name: '404',
    component: () => import('../views/404.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  routes
})

// 重置路由
export const resetRouter = () => {
  router.matcher = new VueRouter({
    mode: 'history',
    routes
  })
}

// 注意：刷新页面会导致页面路由重置
export const setRoutes = () => {
  const storeMenus = localStorage.getItem("menus");
  if (storeMenus) {
    // 拼装动态路由
    const manageRoute = { path: '/', name: 'Manage', component: () => import('../views/Manage.vue'), redirect: "/home", children: [
        { path: 'person', name: '个人信息', component: () => import('../views/Person.vue')},
        { path: 'password', name: '修改密码', component: () => import('../views/Password.vue')},
        { path: 'home', name: '首页', component: () => import('../views/Home.vue')},
      ] }
    const menus = JSON.parse(storeMenus)
    console.log('设置动态路由，菜单数据:', menus);
    
    menus.forEach(item => {
      if (item.path) {  // 当且仅当path不为空的时候才去设置路由
        // 修正路径处理逻辑：确保路径以斜杠开头
        let routePath = item.path.startsWith('/') ? item.path.substring(1) : item.path;
        let itemMenu = { 
          path: routePath, 
          name: item.name, 
          component: () => import('../views/' + item.pagePath + '.vue')
        }
        manageRoute.children.push(itemMenu)
        console.log(`添加路由: ${item.name} -> ${routePath}`);
      } else if(item.children && item.children.length) {
        item.children.forEach(child => {
          if (child.path) {
            // 修正路径处理逻辑：确保路径以斜杠开头
            let routePath = child.path.startsWith('/') ? child.path.substring(1) : child.path;
            let itemMenu = { 
              path: routePath, 
              name: child.name, 
              component: () => import('../views/' + child.pagePath + '.vue')
            }
            manageRoute.children.push(itemMenu)
            console.log(`添加子菜单路由: ${child.name} -> ${routePath}`);
          }
        })
      }
    })

    // 获取当前的路由对象名称数组
    const currentRouteNames = router.getRoutes().map(v => v.name)
    console.log('当前已注册的路由:', currentRouteNames);
    
    if (!currentRouteNames.includes('Manage')) {
      // 动态添加到现在的路由对象中去
      router.addRoute(manageRoute)
      console.log('动态路由已添加:', manageRoute);
    }
  } else {
    console.warn('没有找到菜单数据，设置默认菜单数据');
    
    // 如果没有菜单数据，设置默认菜单数据
    const defaultMenus = [
      { id: 1, name: "首页", path: "/home", icon: "el-icon-s-home", pagePath: "Home" },
      { id: 2, name: "线路管理", path: "", icon: "el-icon-location", children: [
        { id: 21, name: "线路列表", path: "/routelist", icon: "el-icon-document", pagePath: "RouteList" },
        { id: 22, name: "线路规划", path: "/routeplan", icon: "el-icon-guide", pagePath: "RoutePlan" },
        { id: 23, name: "线路分析", path: "/routeanalysis", icon: "el-icon-data-analysis", pagePath: "RouteAnalysis" }
      ]},
      { id: 3, name: "车辆管理", path: "/truck", icon: "el-icon-truck", pagePath: "Truck" },
      { id: 4, name: "订单管理", path: "/orderform", icon: "el-icon-s-order", pagePath: "OrderForm" },
      { id: 5, name: "用户管理", path: "/user", icon: "el-icon-user", pagePath: "User" },
      { id: 6, name: "系统设置", path: "", icon: "el-icon-setting", children: [
        { id: 61, name: "菜单管理", path: "/menu", icon: "el-icon-menu", pagePath: "Menu" },
        { id: 62, name: "角色管理", path: "/role", icon: "el-icon-s-custom", pagePath: "Role" }
      ]}
    ];
    
    // 将默认菜单数据存储到localStorage
    localStorage.setItem("menus", JSON.stringify(defaultMenus));
    console.log("已设置默认菜单数据到localStorage");
    
    // 直接处理默认菜单数据，避免递归调用
    const manageRoute = { path: '/', name: 'Manage', component: () => import('../views/Manage.vue'), redirect: "/home", children: [
        { path: 'person', name: '个人信息', component: () => import('../views/Person.vue')},
        { path: 'password', name: '修改密码', component: () => import('../views/Password.vue')},
        { path: 'home', name: '首页', component: () => import('../views/Home.vue')},
      ] }
    
    defaultMenus.forEach(item => {
      if (item.path) {
        let routePath = item.path.startsWith('/') ? item.path.substring(1) : item.path;
        let itemMenu = { 
          path: routePath, 
          name: item.name, 
          component: () => import('../views/' + item.pagePath + '.vue')
        }
        manageRoute.children.push(itemMenu)
        console.log(`添加默认路由: ${item.name} -> ${routePath}`);
      } else if(item.children && item.children.length) {
        item.children.forEach(child => {
          if (child.path) {
            let routePath = child.path.startsWith('/') ? child.path.substring(1) : child.path;
            let itemMenu = { 
              path: routePath, 
              name: child.name, 
              component: () => import('../views/' + child.pagePath + '.vue')
            }
            manageRoute.children.push(itemMenu)
            console.log(`添加默认子菜单路由: ${child.name} -> ${routePath}`);
          }
        })
      }
    })
    
    // 获取当前的路由对象名称数组
    const currentRouteNames = router.getRoutes().map(v => v.name)
    
    if (!currentRouteNames.includes('Manage')) {
      router.addRoute(manageRoute)
      console.log('默认动态路由已添加:', manageRoute);
    }
  }
}

// 每次刷新页面都要重新设置路由，否则路由就会被重置
setRoutes()

router.beforeEach((to, from, next) => {
  console.log('路由守卫触发:', { from: from.path, to: to.path });
  localStorage.setItem("currentPathName", to.name)  // 设置当前的路由名称
  store.commit("setPath")

  // 检查是否有匹配的路由记录
  if (!to.matched.length) {
    console.warn('路由未匹配:', to.path);
    const menus = localStorage.getItem("menus")
    console.log('检查菜单数据是否存在:', !!menus);

    if (!menus) {
      // 未登录，重定向到登录页
      console.log('未登录，重定向到登录页');
      next("/login")
    } else {
      // 已登录（有菜单）但路由未匹配，说明可能是动态路由还没加载完毕。

      // 1. 确保路由已被加载（尽管在全局已调用，这里是安全措施）
      console.log('重新设置路由');
      setRoutes()

      // 2. 重新导航到目标路径，强制 Vue Router 重新匹配路由表，解决时序问题。
      // 使用 replace: true 避免在历史记录中留下重定向的路径。
      console.log('重新导航到目标路径:', to.fullPath);
      next({ path: to.fullPath, replace: true })
    }
  } else if ((to.path === '/home' || to.path === '/') && !localStorage.getItem("user")) {
    // 如果直接访问/home或/但未登录，重定向到登录页
    console.log('未登录访问首页，重定向到登录页');
    next("/login")
  } else {
    console.log('路由匹配成功:', to.path);
    next()
  }
})

export default router