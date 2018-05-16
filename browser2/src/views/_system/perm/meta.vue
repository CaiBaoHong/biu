<template>
  <div class="app-container">
    权限元数据

    <el-button @click="handleListApiPermMetadata">
      列出所有接口权限数据
    </el-button>

  </div>
</template>

<script>

  import { listApiPermMetadata } from '@/api/perm'

  export default {
    name: 'PermMetaDataManage',
    data() {
      return {

      }
    },
    methods:{
      handleListApiPermMetadata(){
        listApiPermMetadata().then(res=>{
          console.log("listApiPermMetadata: %o",res)
        })
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>

</style>
