<template>
  <div class="app-container">

    <el-alert
      title="基于资源的权限控制"
      type="info"
      show-icon>
      前后台的均设计成基于资源的权限控制，因为这样比基于角色的控制粒度更细，建议看看这文章：https://stormpath.com/blog/new-rbac-resource-based-access-control
    </el-alert>

    <h3>您的权限：</h3>
    {{perms}}
    <div style="margin-bottom: 20px;"></div>

    <h3>演示动态增删菜单权限：</h3>

    <el-alert title="菜单权限测试提示" type="info">
      <p>左侧菜单栏目是有“菜单1”、“菜单2”、“菜单3”三个菜单的。如果是admin登录，由于admin有三个菜单对应的权限“menu:1”、“menu:2”、“menu:3”，所以admin登录会显示三个菜单。</p>
      <p>而editor登录则菜单会显示不全，因为editor只有部分菜单权限。</p>
    </el-alert>

    <div style="margin-bottom: 20px;"></div>
    <!--
    <el-form :inline="true"  class="demo-form-inline">
      <el-form-item label="权限值">
        <el-input v-model="permVal" placeholder="请输入权限值，如：'menu:1'"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="addMenuPerm" round>添加</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="deleteMenuPerm" round>删除</el-button>
      </el-form-item>
    </el-form>

    <div style="margin-bottom: 20px;"></div>

    <el-alert
      title="警告：虽然动态增删权限能用，很酷，但是不建议使用。"
      type="warning">
      <p>由于目前的实现是这样的：</p>
      <p>
        将待增加（或删除）的权限，加入到vuex的state.perms中，然后重新生成路由配置（GenerateRoutes），
        最后调用router.addRoutes来更新路由。vue-router目前只有addRoutes这个api，没有'deleteRoutes'或'clearRoutes'这种api。
        所以不能做到先清空原来路由信息再加入新生成的路由。
      </p>
      <p>所以：</p>
      <p>最终导致会导致Router内部产生大量重复的路由配置，打开浏览器控制台可以看到警告信息，如：</p>
      <p>vue-router.esm.js:16 [vue-router] Duplicate named routes definition: { name: "errorPages", path: "/error" }</p>
      <p>结论：</p>
      <p>不建议使用</p>
    </el-alert>

    <div style="margin-bottom: 20px;"></div>
    -->

    <h3>演示动态按钮权限：</h3>

    <el-alert title="菜单权限测试提示" type="info">
      <p>下面有4个按钮。如果是admin登录，会全部显示，而editor登录则显示不全，因为editor只有部分权限。</p>
      <p>您也可以点击下面的“添加”、"删除"按钮来动态增删权限来测试按钮的显示的变化（仅仅是修改页面上的vuex中存储的状态，刷新页面会还原）</p>
    </el-alert>

    <div style="margin-bottom: 20px;"></div>

    <el-button v-perm="'btn:1'">按钮1</el-button>
    <el-button v-perm="'btn:2'">按钮2</el-button>
    <el-button v-perm='"btn:3"'>按钮3</el-button>
    <el-button v-perm='"btn:4"'>按钮4</el-button>

    <div style="margin-bottom: 20px;"></div>

    <el-form :inline="true"  class="demo-form-inline">
      <el-form-item label="权限值">
        <el-input v-model="permVal2" placeholder="请输入权限值，如：'btn:1'"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="addBtnPerm" round>添加</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="deleteBtnPerm" round>删除</el-button>
      </el-form-item>
    </el-form>

    <el-alert
      title="提示："
      type="success">
      <p>按钮级别的权限实现非常简单，若您要使用，可以在页面元素上写上自定义指令v-perm="参数"，参数是显示该元素的所需要的权限值。如：
        &lt;el-button v-perm='"btn:3"'&gt;按钮3&lt;/el-button&gt;。
      </p>
      <p>v-perm指令内部会拿到该参数，与vuex的store.getters.perms中存储的后台返回的权限值匹配。如果有匹配的权限则显示元素。细节请看src/directive/perm.js</p>
      <p>安全性方面，v-perm指令根据权限简单地控制元素是否显示。后台也会对接口做权限判断的，所以不必担心恶意用户绕过v-perm指令找到隐藏的元素调用后台接口</p>
    </el-alert>

  </div>
</template>

<script>
import router from '@/router'
import store from '@/store'
import { mapGetters } from 'vuex'

export default{
  name: 'permission',
  data() {
    return {
      permVal:'menu:1',
      permVal2:'btn:1',
    }
  },
  computed: {
    ...mapGetters([
      'perms'
    ])
  },
  methods:{
    addMenuPerm(){
      console.log("addPerm: "+this.permVal)
      if(this.permVal){
        this.$store.commit("ADD_PERM",this.permVal)
        let perms = this.perms;
        store.dispatch('GenerateRoutes', { perms }).then(() => {
          router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
        })
      }
    },
    deleteMenuPerm(){
      if(this.permVal){
        this.$store.commit("DEL_PERM",this.permVal)
        let perms = this.perms;
        store.dispatch('GenerateRoutes', { perms }).then(() => {
          router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
        })
      }
    },
    addBtnPerm(){
      this.$store.commit("ADD_PERM",this.permVal2)
    },
    deleteBtnPerm(){
      this.$store.commit("DEL_PERM",this.permVal2)
    },
  },

  // watch: {
    // switchRoles(val) {
    //   this.$store.dispatch('ChangeRoles', val).then(() => {
    //     this.$router.push({ path: '/permission/index?' + +new Date() })
    //   })
    // }
  // }
}
</script>
