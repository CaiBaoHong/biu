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


/**
 * 下拉选择框数据：连接类型
 *
 */
export const permTypeOptions = [
  {value: 1, label: '菜单'},
  {value: 2, label: '按钮'},
  {value: 3, label: '接口'}
]

/**
 * 订阅消息的类型
 * @type {Map<any, any>}
 */
export const permTypeMap = new Map([
  [1,'菜单'],
  [2,'按钮'],
  [3,'测试']
])

export const addSuccNotify = { title: '成功',message: '添加成功',type: 'success',duration: 2000}
export const deleteSuccNotify = { title: '成功',message: '删除成功',type: 'success',duration: 2000}
export const updateSuccNotify = { title: '成功',message: '更新成功',type: 'success',duration: 2000}
export const deleteConfirm = { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'}

