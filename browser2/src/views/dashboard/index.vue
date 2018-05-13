<template>
  <div class="dashboard-editor-container">
    <div class=" clearfix">
      <pan-thumb style="float: left" :image="avatar"></pan-thumb>
      <div class="info-container">
        <span class="display_name">{{name}}</span>
        <span style="font-size:20px;padding-top:20px;display:inline-block;">editor : dashboard</span>
      </div>
    </div>
  </div>
</template>


<script>
  import { mapGetters } from 'vuex'
  import PanThumb from '@/components/PanThumb'

  export default {
    name: 'dashboard',
    components: { PanThumb },
    data() {
      return {

      }
    },
    computed: {
      ...mapGetters([
        'name',
        'avatar',
        'roles'
      ])
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .emptyGif {
    display: block;
    width: 45%;
    margin: 0 auto;
  }

  .dashboard-editor-container {
    background-color: #F2F6FC;
    min-height: 100vh;
    padding: 50px 60px 0px;
    .pan-info-roles {
      font-size: 12px;
      font-weight: 700;
      color: #333;
      display: block;
    }
    .info-container {
      position: relative;
      margin-left: 190px;
      height: 150px;
      line-height: 200px;
      .display_name {
        font-size: 48px;
        line-height: 48px;
        color: #212121;
        position: absolute;
        top: 25px;
      }
    }
  }
</style>
