<template>
  <div class="dashboard-container">
    <component :is="currentRole"></component>
    <el-button type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleTest">auth.info</el-button>
    <el-button type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleTestUserInfo">handleTestUserInfo</el-button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import adminDashboard from './admin'
import editorDashboard from './editor'
import { testUserInfo} from '@/api/login'

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
    handleTestUserInfo(){
      testUserInfo().then(res => {
        console.log("test user info. success...")
      }).catch(error => {
        console.log("test user info. error...")
      })
    }
  }
}
</script>
