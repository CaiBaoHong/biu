<template>
  <div class="app-container">
    <el-row :gutter="20">

      <!--左侧菜单按钮权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header"  >
            <div style="display: flex;justify-content:space-between;align-items: center;">
              <span style="font-size: 22px;">菜单、按钮权限元数据</span>
              <el-tooltip content="添加顶级菜单" placement="top">
                <el-button style="font-size: 25px;"
                           type="text" @click="handleAddTopMenu"
                           icon="el-icon-circle-plus-outline"
                           circle></el-button>
              </el-tooltip>
            </div>
            <span style="font-size: 14px;color:#909399;">提示：可以通过拖拽来调整菜单或按钮的位置</span>
          </div>
          <el-input style="margin-bottom: 15px;" placeholder="输入关键字进行过滤" v-model="filterMenuButtonText">
          </el-input>
          <el-tree
            draggable
            ref="menuButtonTreeRef"
            :filter-node-method="filterNode"
            @node-drop="handleMenuButtonDrop"
            :data="menuButtonTree"
            :props="{label:'pname',children:'children'}"
            show-checkbox
            node-key="pid"
            default-expand-all
            :expand-on-click-node="false">
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>
                  <span style="margin-right: 10px;">{{ node.label }}</span>
                  <el-tag v-if="data.ptype==1" type="success" size="mini" >菜单</el-tag>
                  <el-tag v-else-if="data.ptype==2" type="warning" size="mini" ><i class="el-icon-rank"></i>按钮</el-tag>
                  <i style="margin-left: 10px;" class="el-icon-rank"></i>
                </span>
                <span>
                  <el-tooltip content="添加子菜单权限或按钮权限" placement="top">
                    <el-button :disabled="data.ptype!=1" type="text" size="mini" icon="el-icon-plus" @click="handleAddMenuOrButton(data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="更新" placement="top">
                    <el-button style="margin-left:20px;" type="text" size="mini" icon="el-icon-edit" @click="handleUpdateMenuOrButton(data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <el-button style="margin-left:20px;color:#F56C6C;" type="text" size="mini" icon="el-icon-delete" @click="handleDeleleMenuOrButton(data)"></el-button>
                  </el-tooltip>
                </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>

      <!--右侧后台接口权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header"  >
            <div style="display: flex;justify-content:space-between;align-items: center;">
              <span style="font-size: 22px;">后台接口权限元数据</span>
              <el-tooltip content="添加接口模块" placement="top">
                <el-button style="font-size: 25px;"
                           type="text" @click="handleAddTopApi"
                           icon="el-icon-circle-plus-outline"
                           circle></el-button>
              </el-tooltip>
            </div>
            <span style="font-size: 14px;color:#909399;">提示：可以通过拖拽来调整接口的位置</span>
          </div>
          <el-input style="margin-bottom: 15px;" placeholder="输入关键字进行过滤" v-model="filterApiText">
          </el-input>
          <el-tree
            draggable
            ref="apiTreeRef"
            :filter-node-method="filterNode"
            @node-drop="handleApiDrop"
            :data="apiTree"
            :props="{label:'pname',children:'children'}"
            show-checkbox
            node-key="pid"
            default-expand-all
            :expand-on-click-node="false">
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>
                  <span style="margin-right: 10px;">{{ node.label }}</span>
                  <i class="el-icon-rank"></i>
                </span>
                <span>
                  <el-tooltip content="添加接口" placement="top">
                    <el-button type="text" size="mini" icon="el-icon-plus" @click="handleAddApi(data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="更新" placement="top">
                    <el-button style="margin-left:20px;" type="text" size="mini" icon="el-icon-edit" @click="handleUpdateApi(data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <el-button style="margin-left:20px;color:#F56C6C;" type="text" size="mini" icon="el-icon-delete" @click="handleDeleleApi(data)"></el-button>
                  </el-tooltip>
                </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>

    </el-row>


    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form :rules="rules"
               ref="dataForm"
               :model="temp" label-position="top">

        <el-form-item label="权限名" prop="pname" >
          <el-input v-model="temp.pname" placeholder="例如：用户管理、添加用户"></el-input>
        </el-form-item>

        <el-form-item label="权限值" prop="pval" >
          <el-input v-model="temp.pval" placeholder="例如：user:manage、user:add"></el-input>
        </el-form-item>

        <el-form-item label="权限类型" prop="ptype" >
          <el-select v-model="temp.ptype" placeholder="请选择" >
            <el-option
              v-for="item in permTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='addTopMenu'" type="primary" @click="addTopMenu">确定</el-button>
        <el-button v-if="dialogStatus=='addTopApi'" type="primary" @click="addTopApi">确定</el-button>
        <el-button v-if="dialogStatus=='addMenuOrButton'" type="primary" @click="addMenuOrButton">确定</el-button>
        <el-button v-if="dialogStatus=='updateMenuOrButton'" type="primary" @click="updateMenuOrButton">确定</el-button>
        <el-button v-if="dialogStatus=='addApi'" type="primary" @click="addApi">确定</el-button>
        <el-button v-if="dialogStatus=='updateApi'" type="primary" @click="updateApi">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

  import {parseTime, resetTemp} from '@/utils'
  import {addPerm, deletePerm, queryPerm, updatePerm, listApiPermMetadata, listMenuButtonPermissions,listApiPermissions} from '@/api/perm'
  import {permTypeOptions,permTypeMap,deleteConfirm} from '@/utils/constants'
  import debounce from 'lodash/debounce'

  export default {
    name: 'PermMetaDataManage',
    data() {
      return {
        filterMenuButtonText:'',
        filterApiText:'',
        permTypeOptions,
        mockPid: 1000,
        menuButtonList: [],
        apiList: [],
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          addTopMenu: '添加顶级菜单',
          addTopApi: '添加接口模块',
          addMenuOrButton: '添加子菜单权限或按钮权限',
          updateMenuOrButton: '更新菜单按钮权限',
          deleteMenuOrButton: '删除菜单按钮权限',
          addApi: '添加接口权限',
          updateApi: '更新接口权限',
          deleteApi: '删除接口权限'
        },
        temp: {
          idx: null,
          pid: null,
          pname: null,
          ptype: null,
          pval: null,
          leaf: null,
          parent: null
        },
        rules: {
          pname: [{ required: true, message: '必填', trigger: 'blur' }],
          ptype: [{ required: true, message: '必填', trigger: 'blur' }],
          pval: [{ required: true, message: '必填', trigger: 'change' }]
        },
      }
    },
    watch: {
      //延时查询
      'filterMenuButtonText': debounce(function (val) {
        this.$refs.menuButtonTreeRef.filter(val);
      }, 600),
      //延时查询
      'filterApiText': debounce(function (val) {
        this.$refs.apiTreeRef.filter(val);
      }, 600),

    },




    computed: {
      //生成菜单权限树
      menuButtonTree() {
        return this.findChildren(this.menuButtonList, null)
      },
      //生成后台接口权限树
      apiTree() {
        return this.findChildren(this.apiList, null)
      }
    },

    created() {
      this.initData()
    },

    methods: {

      filterNode(value, data) {
        if (!value) return true;
        return data.pname.indexOf(value) !== -1;
      },


      //获取后台权限数据
      initData() {
        listMenuButtonPermissions().then(res => {
          if (res.data.list) {
            res.data.list.forEach(perm => {
              this.menuButtonList.push(perm);
            });
          }
        }),
        listApiPermissions().then(res => {
          if (res.data.list) {
            res.data.list.forEach(perm => {
              this.apiList.push(perm);
            });
          }
        })
      },
      //用于生成权限树
      findChildren(list, pid) {
        let submenus = list.filter(item => item.parent == pid);
        submenus.forEach(perm => {
          perm.children = this.findChildren(list, perm.pid)
        })
        return submenus;
      },


      ////////////////////////////////菜单按钮权限树的相关方法：


      //新增顶级菜单
      handleAddTopMenu() {
        resetTemp(this.temp)
        this.temp.ptype = 1;//菜单
        this.dialogStatus = 'addTopMenu'
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      addTopMenu() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)//copy obj
            addPerm(tempData).then((res) => {
              tempData.pid = res.data.pid;//后台传回来新增记录的id
              tempData.created = res.data.created;//后台传回来新增记录的时间
              this.menuButtonList.unshift(tempData)
              this.dialogFormVisible = false
              this.$message.success("添加顶级菜单成功")
            })
          }
        })
      },

      //添加子菜单或按钮
      handleAddMenuOrButton(data){
        resetTemp(this.temp)
        this.temp.ptype = 1;//菜单
        this.temp.parent = data.pid
        this.dialogStatus = 'addMenuOrButton'
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate() )
      },
      addMenuOrButton() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)//copy obj
            addPerm(tempData).then((res) => {
              tempData.pid = res.data.pid;//后台传回来新增记录的id
              tempData.created = res.data.created;//后台传回来新增记录的时间
              this.menuButtonList.unshift(tempData)
              this.dialogFormVisible = false
              if(tempData.ptype==1){
                this.$message.success("添加子菜单成功")
              }else if(tempData.ptype==2){
                this.$message.success("添加按钮成功")
              }else{
                this.$message.success("添加成功")
              }

            })
          }
        })
      },

      //更新子菜单或按钮
      handleUpdateMenuOrButton(data) {
        this.temp = Object.assign({}, data) // copy obj
        this.dialogStatus = 'updateMenuOrButton'
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      updateMenuOrButton() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)//copy obj
            updatePerm(tempData).then(res => {
              tempData.updated = res.data.updated
              let idx = this.menuButtonList.findIndex(perm=>perm.pid==tempData.pid)
              this.menuButtonList.splice(idx, 1, tempData)
              this.dialogFormVisible = false
              this.$message.success("更新成功")
            })
          }
        })
      },

      //删除菜单或按钮
      handleDeleleMenuOrButton(data) {
        this.$confirm('您确定要永久删除该权限？', '提示',deleteConfirm).then(() => {
          deletePerm( {pid : data.pid} ).then(res => {
            let idx = this.menuButtonList.findIndex(perm=>perm.pid==data.pid)
            this.menuButtonList.splice(idx, 1)
            this.dialogFormVisible = false
            this.$message.success("删除成功")
          })
        }).catch(() => {
          this.$message({ type: 'info', message: '已取消删除' });
        });
      },

      /**
       * 共四个参数，依次为：被拖拽节点对应的 Node、结束拖拽时最后进入的节点、被拖拽节点的放置位置（before、after、inner）、event
       */
      handleMenuButtonDrop(draggingNode, dropNode, dropType, event) {
        console.log('draggingNode：%o, dropNode: %o, dropType: %o, event: %o',draggingNode, dropNode, dropType,event);
        const tempData = Object.assign({}, draggingNode.data)//copy obj
        if(dropType=='before'||dropType=='after'){
          //before、after: dropNode 跟 draggingNode是同一级
          tempData.parent = dropNode.data.parent
          if(Array.isArray(dropNode.parent.data)){
            //顶级菜单
            tempData.parent = null
            tempData.ptype = 1
          }
        }else{
          //inner: dropNode 是 draggingNode的父级
          tempData.parent = dropNode.data.pid
        }
        updatePerm(tempData).then(res => {
          let idx = this.menuButtonList.findIndex(perm=>perm.pid==tempData.pid)
          this.menuButtonList.splice(idx, 1, tempData)
          this.dialogFormVisible = false
          this.$message.success("更新权限位置成功")
        })
      },



      ////////////////////////////////后台接口权限树的相关方法：


      //新增接口权限
      handleAddTopApi() {
        resetTemp(this.temp)
        this.temp.ptype = 3;//接口
        this.dialogStatus = 'addTopApi'
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      addTopApi() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)//copy obj
            addPerm(tempData).then((res) => {
              tempData.pid = res.data.pid;//后台传回来新增记录的id
              tempData.created = res.data.created;//后台传回来新增记录的时间
              this.apiList.unshift(tempData)
              this.dialogFormVisible = false
              this.$message.success("添加接口权限成功")
            })
          }
        })
      },

      //添加接口权限
      handleAddApi(data){
        resetTemp(this.temp)
        this.temp.ptype = 3;//接口
        this.temp.parent = data.pid
        this.dialogStatus = 'addApi'
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate() )
      },
      addApi() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            let tempPerm = Object.assign({}, this.temp) // copy obj
            addPerm(tempPerm).then((res) => {
              tempPerm.pid = res.data.pid;//后台传回来新增记录的id
              tempPerm.created = res.data.created;//后台传回来新增记录的时间
              this.apiList.unshift(tempPerm)
              this.dialogFormVisible = false
              this.$message.success("添加成功")
            })
          }
        })
      },

      //更新接口权限
      handleUpdateApi(data) {
        this.temp = Object.assign({}, data) // copy obj
        this.dialogStatus = 'updateApi'
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      updateApi() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)//copy obj
            updatePerm(tempData).then(res => {
              tempData.updated = res.data.updated
              let idx = this.apiList.findIndex(perm=>perm.pid==tempData.pid)
              this.apiList.splice(idx, 1, tempData)
              this.dialogFormVisible = false
              this.$message.success("更新接口权限成功")
            })
          }
        })
      },

      //删除菜单或按钮
      handleDeleleApi(data) {
        this.$confirm('您确定要永久删除该权限？', '提示',deleteConfirm).then(() => {
          deletePerm( {pid : data.pid} ).then(res => {
            let idx = this.apiList.findIndex(perm=>perm.pid==data.pid)
            this.apiList.splice(idx, 1)
            this.dialogFormVisible = false
            this.$message.success("删除成功")
          })
        }).catch(() => {
          this.$message({ type: 'info', message: '已取消删除' });
        });
      },

      /**
       * 共四个参数，依次为：被拖拽节点对应的 Node、结束拖拽时最后进入的节点、被拖拽节点的放置位置（before、after、inner）、event
       */
      handleApiDrop(draggingNode, dropNode, dropType, event) {
        const tempData = Object.assign({}, draggingNode.data)//copy obj
        if(dropType=='before'||dropType=='after'){
          //before、after: dropNode 跟 draggingNode是同一级
          tempData.parent = dropNode.data.parent
          if(Array.isArray(dropNode.parent.data)){
            //顶级菜单
            tempData.parent = null
            tempData.ptype = 1
          }
        }else{
          //inner: dropNode 是 draggingNode的父级
          tempData.parent = dropNode.data.pid
        }
        updatePerm(tempData).then(res => {
          let idx = this.apiList.findIndex(perm=>perm.pid==tempData.pid)
          this.apiList.splice(idx, 1, tempData)
          this.dialogFormVisible = false
          this.$message.success("更新接口权限位置成功")
        })
      }





    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 100%;
  }
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
  }
  .card-title{
    line-height:50px;
    height:50px;
  }
</style>
