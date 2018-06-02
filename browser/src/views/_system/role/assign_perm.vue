<template>
  <div class="app-container">

    <el-row>
      <!--角色信息-->
      <span class="page-title">编辑角色的权限：<el-tag type="info">{{role.rname}}</el-tag></span>
      <router-link to="/system/role_manage" style="float:right">
        <el-button type="text" icon="el-icon-back" >返回角色管理页面</el-button>
      </router-link>
    </el-row>

    <div style="margin-bottom: 30px;"></div>

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
              </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script>

  import tree from '../tree'
  import {parseTime, resetTemp} from '@/utils'
  import permApi from '@/api/perm'
  import roleApi from '@/api/role'
  import { permType, permTypeMap} from '@/utils/constants'
  import {asyncRouterMap} from '@/router' //路由表，定义了菜单和按钮的元数据，可以用来生成权限控制的菜单按钮树
  import debounce from 'lodash/debounce'

  export default {
    name: 'AssignPerm',
    data() {
      return {
        permType,

        //当前授权的角色
        roleId: null,
        role: {},

        //节点过滤
        filterPlaceholderText: '输入权限名称、权限值过滤',
        filterMenuPermText: '',
        filterButtonPermText: '',
        filterApiPermText: '',

        roleMenuPermUpdateSum: 0,
        roleApiPermUpdateSum: 0,

        //角色的权限值
        roleMenuPvals: [],
        roleApiPvals: [],
        roleBtnPvals:[],

        menuPermissionTree: [],//菜单权限树
        buttonPermissionTree: [],//按钮权限树
        apiPermissionTree: [],//菜单权限树
        //挂载到按钮权限树的按钮权限数据。由于按钮权限在菜单权限下，key是菜单权限值，value是按钮权限
        btnPermMap:{},

        treeProps: {
          label: 'pname',
          children: 'children'
        },
      }
    },

    computed:{
      btnCheckboxMap(){
        let map = {}
        this.roleBtnPvals.forEach(pval=>{
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

      //获取后台权限数据
      initData() {
        //获取路由中的角色id
        this.roleId = this.$route.params.roleId
        //显示菜单权限树
        this.menuPermissionTree = tree.generateMenuPermissionTree()
        //显示按钮权限树
        let menuPermissionTreeCopy = tree.generateMenuPermissionTree()
        this.generateButtonPermissionTree(menuPermissionTreeCopy)
        //显示接口权限树
        this.loadApiButtonPermissionTree()
        //加载角色的权限
        this.loadRolePerms()
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
          this.roleBtnPvals = res.data.btnPvals
          //显示当前编辑的角色
          this.role = res.data.role
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
        permApi.listBtnPermGroupByParent().then(res=>{
          this.btnPermMap = res.data.btnPermMap || {}
          this.buttonPermissionTree = tree.mapToButtonPermissionTree(this.btnPermMap,menuPermissionTreeCopy)
        })

      },





      /**
       * 更新角色的按钮权限
       * @param checked
       */
      handleUpdateBtnPermForRole(checked,pval){
        let data = {
          rid: this.roleId,
          pval: pval,
          ptype: permType.BUTTON
        }
        if(checked){
          roleApi.addRolePerm(data).then(()=>{
            this.roleBtnPvals.unshift(pval)
            this.$message.success('添加按钮权限成功')
          })
        }else{
          roleApi.deleteRolePerm(data).then(()=>{
            let index = this.roleBtnPvals.findIndex(btnPval=>btnPval==pval)
            this.roleBtnPvals.splice(index,1)
            this.$message.success('删除按钮权限成功')
          })
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
        //发送请求更新角色的权限
        let data = {
          rid: this.roleId,
          ptype: permType.MENU,
          pvals: pvals
        }
        roleApi.updateRolePerms(data).then(res=>{
          this.$message.success('更新菜单权限成功')
        })
      },500),

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
          this.$message.success('更新接口权限成功')
        })
      },500),

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

  .page-title{
    font-size: 24px;
    font-weight: bold;
    color: #303133;
  }


</style>
