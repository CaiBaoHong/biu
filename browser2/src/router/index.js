import Vue from 'vue'
import Router from 'vue-router'
const _import = require('./_import_' + process.env.NODE_ENV)
// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/** note: submenu only apppear when children.length>=1
*   detail see  https://panjiachen.github.io/vue-element-admin-site/#/router-and-nav?id=sidebar
**/

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirct in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    roles: ['admin','editor']     will control the page roles (you can set multiple roles)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if true ,the page will no be cached(default is false)
  }
**/
export const constantRouterMap = [
  { path: '/login', component: _import('login/index'), hidden: true },
  { path: '/authredirect', component: _import('login/authredirect'), hidden: true },
  { path: '/404', component: _import('errorPage/404'), hidden: true },
  { path: '/401', component: _import('errorPage/401'), hidden: true },
  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    children: [{
      path: 'dashboard',
      component: _import('dashboard/index'),
      name: 'dashboard',
      meta: { title: '首页', icon: 'dashboard', noCache: true }
    }]
  },
]

export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [
  {
    path: '/permission',
    component: Layout,
    redirect: '/permission/index',
    alwaysShow: true, // will always show the root menu
    meta: {
      title: '权限测试页',
      icon: 'lock',
      roles: ['admin', 'editor'] // you can set roles in root nav
    },
    children: [{
      path: 'page',
      component: _import('permission/page'),
      name: 'pagePermission',
      meta: {
        title: '页面权限',
        roles: ['admin'] // or you can only set roles in sub nav
      }
    }, {
      path: 'directive',
      component: _import('permission/directive'),
      name: 'directivePermission',
      meta: {
        title: '指令权限'
        // if do not set roles, means: this page does not require permission
      }
    }]
  },

  {
    path: '/menu1',
    component: Layout,
    children: [{
      path: 'index',
      name: 'menu1',
      component: _import('menu/menu1'),
      meta: { title: '菜单1', icon: 'icon' }
    }]
  },

  {
    path: '/menu2',
    component: Layout,
    children: [{
      path: 'index',
      name: 'menu2',
      component: _import('menu/menu2'),
      meta: { title: '菜单2', icon: 'icon' }
    }]
  },

  {
    path: '/menu3',
    component: Layout,
    meta: {
      title: '菜单3',
      icon: 'chart'
    },
    children: [
      { path: 'menu3_1', component: _import('menu/menu3_1'), name: 'menu3_1', meta: { title: '菜单3-1', icon: 'chart', noCache: true }},
      { path: 'menu3_2', component: _import('menu/menu3_2'), name: 'menu3_2', meta: { title: '菜单3-2', icon: 'chart', noCache: true }},
      { path: 'menu3_3', component: _import('menu/menu3_3'), name: 'menu3_3', meta: { title: '菜单3-3', icon: 'chart', noCache: true }}
    ]
  },

  {
    path: '/menu4',
    name: 'menu4',
    component: Layout,
    redirect: '/menu4/menu4_1/a',
    meta: {
      title: '菜单4',
      icon: 'example'
    },
    children: [
      {
        path: '/menu4/menu4_1',
        name: 'menu4_1',
        component: _import('menu/menu4_1/index'),
        redirect: '/menu4/menu4_1/a',
        meta: {
          title: '菜单4-1',
          icon: 'table'
        },
        children: [
          { path: 'a', name: 'menu4_1_a', component: _import('menu/menu4_1/a'), meta: { title: '菜单4-1-a' }},
          { path: 'b', name: 'menu4_1_b', component: _import('menu/menu4_1/b'), meta: { title: '菜单4-1-b' }},
          { path: 'c', name: 'menu4_1_c', component: _import('menu/menu4_1/c'), meta: { title: '菜单4-1-c' }}
        ]
      },
      { path: 'menu4/menu4_2', name: 'menu4_2', icon: 'tab', component: _import('menu/menu4_2/index'), meta: { title: '菜单4-2' }}
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
]
