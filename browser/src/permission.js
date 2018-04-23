import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css'// progress bar style
import { getToken } from '@/utils/auth' // getToken from cookie

NProgress.configure({ showSpinner: false })// NProgress Configuration

/**
 * 匹配权限
 * @param userPerms 用户拥有的权限集合，后台返回来，存在vuex，数据类型是Set
 * @param routerPerm 定义的src/router/index.js的路由表asyncRouterMap中
 * @returns {*}
 */
function hasPermission(userPerms, routerPerm) {
  //特殊值，*代表所有资源权限
  if(userPerms.has('*')){
    return true;
  }
  //如果菜单路由上没有声明perm属性，默认显示该菜单，代表所有人可以访问
  if(!routerPerm){
    return true;
  }
  //判断用户的资源权限集合中是否包含该菜单路由声明的资源权限
  return userPerms.has(routerPerm);
}

const loginRoute = "/login"
const whiteList = new Set([loginRoute,'/authredirect'])// no redirect whitelist

//在路由变化时候检查登录状态
router.beforeEach((to, from, next) => {

  NProgress.start(); // start progress bar

  let token = getToken();
  let hasToken = token != 'undefined' && token != undefined && token !=null && token != '';

  // 1.cookie中没有token的情况：
  if(!hasToken){
    // 如果要访问的路由在白名单中，直接进入；否则，重定向到登录页面
    whiteList.has(to.path) ? next() : next(loginRoute);
    NProgress.done();
    return;
  }

  // 2.cookie中有token的情况：
  // 如果访问登录页面，因为有token，视为登录状态，跳转到首页
  if(to.path == loginRoute){
    next('/');
    NProgress.done();
    return;
  }

  // 3. 点击左侧菜单时会触发页面路由变化，下面判断是否有权限跳转页面
  // 3.1 如果vuex中有权限信息，判断
  if(store.getters.perms.size > 0){
    if (hasPermission(store.getters.perms, to.meta.perm)) {
      next()
    } else {
      next({ path: '/401', replace: true, query: { noGoBack: true }})
    }
    return;
  }

  // 3.2 如果vuex中没有权限信息，就查询后台
  store.dispatch('GetUserInfo').then(res => {
    // 如果后台session超时退出登录了，会被src/utils/request.js拦截，会弹出提示用户重新登录
    // 所以这里肯定是通过拦截后返回来的
    const perms = res.data.perms // perms: ['menu:1','menu:2']

    if(perms==null || perms==undefined || perms=='undefined' || perms==''){
      Message.error('找不到登录用户的菜单权限信息')
      NProgress.done();
    }

    // 根据后台返回的资源权限动态生成路由表
    store.dispatch('GenerateRoutes', { perms }).then(() => {
      router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
      next({ ...to, replace: true }) // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
    })
  }).catch(() => {
    store.dispatch('FedLogOut').then(() => {
      Message.error('查询登录用户的信息失败，请重试')
      next({ path: '/login' })
    })
  })


  /*
  if (store.getters.roles.length == 0) { // 判断当前用户是否已拉取完user_info信息
    store.dispatch('GetUserInfo').then(res => { // 拉取user_info

      const roles = res.data.roles // note: roles must be a array! such as: ['editor','develop']

      if( roles==null || roles.length == 0 ){

        Message.error('找不到用户的角色信息')
        NProgress.done();

      }else{

        store.dispatch('GenerateRoutes', { roles }).then(() => { // 根据roles权限生成可访问的路由表
          router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
          next({ ...to, replace: true }) // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
        })

      }

    }).catch(() => {
      store.dispatch('FedLogOut').then(() => {
        Message.error('查询登录用户的信息失败，请重试')
        next({ path: '/login' })
      })
    })
  } else {
    // 没有动态改变权限的需求可直接next() 删除下方权限判断 ↓
    if (hasPermission(store.getters.roles, to.meta.roles)) {
      next()//
    } else {
      next({ path: '/401', replace: true, query: { noGoBack: true }})
    }
    // 可删 ↑
  }
  */




})

router.afterEach(() => {
  NProgress.done() // finish progress bar
})
