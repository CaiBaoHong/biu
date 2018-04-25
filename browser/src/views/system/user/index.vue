<template>
  <div class="app-container">

    <el-row>
      <el-input style="width:200px;" v-model="tableQuery.nick" placeholder="昵称"></el-input>
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
      <el-table-column prop="uid" label="用户id" ></el-table-column>
      <el-table-column prop="uname" label="登录名" ></el-table-column>
      <el-table-column prop="nick" label="昵称" ></el-table-column>

      <el-table-column prop="time" label="创建时间" >
        <template slot-scope="scope">
          <span v-text="parseTime(scope.row.created)"></span>
        </template>
      </el-table-column>

      <el-table-column prop="time" label="更新时间" >
        <template slot-scope="scope">
          <span v-text="parseTime(scope.row.updated)"></span>
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

        <el-form-item label="登录名" prop="uname" v-if="dialogStatus=='create'">
          <el-input v-model="temp.uname"></el-input>
        </el-form-item>

        <el-form-item label="昵称" prop="nick">
          <el-input v-model="temp.nick"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="pwd">
          <el-input type="password" v-model="temp.pwd"></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="pwd2">
          <el-input type="password" v-model="temp.pwd2"></el-input>
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

  import { queryUser,updateUser,addUser,deleteUser } from '@/api/user'
  import { parseTime} from '@/utils'
  import { pageParamNames } from '@/utils/constants'
  import debounce from 'lodash/debounce'

  export default {
    name: 'UserManage',
    data() {

      var validateName = (rule, value, callback) => {
        if (this.dialogStatus=='create' && value === '') {
          callback(new Error('必填'));
        }else{
          callback();
        }
      };

      var validatePass = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入密码'));
        } else {
          if (this.temp.pwd2 !== '') {
            this.$refs.dataForm.validateField('pwd2');
          }
          callback();
        }
      };
      var validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (value != this.temp.pwd) {
          console.log("value: "+value+", pwd:"+this.temp.pwd)
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      };

      return {
        parseTime: parseTime,
        tableLoading:false,
        tableData: [],
        tableQuery:{
          nick: null
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
          uid: null,
          uname: null,
          nick: null,
          pwd: null,
          pwd2: null,
          created: null
        },
        textMap: {
          update: '编辑用户',
          create: '新增用户'
        },
        rules: {
          //uname: [{ required: true, message: '必填', trigger: 'blur' }],
          uname: [{ validator: validateName, trigger: 'blur' }],
          pwd: [{ validator: validatePass, trigger: 'blur' }],
          pwd2: [{ validator: validatePass2, trigger: 'change' }]
        },

      }
    },

    computed: {
      searchIndicator() {
        if (this.searchBox.searching) {
          return '⟳ Fetching new results'
        } else if (this.searchBox.dirty) {
          return '... Typing'
        } else {
          return '✓ Done'
        }
      }
    },

    created(){
      this.fetchData()
    },

    watch:{
      //延时查询
      'tableQuery.nick': debounce( function(){
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
        queryUser(this.tableQuery,this.tablePage).then(res => {
          this.tableData = res.data.page.records
          this.tableLoading = false
          //设置后台返回的分页参数
          pageParamNames.forEach(name=>this.$set(this.tablePage,name,res.data.page[name]))
        })
      },

      //更新
      handleUpdate(idx,row) {
        this.temp = Object.assign({}, row) // copy obj
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
            updateUser(tempData).then(res => {
              this.temp.updated = res.data.updated
              for (const v of this.tableData) {
                if (v.uid === this.temp.uid) {
                  const index = this.tableData.indexOf(v)
                  this.tableData.splice(index, 1, this.temp)
                  break
                }
              }
              this.dialogFormVisible = false
              this.$notify({
                title: '成功',
                message: '更新成功',
                type: 'success',
                duration: 2000
              })
            })
          }
        })
      },

      //删除
      handleDelete(idx,row) {

        this.$confirm('您确定要永久删除该用户？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {

          deleteUser( {uid : row.uid} ).then(res => {
            this.tableData.splice(idx, 1)
            this.dialogFormVisible = false
            this.$notify({ title: '成功', message: '删除成功', type: 'success', duration: 2000 })
          })
        }).catch((err) => {
          console.log(err)
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },

      //新增
      handleCreate() {
        this.resetTemp()
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        console.log("createDate...")
        this.$refs['dataForm'].validate((valid) => {
          console.log("valid: "+valid)
          if (valid) {
            addUser(this.temp).then((res) => {
              this.temp.uid = res.data.uid;//后台传回来新增记录的id
              this.temp.created = res.data.created;//后台传回来新增记录的时间
              this.tableData.unshift(this.temp)
              this.dialogFormVisible = false
              this.$notify({
                title: '成功',
                message: '创建成功',
                type: 'success',
                duration: 2000
              })
            })
          }
        })
      },

      //清空缓存对象
      resetTemp() {
        this.temp = {
          uid: null,
          uname: null,
          nick: null,
          pwd: null,
          pwd2: null,
          created: null
        }
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
