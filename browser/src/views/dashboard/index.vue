<template>
  <div class="dashboard-container">
    <component :is="currentRole"></component>
    <el-button type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleTest">auth.info</el-button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import adminDashboard from './admin'
import editorDashboard from './editor'

export default {
  name: 'dashboard',
  components: { adminDashboard, editorDashboard },
  data() {
    return {
      currentRole: 'adminDashboard'
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  created() {
    if (!this.roles.includes('admin')) {
      this.currentRole = 'editorDashboard'
    }
  },
  methods:{
    handleTest() {
      this.$store.dispatch('GetUserInfo')
    },
  }
}
</script>
