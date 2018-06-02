/** 分页查询时，后台返回的数据中分页参数的命名，返回示例：
 *
 * {
 *
 * "msg":"ok",
 * "code":1,
 * "succ":true,
 * "oper":"default",
 * "page":{
 *          "current":1,
 *          "pages":2,
 *          "records":[],
 *          "size":3,
 *          "total":5
 *        }
 * }
 * */
export const pageParamNames = ["current","pages","size","total"]

export const permType = {
  MENU: 1,
  BUTTON: 2,
  API: 3,
}

/**
 * 下拉选择框数据：权限类型
 *
 */
export const permTypeOptions = [
  {value: permType.MENU, label: '菜单'},
  {value: permType.BUTTON, label: '按钮'},
  {value: permType.API, label: '接口'}
]

/**
 * 权限类型
 * @type {Map<any, any>}
 */
export const permTypeMap = new Map([
  [permType.MENU,'菜单'],
  [permType.BUTTON,'按钮'],
  [permType.API,'测试']
])

export const confirm = { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'}

export const root = {
  rval: 'root',
  pval: '*'
}
