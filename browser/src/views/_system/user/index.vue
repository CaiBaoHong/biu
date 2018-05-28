<template>
  <div class="app-container">

    <el-row>
      <el-input style="width:200px;" v-model="tableQuery.nick" placeholder="昵称"></el-input>
      <span style="margin-right: 15px;"></span>
      <el-tooltip class="item" content="搜索" placement="top">
        <el-button icon="el-icon-search" circle @click="fetchData(1)"></el-button>
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
      <el-table-column label="角色">
        <template slot-scope="scope">
          <el-tag style="margin: 2px;"
                  v-for="role in scope.row.roleList"
                  :key="role.rid">{{role.rname}}</el-tag>
        </template>
      </el-table-column>
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
          <el-tooltip content="修改角色" placement="top" v-if="!hasAdminRole(scope.row)">
            <el-button @click="handleUpdateUserRoles(scope.$index,scope.row)" size="medium" type="warning"
                       icon="el-icon-star-off"
                       circle plain></el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="top" v-if="!hasAdminRole(scope.row)">
            <el-button @click="handleDelete(scope.$index,scope.row)" size="medium" type="danger" icon="el-icon-delete"
                       circle plain></el-button>
          </el-tooltip>

          <el-popover trigger="hover" placement="top" v-else style="display: inline-block;">
            <el-alert type="warning" :closable="false" title="权限说明">
              <div>为保证管理员在系统中的最高权限</div>
              <div>不允许编辑管理员自身的角色</div>
              <div>不允许删除管理员账号</div>
            </el-alert>
            <div slot="reference" >
              <el-tag style="margin-left: 10px;" type="info">权限说明</el-tag>
            </div>
          </el-popover>


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

    <!--弹出窗口：新增/编辑用户-->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
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
    <el-dialog title="修改用户的角色" :visible.sync="editRolesDialogVisible" width="50%">
      <div>
        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
        <div style="margin: 15px 0;"></div>
        <el-checkbox-group v-model="updateUserRolesData.rids">
          <el-checkbox v-for="role in roleOptions"
                       :disabled="role.val2=='root'"
                       :label="role.id"
                       :key="role.id">{{role.val}}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editRolesDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="checkUpdateUserRolesData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

  import {addUser, deleteUser, findUserRoleIds, queryUser, updateUser, updateUserRoles} from '@/api/user'
  import {listRoles} from '@/api/role'
  import {parseTime, resetTemp} from '@/utils'
  import {addSuccNotify, deleteConfirm, deleteSuccNotify, pageParamNames, updateSuccNotify} from '@/utils/constants'
  import debounce from 'lodash/debounce'
  import optionApi from '@/api/option'

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
        allRoles: [],
        updateUserRolesData: {
          idx: null,
          uid: null,
          rids: []
        },
        roleOptions:[],
        roleMap: new Map()
      }

    },

    created() {
      this.initData()
    },

    watch: {
      //延时查询
      'tableQuery.nick': debounce(function () {
        this.fetchData()
      }, 500)
    },//watch

    methods: {

      initData(){
        optionApi.listRoleOptions().then(res => {
          res.data.options.forEach(obj => {
            this.roleOptions.push(obj)
            this.roleMap.set(obj.id, obj.val)
          })
          //查询数据
          this.fetchData()
        })
      },

      hasAdminRole(row){
        if(row && row.roleList){
          return row.roleList.some(role=>role.rval=='root')
        }
        return false
      },

      //全选
      handleCheckAllChange(val) {
        let allRids = this.allRoles.map(role => role.rid)
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
      fetchData(current) {
        if(current){
          this.tablePage.current = current
        }
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
            this.$message.success("更新成功")
          })
        })
      },


      //更新用户的角色
      handleUpdateUserRoles(idx, row) {
        // 更新缓存数据
        this.updateUserRolesData = {
          idx: idx,
          uid: row.uid,
          rids: row.roleList.map(role=>role.rid)
        }
        // 显示弹窗
        this.editRolesDialogVisible = true
      },

      checkUpdateUserRolesData() {
        if(this.updateUserRolesData && this.updateUserRolesData.rids && this.updateUserRolesData.rids.length ==0){
          this.$confirm('当前没有选中任何角色，会清除该用户已有的角色, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.invokeUpdateUserRolesApi()
          }).catch(() => {
            this.$message("已取消编辑用户角色");
          });
        }else{
          this.invokeUpdateUserRolesApi()
        }
      },

      invokeUpdateUserRolesApi(){
        updateUserRoles(this.updateUserRolesData).then(res => {
          let newRoles = this.updateUserRolesData.rids.map(rid=>{
            let rname = this.roleMap.get(rid);
            if(rname) return {rid,rname}
          })
          this.tableData[this.updateUserRolesData.idx].roleList = newRoles
          this.editRolesDialogVisible = false
          this.$message.success("更新成功")
        })
      },

      //删除
      handleDelete(idx, row) {

        this.$confirm('您确定要永久删除该用户？', '提示', deleteConfirm).then(() => {

          deleteUser({uid: row.uid}).then(res => {
            this.tableData.splice(idx, 1)
            --this.tablePage.total
            this.dialogFormVisible = false
            this.$message.success("删除成功")
          })

        }).catch((err) => {
          console.log(err)
          this.$message.info("已取消删除")
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
            this.$message.success("添加成功")
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

  .card-title {
    color: #409EFF;
    text-align: center;
    font-size: 18px;
    font-weight: 300;
    margin-bottom: 20px;
  }
</style>
