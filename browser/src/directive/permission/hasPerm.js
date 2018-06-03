/** 检查权限的方法 */
import store from '@/store'

export default function(permStr) {
  // 如果传入的权限字符串值是空的，视为有权限
  if(!permStr || permStr=='') return true;
  // 超级管理员权限
  if(store.getters.perms.some(p=>p.val=="*")) return true;
  // 校验权限
  return store.getters.perms.some(p=>p.val==permStr)
}

