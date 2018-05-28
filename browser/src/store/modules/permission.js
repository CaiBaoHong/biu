import { asyncRouterMap, constantRouterMap } from '@/router'

/**
 * 通过meta.perm判断是否与当前用户权限匹配
 * @param perms 登录用户的权限
 * @param route 路由对象
 */
function hasPermission(perms, route) {
  //如果没有声明meta或者meta.perm，都视为可以公共访问的路由
  if (!route.meta || !route.meta.perm) {
    return true;
  }
  return perms.some(p=>p.val==route.meta.perm)
}

/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param asyncRouterMap
 * @param roles
 */
function filterAsyncRouter(asyncRouterMap, perms) {
  const accessedRouters = asyncRouterMap.filter(route => {
    if (hasPermission(perms, route)) {
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children, perms)
      }
      return true
    }
    return false
  })
  return accessedRouters
}

const permission = {
  state: {
    routers: constantRouterMap,
    addRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers)
    }
  },
  actions: {
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {

        let accessedRouters
        let perms = data.perms

        if(perms.some(p=>p.val=="*")) {
          accessedRouters = asyncRouterMap
        } else {
          accessedRouters = filterAsyncRouter(asyncRouterMap, perms)
        }

        commit('SET_ROUTERS', accessedRouters)
        resolve()
      })
    }
  }
}

export default permission
