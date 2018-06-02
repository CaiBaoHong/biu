<template>
  <div class="app-container">
    <!--查询-->
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
    <!--列表-->
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
          <el-tooltip content="修改权限" placement="top" v-if="!hasAdminRole(scope.row)">
            <el-button @click="handleUpdateRolePerms(scope.$index,scope.row)" size="medium" type="warning" icon="el-icon-view" circle plain></el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="top" v-if="!hasAdminRole(scope.row)">
            <el-button @click="handleDelete(scope.$index,scope.row)" size="medium" type="danger" icon="el-icon-delete" circle plain></el-button>
          </el-tooltip>
          <el-popover trigger="hover" placement="top" v-else style="display: inline-block;">
            <el-alert type="warning" :closable="false" title="权限说明">
              <div>为保证管理员在系统中的最高权限</div>
              <div>不允许编辑管理员自身的权限</div>
              <div>不允许删除管理员角色</div>
            </el-alert>
            <div slot="reference" >
              <el-tag style="margin-left: 10px;" type="info">权限说明</el-tag>
            </div>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-bottom: 30px;"></div>
    <!--分页-->
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
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="150px" >
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
  </div>
</template>

<script>

  import roleApi from '@/api/role'
  import { parseTime, resetTemp } from '@/utils'
  import { pageParamNames, confirm, root } from '@/utils/constants'
  import debounce from 'lodash/debounce'

  export default {
    name: 'RoleManage',
    data() {
      return {
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
      parseTime,
      hasAdminRole(row){
        return row && row.rval==root.rval
      },
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
        roleApi.queryRole(this.tableQuery,this.tablePage).then(res => {
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
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
            const tempData = Object.assign({}, this.temp)//copy obj
            roleApi.updateRole(tempData).then(res => {
              tempData.updated = res.data.updated
              this.tableData.splice(tempData.idx, 1, tempData)
              this.dialogFormVisible = false
              this.$message.success('更新角色信息成功')
            })
        })
      },

      //更新用户的角色
      handleUpdateRolePerms(idx, row) {
        this.$router.push({path:'/system/role_manage/'+row.rid+'/assign_perm'})
      },

      //删除
      handleDelete(idx,row) {
        this.$confirm('您确定要永久删除该用户？', '提示', confirm).then(() => {
          roleApi.deleteRole( {rid : row.rid} ).then(res => {
            this.tableData.splice(idx, 1)
            --this.tablePage.total
            this.dialogFormVisible = false
            this.$message.success('删除角色成功')
          })
        }).catch(() => {
          this.$message.info('已取消删除')
        })
      },

      //新增
      handleCreate() {
        resetTemp(this.temp)
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => this.$refs['dataForm'].clearValidate())
      },
      createData() {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return;
          roleApi.addRole(this.temp).then((res) => {
              this.temp.rid = res.data.rid;//后台传回来新增记录的id
              this.temp.created = res.data.created;//后台传回来新增记录的时间
              this.tableData.unshift(Object.assign({},this.temp))
              ++this.tablePage.total
              this.dialogFormVisible = false
              this.message.success('添加角色成功')
            })
        })
      },
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

</style>
