/**
 * “权限管理”相关接口
 */
import request from '@/utils/request'

export default{
  /**
   * 添加权限
   * @param data
   */
  addPerm(data) {
    return request({
      url: '/sys_perm',
      method: 'post',
      data
    })
  },

  /**
   * 批量添加权限
   * @param data
   */
  batchSavePerms(permArr) {
    return request({
      url: '/sys_perm/batch_save',
      method: 'post',
      data: permArr
    })
  },

  /**
   * 删除权限
   * @param data
   */
  deletePerm(data) {
    return request({
      url: '/sys_perm',
      method: 'delete',
      data
    })
  },

  /**
   * 查询权限
   * @param queryParam
   * @param pageParam
   */
  queryPerm(queryParam,pageParam) {
    return request({
      url: '/sys_perm/query',
      method: 'post',
      data: {
        ...queryParam,
        current: pageParam.current,
        size: pageParam.size
      }
    })
  },

  /**
   * 更新权限
   * @param data
   */
  updatePerm(data) {
    return request({
      url: '/sys_perm/info',
      method: 'patch',
      data
    })
  },

  /**
   * 更新权限的权限
   * @param perm
   */
  listApiPermMetadata() {
    return request({
      url: '/sys_perm/meta/api',
      method: 'get'
    })
  },

  /**
   * 列出所有菜单、按钮、接口等权限
   * @param perm
   */
  listAllPermissions() {
    return request({
      url: '/sys_perm/list/all',
      method: 'get'
    })
  },

  /**
   * 列出所有菜单和按钮
   * @param perm
   */
  listApiPermissions() {
    return request({
      url: '/sys_perm/list/api',
      method: 'get'
    })
  }

}


