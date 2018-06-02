/**
 * “角色管理”相关接口
 */
import request from '@/utils/request'

export default {
  /**
   * 添加角色
   * @param data
   */
  addRole(data) {
    return request({
      url: '/sys_role',
      method: 'post',
      data
    })
  },

  /**
   * 删除角色
   * @param data
   */
  deleteRole(data) {
    return request({
      url: '/sys_role',
      method: 'delete',
      data
    })
  },

  /**
   * 查询角色
   * @param queryParam
   * @param pageParam
   */
  queryRole(queryParam,pageParam) {
    return request({
      url: '/sys_role/query',
      method: 'post',
      data: {
        ...queryParam,
        current: pageParam.current,
        size: pageParam.size
      }
    })
  },

  /**
   * 更新角色
   * @param data
   */
  updateRole(data) {
    return request({
      url: '/sys_role/info',
      method: 'patch',
      data
    })
  },

  /**
   * 更新角色的权限
   * @param perm
   */
  updateRolePerms(data) {
    return request({
      url: '/sys_role/perm',
      method: 'patch',
      data
    })
  },

  /**
   * 添加角色的权限
   * @param perm
   */
  addRolePerm(data) {
    return request({
      url: '/sys_role/perm',
      method: 'post',
      data
    })
  },

  /**
   * 删除角色的权限
   * @param perm
   */
  deleteRolePerm(data) {
    return request({
      url: '/sys_role/perm',
      method: 'delete',
      data
    })
  },

  /**
   * 查选角色的所有权限值
   * @param rid
   */
  findRolePerms(rid) {
    return request({
      url: '/sys_role/'+rid+'/perms',
      method: 'get'
    })
  }

}

