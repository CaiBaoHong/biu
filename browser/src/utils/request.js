import axios from 'axios'
import {Message,MessageBox} from 'element-ui'
import store from '@/store'
import {getToken} from '@/utils/auth'
import Code from '@/utils/code'

// create an axios instance
const service = axios.create({
  baseURL: process.env.BASE_API, // api的base_url
  timeout: 15000, // 请求超时时间
  withCredentials: true //使前台能够保存cookie
})

// request interceptor
service.interceptors.request.use(config => {
  // Do something before request is sent
  if (store.getters.token) {
    config.headers['X-Token'] = getToken() // 让每个请求携带token-- ['X-Token']为自定义key 请根据实际情况自行修改
  }
  return config
}, error => {
  // Do something with request error
  console.log(error) // for debug
  Promise.reject(error)
})

// respone interceptor
service.interceptors.response.use(

  res => {
    //如果后台返回的json显示成功，pass
    if(res.data.succ) return res;

    // 显示错误信息
    Message({ message: res.data.msg, type: 'error', duration: 5000});

    // 提示登录
    if(res.data.code == Code.UNAUTHEN
      //|| res.data.code == Code.UNAUTHZ //没有权限不用管，提示够了，不需要提示用户重新登录
      || res.data.code == Code.SESSION_TIMOUT){
      MessageBox.confirm('你已被登出，可以取消继续留在该页面，或者重新登录', '确定登出', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        store.dispatch('FedLogOut').then(() => {
          location.reload();// 为了重新实例化vue-router对象 避免bug
        });
      })
    }
    return Promise.reject('error');
  },

  err => {
    console.log('err' + error)// for debug
    Message({message: err.message, type: 'error', duration: 5000})
    return Promise.reject(error)
  }

)

export default service
