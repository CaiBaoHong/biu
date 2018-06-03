/** 权限指令 */
import hasPerm from './hasPerm'

export default {
  bind: function (el,binding) {
    if(el && binding.value){
      console.log("current user has required permission: "+binding.value+"? --> "+hasPerm(binding.value))
      if(!hasPerm(binding.value)){
        el.parentNode.removeChild(el)
      }
    }
  },
}
