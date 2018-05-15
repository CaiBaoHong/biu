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
      <el-table-column prop="uid" label="用户id"></el-table-column>
      <el-table-column prop="uname" label="登录名"></el-table-column>
      <el-table-column prop="nick" label="昵称"></el-table-column>

      <el-table-column prop="time" label="创建时间">
        <template slot-scope="scope">
          <span v-text="parseTime(scope.row.created)"></span>
        </template>
      </el-table-column>

      <el-table-column prop="time" label="更新时间">
        <template slot-scope="scope">
          <span v-text="parseTime(scope.row.updated)"></span>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-tooltip content="编辑" placement="top">
            <el-button @click="handleUpdate(scope.$index,scope.row)" size="medium" type="info" icon="el-icon-edit"
                       circle plain></el-button>
          </el-tooltip>

          <el-tooltip content="修改角色" placement="top">
            <el-button @click="handleUpdateUserRoles(scope.$index,scope.row)" size="medium" type="warning" icon="el-icon-star-off"
                       circle plain></el-button>
          </el-tooltip>

          <el-tooltip content="删除" placement="top">
            <el-button @click="handleDelete(scope.$index,scope.row)" size="medium" type="danger" icon="el-icon-delete"
                       circle plain></el-button>
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

    <!--弹出窗口：编辑用户-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%" >
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="150px"
               style='width: 400px; margin-left:50px;'>

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

    <!--弹出窗口：修改用户角色-->
    <el-dialog title="修改用户的角色" :visible.sync="editRolesDialogVisible" width="50%" >
      <div>
        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
        <div style="margin: 15px 0;"></div>
        <el-checkbox-group v-model="updateUserRolesData.rids" >
          <el-checkbox v-for="role in allRoles"
                       :disabled="role.rval=='root'"
                       :label="role.rid"
                       :key="role.rid">{{role.rname}}</el-checkbox>
        </el-checkbox-group>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editRolesDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateUserRoles">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

  import {queryUser, updateUser, addUser, deleteUser,findUserRoleIds,updateUserRoles} from '@/api/user'
  import {listRoles} from '@/api/role'
  import {parseTime,resetTemp} from '@/utils'
  import {pageParamNames, addSuccNotify, deleteSuccNotify, updateSuccNotify,deleteConfirm} from '@/utils/constants'
  import debounce from 'lodash/debounce'

  export default {

    name: 'UserManage',

    data() {

      let validateName = (rule, value, callback) => {
        if (this.dialogStatus == 'create' && value === '') {
          callback(new Error('必填'));
        } else {
          callback();
        }
      };

      let validatePass = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入密码'));
        } else {
          if (this.temp.pwd2 !== '') {
            this.$refs.dataForm.validateField('pwd2');
          }
          callback();
        }
      };

      let validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (value != this.temp.pwd) {
          console.log("value: " + value + ", pwd:" + this.temp.pwd)
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      };

      return {
        parseTime: parseTime,
        tableLoading: false,
        tableData: [],
        tableQuery: {
          nick: null
        },
        tablePage: {
          current: null,
          pages: null,
          size: null,
          total: null
        },
        dialogFormVisible: false,
        editRolesDialogVisible: false,
        dialogStatus: '',
        temp: {
          idx: null, //tableData中的下标
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
          uname: [{validator: validateName, trigger: 'blur'}],
          pwd: [{validator: validatePass, trigger: 'blur'}],
          pwd2: [{validator: validatePass2, trigger: 'change'}]
        },
        checkAll: false,
        isIndeterminate: true,
        // 可供选择的角色
        allRoles:[],
        updateUserRolesData:{
          uid: null,
          rids: []
        }


      }
    },

    created() {
      this.fetchData()
    },

    watch: {
      //延时查询
      'tableQuery.nick': debounce(function () {
        this.fetchData()
      }, 500)
    },//watch

    methods: {

      //全选
      handleCheckAllChange(val) {
        let allRids = this.allRoles.map(role=>role.rid)
        this.updateUserRolesData.rids = val ? allRids : [];
        this.isIndeterminate = false;
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
        queryUser(this.tableQuery, this.tablePage).then(res => {
          this.tableData = res.data.page.records
          this.tableLoading = false
          //设置后台返回的分页参数
          pageParamNames.forEach(name => this.$set(this.tablePage, name, res.data.page[name]))
        })
      },

      //更新
      handleUpdate(idx, row) {
        this.temp = Object.assign({}, row) // copy obj
        this.temp.idx = idx
        this.temp.pwd = null
        this.temp.pwd2 = null
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        //清空验证表单
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) return
          const tempData = Object.assign({}, this.temp)//copy obj
          updateUser(tempData).then(res => {
            this.temp.updated = res.data.updated
            this.tableData.splice(this.temp.idx, 1, this.temp)
            this.dialogFormVisible = false
            this.$notify(updateSuccNotify)
          })
        })
      },


      //更新用户的角色
      handleUpdateUserRoles(idx, row) {
        // 更新缓存数据
        this. updateUserRolesData = { uid: row.uid, rids: [] }
        // 加载所有角色，假设整个系统只允许有一个超级管理员，不显示超级管理员角色。
        if(this.allRoles.length==0){
          listRoles().then(res=>{
            this.allRoles = res.data.list
          })
        }
        // 加载用户已有角色。
        findUserRoleIds(row.uid).then(res=>{
          this.updateUserRolesData.rids = res.data.rids
        })
        // 显示弹窗
        this.editRolesDialogVisible = true
      },
      updateUserRoles() {
        updateUserRoles(this.updateUserRolesData).then(res=>{
          this.editRolesDialogVisible = false
          this.$notify(updateSuccNotify)
        })
      },

      //删除
      handleDelete(idx, row) {

        this.$confirm('您确定要永久删除该用户？', '提示', deleteConfirm).then(() => {

          deleteUser({uid: row.uid}).then(res => {
            this.tableData.splice(idx, 1)
            --this.tablePage.total
            this.dialogFormVisible = false
            this.$notify(deleteSuccNotify)
          })

        }).catch((err) => {
          console.log(err)
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
          addUser(this.temp).then((res) => {
            this.temp.uid = res.data.uid;//后台传回来新增记录的id
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
