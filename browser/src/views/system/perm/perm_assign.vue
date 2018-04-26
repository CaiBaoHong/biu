<template>

  <div class="app-container">

    <h3>视图中可以做权限控制的菜单/按钮的树状结构：</h3>

    <el-alert title="" type="info" :closable="false">
      这里演示了如果从路由表过滤出可以做权限控制的路由，并把遍历出来。generateTitle()是国际化用来翻译菜单名的方法
    </el-alert>

    <ul v-for="item in menuBtnTreeWhichNeedPermissionControl">
      {{"title: "+generateTitle(item.meta.title)+", perm: "+item.meta.perm}}
      <ul v-if="item.children">
        <li v-for="child in item.children">
          {{"title: "+generateTitle(child.meta.title)+", perm: "+child.meta.perm}}
          <ol v-if="child.meta.btns">
            <li v-for="btn in child.meta.btns">
              {{"title: "+btn.title+", perm: "+btn.perm}}
            </li>
          </ol>
        </li>
      </ul>
    </ul>

    <hr>

    <h3>树形控件：</h3>
    <h4>菜单按钮树的json：</h4>
    <el-alert title="" type="info" :closable="false">
      下面的json是通过对路由表过滤出可以做权限控制的菜单按纽树，但是由于这个树的结构不符合传入&lt;el-tree&gt;组件的格式，
      因此我用this.menuBtnTree = this.mapToTree(this.menuBtnTreeWhichNeedPermissionControl)这个方法做了处理，
      转换成&lt;el-tree&gt;能识别的数据结构。
    </el-alert>
    <vue-json-pretty :data="menuBtnTree"></vue-json-pretty>

    <h4>被选中的菜单按钮节点的json：</h4>

    <el-row :gutter="20">
      <el-col :span="4">
        <el-button @click="getCheckedKeys">获取选中节点的key</el-button>
      </el-col>
      <el-col :span="20">
        <vue-json-pretty :data="checkedKeys"></vue-json-pretty>
      </el-col>
    </el-row>

    <div style="margin-bottom: 10px;"></div>

    <el-row :gutter="20">
      <el-col :span="4">
        <el-button @click="getHalfCheckedKeys">获取半选节点的key</el-button>
      </el-col>
      <el-col :span="20">
        <vue-json-pretty :data="halfCheckedKeys"></vue-json-pretty>
      </el-col>
    </el-row>

    <div style="margin-bottom: 10px;"></div>

    <el-row :gutter="20">
      <el-col :span="4">
        <el-button @click="getCheckedNodes">获取选中节点的Node</el-button>
      </el-col>
      <el-col :span="20">
        <vue-json-pretty :data="checkedNodes"></vue-json-pretty>
      </el-col>
    </el-row>

    <div style="margin-bottom: 10px;"></div>

    <el-row :gutter="20">
      <el-col :span="4">
        <el-button @click="getHalfCheckedNodes">获取半选节点的Node</el-button>
      </el-col>
      <el-col :span="20">
        <vue-json-pretty :data="halfCheckedNodes"></vue-json-pretty>
      </el-col>
    </el-row>


    <h4>下面演示完整的菜单按钮树：</h4>
    <el-tree
      ref="permTree"
      :data="menuBtnTree"
      show-checkbox
      node-key="id"
      default-expand-all
      :default-checked-keys="permsArr"
      :props="{children: 'children',label: 'label'}">

      <span slot-scope="{ node, data }">
        <span>{{node.label + " "+ data.id}}</span>
        <el-tag v-if="data.type==1" type="success" size="mini" >菜单</el-tag>
        <el-tag v-else-if="data.type==2" type="warning" size="mini" >按钮</el-tag>
      </span>

    </el-tree>

  </div>
</template>

