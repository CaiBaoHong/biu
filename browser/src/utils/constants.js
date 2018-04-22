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
export const connTypeOptions = [
  {value: 'HW_COAP',label: 'HW_COAP'},
  {value: 'TCP',label: 'TCP'},
  {value: 'UDP',label: 'UDP'}
]

/**
 * 订阅消息的类型
 * @type {Map<any, any>}
 */
export const subscriptionType = new Map([
  ['deviceInfoChanged','设备信息变化'],
  ['deviceDataChanged','设备数据变化'],
  ['deviceDeleted','删除设备'],
  ['deviceEvent','设备事件']
])


