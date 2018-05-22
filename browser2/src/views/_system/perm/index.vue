<template>
  <div class="app-container">



    <el-row>
      <el-input style="width:200px;" v-model="tableQuery.pname" placeholder="权限名"></el-input>
      <span style="margin-right: 15px;"></span>
      <el-input style="width:200px;" v-model="tableQuery.pval" placeholder="权限值"></el-input>
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
      <el-table-column prop="pid" label="权限id" ></el-table-column>
      <el-table-column prop="pname" label="权限名" ></el-table-column>
      <el-table-column prop="ptype" label="权限类型" >
        <template slot-scope="scope">
          <el-tag :type="scope.row.ptype | permTypeFilter">
            {{permTypeMap.get(scope.row.ptype) || scope.row.ptype}}
          </el-tag>
        </template>



      </el-table-column>
      <el-table-column prop="pval" label="权限值" ></el-table-column>

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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="150px" style='width: 400px; margin-left:50px;'>

        <el-form-item label="权限名" prop="pname" >
          <el-input v-model="temp.pname"></el-input>
        </el-form-item>

        <el-form-item label="权限值" prop="pval" >
          <el-input v-model="temp.pval"></el-input>
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
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">创建</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

  import { addPerm, deletePerm, queryPerm, updatePerm } from '@/api/perm'
  import { parseTime, resetTemp} from '@/utils'
  import { pageParamNames,permTypeOptions,permTypeMap, addSuccNotify, deleteSuccNotify, updateSuccNotify,deleteConfirm  } from '@/utils/constants'
  import debounce from 'lodash/debounce'

  export default {
    name: 'PermManage',
    data() {
      return {
        parseTime,
        permTypeOptions,
        permTypeMap,
        tableLoading:false,
        tableData: [],
        tableQuery:{
          pname: null,
          pval: null,
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
          idx: null,//tableData中的下标
          pid: null,
          pname: null,
          rtype: null,
          pval: null,
          created: null,
          updated: null
        },
        textMap: {
          update: '编辑权限',
          create: '新增权限'
        },
        rules: {
          pname: [{ required: true, message: '必填', trigger: 'blur' }],
          ptype: [{ required: true, message: '必填', trigger: 'blur' }],
          pval: [{ required: true, message: '必填', trigger: 'blur' }]
        },

      }
    },

    filters: {
      permTypeFilter(type) {
        const statusArr = [null,'success','info','warning','danger']
        return statusArr[type]
      }
    },

    created(){
      this.fetchData()
    },

    watch:{
      //延时查询
      'tableQuery.pname': debounce( function(){
        this.fetchData()
      },500),
      'tableQuery.pval': debounce( function(){
        this.fetchData()
      },500)
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
        queryPerm(this.tableQuery,this.tablePage).then(res => {
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
          if (valid) {
            const tempData = Object.assign({}, this.temp)//copy obj
            updatePerm(tempData).then(res => {
              this.temp.updated = res.data.updated
              this.tableData.splice(this.temp.idx, 1, this.temp)
              this.dialogFormVisible = false
              this.$notify(updateSuccNotify)
            })
          }
        })
      },

      //删除
      handleDelete(idx,row) {

        this.$confirm('您确定要永久删除该权限？', '提示',deleteConfirm).then(() => {

          deletePerm( {pid : row.pid} ).then(res => {
            this.tableData.splice(idx, 1)
            --this.tablePage.total
            this.dialogFormVisible = false
            this.$notify(deleteSuccNotify)
          })

        }).catch(() => {
          this.$message({ type: 'info', message: '已取消删除' });
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
          if (valid) {
            addPerm(this.temp).then((res) => {
              this.temp.pid = res.data.pid;//后台传回来新增记录的id
              this.temp.created = res.data.created;//后台传回来新增记录的时间
              this.tableData.unshift(this.temp)
              ++this.tablePage.total
              this.dialogFormVisible = false
              this.$notify(addSuccNotify)
            })
          }
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
