/**
 * “角色管理”相关接口
 */
import request from '@/utils/request'

/**
 * 添加角色
 * @param data
 */
export function addRole(data) {
  return request({
    url: '/sys_role',
    method: 'post',
    data
  })
}

/**
 * 删除角色
 * @param data
 */
export function deleteRole(data) {
  return request({
    url: '/sys_role',
    method: 'delete',
    data
  })
}

/**
 * 查询角色
 * @param queryParam
 * @param pageParam
 */
export function queryRole(queryParam,pageParam) {
  return request({
    url: '/sys_role/query',
    method: 'post',
    data: {
      ...queryParam,
      current: pageParam.current,
      size: pageParam.size
    }
  })
}

/**
 * 更新角色
 * @param data
 */
export function updateRole(data) {
  return request({
    url: '/sys_role/info',
    method: 'patch',
    data
  })
}

/**
 * 更新角色的权限
 * @param perm
 */
export function updateRolePerms(data) {
  return request({
    url: '/sys_role/perm',
    method: 'patch',
    data
  })
}

/**
 * 列出除了管理员之外的所有角色
 * @param perm
 */
export function listRoles() {
  return request({
    url: '/sys_role/list',
    method: 'get'
  })
}

/**
 * 查选角色的所有权限值
 * @param rid
 */
export function findRolePvals(rid) {
  return request({
    url: '/sys_role/'+rid+'/perms',
    method: 'get'
  })
}
