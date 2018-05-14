<template>
  <div class="dashboard-editor-container">
    <div class=" clearfix">
      <div class="pan-item" >
        <div class="pan-info"></div>
        <img class="pan-thumb" :src="avatar">
      </div>
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
        'nick',
        'avatar',
        'roles',
        'perms'
      ])
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .pan-info {
    position: absolute;
    width: inherit;
    height: inherit;
    border-radius: 50%;
    overflow: hidden;
    box-shadow: inset 0 0 0 5px rgba(0, 0, 0, 0.05);
  }
  .pan-item {
    float: left;
    z-index: 1;
    width: 150px;
    height: 150px;
    border-radius: 50%;
    display: inline-block;
    position: relative;
    cursor: default;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  }
  .pan-thumb {
    width: 100%;
    height: 100%;
    background-size: 100%;
    border-radius: 50%;
    overflow: hidden;
    position: absolute;
    transform-origin: 95% 40%;
    transition: all 0.3s ease-in-out;
  }

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
