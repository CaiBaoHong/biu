/**
 * “用户管理”相关接口
 */
import request from '@/utils/request'

export default {

  queryUser(queryParam,pageParam) {
    return request({
      url: '/sys_user/query',
      method: 'post',
      data: {
        ...queryParam,
        current: pageParam.current,
        size: pageParam.size
      }
    })
  },

  updateUser(data) {
    return request({
      url: '/sys_user/info',
      method: 'patch',
      data
    })
  },

  updatePwd(data) {
    return request({
      url: '/sys_user/pwd',
      method: 'patch',
      data
    })
  },

  addUser(data) {
    return request({
      url: '/sys_user',
      method: 'post',
      data
    })
  },

  deleteUser(data) {
    return request({
      url: '/sys_user',
      method: 'delete',
      data
    })
  },

  /**
   * 更新用户的角色
   * @param perm
   */
  updateUserRoles(data) {
    return request({
      url: '/sys_user/role',
      method: 'patch',
      data
    })
  }

}

