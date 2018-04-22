import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css'// progress bar style
import { getToken } from '@/utils/auth' // getToken from cookie

NProgress.configure({ showSpinner: false })// NProgress Configuration

// permissiom judge function
function hasPermission(roles, permissionRoles) {
  if (roles.indexOf('admin') >= 0) return true // admin permission passed directly
  if (!permissionRoles) return true
  return roles.some(role => permissionRoles.indexOf(role) >= 0)
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




})

router.afterEach(() => {
  NProgress.done() // finish progress bar
})
