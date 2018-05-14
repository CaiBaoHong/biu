import request from '@/utils/request'

export function loginByUsername(username, password) {
  const data = {
    uname: username,
    pwd: password
  }
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function getUserInfo(token) {
  return request({
    url: '/auth/info',
    method: 'get',
    params: { token }
  })
}

export function testUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

