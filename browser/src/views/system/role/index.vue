<template>
  <div class="app-container">

    <el-row>
      <el-input style="width:200px;" v-model="tableQuery.rname" placeholder="角色名"></el-input>
      <span style="margin-right: 15px;"></span>
      <el-input style="width:200px;" v-model="tableQuery.rval" placeholder="角色值"></el-input>
      <span style="margin-right: 15px;"></span>
      <el-tooltip class="item" content="搜索" placement="top">
        <el-button icon="el-icon-search" circle @click="fetchData()"></el-button>
      </el-tooltip>
    </el-row>

    <div style="margin-bottom: 30px;"></div>

    <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleCreate">{{textMap.create}}</el-button>

    <div style="margin-bottom: 30px;"></div>

    <el-table style="width: 100%"
              :data="tableData"
              v-loading.body="tableLoading"
              element-loading-text="Loading"
              border fit highlight-current-row>
      <el-table-column prop="rid" label="角色id" ></el-table-column>
      <el-table-column prop="rname" label="角色名" ></el-table-column>
      <el-table-column prop="rdesc" label="角色描述" ></el-table-column>
      <el-table-column prop="rval" label="角色值" ></el-table-column>

      <el-table-column prop="created" label="创建时间" >
        <template slot-scope="scope">
          <span>{{parseTime(scope.row.created)}}</span>
        </template>
      </el-table-column>

      <el-table-column prop="updated" label="更新时间" >
        <template slot-scope="scope">
          <span>{{parseTime(scope.row.updated)}}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-tooltip content="编辑" placement="top">
            <el-button @click="handleUpdate(scope.$index,scope.row)" size="medium" type="info" icon="el-icon-edit" circle plain></el-button>
          </el-tooltip>

          <el-tooltip content="修改权限" placement="top">
            <el-button @click="handleUpdateRolePerms(scope.$index,scope.row)" size="medium" type="warning" icon="el-icon-view" circle plain></el-button>
          </el-tooltip>

          <el-tooltip content="删除" placement="top">
            <el-button @click="handleDelete(scope.$index,scope.row)" size="medium" type="danger" icon="el-icon-delete" circle plain></el-button>
          </el-tooltip>
        </template>
      </el-table-column>

    </el-table>

    <div style="margin-bottom: 30px;"></div>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="tablePage.current"
      :page-sizes="[10, 20, 30, 40, 50]"
      :page-size="tablePage.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="tablePage.total">
    </el-pagination>

    <!--弹出窗口：编辑角色-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="150px" style='width: 400px; margin-left:50px;'>

        <el-form-item label="角色名" prop="rname" >
          <el-input v-model="temp.rname"></el-input>
        </el-form-item>

        <el-form-item label="角色值" prop="rval" v-if="dialogStatus=='create'">
          <el-input v-model="temp.rval"></el-input>
        </el-form-item>

        <el-form-item label="角色描述" prop="rdesc">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" placeholder="请输入" v-model="temp.rdesc">
          </el-input>
        </el-form-item>



      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">创建</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <!--弹出窗口：修改角色的权限-->
    <el-dialog title="修改角色的权限" :visible.sync="editPermsDialogVisible" width="50%" >
      <div>
        <el-tree
          ref="permTree"
          :data="menuBtnTree"
          show-checkbox
          node-key="id"
          default-expand-all
          :props="{children: 'children',label: 'label'}">

          <span slot-scope="{ node, data }">
            <span>{{node.label + " "+ data.id}}</span>
            <el-tag v-if="data.type==1" type="success" size="mini" >菜单</el-tag>
            <el-tag v-else-if="data.type==2" type="warning" size="mini" >按钮</el-tag>
          </span>

        </el-tree>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editPermsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateRolePerms">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

  import { mapGetters } from 'vuex'
  import { generateTitle } from '@/utils/i18n'  //国际化方法，用来翻译菜单的标题
  import { asyncRouterMap } from '@/router'     //路由表，定义了菜单和按钮的元数据，可以用来生成权限控制的菜单按钮树
  import { addRole, deleteRole, queryRole, updateRole, updateRolePerms,findRolePvals } from '@/api/role'
  import { parseTime, resetTemp } from '@/utils'
  import { pageParamNames, addSuccNotify, deleteSuccNotify, updateSuccNotify,deleteConfirm } from '@/utils/constants'
  import debounce from 'lodash/debounce'

  export default {
    name: 'RoleManage',
    data() {
      return {
        parseTime,
        tableLoading:false,
        tableData: [],
        tableQuery:{
          rname: null
        },
        tablePage: {
          current: null,
          pages: null,
          size:null,
          total: null
        },
        dialogFormVisible: false,
        editPermsDialogVisible: false,
        dialogStatus: '',
        temp: {
          idx: null,//表格的下标
          rid: null,
          rname: null,
          rdesc: null,
          rval: null,
          created: null,
          updated: null
        },
        textMap: {
          update: '编辑角色',
          create: '新增角色'
        },
        rules: {
          rname: [{ required: true, message: '必填', trigger: 'blur' }],
          rval: [{ required: true, message: '必填', trigger: 'blur' }]
        },

        /**
         * 最终处理完成的可以做权限控制的菜单和按钮树状结构，，由于menuBtnTreeWhichNeedPermissionControl处理而来，
         * 使用递归（filterPermControlRouter方法）将原来不符合el-tree组件要求的数据格式映射成复合要求的数据格式
         */
        menuBtnTree:[],
        updateRolePermsData:{
          rid: null,
          perms: []
        }

      }
    },

    created(){
      this.fetchData()

    },

    watch:{
      //延时查询
      'tableQuery.rname': debounce( function(){
        this.fetchData()
      },500),
      'tableQuery.rval': debounce( function(){
        this.fetchData()
      },500),
    },//watch

    methods: {

      //分页
      handleSizeChange(val) {
        this.tablePage.size = val;
        this.fetchData();
      },
      handleCurrentChange(val) {
        this.tablePage.current = val;
        this.fetchData();
      },

      //查询
      fetchData() {
        this.tableLoading = true
        queryRole(this.tableQuery,this.tablePage).then(res => {
          this.tableData = res.data.page.records
          this.tableLoading = false
          //设置后台返回的分页参数
          pageParamNames.forEach(name=>this.$set(this.tablePage,name,res.data.page[name]))
        })
      },

      //更新
      handleUpdate(idx,row) {
        this.temp = Object.assign({}, row) // copy obj
        this.temp.idx = idx
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        //清空验证表单
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
            const tempData = Object.assign({}, this.temp)//copy obj
            updateRole(tempData).then(res => {
              this.temp.updated = res.data.updated
              this.tableData.splice(this.temp.idx, 1, this.temp)
              this.dialogFormVisible = false
              this.$notify(updateSuccNotify)
            })

        })
      },

      //更新用户的角色
      handleUpdateRolePerms(idx, row) {
        // 生成可以做权限控制的菜单、按钮树
        if(this.menuBtnTree.length==0){
          this.generateMenuBtnTree()
        }
        //加载选中的角色的权限值
        findRolePvals(row.rid).then(res=>{
          if(res.data.pvals){
            console.log(res.data.pvals.filter( p => p.leaf==true ))
            //过滤出子节点
            this.$refs.permTree.setCheckedKeys(
              res.data.pvals
                .filter( p => p.leaf==true )
                .map( p => p.pval)
            )
          }

        })
        //清空缓存
        this.updateRolePermsData = {
          rid: row.rid,
          perms: []
        }
        this.editPermsDialogVisible = true
      },

      updateRolePerms() {
        this.updateRolePermsData.perms = this.getCheckedPerms()
        updateRolePerms(this.updateRolePermsData).then(res=>{
          this.editPermsDialogVisible = false
          this.$notify(updateSuccNotify)
        })

      },

      //删除
      handleDelete(idx,row) {

        this.$confirm('您确定要永久删除该用户？', '提示', deleteConfirm).then(() => {

          deleteRole( {rid : row.rid} ).then(res => {
            this.tableData.splice(idx, 1)
            --this.tablePage.total
            this.dialogFormVisible = false
            this.$notify(deleteSuccNotify)
          })

        }).catch(() => {
          this.$message({type: 'info',message: '已取消删除'});
        });
      },

      //新增
      handleCreate() {
        resetTemp(this.temp)
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
            addRole(this.temp).then((res) => {
              this.temp.rid = res.data.rid;//后台传回来新增记录的id
              this.temp.created = res.data.created;//后台传回来新增记录的时间
              this.tableData.unshift(this.temp)
              ++this.tablePage.total
              this.dialogFormVisible = false
              this.$notify(addSuccNotify)
            })

        })
      },

      //======下面是生成成权限树相关的方法：======

      //国际化方法，用来翻译菜单的标题
      generateTitle,

      //对路由元数据（即路由表）做处理生成可以做权限控制的菜单、按钮树
      generateMenuBtnTree(){
        let routeArr = asyncRouterMap.map(route=>{
          let temp = Object.assign({}, route) // copy obj

          //鉴别特殊情况，只有一个子菜单的顶级菜单
          if(temp.meta
            && !temp.meta.title //temp.meta中没有声明title信息
            && temp.children.length==1 //且只有一个子菜单
            && temp.children[0].meta.title){

            //把子级的菜单title复制到父级用于生成菜单树时显示菜单名称
            temp.meta.title = temp.children[0].meta.title
            //把子级的菜单删除，避免重复显示一个菜单的
            temp.children = []
          }
          return temp
        })
        //过滤路由表，得到需要进行权限控制的菜单按钮树
        let menuBtnTreeWhichNeedPermissionControl =  this.filterPermControlRouter(routeArr)

        //递归形成菜单树
        this.menuBtnTree = this.mapToTree(menuBtnTreeWhichNeedPermissionControl)
      },

      mapToTree(tempRouteArr){
        return tempRouteArr.map(route=>{
          let obj = {};

          if(route.meta){
            //菜单
            obj.id = route.meta.perm
            obj.label = this.generateTitle(route.meta.title)
            obj.type = 1;// 1是菜单代码
          }else{
            // 按钮
            obj.id = route.perm
            obj.label = route.title
            obj.type = 2;// 2是按钮代码
          }

          if(route.children){
            //菜单
            obj.children = this.mapToTree(route.children)
          }else if(route.meta && route.meta.btns){
            //按钮
            obj.children = this.mapToTree(route.meta.btns)
          }
          return obj;
        })
      },

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

      // 获取已选中的权限
      getCheckedPerms() {
        let checkedNodes = this.$refs.permTree.getCheckedNodes();
        let halfCheckedNodes = this.$refs.permTree.getHalfCheckedNodes();
        let checked = checkedNodes.map(obj=>{
          return {
            pval: obj.id,
            pname: obj.label,
            ptype: obj.type,
            leaf: true
          }
        })
        let halfChecked = halfCheckedNodes.map(obj=>{
          return {
            pval: obj.id,
            pname: obj.label,
            ptype: obj.type,
            leaf: false
          }
        })
        return [...checked,...halfChecked]
      },



    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .dashboard {
    &-container {
      margin: 30px;
    }
    &-text {
      font-size: 18px;
      line-height: 46px;
    }
  }
</style>