<script>

  import { mapGetters } from 'vuex'
  import { generateTitle } from '@/utils/i18n'  //国际化方法，用来翻译菜单的标题
  import { asyncRouterMap } from '@/router'     //路由表，定义了菜单和按钮的元数据，可以用来生成权限控制的菜单按钮树
  import VueJsonPretty from 'vue-json-pretty'

  export default {
    name: 'PermAssign',
    components: {
      VueJsonPretty
    },
    data() {
      return {
        /**
         * 可以做权限控制的菜单和按钮的路由表，由路由元数据asyncRouterMap处理而来，
         * 使用递归（filterPermControlRouter方法）筛选了路由表中含有"perm"属性的路由
         */
        menuBtnTreeWhichNeedPermissionControl:[],
        /**
         * 最终处理完成的可以做权限控制的菜单和按钮树状结构，，由于menuBtnTreeWhichNeedPermissionControl处理而来，
         * 使用递归（filterPermControlRouter方法）将原来不符合el-tree组件要求的数据格式映射成复合要求的数据格式
         */
        menuBtnTree:[],
        /**
         * 被选中的节点的key
         */
        checkedKeys:[],
        /**
         * 半选中状态的节点的key
         */
        halfCheckedKeys:[],
        /**
         * 被选中的节点的node
         */
        checkedNodes:[],
        /**
         * 半选中状态的节点的node
         */
        halfCheckedNodes:[],
      }
    },
    computed: {
      // 从vuex store中获取用户权限数据。这个数据是用户登录后，存入到vuex store的，如：['menu:1','user:add']
      ...mapGetters([
        'permsArr'
      ])
    },

    created() {
      //页面加载时，生成可以做权限控制的菜单、按钮树
      this.generateMenuBtnTree()
    },

    methods: {

      //国际化方法，用来翻译菜单的标题
      generateTitle,

      //获取选中状态的节点的key
      getCheckedKeys() {
        this.checkedKeys = this.$refs.permTree.getCheckedKeys();
      },

      //获取半选中状态的节点的key
      getHalfCheckedKeys(){
        this.halfCheckedKeys = this.$refs.permTree.getHalfCheckedKeys();
      },

      //获取选中状态的节点的key
      getCheckedNodes() {
        this.checkedNodes = this.$refs.permTree.getCheckedNodes();
      },

      //获取半选中状态的节点的key
      getHalfCheckedNodes(){
        this.halfCheckedNodes = this.$refs.permTree.getHalfCheckedNodes();
      },

      //对路由元数据（即路由表）做处理生成可以做权限控制的菜单、按钮树
      generateMenuBtnTree(){
        /**
         * 对路由表预处理，因为路由表对单个菜单的定义有点特殊，也是含有children属性的树状结果，如：
         *   {
         *     path: '/menu1',
         *     redirect: '/menu1/index',
         *     component: Layout,
         *     meta: { perm: 'menu:1' },
         *     children: [{
         *       path: 'index',
         *       name: 'menu1',
         *       component: _import('menu1/index'),
         *       meta: { title: 'menu1', icon: 'lock' }
         *     }]
         *   },
         *
         *   meta.perm 和 childen[0].meta.perm的作用是不一样的：
         *
         *   在用户登录生成左侧菜单树的时候，声明了{ perm: 'menu:1' }，菜单才能显示出来。
         *
         *   1. 如果meta中声明了perm，且childen[0].meta也声明了perm，则会显示成一个可展开的菜单。
         *   2. 如果meta中没有声明perm，但childen[0].meta声明了perm，则会导致显示一个空白的菜单。
         *
         *
         *   meta.title 和 childen[0].meta.title的作用是不一样的：
         *   3. 如果meta中声明了title，但childen[0].meta没有声明了title，则会导致在页面上无法显示菜单名（点击仍可以访问）。
         *   4. 如果meta中没有声明title，但childen[0].meta声明了title，是可以正常显示菜单，这也说明菜单栏上的名的生成是在从children[0].meta.title拿数据来生成的。
         *   5. 但是，第4点虽然可以正常显示菜单，但是我们生成菜单树的时候如果按照这种状结构，会显示一个多余的菜单在菜单树中。
         *
         *   所以下面就要对路由元数据做预处理。修正这种特殊的情况。把单个菜单但是仍在children中声明路由信息的树状结构，改成一个非树状结构。
         *
         *   最佳实践：
         *   在meta中声明perm
         *   在children[0].meta中声明title，
         *
         *
         */
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
        this.menuBtnTreeWhichNeedPermissionControl =  this.filterPermControlRouter(routeArr)

        //递归形成菜单树
        //let tempRouteArr = this.filterPermControlRouter(routeArr)
        this.menuBtnTree = this.mapToTree(this.menuBtnTreeWhichNeedPermissionControl)


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
