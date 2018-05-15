/**
 * “权限管理”相关接口
 */
import request from '@/utils/request'

/**
 * 添加权限
 * @param data
 */
export function addPerm(data) {
  return request({
    url: '/perm',
    method: 'post',
    data
  })
}

/**
 * 删除权限
 * @param data
 */
export function deletePerm(data) {
  return request({
    url: '/perm',
    method: 'delete',
    data
  })
}

/**
 * 查询权限
 * @param queryParam
 * @param pageParam
 */
export function queryPerm(queryParam,pageParam) {
  return request({
    url: '/perm/query',
    method: 'post',
    data: {
      ...queryParam,
      current: pageParam.current,
      size: pageParam.size
    }
  })
}

/**
 * 更新权限
 * @param data
 */
export function updatePerm(data) {
  return request({
    url: '/perm/info',
    method: 'patch',
    data
  })
}

/**
 * 更新权限的权限
 * @param perm
 */
export function updatePermPerms(data) {
  return request({
    url: '/perm/perm',
    method: 'patch',
    data
  })
}
