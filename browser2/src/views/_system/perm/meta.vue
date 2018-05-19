<template>
  <div class="app-container">

    <el-row :gutter="20">
      <el-col :span="12">
        <el-button @click="addTopMenu">
          添加顶级菜单
        </el-button>

        <el-tree
          :data="menuButtonTree"
          :props="{label:'pname',children:'children'}"
          show-checkbox
          node-key="id"
          default-expand-all
          :expand-on-click-node="false">
          <span class="custom-tree-node" slot-scope="{ node, data }">


              <span>{{ node.label }}</span>
              <span>
                <el-button
                  type="text"
                  size="mini"
                  @click="() => append(data)">
                  添加
                </el-button>
                <el-button
                  type="text"
                  size="mini"
                  @click="() => remove(node, data)">
                  删除
                </el-button>
              </span>

          </span>
        </el-tree>
      </el-col>
      <el-col :span="12">

      </el-col>
    </el-row>

    <el-button @click="mockGetMenuButtonListDataFromServer">生成菜单树</el-button>
    {{menuButtonTree}}

  </div>
</template>

<script>

  import {listApiPermMetadata} from '@/api/perm'

  export default {
    name: 'PermMetaDataManage',
    data() {
      return {
        mockPid : 1000,

        menuButtonList:[],

        mockMenuButtonList:[
          {pid:'1',pname:'菜单1',ptype:1,pval:'menu1',parent:null},
          {pid:'2',pname:'菜单2',ptype:1,pval:'menu2',parent:null},
          {pid:'3',pname:'菜单3',ptype:1,pval:'menu3',parent:null},
          {pid:'4',pname:'菜单4',ptype:1,pval:'menu4',parent:null},

          {pid:'31',pname:'菜单3-1',ptype:1,pval:'menu3:1',parent:'3'},
          {pid:'32',pname:'菜单3-2',ptype:1,pval:'menu3:2',parent:'3'},
          {pid:'33',pname:'菜单3-3',ptype:1,pval:'menu3:3',parent:'3'},

          {pid:'41',pname:'菜单4-1',ptype:1,pval:'menu4:1',parent:'4'},
          {pid:'42',pname:'菜单4-2',ptype:1,pval:'menu4:2',parent:'4'},

          {pid:'41a',pname:'菜单4-1-a',ptype:1,pval:'menu4:1:a',parent:'41'},
          {pid:'41b',pname:'菜单4-1-b',ptype:1,pval:'menu4:1:b',parent:'41'},
          {pid:'41c',pname:'菜单4-1-c',ptype:1,pval:'menu4:1:c',parent:'41'},

          {pid:'btn1',pname:'按钮1',ptype:2,pval:'btn1',parent:'1'},
          {pid:'btn2',pname:'按钮2',ptype:2,pval:'btn2',parent:'1'},
          {pid:'btn3',pname:'按钮3',ptype:2,pval:'btn3',parent:'1'},

        ],

        //menuButtonTree:[]
      }
    },
    computed:{
      menuButtonTree(){
       return this.findChildren(this.menuButtonList,null)
      }
    },
    methods: {

      append(data) {
        console.log(data)
        let pid = this.mockPid++;
        let newPerm = {pid: pid,pname:'权限'+pid,ptype:1,pval:'menu'+pid,parent:data.pid,children:[]}
        this.menuButtonList.push(newPerm)
        // const newChild = { pid: this.mockPid++, label: 'testtest', children: [] };
        // if (!data.children) {
        //   this.$set(data, 'children', []);
        // }
        // data.children.push(newChild);
        // console.log(data)
      },

      remove(node, data) {
        const index = this.menuButtonList.findIndex(perm => perm.pid === data.pid);
        this.menuButtonList.splice(index, 1);
      },

      handleListApiPermMetadata() {
        listApiPermMetadata().then(res => {
          console.log("listApiPermMetadata: %o", res)
        })
      },

      /*
      menuList(){
        this.menuTreeList = this.findChildren(null);
      },

      findChildren(pid){
        let submenus = this.mockPermList.filter(item=> item.parent == pid);
        submenus.forEach(perm=>{
          perm.children = this.findChildren(perm.pid)
        })
        return submenus;
      }
      */

      mockGetMenuButtonListDataFromServer(){
        this.mockMenuButtonList.forEach(perm=>{
          this.menuButtonList.push(perm);
        });
        //this.menuButtonTree = this.findChildren(this.menuButtonList,null);
      },

      findChildren(list,pid){
        let submenus = list.filter(item=> item.parent == pid);
        submenus.forEach(perm=>{
          perm.children = this.findChildren(list,perm.pid)
        })
        return submenus;
      },

      addTopMenu(){
        let pid = this.mockPid++;
        let newPerm = {pid: pid,pname:'权限'+pid,ptype:1,pval:'menu'+pid,parent:null,children:[]}
        this.menuButtonList.push(newPerm)
      }


    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

</style>
