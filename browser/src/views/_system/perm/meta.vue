<template>
  <div class="app-container">


    <el-row :gutter="20">

      <!--菜单权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header">
            <div class="title-box">
              <span>菜单权限元数据</span>
              <el-tooltip content="同步菜单权限数据" placement="top">
                <el-button style="font-size: 25px;" type="text" @click="handleSyncMenuPermissionData" icon="el-icon-refresh" circle></el-button>
              </el-tooltip>
            </div>
            <span class="tips-text">提示：菜单权限由页面路由定义，不提供任何编辑功能，只能只能将权限数据同步到数据库的操作。</span>
          </div>
          <el-tree draggable :data="menuPermissionTree" :props="treeProps" node-key="pval" default-expand-all>
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span>
                <span class="mgl-10">{{ data.pname }}</span>
                <span class="mgl-10 tips-text">{{ data.pval }}</span>
                <el-tag class="mgl-10" type="success" size="mini">菜单</el-tag>
                <el-tag v-if="!menuPermValSet.has(data.pval)" class="mgl-10" type="danger" size="mini">未同步</el-tag>
              </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>


      <!--按钮权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header">
            <div class="title-box" style="padding-top: 10px; padding-bottom: 16px;">
              <span>按钮权限元数据</span>
            </div>
            <span class="tips-text">提示：按钮权限是依附在菜单权限下的，这样能帮助您更好区分相似的按钮权限。</span>
          </div>
          <el-input class="mgb-15" placeholder="输入关键字进行过滤" v-model="filterMenuButtonText">
          </el-input>
          <el-tree draggable ref="menuButtonTreeRef" :filter-node-method="filterNode" @node-drop="handleMenuButtonDrop"
                   :data="buttonPermissionTree" :props="treeProps" node-key="pid" default-expand-all :expand-on-click-node="false">
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>
                  <span class="mgl-10">{{ data.pname }}</span>
                  <span class="mgl-10 tips-text">{{ data.pval }}</span>
                  <el-tag class="mgl-10" v-if="data.ptype==1" type="success" size="mini">菜单</el-tag>
                  <el-tag class="mgl-10" v-else-if="data.ptype==2" type="warning" size="mini">按钮</el-tag>
                </span>
                <el-tooltip v-if="data.ptype==permType.MENU" style="margin-right: 80px;" content="添加按钮权限" placement="top">
                  <el-button type="text" size="mini" icon="el-icon-plus" @click="handleAddNode(permType.BUTTON,data)"></el-button>
                </el-tooltip>
                <span v-if="data.ptype==permType.BUTTON">
                  <el-tooltip content="更新" placement="top">
                    <el-button class="update-btn" type="text" size="mini" icon="el-icon-edit" @click="handleUpdateNode(permType.BUTTON,data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <el-button class="delete-btn" type="text" size="mini" icon="el-icon-delete" @click="handleDeleleNode(permType.BUTTON,data)"></el-button>
                  </el-tooltip>
                </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>

      <!--右侧后台接口权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header">
            <div class="title-box">
              <span>后台接口权限元数据</span>
              <el-tooltip content="添加接口模块" placement="top">
                <el-button style="font-size: 25px;" type="text" @click="handleAddTopNode(permType.API)"
                           icon="el-icon-circle-plus-outline" circle></el-button>
              </el-tooltip>
            </div>
            <span class="tips-text">提示：可以通过拖拽来调整接口的位置</span>
          </div>
          <el-input class="mgb-15" placeholder="输入关键字进行过滤" v-model="filterApiText">
          </el-input>
          <el-tree draggable ref="apiTreeRef" :filter-node-method="filterNode" @node-drop="handleApiDrop"
                   :data="apiTree" :props="treeProps" node-key="pid" default-expand-all>
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>
                  <span class="mgl-10">{{ node.label }}</span>
                  <i class="el-icon-rank mgl-10"></i>
                </span>
                <span>
                  <el-tooltip content="添加接口" placement="top">
                    <el-button type="text" size="mini" icon="el-icon-plus"
                               @click="handleAddNode(permType.API,data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="更新" placement="top">
                    <el-button class="update-btn" type="text" size="mini" icon="el-icon-edit"
                               @click="handleUpdateNode(permType.API,data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <el-button class="delete-btn" type="text" size="mini" icon="el-icon-delete"
                               @click="handleDeleleNode(permType.API,data)"></el-button>
                  </el-tooltip>
                </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>
    </el-row>

    <!--弹窗：新增或编辑权限-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="top">
        <el-form-item label="权限名" prop="pname">
          <el-input v-model="temp.pname" placeholder="例如：用户管理、添加用户"></el-input>
        </el-form-item>
        <el-form-item label="权限值" prop="pval">
          <el-input v-model="temp.pval" placeholder="例如：user:manage、user:add" :disabled="dialogStatus=='updateButton'||dialogStatus=='updateApi'"></el-input>
        </el-form-item>
        <el-form-item label="权限类型" prop="ptype">
          <el-select v-model="temp.ptype" v-if="dialogStatus=='updateButton'||dialogStatus=='addButton'" :disabled="true">
            <el-option label="按钮" :value="2"></el-option>
          </el-select>
          <el-select v-model="temp.ptype" v-if="dialogStatus=='addTopApi'||dialogStatus=='addApi'||dialogStatus=='updateApi'" :disabled="true">
            <el-option label="接口" :value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='addTopApi'" type="primary" @click="addTopNode(permType.API)">确定</el-button>
        <el-button v-if="dialogStatus=='addButton'" type="primary" @click="addNode(permType.BUTTON)">确定</el-button>
        <el-button v-if="dialogStatus=='updateButton'" type="primary" @click="updateNode(permType.BUTTON)">确定</el-button>
        <el-button v-if="dialogStatus=='addApi'" type="primary" @click="addNode(permType.API)">确定</el-button>
        <el-button v-if="dialogStatus=='updateApi'" type="primary" @click="updateNode(permType.API)">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

  import {parseTime, resetTemp} from '@/utils'
  import permApi from '@/api/perm'
  import {
    apiPermissionOptions,
    deleteConfirm,
    menuButtonPermissionOptions,
    permType,
    permTypeMap
  } from '@/utils/constants'
  import {asyncRouterMap} from '@/router' //路由表，定义了菜单和按钮的元数据，可以用来生成权限控制的菜单按钮树
  import debounce from 'lodash/debounce'

  export default {
    name: 'PermMetaDataManage',
    data() {
      return {
        menuPermList: [],
        menuPermValSet: new Set(),
        buttonPermList: [],
        apiPermList: [],
        btnPermMap:{},//按parent字段分组的map

        menuPermissionTree: [],//菜单权限树

        buttonPermissionTree: [],//菜单权限树


        permType,
        filterMenuButtonText: '',
        filterApiText: '',
        menuButtonPermissionOptions,
        apiPermissionOptions,
        mockPid: 1000,
        menuButtonList: [],
        apiList: [],
        treeProps: {
          label: 'pname',
          children: 'children'
        },
        dialogFormVisible: false,
        dialogStatus: '',
        textMap: {
          addButton: '添加按钮权限',
          updateButton: '更新按钮权限',
          deleteButton: '删除按钮权限',
          addTopApi: '添加接口模块',
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
          pname: [{required: true, message: '必填', trigger: 'blur'}],
          ptype: [{required: true, message: '必填', trigger: 'blur'}],
          pval: [{required: true, message: '必填', trigger: 'change'}]
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

      //生成后台接口权限树
      apiTree() {
        return this.findChildren(this.apiList, null)
      },

    },

    created() {
      this.initData()
    },

    methods: {

      //获取后台权限数据
      initData() {
        permApi.listAllPermissions().then(res => {
          this.btnPermMap = res.data.btnPermMap
          this.menuPermList = res.data.permMap[permType.MENU]
          this.buttonPermList = res.data.permMap[permType.BUTTON]
          this.apiPermList = res.data.permMap[permType.API]
          this.menuPermValSet = new Set(this.menuPermList.map(p=>p.pval))
          //显示菜单权限树
          this.menuPermissionTree = this.generateMenuPermissionTree()
          //显示按钮权限树
          let menuPermissionTreeCopy = this.generateMenuPermissionTree()
          this.generateButtonPermissionTree(menuPermissionTreeCopy)
          //显示接口权限树
          this.getApiButtonPermissionTree()
        })
      },

      //过滤节点
      filterNode(value, data) {
        if (!value) return true;
        return data.pname.indexOf(value) !== -1;
      },


      //用于生成权限树的方法
      findChildren(list, pid) {
        let submenus = list.filter(item => item.parent == pid);
        submenus.forEach(perm => {
          perm.children = this.findChildren(list, perm.pid)
        })
        return submenus;
      },

      //用于生成权限树的方法
      findChildren2(list, pval) {
        let submenus = list.filter(item => item.parent == pval);
        submenus.forEach(perm => {
          perm.children = this.findChildren2(list, perm.pval)
        })
        return submenus;
      },

      ////////////////////////////////菜单按钮权限树的相关方法：

      changeDialogStatus(prefix,ptype){
        switch (ptype){
          case permType.BUTTON:
            this.dialogStatus = prefix+'Button';
            break;
          case permType.API:
            this.dialogStatus = prefix+'Api';
            break;
          default:
            this.dialogStatus = prefix+'Button';
        }
      },

      //新增顶级菜单、接口权限
      handleAddTopNode(ptype) {
        resetTemp(this.temp)
        this.temp.ptype = ptype
        if (ptype == permType.MENU) {
          this.dialogStatus = 'addTopMenu'
        } else {
          this.dialogStatus = 'addTopApi'
        }
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      addTopNode(ptype) {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)//copy obj
            permApi.addPerm(tempData).then((res) => {
              tempData.pid = res.data.pid;//后台传回来新增记录的id
              tempData.created = res.data.created;//后台传回来新增记录的时间
              this.dialogFormVisible = false
              if (ptype == permType.MENU) {
                this.menuButtonList.unshift(tempData)
                this.$message.success("添加顶级菜单成功")
              } else {
                this.apiList.unshift(tempData)
                this.$message.success("添加接口权限成功")
              }
            })
          }
        })
      },

      //添加按钮权限、接口权限
      handleAddNode(ptype, data) {
        this.changeDialogStatus('add',ptype)
        resetTemp(this.temp)
        this.temp.ptype = ptype;
        this.temp.parent = data.pval
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      addNode(ptype) {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
          const data = Object.assign({}, this.temp)//copy obj
          permApi.addPerm(data).then(() => {
            this.dialogFormVisible = false
            this.initData()
            switch(ptype){
              case permType.BUTTON:
                this.$message.success("添加按钮权限成功");
                break;
              case permType.API:
                this.$message.success("添加接口权限成功");
                break;
              default:
            }
          })
        })
      },

      //更新按钮权限、接口权限
      handleUpdateNode(ptype, data) {
        this.changeDialogStatus('update',ptype)
        this.temp = Object.assign({}, data) // copy obj
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      updateNode(ptype) {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
          const data = Object.assign({}, this.temp)//copy obj
          permApi.updatePerm(data).then(res => {
            this.dialogFormVisible = false
            this.initData()
            switch(ptype){
              case permType.BUTTON:
                this.$message.success("添加按钮权限成功");
                break;
              case permType.API:
                this.$message.success("添加接口权限成功");
                break;
              default:
            }
          })
        })
      },

      //删除按钮权限或接口权限
      handleDeleleNode(ptype, data) {
        this.$confirm('您确定要永久删除该权限？', '提示', deleteConfirm).then(() => {
          permApi.deletePerm({pval: data.pval}).then(() => {
            this.initData()
            switch(ptype){
              case permType.BUTTON:
                this.$message.success("删除按钮权限成功");
                break;
              case permType.API:
                this.$message.success("删除接口权限成功");
                break;
              default:
            }
          })
        }).catch(() => {
          this.$message({type: 'info', message: '已取消删除'});
        });
      },

      //拖拽调整节点位置
      handleNodeDrop(ptype, draggingNode, dropNode, dropType, event) {

        const tempData = Object.assign({}, draggingNode.data)//copy obj
        if (dropType == 'before' || dropType == 'after') {
          //before、after: dropNode 跟 draggingNode是同一级
          tempData.parent = dropNode.data.parent
          if (Array.isArray(dropNode.parent.data)) {
            tempData.parent = null
            if (ptype == permType.MENU) {
              //顶级菜单
              tempData.ptype = permType.MENU
            }
          }
        } else {
          //inner: dropNode 是 draggingNode的父级
          tempData.parent = dropNode.data.pid
        }
        //父节点没有变，不需要调用接口更新
        if (tempData.parent == draggingNode.data.parent) {
          return;
        }
        permApi.updatePerm(tempData).then(res => {
          if (ptype == permType.MENU) {
            let idx = this.menuButtonList.findIndex(perm => perm.pid == tempData.pid)
            this.menuButtonList.splice(idx, 1, tempData)
            this.$message.success("更新权限位置成功")
          } else {
            let idx = this.apiList.findIndex(perm => perm.pid == tempData.pid)
            this.apiList.splice(idx, 1, tempData)
            this.$message.success("更新接口权限位置成功")
          }
          this.dialogFormVisible = false
        })

      },


      /**
       * 共四个参数，依次为：被拖拽节点对应的 Node、结束拖拽时最后进入的节点、被拖拽节点的放置位置（before、after、inner）、event
       */
      handleMenuButtonDrop(draggingNode, dropNode, dropType, event) {
        this.handleNodeDrop(permType.MENU, draggingNode, dropNode, dropType, event)
      },
      handleApiDrop(draggingNode, dropNode, dropType, event) {
        this.handleNodeDrop(permType.API, draggingNode, dropNode, dropType, event)
      },

      /**
       * 从服务器端加载接口权限树
       */
      getApiButtonPermissionTree(){
        permApi.listApiPermMetadata()
      },

      /**
       * 根据菜单树，生成按钮权限树
       */
      generateButtonPermissionTree(menuPermissionTreeCopy) {
        this.buttonPermissionTree = this.mapToButtonPermissionTree(menuPermissionTreeCopy)
      },

      /**
       * 根据菜单树，生成按钮权限树
       * @param menuPermissionTree 菜单树
       */
      mapToButtonPermissionTree(menuPermissionTree) {
        return menuPermissionTree.map(perm => {

          if(perm){
            if(!perm.children){
              perm.children = []
            }
            if(perm.ptype==permType.MENU){
              let btnPerms = this.btnPermMap[perm.pval]
              if(btnPerms){
                btnPerms.forEach(p=>{
                  perm.children.push(p)
                })
              }
            }
            if(perm.children&&perm.children.length>0){
              this.mapToButtonPermissionTree(perm.children)
            }
          }
          return perm;

        })
      },


      /**
       * 根据前端定义的路由表，生成菜单权限列表
       */
      generateMenuPermissionTree() {
        //预处理
        let routeArr = asyncRouterMap.map(route => {
          let temp = Object.assign({}, route) // copy obj
          if (!temp.alwaysShow && temp.children && temp.children.length == 1) {
            //如果是只有一个子菜单的顶级菜单，把子级的菜单meta复制到父级用于生成菜单树时显示菜单名称
            temp.meta = temp.children[0].meta
            temp.children = []
          }
          return temp
        })
        //过滤路由表，得到需要进行权限控制的菜单树
        let permissionControlRoutes = this.filterPermControlRouter(routeArr)
        //递归形成菜单树
        return this.mapToMenuPermissionTree(permissionControlRoutes, null)
      },

      /**
       * 根据输入的路由表过滤出一个需要进行权限控制的路由表
       * @param routeArr
       */
      filterPermControlRouter(routeArr) {
        const routes = routeArr.filter(route => {
          if (route.meta && route.meta.perm) {
            if (route.children && route.children.length) {
              route.children = this.filterPermControlRouter(route.children)
            }
            return true
          }
          return false
        })
        return routes
      },


      /**
       * 根据输入的路由表，生成菜单权限树
       * @param routeArr 路由表
       * @param parentPermVal 初始的父级权限值
       */
      mapToMenuPermissionTree(routeArr, parentPermVal) {
        return routeArr.map(route => {
          let obj = {};
          if (route.meta && route.meta.perm) {
            obj.pval = route.meta.perm
            obj.pname = route.meta.title
            obj.ptype = permType.MENU;
            obj.parent = parentPermVal;
          }
          if (route.children) {
            obj.children = this.mapToMenuPermissionTree(route.children, obj.pval)
          }
          return obj;
        })
      },

      /**
       * 同步菜单权限数据
       */
      handleSyncMenuPermissionData() {
        let list = []
        this.menuPermissionTreeToList(list, this.menuPermissionTree)
        let notSyncedData = list.filter(perm=>{
          return !this.menuPermValSet.has(perm.pval)
        })
        permApi.batchSavePerms(notSyncedData).then(res=>{
          this.initData()
          this.$message.success('菜单权限数据同步成功')
        })

      },


      /**
       * 菜单权限树转换成列表形式
       */
      menuPermissionTreeToList(list, tree) {
        tree.forEach(perm => {
          let temp = Object.assign({}, perm)
          temp.children = []
          list.push(temp)
          if (perm.children && perm.children.length > 0) {
            this.menuPermissionTreeToList(list, perm.children)
          }
        })
      },

      /*
      menuPermissionTreeToList(list,routeArr,parentPval){
        routeArr.forEach(route=>{
          if (route.meta && route.meta.perm) {
            if (route.children && route.children.length>0) {
              route.children = this.menuPermissionTreeToList(list,route.children,route.meta.perm)
            }
            list.push({
              pname: route.meta.title,
              pval: route.meta.perm,
              ptype: permType.MENU,
              parent: parentPval
            })
          }
        })
      },
      */


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

  .card-title {
    line-height: 50px;
    height: 50px;
  }

  .tips-text {
    font-size: 14px;
    color: #909399;
  }

  .title-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
    span {
      font-size: 22px;
    }
  }

  .update-btn {
    margin-left: 20px;
  }

  .delete-btn {
    margin-left: 20px;
    color: #F56C6C;
  }

  .mgl-10 {
    margin-left: 10px;
  }

  .mgb-15 {
    margin-bottom: 15px;
  }


</style>
