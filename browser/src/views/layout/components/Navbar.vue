<template>
  <el-menu class="navbar" mode="horizontal">
    <hamburger class="hamburger-container" :toggleClick="toggleSideBar" :isActive="sidebar.opened"></hamburger>

    <breadcrumb class="breadcrumb-container"></breadcrumb>

    <div class="right-menu">
      <error-log class="errLog-container right-menu-item"></error-log>
      <div style="display: inline-block;">
        <span>帐号：</span><el-tag style="margin-right: 20px;">{{name}}</el-tag>
        <span>角色：</span>
        <el-tag style="margin-right: 5px;" type="danger" v-if="roles.length==0" >游客（未配置任何角色）</el-tag>
        <el-tag style="margin-right: 5px;" type="success" v-else v-for="r in roles" :key="r.val">{{r.name}}</el-tag>
      </div>
      <el-dropdown class="avatar-container right-menu-item" trigger="click">
        <div class="avatar-wrapper">
          <img class="user-avatar" :src="avatar">
          <i class="el-icon-caret-bottom"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/">
            <el-dropdown-item>
              首页
            </el-dropdown-item>
          </router-link>
          <el-dropdown-item>
            <span @click="handleUpdatePwd" style="display:block;">修改密码</span>
          </el-dropdown-item>
          <el-dropdown-item divided>
            <span @click="logout" style="display:block;">退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <!--弹出窗口：修改密码-->
    <el-dialog title="修改密码" :visible.sync="dialogVisible" width="20%">
      <el-form :rules="rules" ref="dataForm" :model="temp" label-position="left" label-width="120px">

        <el-form-item label="密码" prop="pwd">
          <el-input type="password" v-model="temp.pwd"></el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="pwd2">
          <el-input type="password" v-model="temp.pwd2"></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePwd">确定</el-button>
      </div>
    </el-dialog>
  </el-menu>



</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import ErrorLog from '@/components/ErrorLog'
import userApi from '@/api/user'

export default {

  data(){

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
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      dialogVisible: false,
      temp: {
        pwd: null,
        pwd2: null
      },
      rules: {
        pwd: [{validator: validatePass, trigger: 'blur'}],
        pwd2: [{validator: validatePass2, trigger: 'change'}]
      },
    }

  },
  components: {
    Breadcrumb,
    Hamburger,
    ErrorLog,
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'name',
      'avatar',
      'roles',
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('toggleSideBar')
    },
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        location.reload()// In order to re-instantiate the vue-router object to avoid bugs
      })
    },
    handleUpdatePwd(){
      this.dialogVisible = true
      this.$nextTick(() => this.$refs['dataForm'].clearValidate())
    },
    updatePwd(){
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) return
        const tempData = Object.assign({}, this.temp)//copy obj
        userApi.updatePwd(tempData).then(res => {
          this.dialogVisible = false
          this.$message.success("更新密码成功")
        })
      })
    },


  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0px !important;
  .hamburger-container {
    line-height: 58px;
    height: 50px;
    float: left;
    padding: 0 10px;
  }
  .breadcrumb-container{
    float: left;
  }
  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }
  .right-menu {
    float: right;
    height: 100%;
    &:focus{
     outline: none;
    }
    .right-menu-item {
      display: inline-block;
      margin: 0 8px;
    }
    .screenfull {
      height: 20px;
    }
    .international{
      vertical-align: top;
    }
    .theme-switch {
      vertical-align: 15px;
    }
    .avatar-container {
      height: 50px;
      margin-right: 30px;
      .avatar-wrapper {
        cursor: pointer;
        margin-top: 5px;
        position: relative;
        .user-avatar {
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }
        .el-icon-caret-bottom {
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
