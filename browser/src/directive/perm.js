/** 权限指令 */
import store from '@/store'

export default {
  inserted: function (el,binding) {
    displayByPermission(el,binding)
  },
  update: function (el,binding) {
    displayByPermission(el,binding)
  },
}

function displayByPermission(el,binding){
  if(el && binding.value){
    if(store.getters.perms.has(binding.value)){
      el.style.display = 'inline'
    }else{
      el.style.display = 'none'
    }
  }
}
