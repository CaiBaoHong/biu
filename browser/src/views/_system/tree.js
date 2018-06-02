import {permType} from '@/utils/constants'
import {asyncRouterMap} from '@/router' //路由表，定义了菜单和按钮的元数据，可以用来生成权限控制的菜单按钮树

export default{
  /**
   * 根据前端定义的路由表，生成菜单权限列表
   */
  generateMenuPermissionTree() {
    //预处理
    let routeArr = asyncRouterMap.map(route => {
      let temp = Object.assign({}, route) // copy obj
      if (!temp.alwaysShow && temp.children && temp.children.length == 1) {
        //如果是只有一个子菜单的顶级菜单，把子级的菜单meta复制到父级用于生成菜单树时显示菜单名称
        temp.meta = temp.children[0].meta
        temp.children = []
      }
      return temp
    })
    //过滤路由表，得到需要进行权限控制的菜单树
    let permissionControlRoutes = this.filterPermControlRouter(routeArr)
    //递归形成菜单树
    return this.mapToMenuPermissionTree(permissionControlRoutes, null)
  },

  /**
   * 根据输入的路由表过滤出一个需要进行权限控制的路由表
   * @param routeArr
   */
  filterPermControlRouter(routeArr) {
    const routes = routeArr.filter(route => {
      if (route.meta && route.meta.perm) {
        if (route.children && route.children.length) {
          route.children = this.filterPermControlRouter(route.children)
        }
        return true
      }
      return false
    })
    return routes
  },


  /**
   * 根据输入的路由表，生成菜单权限树
   * @param routeArr 路由表
   * @param parentPermVal 初始的父级权限值
   */
  mapToMenuPermissionTree(routeArr, parentPermVal) {
    return routeArr.map(route => {
      let obj = {};
      if (route.meta && route.meta.perm) {
        obj.pval = route.meta.perm
        obj.pname = route.meta.title
        obj.ptype = permType.MENU;
        obj.parent = parentPermVal;
      }
      if (route.children) {
        obj.children = this.mapToMenuPermissionTree(route.children, obj.pval)
      }
      return obj;
    })
  },

  /**
   * 根据菜单树，生成按钮权限树
   * @param btnPermMap 按钮权限，按parent分组
   * @param menuPermissionTree 菜单树
   */
  mapToButtonPermissionTree(btnPermMap,menuPermissionTree) {
    return menuPermissionTree.map(perm => {

      if(perm){
        if(!perm.children){
          perm.children = []
        }
        if(perm.ptype==permType.MENU){
          let btnPerms = btnPermMap[perm.pval]
          if(btnPerms){
            btnPerms.forEach(p=>{
              perm.children.push(p)
            })
          }
        }
        if(perm.children&&perm.children.length>0){
          this.mapToButtonPermissionTree(btnPermMap,perm.children)
        }
      }
      return perm;

    })
  },


}
