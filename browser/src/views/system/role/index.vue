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
          <el-tooltip class="item" content="编辑" placement="top">
            <el-button @click="handleUpdate(scope.$index,scope.row)" size="mini" type="info" icon="el-icon-edit" circle plain></el-button>
          </el-tooltip>
          <el-tooltip class="item" content="删除" placement="top">
            <el-button @click="handleDelete(scope.$index,scope.row)" size="mini" type="danger" icon="el-icon-delete" circle plain></el-button>
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

  </div>
</template>

<script>

  import { addRole, deleteRole, queryRole, updateRole, updateRolePerms } from '@/api/role'
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
