<template>
  <div class="app-container">

    <el-row :gutter="20">

      <!--菜单权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header">
            <div class="title-box">
              <span><el-tag type="success" >菜单</el-tag>&nbsp;权限元数据</span>
              <el-tooltip content="同步菜单权限数据" placement="top">
                <el-button style="font-size: 25px;" type="text" @click="handleSyncMenuPermissionData" icon="el-icon-refresh" circle></el-button>
              </el-tooltip>
            </div>
            <span class="tips-text">提示：菜单权限由页面路由定义，不提供任何编辑功能，只能执行将权限数据同步到数据库的操作。
              菜单权限值建议使用前缀&nbsp;<el-tag size="mini" type="success">m:</el-tag>
            </span>
          </div>
          <el-input class="mgb-15" :placeholder="filterPlaceholderText" v-model="filterMenuPermText"></el-input>
          <el-tree ref="menuPermTreeRef" :filter-node-method="filterNode" :data="menuPermissionTree"
                   :props="treeProps" node-key="pval" default-expand-all :expand-on-click-node="false">
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
            <span class="tips-text">提示：按钮权限是依附在菜单权限下的，这样能帮助您更好区分相似的按钮权限。
              按钮权限值建议使用前缀&nbsp;<el-tag size="mini" type="warning">b:</el-tag>
            </span>
          </div>
          <el-input class="mgb-15" :placeholder="filterPlaceholderText" v-model="filterButtonPermText"></el-input>
          <el-tree ref="buttonPermTreeRef" :filter-node-method="filterNode" :data="buttonPermissionTree"
                   :props="treeProps" node-key="pval" default-expand-all :expand-on-click-node="false">
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>
                  <span class="mgl-10">{{ data.pname }}</span>
                  <span class="mgl-10 tips-text">{{ data.pval }}</span>
                  <el-tag class="mgl-10" v-if="data.ptype==permType.MENU" type="success" size="mini">菜单</el-tag>
                  <el-tag class="mgl-10" v-else-if="data.ptype==permType.BUTTON" type="warning" size="mini">按钮</el-tag>
                </span>
                <el-tooltip v-if="data.ptype==permType.MENU" style="margin-right: 80px;" content="添加按钮权限" placement="top">
                  <el-button type="text" size="mini" icon="el-icon-plus" @click="handleAddButton(data)"></el-button>
                </el-tooltip>
                <span v-if="data.ptype==permType.BUTTON">
                  <el-tooltip content="更新" placement="top">
                    <el-button class="update-btn" type="text" size="mini" icon="el-icon-edit" @click="handleUpdateButton(data)"></el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <el-button class="delete-btn" type="text" size="mini" icon="el-icon-delete" @click="handleDeleleButton(data)"></el-button>
                  </el-tooltip>
                </span>
            </span>
          </el-tree>
        </el-card>
      </el-col>

      <!--接口权限树-->
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header">
            <div class="title-box">
              <span><el-tag>接口</el-tag>&nbsp;权限元数据</span>
              <el-tooltip content="同步接口权限数据" placement="top">
                <el-button style="font-size: 25px;" type="text" @click="handleSyncApiPermissionData" icon="el-icon-refresh" circle></el-button>
              </el-tooltip>
            </div>
            <span class="tips-text">提示：接口菜单权限由后台定义，不提供任何编辑功能，只能执行将权限数据同步到数据库的操作。
              接口权限值建议使用前缀&nbsp;<el-tag size="mini" >a:</el-tag>
            </span>
          </div>
          <el-input class="mgb-15" :placeholder="filterPlaceholderText" v-model="filterApiPermText"></el-input>
          <el-tree ref="apiPermTreeRef" :filter-node-method="filterNode" :data="apiPermissionTree"
                   :props="treeProps" node-key="pval" default-expand-all :expand-on-click-node="false">
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
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%" >
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="top">
        <el-form-item label="权限名" prop="pname">
          <el-input v-model="temp.pname" placeholder="例如：用户管理、添加用户"></el-input>
        </el-form-item>
        <el-form-item label="权限值" prop="pval">
          <el-input v-model="temp.pval" placeholder="已自动加上前缀'b:'，您只需填剩余部分，如：user:manage、user:add" :disabled="dialogStatus=='updateButton'">
            <template slot="prepend" v-if="dialogStatus=='addButton'">{{btnPermPrefix}}</template>
          </el-input>
          <span class="tips-text" >提示：接口权限值建议使用前缀&nbsp;<el-tag size="mini" type="warning" >b:</el-tag></span>
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

  import tree from '../tree'
  import {parseTime, resetTemp} from '@/utils'
  import permApi from '@/api/perm'
  import {
    confirm,
    permType,
    permTypeMap
  } from '@/utils/constants'
  import {asyncRouterMap} from '@/router' //路由表，定义了菜单和按钮的元数据，可以用来生成权限控制的菜单按钮树
  import debounce from 'lodash/debounce'

  export default {
    name: 'PermManage',
    data() {
      return {

        btnPermPrefix: 'b:',

        filterPlaceholderText: '输入权限名称、权限值过滤',

        menuPermValSet: new Set(),
        apiPermValSet: new Set(),

        btnPermMap:{},//按parent字段分组的map

        menuPermissionTree: [],//菜单权限树
        buttonPermissionTree: [],//菜单权限树
        apiPermissionTree: [],//菜单权限树

        filterMenuPermText: '',
        filterButtonPermText: '',
        filterApiPermText: '',

        permType,



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
        permApi.listAllPermissions().then(res => {
          //按parent分组的按钮权限
          this.btnPermMap = res.data.btnPermMap || {}
          //含有所有权限map，key是ptype
          let permMap = res.data.permMap || {}
          //后台存储的所有菜单权限，用于比较前后台数据是否同步
          let menuPermList = permMap[permType.MENU] || []
          this.menuPermValSet = new Set(menuPermList.map(p=>p.pval))
          //后台存储的所有接口权限，用于比较数据是否同步
          let apiPermList = permMap[permType.API] || []
          this.apiPermValSet = new Set(apiPermList.map(p=>p.pval))
          //显示菜单权限树
          this.menuPermissionTree = tree.generateMenuPermissionTree()
          //显示按钮权限树
          let menuPermissionTreeCopy = tree.generateMenuPermissionTree()
          this.buttonPermissionTree = this.generateButtonPermissionTree(menuPermissionTreeCopy)
          //显示接口权限树
          this.loadApiButtonPermissionTree()
        })
      },

      /**
       * 过滤节点
       */
      filterNode(value, data) {
        if (!value) return true;
        return data.pname.indexOf(value) !== -1 || data.pval.indexOf(value) !== -1 ;
      },

      /**
       * 添加按钮权限
       */
      handleAddButton(data) {
        this.dialogStatus = 'addButton';
        resetTemp(this.temp)
        this.temp.ptype = permType.BUTTON;
        this.temp.parent = data.pval
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      addButton() {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
          const data = Object.assign({}, this.temp)//copy obj
          data.pval = this.btnPermPrefix + data.pval
          permApi.addPerm(data).then(() => {
            this.dialogFormVisible = false
            this.initData()
            this.$message.success("添加按钮权限成功")
          })
        })
      },

      /**
       * 更新按钮权限
       */
      handleUpdateButton(data) {
        this.dialogStatus = 'updateButton';
        this.temp = Object.assign({}, data) // copy obj
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      updateButton() {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
          const data = Object.assign({}, this.temp)//copy obj
          permApi.updatePerm(data).then(res => {
            this.dialogFormVisible = false
            this.initData()
            this.$message.success("更新按钮权限成功")
          })
        })
      },

      /**
       * 删除按钮权限
       */
      handleDeleleButton(data) {
        this.$confirm('您确定要永久删除该权限？', '提示', confirm).then(() => {
          permApi.deletePerm({pval: data.pval}).then(() => {
            this.initData()
            this.$message.success("删除按钮权限成功")
          })
        }).catch(() => {
          this.$message({type: 'info', message: '已取消删除'});
        });
      },

      /**
       * 从服务器端加载接口权限树
       */
      loadApiButtonPermissionTree(){
        permApi.listApiPermMetadata().then(res=>{
          this.apiPermissionTree = res.data.apiList
        })
      },

      /**
       * 递归生成按钮权限树
       */
      generateButtonPermissionTree(menuPermissionTreeCopy) {
        return tree.mapToButtonPermissionTree(this.btnPermMap,menuPermissionTreeCopy)
      },







      ////////////////普通用户修改密码的页面和接口！！！！










      /**
       * 同步菜单权限数据
       */
      handleSyncMenuPermissionData() {
        let list = []
        this.permissionTreeToList(list, this.menuPermissionTree)
        permApi.syncMenuPerms(list).then(res=>{
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
        permApi.syncApiPerms(list).then(res=>{
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
            this.permissionTreeToList(list, perm.children)
          }else{
            temp.leaf = true
          }
          list.push(temp)
        })
      },

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
