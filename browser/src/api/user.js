/**
 * “用户管理”相关接口
 */
import request from '@/utils/request'

export function queryUser(queryParam,pageParam) {
  return request({
    url: '/user/query',
    method: 'post',
    data: {
      ...queryParam,
      current: pageParam.current,
      size: pageParam.size
    }
  })
}

export function updateUser(tempData) {
  return request({
    url: '/user/info',
    method: 'patch',
    data: tempData
  })
}

export function addUser(tempData) {
  return request({
    url: '/user',
    method: 'post',
    data: tempData
  })
}

