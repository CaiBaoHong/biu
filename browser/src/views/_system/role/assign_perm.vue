<template>
  <div class="app-container">

    <el-row :gutter="20">

      <!--菜单权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header">
            <div class="title-box" style="padding-top: 10px; padding-bottom: 13px;">
              <span><el-tag type="success" >菜单</el-tag>&nbsp;权限元数据</span>
            </div>
            <span class="tips-text">提示：勾选权限即可为角色授权</span>
          </div>
          <el-input class="mgb-15" :placeholder="filterPlaceholderText" v-model="filterMenuPermText"></el-input>
          <el-tree @check-change="handleUpdateMenuPermForRole" show-checkbox ref="menuPermTreeRef" :filter-node-method="filterNode"
                   :data="menuPermissionTree" :props="treeProps" node-key="pval" default-expand-all :expand-on-click-node="false">
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
            <div class="title-box" style="padding-top: 10px; padding-bottom: 13px;">
              <span><el-tag type="warning" >按钮</el-tag>&nbsp;权限元数据</span>
            </div>
            <span class="tips-text">提示：勾选权限即可为角色授权</span>
          </div>
          <el-input class="mgb-15" :placeholder="filterPlaceholderText" v-model="filterButtonPermText"></el-input>
          <el-tree ref="buttonPermTreeRef" :filter-node-method="filterNode" :data="buttonPermissionTree"
                   :props="treeProps" node-key="pid" default-expand-all :expand-on-click-node="false">
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>
                  <el-checkbox v-if="data.ptype==permType.BUTTON" v-model="btnCheckboxMap[data.pval]"
                               @change="(checked)=>{handleUpdateBtnPermForRole(checked,data.pval)}"></el-checkbox>
                  <span class="mgl-10">{{ data.pname }}</span>
                  <span class="mgl-10 tips-text">{{ data.pval }}</span>
                  <el-tag class="mgl-10" v-if="data.ptype==permType.MENU" type="success" size="mini">菜单</el-tag>
                  <el-tag class="mgl-10" v-else-if="data.ptype==permType.BUTTON" type="warning" size="mini">按钮</el-tag>
                </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>

      <!--接口权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header">
            <div class="title-box" style="padding-top: 10px; padding-bottom: 13px;">
              <span><el-tag>接口</el-tag>&nbsp;权限元数据</span>
            </div>
            <span class="tips-text">提示：勾选权限即可为角色授权</span>
          </div>
          <el-input class="mgb-15" :placeholder="filterPlaceholderText" v-model="filterApiPermText"></el-input>
          <el-tree @check-change="handleUpdateApiPermForRole" show-checkbox ref="apiPermTreeRef" :filter-node-method="filterNode"
                   :data="apiPermissionTree" :props="treeProps" node-key="pval" default-expand-all :expand-on-click-node="false">
            <span class="custom-tree-node" slot-scope="{node,data}">
              <span>
                <span class="mgl-10">{{ data.pname }}</span>
                <span class="mgl-10 tips-text">{{ data.pval }}</span>
                <el-tag class="mgl-10" size="mini">接口</el-tag>
                <el-tag v-if="!apiPermValSet.has(data.pval)" class="mgl-10" type="danger" size="mini">未同步</el-tag>
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
          <el-input v-model="temp.pval" placeholder="例如：b:user:manage、b:user:add" :disabled="dialogStatus=='updateButton'"></el-input>
        </el-form-item>
        <el-form-item label="父级权限值" prop="parent">
          <el-input v-model="temp.parent" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="权限类型" prop="ptype">
          <el-select v-model="temp.ptype" v-if="dialogStatus=='updateButton'||dialogStatus=='addButton'" :disabled="true">
            <el-option label="按钮" :value="permType.BUTTON"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='addButton'" type="primary" @click="addButton">确定</el-button>
        <el-button v-if="dialogStatus=='updateButton'" type="primary" @click="updateButton">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

  import {parseTime, resetTemp} from '@/utils'
  import permApi from '@/api/perm'
  import roleApi from '@/api/role'
  import {
    apiPermissionOptions,
    deleteConfirm,
    menuButtonPermissionOptions,
    permType,
    permTypeMap
  } from '@/utils/constants'
  import {asyncRouterMap} from '@/router' //路由表，定义了菜单和按钮的元数据，可以用来生成权限控制的菜单按钮树
  import debounce from 'lodash/debounce'
  import throttle from 'lodash/throttle'

  export default {
    name: 'PermManage',
    data() {
      return {

        filterPlaceholderText: '输入权限名称、权限值过滤',

        roleId: null,
        roleMenuPermUpdateSum: 0,
        roleApiPermUpdateSum: 0,

        menuPermList: [],
        apiPermList: [],

        roleMenuPvals: [],
        roleApiPvals: [],

        menuPermValSet: new Set(),
        apiPermValSet: new Set(),

        btnPvals:[],

        btnPermMap:{},//按parent字段分组的map

        menuPermissionTree: [],//菜单权限树
        buttonPermissionTree: [],//菜单权限树
        apiPermissionTree: [],//菜单权限树

        filterMenuPermText: '',
        filterButtonPermText: '',
        filterApiPermText: '',

        permType,
        menuButtonPermissionOptions,
        apiPermissionOptions,

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

    computed:{
      btnCheckboxMap(){
        let map = {}
        this.btnPvals.forEach(pval=>{
          map[pval] = true
        })
        return map;
      },

    },

    watch: {
      'filterMenuPermText': debounce(function (val) {
        this.$refs.menuPermTreeRef.filter(val);
      }, 600),
      'filterButtonPermText': debounce(function (val) {
        this.$refs.buttonPermTreeRef.filter(val);
      }, 600),
      'filterApiPermText': debounce(function (val) {
        this.$refs.apiPermTreeRef.filter(val);
      }, 600),
    },

    created() {
      this.initData()
    },

    methods: {

      debounce,
      throttle,


      //获取后台权限数据
      initData() {
        this.roleId = this.$route.params.roleId
        permApi.listAllPermissions().then(res => {
          this.btnPermMap = res.data.btnPermMap || {}
          let permMap = res.data.permMap || {}
          this.menuPermList = permMap[permType.MENU] || []
          //let buttonPermList = permMap[permType.BUTTON] || []
          this.apiPermList = permMap[permType.API] || []
          this.menuPermValSet = new Set(this.menuPermList.map(p=>p.pval))
          this.apiPermValSet = new Set(this.apiPermList.map(p=>p.pval))
          //显示菜单权限树
          this.menuPermissionTree = this.generateMenuPermissionTree()
          //显示按钮权限树
          let menuPermissionTreeCopy = this.generateMenuPermissionTree()
          this.buttonPermissionTree = this.generateButtonPermissionTree(menuPermissionTreeCopy)
          //显示接口权限树
          this.loadApiButtonPermissionTree()
          //加载角色的权限
          this.loadRolePerms()
        })
      },

      /**
       * 加载角色的权限并回显
       */
      loadRolePerms(){
        if(!this.roleId){
          this.$message.error('无法显示角色的权限信息：找不到角色id')
          return;
        }
        roleApi.findRolePerms(this.roleId).then(res=>{
          if(res.data.menuPvals.length>0){
            this.roleMenuPvals = res.data.menuPvals
            this.$refs.menuPermTreeRef.setCheckedKeys(res.data.menuPvals)
          }
          if(res.data.apiPvals.length>0){
            this.roleApiPvals = res.data.menuPvals
            this.$refs.apiPermTreeRef.setCheckedKeys(res.data.apiPvals)

          }
          //用于回显角色的按钮权限
          this.btnPvals = res.data.btnPvals

        })
      },

      /**
       * 过滤节点
       */
      filterNode(value, data) {
        if (!value) return true;
        return data.pname.indexOf(value) !== -1 || data.pval.indexOf(value) !== -1 ;
      },

      /////////////////////////// 接口权限树

      /**
       * 从服务器端加载接口权限树
       */
      loadApiButtonPermissionTree(){
        permApi.listApiPermMetadata().then(res=>{
          this.apiPermissionTree = res.data.apiList
        })
      },

      /////////////////////////// 按钮权限树

      /**
       * 递归生成按钮权限树
       */
      generateButtonPermissionTree(menuPermissionTreeCopy) {
        return this.mapToButtonPermissionTree(menuPermissionTreeCopy)
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

      /////////////////////////// 菜单权限树

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
      handleUpdateRoleMenuPermission() {
        let list = []
        this.permissionTreeToList(list, this.menuPermissionTree)
        let notSyncedData = list.filter(perm=>{
          return !this.menuPermValSet.has(perm.pval)
        })
        permApi.syncMenuPerms(notSyncedData).then(res=>{
          this.initData()
          this.$message.success('菜单权限数据同步成功')
        })
      },

      /**
       * 同步接口权限数据
       */
      handleSyncApiPermissionData() {

        let list = []
        this.permissionTreeToList(list, this.apiPermissionTree)
        let notSyncedData = list.filter(perm=>{
          return !this.apiPermValSet.has(perm.pval)
        })
        permApi.syncApiPerms(notSyncedData).then(res=>{
          this.initData()
          this.$message.success('接口权限数据同步成功')
        })

      },

      /**
       * 菜单权限树转换成列表形式
       */
      permissionTreeToList(list, tree) {
        tree.forEach(perm => {
          let temp = Object.assign({}, perm)
          temp.children = []
          if (perm.children && perm.children.length > 0) {
            temp.leaf = false
            console.log("leaf: false")
            this.permissionTreeToList(list, perm.children)
          }else{
            temp.leaf = true
            console.log("leaf: true")
          }
          list.push(temp)
        })
      },

      ///////////////////

      /**
       * 更新角色的按钮权限
       * @param checked
       */
      handleUpdateBtnPermForRole(checked,pval){
        console.log("roleId: "+this.roleId+", checked: "+checked+", pval: "+pval)
        let data = {
          rid: this.roleId,
          pval: pval,
          ptype: permType.BUTTON
        }
        if(checked){
          roleApi.addRolePerm(data).then(()=>this.$message.success('添加角色的权限成功'))
        }else{
          roleApi.deleteRolePerm(data).then(()=>this.$message.success('删除角色的权限成功'))
        }
      },

      /**
       * 更新角色的菜单权限
       */
      handleUpdateMenuPermForRole: debounce(function(){
        this.roleMenuPermUpdateSum++;
        //因为初始化勾选角色的权限会触发一次，但这次的权限数据跟后台是一样的，所以不需要触发更新角色的权限
        if(this.roleMenuPvals.length>0 && this.roleMenuPermUpdateSum==1) return;
        let checkedNodes = this.$refs.menuPermTreeRef.getCheckedNodes();
        let halfCheckedNodes = this.$refs.menuPermTreeRef.getHalfCheckedNodes();
        let pvals = [...checkedNodes,...halfCheckedNodes].map(perm=>perm.pval)
        console.log("pvals to be update: "+pvals)
        //发送请求更新角色的权限
        let data = {
          rid: this.roleId,
          ptype: permType.MENU,
          pvals: pvals
        }
        roleApi.updateRolePerms(data).then(res=>{
          this.$message.success('更新角色的菜单权限成功')
        })
      },1000),

      /**
       * 更新角色的接口权限
       */
      handleUpdateApiPermForRole: debounce(function(){
        this.roleApiPermUpdateSum++;
        //因为初始化勾选角色的权限会触发一次，但这次的权限数据跟后台是一样的，所以不需要触发更新角色的权限
        if(this.roleApiPvals.length>0 && this.roleApiPermUpdateSum==1) return;
        let checkedNodes = this.$refs.apiPermTreeRef.getCheckedNodes();
        let halfCheckedNodes = this.$refs.apiPermTreeRef.getHalfCheckedNodes();
        let pvals = [...checkedNodes,...halfCheckedNodes].map(perm=>perm.pval)
        //发送请求更新角色的权限
        let data = {
          rid: this.roleId,
          ptype: permType.API,
          pvals: pvals
        }
        roleApi.updateRolePerms(data).then(res=>{
          this.$message.success('更新角色的接口权限成功')
        })
      },1000),



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
