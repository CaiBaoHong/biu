import { asyncRouterMap, constantRouterMap } from '@/router'

/**
 * 通过meta.role判断是否与当前用户权限匹配
 * @param roles
 * @param route
 */
/*
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    let hasSome =  roles.some(role => {
      let result = route.meta.roles.indexOf(role) >= 0
      return result
    })

  } else {

    return true
  }
}
*/
function hasPermission(perms, route) {
  //如果没有声明meta或者meta.perm，都视为可以公共访问的路由
  if (!route.meta || !route.meta.perm) {
    return true;
  }
  console.log("route.meta.title: "+route.meta.title+", hasPermission: "+perms.has(route.meta.perm))
  return perms.has(route.meta.perm);
}



/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param asyncRouterMap
 * @param roles
 */
/*
function filterAsyncRouter(asyncRouterMap, roles) {
  const accessedRouters = asyncRouterMap.filter(route => {
    if (hasPermission(roles, route)) {
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children, roles)
      }
      return true
    }
    return false
  })
  return accessedRouters
}
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
    /*
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {
        const { roles } = data
        let accessedRouters
        if (roles.indexOf('admin') >= 0) {
          accessedRouters = asyncRouterMap
        } else {
          accessedRouters = filterAsyncRouter(asyncRouterMap, roles)
        }
        commit('SET_ROUTERS', accessedRouters)
        resolve()
      })
    }
    */
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {

        let accessedRouters;
        let perms = new Set(data.perms);

        // * 是特殊值，代表所有权限
        if (perms.has('*')) {
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
